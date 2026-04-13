package com.gnosis.paramcheck.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ParamCheckInitService {

    private static final Logger log = LoggerFactory.getLogger(ParamCheckInitService.class);

    @Autowired
    @Qualifier("gnosisJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        log.info("[ParamCheckInit] 开始初始化数据库...");
        try {
            createTables();
            initTestData();
            log.info("[ParamCheckInit] 数据库初始化完成");
        } catch (Exception e) {
            log.error("[ParamCheckInit] 数据库初始化失败", e);
        }
    }

    private void createTables() {
        safeExecute(
            "CREATE TABLE IF NOT EXISTS gnosis_sample.sys_validation_flows (" +
            "    flow_id         VARCHAR(64) PRIMARY KEY," +
            "    flow_name       VARCHAR(128) NOT NULL," +
            "    mode_type       VARCHAR(20) DEFAULT 'FLOW'," +
            "    el_expression   TEXT," +
            "    handler_code    VARCHAR(64)," +
            "    component_config TEXT," +
            "    business_type   VARCHAR(255)," +
            "    is_active       BOOLEAN DEFAULT TRUE," +
            "    version         INT DEFAULT 1," +
            "    create_user_id  VARCHAR(128)," +
            "    update_user_id  VARCHAR(128)," +
            "    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "sys_validation_flows"
        );

        safeExecute(
            "CREATE TABLE IF NOT EXISTS gnosis_sample.sys_validation_logs (" +
            "    log_id          BIGSERIAL PRIMARY KEY," +
            "    flow_id         VARCHAR(64)," +
            "    request_id      VARCHAR(64)," +
            "    mode_type       VARCHAR(20)," +
            "    input_snapshot  TEXT," +
            "    failed_node     VARCHAR(64)," +
            "    error_msg       TEXT," +
            "    is_active       BOOLEAN DEFAULT TRUE," +
            "    create_user_id  VARCHAR(128)," +
            "    update_user_id  VARCHAR(128)," +
            "    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "sys_validation_logs"
        );

        safeExecute(
            "CREATE TABLE IF NOT EXISTS gnosis_sample.sys_business_types (" +
            "    code            VARCHAR(64) PRIMARY KEY," +
            "    name            VARCHAR(128) NOT NULL," +
            "    description     TEXT," +
            "    is_active       BOOLEAN DEFAULT TRUE," +
            "    create_user_id  VARCHAR(128)," +
            "    update_user_id  VARCHAR(128)," +
            "    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
            "    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "sys_business_types"
        );

        safeExecute(
            "CREATE TABLE IF NOT EXISTS gnosis_sample.product_stock (" +
            "    sku_id          VARCHAR(64) PRIMARY KEY," +
            "    sku_name        VARCHAR(128)," +
            "    stock_qty       INT DEFAULT 0," +
            "    update_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "product_stock"
        );

        safeExecute(
            "CREATE TABLE IF NOT EXISTS gnosis_sample.order_record (" +
            "    order_id        BIGSERIAL PRIMARY KEY," +
            "    order_no        VARCHAR(64) NOT NULL," +
            "    user_id         VARCHAR(64)," +
            "    amount          DECIMAL(15,2)," +
            "    status          VARCHAR(20)," +
            "    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "order_record"
        );

        safeExecute(
            "CREATE TABLE IF NOT EXISTS gnosis_sample.sys_users (" +
            "    user_id         VARCHAR(64) PRIMARY KEY," +
            "    username        VARCHAR(64) NOT NULL UNIQUE," +
            "    password        VARCHAR(128)," +
            "    email           VARCHAR(128)," +
            "    status          INT DEFAULT 1," +
            "    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
            ")",
            "sys_users"
        );

        log.info("[ParamCheckInit] 建表完成");
    }

    private void initTestData() {
        initBusinessType("ORDER_CREATE", "订单创建", "订单创建相关的参数校验");
        initBusinessType("USER_REGISTER", "用户注册", "用户注册相关的参数校验");
        initBusinessType("BASIC_DATA", "基础数据录入", "基础数据录入相关的参数校验");
        initBusinessType("OTHER", "其他", "其他业务类型的参数校验");

        initStock("SKU001", "iPhone 15 Pro", 100);
        initStock("SKU002", "MacBook Pro 14", 50);
        initStock("SKU003", "AirPods Pro", 200);

        initUser("U001", "zhangsan", "zhangsan@example.com", 1);
        initUser("U002", "lisi", "lisi@example.com", 1);
        initUser("U003", "wangwu", "wangwu@example.com", 0);

        upsertFlow("ORDER_CREATE_FLOW", "订单创建校验(混合模式)", "ORDER_CREATE", "HYBRID",
            "THEN(parse_param, check_format, check_db_user)", "OrderBusinessHandler",
            "{\"parse_param\":{\"json_path\":\"$\"},\"check_format\":{\"rules\":[{\"path\":\"$.orderNo\",\"type\":\"REGEX\",\"pattern\":\"^ORD[0-9]{10}$\",\"msg\":\"订单号格式错误\"},{\"path\":\"$.amount\",\"type\":\"RANGE\",\"min\":0.01,\"max\":1000000,\"msg\":\"订单金额超出范围\"},{\"path\":\"$.userId\",\"type\":\"NOT_NULL\",\"msg\":\"用户ID不能为空\"}]},\"check_db_user\":{\"type\":\"DB_QUERY\",\"sql\":\"SELECT COUNT(1) FROM gnosis_sample.sys_users WHERE user_id = ? AND status = 1\",\"param_path\":\"$.userId\",\"msg\":\"用户不存在或已被禁用\"}}");

        upsertFlow("USER_REGISTER_FLOW", "用户注册(纯接口模式)", "USER_REGISTER", "HANDLER",
            null, "UserRegisterHandler", null);

        upsertFlow("SIMPLE_CHECK_FLOW", "简单格式校验(纯编排模式)", "BASIC_DATA", "FLOW",
            "THEN(parse_param, check_format)", null,
            "{\"parse_param\":{\"json_path\":\"$\"},\"check_format\":{\"rules\":[{\"path\":\"$.email\",\"type\":\"EMAIL\",\"msg\":\"邮箱格式错误\"},{\"path\":\"$.phone\",\"type\":\"PHONE\",\"msg\":\"手机号格式错误\"}]}}");

        log.info("[ParamCheckInit] 初始化数据完成");
    }

    private void upsertFlow(String flowId, String flowName, String businessType, String modeType,
                           String elExpression, String handlerCode, String componentConfig) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM gnosis_sample.sys_validation_flows WHERE flow_id = ?",
                Integer.class, flowId);
            if (count != null && count > 0) {
                jdbcTemplate.update(
                    "UPDATE gnosis_sample.sys_validation_flows " +
                    "SET flow_name=?, business_type=?, mode_type=?, el_expression=?, handler_code=?, component_config=?, version=version+1, update_time=CURRENT_TIMESTAMP " +
                    "WHERE flow_id=?",
                    flowName, businessType, modeType, elExpression, handlerCode, componentConfig, flowId);
            } else {
                jdbcTemplate.update(
                    "INSERT INTO gnosis_sample.sys_validation_flows " +
                    "(flow_id, flow_name, business_type, mode_type, el_expression, handler_code, component_config, is_active, create_user_id, update_user_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, TRUE, 'admin', 'admin')",
                    flowId, flowName, businessType, modeType, elExpression, handlerCode, componentConfig);
            }
        } catch (Exception e) {
            log.warn("[ParamCheckInit] upsert流程失败: {} - {}", flowId, e.getMessage());
        }
    }

    private void initBusinessType(String code, String name, String description) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM gnosis_sample.sys_business_types WHERE code = ?",
                Integer.class, code);
            if (count == null || count == 0) {
                jdbcTemplate.update(
                    "INSERT INTO gnosis_sample.sys_business_types (code, name, description, is_active, create_user_id, update_user_id) VALUES (?, ?, ?, TRUE, 'admin', 'admin')",
                    code, name, description);
            }
        } catch (Exception e) {
            log.warn("[ParamCheckInit] 插入业务类型失败: {} - {}", code, e.getMessage());
        }
    }

    private void initStock(String skuId, String skuName, int qty) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM gnosis_sample.product_stock WHERE sku_id = ?",
                Integer.class, skuId);
            if (count == null || count == 0) {
                jdbcTemplate.update(
                    "INSERT INTO gnosis_sample.product_stock (sku_id, sku_name, stock_qty) VALUES (?, ?, ?)",
                    skuId, skuName, qty);
            }
        } catch (Exception e) {
            log.warn("[ParamCheckInit] 插入库存失败: {} - {}", skuId, e.getMessage());
        }
    }

    private void initUser(String userId, String username, String email, int status) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(1) FROM gnosis_sample.sys_users WHERE user_id = ?",
                Integer.class, userId);
            if (count == null || count == 0) {
                jdbcTemplate.update(
                    "INSERT INTO gnosis_sample.sys_users (user_id, username, email, status) VALUES (?, ?, ?, ?)",
                    userId, username, email, status);
            }
        } catch (Exception e) {
            log.warn("[ParamCheckInit] 插入用户失败: {} - {}", userId, e.getMessage());
        }
    }

    private void safeExecute(String sql, String tableName) {
        try {
            jdbcTemplate.execute(sql);
            log.info("[ParamCheckInit] 执行成功: {}", tableName);
        } catch (Exception e) {
            log.debug("[ParamCheckInit] 执行跳过[{}]: {}", tableName, e.getMessage());
        }
    }
}
