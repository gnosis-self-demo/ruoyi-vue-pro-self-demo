package com.gnosis.paramcheck.service;

import org.postgresql.util.PGobject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * ParamCheck 模块数据库初始化服务
 *
 * 在应用启动后执行建表和初始化数据。
 * 所有语句均使用 try-catch 包装，幂等执行：
 * - 表已存在 → 跳过（不报错）
 * - 数据已存在 → UPDATE 确保数据正确（不报错）
 *
 * 原因: openGauss 3.0.0 不支持以下 PostgreSQL 语法:
 * - CREATE INDEX IF NOT EXISTS
 * - INSERT ... ON CONFLICT ... DO NOTHING
 * - ::jsonb 在 JDBC PreparedStatement 中用 ? 占位符无法自动识别
 * 因此放弃 Spring SQL 自动初始化，改为手动初始化。
 */
@Service
public class ParamCheckInitService {

    private static final Logger log = LoggerFactory.getLogger(ParamCheckInitService.class);

    @Autowired
    private JdbcTemplate gnosisJdbcTemplate;

    @PostConstruct
    public void init() {
        log.info("[ParamCheckInit] 开始初始化数据库...");
        try {
            createTables();
            initTestData();
            log.info("[ParamCheckInit] 数据库初始化完成");
        } catch (Exception e) {
            log.error("[ParamCheckInit] 数据库初始化失败", e);
            // 不阻断启动，模块降级
        }
    }

    private void createTables() {
        safeExecute(
            "CREATE TABLE gnosis_sample.sys_validation_flows (" +
            "    flow_id         VARCHAR(64) PRIMARY KEY," +
            "    flow_name       VARCHAR(128) NOT NULL," +
            "    mode_type       VARCHAR(20) DEFAULT 'FLOW'," +
            "    el_expression   TEXT," +
            "    handler_code    VARCHAR(64)," +
            "    component_config TEXT," +
            "    is_active       BOOLEAN DEFAULT TRUE," +
            "    version         INT DEFAULT 1," +
            "    updated_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "sys_validation_flows"
        );

        safeExecute(
            "CREATE TABLE gnosis_sample.sys_validation_logs (" +
            "    log_id          BIGSERIAL PRIMARY KEY," +
            "    flow_id         VARCHAR(64)," +
            "    request_id      VARCHAR(64)," +
            "    mode_type       VARCHAR(20)," +
            "    input_snapshot  TEXT," +
            "    failed_node     VARCHAR(64)," +
            "    error_msg       TEXT," +
            "    created_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "sys_validation_logs"
        );

        safeExecute(
            "CREATE TABLE gnosis_sample.product_stock (" +
            "    sku_id          VARCHAR(64) PRIMARY KEY," +
            "    sku_name        VARCHAR(128)," +
            "    stock_qty       INT DEFAULT 0," +
            "    updated_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "product_stock"
        );

        safeExecute(
            "CREATE TABLE gnosis_sample.order_record (" +
            "    order_id        BIGSERIAL PRIMARY KEY," +
            "    order_no        VARCHAR(64) NOT NULL," +
            "    user_id         VARCHAR(64)," +
            "    amount          DECIMAL(15,2)," +
            "    status          VARCHAR(20)," +
            "    created_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "order_record"
        );

        safeExecute(
            "CREATE TABLE gnosis_sample.sys_users (" +
            "    user_id         VARCHAR(64) PRIMARY KEY," +
            "    username        VARCHAR(64) NOT NULL UNIQUE," +
            "    password        VARCHAR(128)," +
            "    email           VARCHAR(128)," +
            "    status          INT DEFAULT 1," +
            "    created_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "sys_users"
        );

        log.info("[ParamCheckInit] 建表完成");
    }

    private void initTestData() {
        // 初始化产品库存
        initStock("SKU001", "iPhone 15 Pro", 100);
        initStock("SKU002", "MacBook Pro 14", 50);
        initStock("SKU003", "AirPods Pro", 200);

        // 初始化用户
        initUser("U001", "zhangsan", "zhangsan@example.com", 1);
        initUser("U002", "lisi", "lisi@example.com", 1);
        initUser("U003", "wangwu", "wangwu@example.com", 0);

        // 初始化流程配置
        upsertFlow("ORDER_CREATE_FLOW", "订单创建校验 (混合模式)", "HYBRID",
            "THEN(parse_param, check_format, check_db_user)", "OrderBusinessHandler",
            "{\"parse_param\":{\"json_path\":\"$\"},\"check_format\":{\"rules\":[{\"path\":\"$.orderNo\",\"type\":\"REGEX\",\"pattern\":\"^ORD[0-9]{10}$\",\"msg\":\"订单号格式错误，格式应为 ORD+10位数字\"},{\"path\":\"$.amount\",\"type\":\"RANGE\",\"min\":0.01,\"max\":1000000,\"msg\":\"订单金额超出范围(0.01~1000000)\"},{\"path\":\"$.userId\",\"type\":\"NOT_NULL\",\"msg\":\"用户ID不能为空\"}]},\"check_db_user\":{\"type\":\"DB_QUERY\",\"sql\":\"SELECT COUNT(1) FROM gnosis_sample.sys_users WHERE user_id = ? AND status = 1\",\"param_path\":\"$.userId\",\"msg\":\"用户不存在或已被禁用\"}}");

        upsertFlow("USER_REGISTER_FLOW", "用户注册 (纯接口模式)", "HANDLER",
            null, "UserRegisterHandler", null);

        upsertFlow("SIMPLE_CHECK_FLOW", "简单格式校验 (纯编排模式)", "FLOW",
            "THEN(parse_param, check_format)", null,
            "{\"parse_param\":{\"json_path\":\"$\"},\"check_format\":{\"rules\":[{\"path\":\"$.email\",\"type\":\"EMAIL\",\"msg\":\"邮箱格式错误\"},{\"path\":\"$.phone\",\"type\":\"PHONE\",\"msg\":\"手机号格式错误\"}]}}");

        log.info("[ParamCheckInit] 初始化数据完成");
    }

