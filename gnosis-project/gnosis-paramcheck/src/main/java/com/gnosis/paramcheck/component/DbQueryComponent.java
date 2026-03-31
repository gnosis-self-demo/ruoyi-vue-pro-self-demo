package com.gnosis.paramcheck.component;

import com.gnosis.paramcheck.exception.ValidationException;
import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * LiteFlow 组件: 数据库查询校验节点
 * 节点 ID: check_db_user
 *
 * 根据 requestData 中配置的 SQL 执行查询，验证数据是否存在
 * 所有逻辑均为 Java 代码 + JdbcTemplate，无存储过程
 *
 * SQL 配置来源: requestData 中的 db_sql / db_param_path / db_error_msg
 * (由 DynamicValidationService 从 DB component_config 注入)
 */
@Component("check_db_user")
public class DbQueryComponent extends NodeComponent {

    @Autowired
    @Qualifier("gnosisJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public void process() {
        Object requestData = this.getRequestData();
        if (requestData == null) {
            return;
        }

        // 从请求数据中获取 SQL 配置
        String sql = extractString(requestData, "db_sql");
        if (sql == null || sql.trim().isEmpty()) {
            return;
        }

        String paramPath = extractString(requestData, "db_param_path");
        String errorMsg = extractString(requestData, "db_error_msg");
        if (errorMsg == null) {
            errorMsg = "DB Validation Failed";
        }

        // 从请求数据中提取 SQL 参数
        Object paramValue = null;
        if (paramPath != null) {
            try {
                paramValue = com.jayway.jsonpath.JsonPath.read(requestData, paramPath);
            } catch (Exception e) {
                // 参数提取失败
            }
        }

        try {
            boolean exists;
            if (paramValue != null) {
                Integer result = jdbcTemplate.queryForObject(sql, Integer.class, paramValue);
                exists = (result != null && result > 0);
            } else {
                Integer result = jdbcTemplate.queryForObject(sql, Integer.class);
                exists = (result != null && result > 0);
            }

            if (!exists) {
                throw new ValidationException("DB_CHECK_FAILED", errorMsg, "check_db_user");
            }
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ValidationException("DB_ERROR",
                    "DB Query Error: " + e.getMessage(), "check_db_user");
        }
    }

    private String extractString(Object data, String key) {
        if (data instanceof java.util.Map) {
            Object val = ((java.util.Map<?, ?>) data).get(key);
            return val != null ? String.valueOf(val) : null;
        }
        return null;
    }
}
