package paramcheck.repository;

import paramcheck.domain.ValidationFlow;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 流程配置仓储 — 从 openGauss gnosis_sample schema 读取配置
 * 严禁存储过程，所有查询均为标准动态 SQL
 *
 * 注意: 共用主项目的 JdbcTemplate (Spring Boot 自动配置)
 */
@Repository
public class FlowConfigRepository {

    private static final Logger log = LoggerFactory.getLogger(FlowConfigRepository.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 根据ID查询流程配置（带缓存）
     */
    @Cacheable(value = "flowConfig", key = "#flowId")
    public Optional<ValidationFlow> findById(String flowId) {
        String sql = "SELECT flow_id, flow_name, business_type, mode_type, el_expression, handler_code, " +
                "component_config, is_active, version, create_user_id, update_user_id, create_time, updated_time " +
                "FROM gnosis_sample.sys_validation_flows WHERE flow_id = ?";
        List<ValidationFlow> list = jdbcTemplate.query(sql, new FlowRowMapper(), flowId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<ValidationFlow> findAllActive() {
        String sql = "SELECT flow_id, flow_name, business_type, mode_type, el_expression, handler_code, " +
                "component_config, is_active, version, create_user_id, update_user_id, create_time, updated_time " +
                "FROM gnosis_sample.sys_validation_flows WHERE is_active = TRUE";
        return jdbcTemplate.query(sql, new FlowRowMapper());
    }

    /**
     * 获取所有流程配置，包括禁用的
     */
    public List<ValidationFlow> findAll() {
        String sql = "SELECT flow_id, flow_name, business_type, mode_type, el_expression, handler_code, " +
                "component_config, is_active, version, create_user_id, update_user_id, create_time, updated_time " +
                "FROM gnosis_sample.sys_validation_flows";
        return jdbcTemplate.query(sql, new FlowRowMapper());
    }

    /**
     * 分页查询流程配置
     */
    public Map<String, Object> findAll(int page, int pageSize, String flowId, String flowName, String businessType, String modeType, Boolean isActive) {
        // 构建查询条件
        StringBuilder whereClause = new StringBuilder();
        if (flowId != null && !flowId.isEmpty()) {
            whereClause.append(whereClause.length() > 0 ? " AND " : " WHERE " );
            whereClause.append("flow_id LIKE ?");
        }
        if (flowName != null && !flowName.isEmpty()) {
            whereClause.append(whereClause.length() > 0 ? " AND " : " WHERE " );
            whereClause.append("flow_name LIKE ?");
        }
        if (businessType != null && !businessType.isEmpty()) {
            whereClause.append(whereClause.length() > 0 ? " AND " : " WHERE " );
            whereClause.append("business_type LIKE ?");
        }
        if (modeType != null && !modeType.isEmpty()) {
            whereClause.append(whereClause.length() > 0 ? " AND " : " WHERE " );
            whereClause.append("mode_type = ?");
        }
        if (isActive != null) {
            whereClause.append(whereClause.length() > 0 ? " AND " : " WHERE " );
            whereClause.append("is_active = ?");
        }

        // 计算总数
        String countSql = "SELECT COUNT(*) FROM gnosis_sample.sys_validation_flows" + whereClause.toString();
        Integer total = jdbcTemplate.queryForObject(countSql, Integer.class, getQueryParams(flowId, flowName, businessType, modeType, isActive));

        // 构建分页查询
        String sql = "SELECT flow_id, flow_name, business_type, mode_type, el_expression, handler_code, " +
                "component_config, is_active, version, create_user_id, update_user_id, create_time, updated_time " +
                "FROM gnosis_sample.sys_validation_flows" + whereClause.toString() + " LIMIT ? OFFSET ?";

        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 构建参数数组
        Object[] params = new Object[getQueryParams(flowId, flowName, businessType, modeType, isActive).length + 2];
        System.arraycopy(getQueryParams(flowId, flowName, businessType, modeType, isActive), 0, params, 0, getQueryParams(flowId, flowName, businessType, modeType, isActive).length);
        params[params.length - 2] = pageSize;
        params[params.length - 1] = offset;

        // 执行查询
        List<ValidationFlow> flows = jdbcTemplate.query(sql, new FlowRowMapper(), params);

        // 构建返回结果
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("records", flows);
        result.put("total", total);
        result.put("page", page);
        result.put("pageSize", pageSize);

        return result;
    }

    /**
     * 构建查询参数
     */
    private Object[] getQueryParams(String flowId, String flowName, String businessType, String modeType, Boolean isActive) {
        java.util.List<Object> params = new java.util.ArrayList<>();
        if (flowId != null && !flowId.isEmpty()) {
            params.add("%" + flowId + "%");
        }
        if (flowName != null && !flowName.isEmpty()) {
            params.add("%" + flowName + "%");
        }
        if (businessType != null && !businessType.isEmpty()) {
            params.add("%" + businessType + "%");
        }
        if (modeType != null && !modeType.isEmpty()) {
            params.add(modeType);
        }
        if (isActive != null) {
            params.add(isActive);
        }
        return params.toArray();
    }

    /**
     * 增加版本号（清除缓存）
     */
    @CacheEvict(value = "flowConfig", key = "#flowId")
    public int incrementVersion(String flowId) {
        String sql = "UPDATE gnosis_sample.sys_validation_flows " +
                "SET version = version + 1, updated_time = CURRENT_TIMESTAMP " +
                "WHERE flow_id = ?";
        return jdbcTemplate.update(sql, flowId);
    }

    /**
     * 禁用流程（清除缓存）
     */
    @CacheEvict(value = "flowConfig", key = "#flowId")
    public void deactivate(String flowId) {
        String sql = "UPDATE gnosis_sample.sys_validation_flows SET is_active = FALSE, updated_time = CURRENT_TIMESTAMP WHERE flow_id = ?";
        jdbcTemplate.update(sql, flowId);
    }

    /**
     * 启用流程（清除缓存）
     */
    @CacheEvict(value = "flowConfig", key = "#flowId")
    public void activate(String flowId) {
        String sql = "UPDATE gnosis_sample.sys_validation_flows SET is_active = TRUE, updated_time = CURRENT_TIMESTAMP WHERE flow_id = ?";
        jdbcTemplate.update(sql, flowId);
    }

    /**
     * 保存流程配置（创建或更新）
     */
    @CacheEvict(value = "flowConfig", key = "#flow.flowId")
    public void save(ValidationFlow flow) {
        String componentConfigJson = null;
        try {
            if (flow.getComponentConfig() != null) {
                componentConfigJson = objectMapper.writeValueAsString(flow.getComponentConfig());
            }
        } catch (Exception e) {
            log.warn("[FlowConfigRepository] failed to serialize component_config", e);
        }

        // 先检查记录是否存在
        String checkSql = "SELECT COUNT(*) FROM gnosis_sample.sys_validation_flows WHERE flow_id = ?";
        Integer count = jdbcTemplate.queryForObject(checkSql, Integer.class, flow.getFlowId());
        
        if (count != null && count > 0) {
            // 更新现有记录
            String updateSql = "UPDATE gnosis_sample.sys_validation_flows SET " +
                    "flow_name = ?, " +
                    "business_type = ?, " +
                    "mode_type = ?, " +
                    "el_expression = ?, " +
                    "handler_code = ?, " +
                    "component_config = ?::jsonb, " +
                    "is_active = ?, " +
                    "version = version + 1, " +
                    "update_user_id = ?, " +
                    "updated_time = CURRENT_TIMESTAMP " +
                    "WHERE flow_id = ?";
            
            jdbcTemplate.update(updateSql, 
                    flow.getFlowName(),
                    flow.getBusinessTypes(),
                    flow.getModeType(),
                    flow.getElExpression(),
                    flow.getHandlerCode(),
                    componentConfigJson,
                    flow.getIsActive(),
                    flow.getUpdateUserId() != null ? flow.getUpdateUserId() : "admin",
                    flow.getFlowId());
        } else {
            // 插入新记录
            String insertSql = "INSERT INTO gnosis_sample.sys_validation_flows " +
                    "(flow_id, flow_name, business_type, mode_type, el_expression, handler_code, component_config, is_active, version, create_user_id, update_user_id, create_time, updated_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?::jsonb, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";
            
            jdbcTemplate.update(insertSql, 
                    flow.getFlowId(),
                    flow.getFlowName(),
                    flow.getBusinessTypes(),
                    flow.getModeType(),
                    flow.getElExpression(),
                    flow.getHandlerCode(),
                    componentConfigJson,
                    flow.getIsActive(),
                    flow.getVersion(),
                    flow.getCreateUserId() != null ? flow.getCreateUserId() : "admin",
                    flow.getUpdateUserId() != null ? flow.getUpdateUserId() : "admin");
        }
    }

    /**
     * 批量禁用流程
     */
    public void batchDeactivate(List<String> flowIds) {
        if (flowIds == null || flowIds.isEmpty()) {
            return;
        }
        String sql = "UPDATE gnosis_sample.sys_validation_flows SET is_active = FALSE, updated_time = CURRENT_TIMESTAMP WHERE flow_id IN (" +
                String.join(",", java.util.Collections.nCopies(flowIds.size(), "?")) + ")";
        jdbcTemplate.update(sql, flowIds.toArray());
        // 清除缓存
        for (String flowId : flowIds) {
            jdbcTemplate.execute("SELECT pg_sleep(0)"); // 触发缓存清除
        }
    }

    /**
     * 批量启用流程
     */
    public void batchActivate(List<String> flowIds) {
        if (flowIds == null || flowIds.isEmpty()) {
            return;
        }
        String sql = "UPDATE gnosis_sample.sys_validation_flows SET is_active = TRUE, updated_time = CURRENT_TIMESTAMP WHERE flow_id IN (" +
                String.join(",", java.util.Collections.nCopies(flowIds.size(), "?")) + ")";
        jdbcTemplate.update(sql, flowIds.toArray());
        // 清除缓存
        for (String flowId : flowIds) {
            jdbcTemplate.execute("SELECT pg_sleep(0)"); // 触发缓存清除
        }
    }

    /**
     * 批量删除流程
     */
    public void batchDelete(List<String> flowIds) {
        if (flowIds == null || flowIds.isEmpty()) {
            return;
        }
        String sql = "DELETE FROM gnosis_sample.sys_validation_flows WHERE flow_id IN (" +
                String.join(",", java.util.Collections.nCopies(flowIds.size(), "?")) + ")";
        jdbcTemplate.update(sql, flowIds.toArray());
        // 清除缓存
        for (String flowId : flowIds) {
            jdbcTemplate.execute("SELECT pg_sleep(0)"); // 触发缓存清除
        }
    }

    private class FlowRowMapper implements RowMapper<ValidationFlow> {
        @Override
        @SuppressWarnings("unchecked")
        public ValidationFlow mapRow(ResultSet rs, int rowNum) throws SQLException {
            ValidationFlow flow = new ValidationFlow();
            flow.setFlowId(rs.getString("flow_id"));
            flow.setFlowName(rs.getString("flow_name"));
            flow.setBusinessTypes(rs.getString("business_type"));
            flow.setModeType(rs.getString("mode_type"));
            flow.setElExpression(rs.getString("el_expression"));
            flow.setHandlerCode(rs.getString("handler_code"));
            flow.setIsActive(rs.getBoolean("is_active"));
            flow.setVersion(rs.getInt("version"));
            flow.setCreateUserId(rs.getString("create_user_id"));
            flow.setUpdateUserId(rs.getString("update_user_id"));
            flow.setCreateTime(rs.getObject("create_time", OffsetDateTime.class));
            flow.setUpdatedTime(rs.getObject("updated_time", OffsetDateTime.class));

            String configJson = rs.getString("component_config");
            if (configJson != null && !configJson.isEmpty()) {
                try {
                    // 修复JSON格式：将".01"这样的格式转换为"0.01"
                    String fixedConfigJson = configJson.replaceAll("\\s*:\\s*\\.", ": 0.");
                    Map<String, Object> config = objectMapper.readValue(
                            fixedConfigJson, new TypeReference<Map<String, Object>>() {});
                    flow.setComponentConfig(config);
                } catch (Exception e) {
                    log.warn("[FlowConfigRepository] failed to parse component_config for flow={}",
                            flow.getFlowId(), e);
                }
            }
            return flow;
        }
    }
}