    /**
     * 使用 INSERT ... ON CONFLICT DO UPDATE 实现 upsert
     * openGauss 支持此语法（MERGE 语法兼容）
     */
    private void upsertFlow(String flowId, String flowName, String modeType,
                           String elExpression, String handlerCode, String componentConfig) {
        try {
            // 先尝试 UPDATE（已有记录则更新配置）
            int updated = gnosisJdbcTemplate.update(
                "UPDATE gnosis_sample.sys_validation_flows " +
                "SET flow_name=?, mode_type=?, el_expression=?, handler_code=?, component_config=?, version=version+1 " +
                "WHERE flow_id=?",
                flowName, modeType, elExpression, handlerCode, toPgJson(componentConfig), flowId);

            if (updated == 0) {
                // 无记录，执行 INSERT
                gnosisJdbcTemplate.update(
                    "INSERT INTO gnosis_sample.sys_validation_flows " +
                    "(flow_id, flow_name, mode_type, el_expression, handler_code, component_config) " +
                    "VALUES (?, ?, ?, ?, ?, ?)",
                    flowId, flowName, modeType, elExpression, handlerCode, toPgJson(componentConfig));
                log.info("[ParamCheckInit] 插入流程: {}", flowId);
            } else {
                log.info("[ParamCheckInit] 更新流程: {}", flowId);
            }
        } catch (Exception e) {
            log.warn("[ParamCheckInit] upsert流程失败: {} - {}", flowId, e.getMessage());
        }
    }

    /**
     * 将 JSON 字符串转换为 openGauss jsonb 类型
     * 解决 JDBC ? 占位符无法自动识别 jsonb 的问题
     */
    private Object toPgJson(String json) {
        if (json == null) {
            return null;
        }
        try {
            PGobject pg = new PGobject();
            pg.setType("jsonb");
            pg.setValue(json);
            return pg;
        } catch (java.sql.SQLException e) {
            log.warn("[ParamCheckInit] 转换 JSON 失败，回退为字符串", e);
            return json;
        }
    }

    private void initStock(String skuId, String skuName, int qty) {
        try {
            Integer exists = gnosisJdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM gnosis_sample.product_stock WHERE sku_id = ?",
                Integer.class, skuId);
            if (exists == null || exists == 0) {
                gnosisJdbcTemplate.update(
                    "INSERT INTO gnosis_sample.product_stock (sku_id, sku_name, stock_qty) VALUES (?, ?, ?)",
                    skuId, skuName, qty);
                log.info("[ParamCheckInit] 插入库存: {}", skuId);
            }
        } catch (Exception e) {
            log.warn("[ParamCheckInit] 插入库存失败: {} - {}", skuId, e.getMessage());
        }
    }

    private void initUser(String userId, String username, String email, int status) {
        try {
            Integer exists = gnosisJdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM gnosis_sample.sys_users WHERE user_id = ?",
                Integer.class, userId);
            if (exists == null || exists == 0) {
                gnosisJdbcTemplate.update(
                    "INSERT INTO gnosis_sample.sys_users (user_id, username, email, status) VALUES (?, ?, ?, ?)",
                    userId, username, email, status);
                log.info("[ParamCheckInit] 插入用户: {}", userId);
            }
        } catch (Exception e) {
            log.warn("[ParamCheckInit] 插入用户失败: {} - {}", userId, e.getMessage());
        }
    }

    private void safeExecute(String sql, String tableName) {
        try {
            gnosisJdbcTemplate.execute(sql);
            log.info("[ParamCheckInit] 执行成功: {}", tableName);
        } catch (Exception e) {
            // 表已存在或其他问题 — 忽略，不阻断启动
            log.debug("[ParamCheckInit] 执行跳过[{}]: {}", tableName, e.getMessage());
        }
    }
}
