package com.cmbc.oa.module.paramcheck.repository;

import com.cmbc.oa.module.paramcheck.domain.ValidationFlow;
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
        String sql = "SELECT flow_id, flow_name, mode_type, el_expression, handler_code, " +
                "component_config, is_active, version, updated_time " +
                "FROM gnosis_sample.sys_validation_flows WHERE flow_id = ?";
        List<ValidationFlow> list = jdbcTemplate.query(sql, new FlowRowMapper(), flowId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    public List<ValidationFlow> findAllActive() {
        String sql = "SELECT flow_id, flow_name, mode_type, el_expression, handler_code, " +
                "component_config, is_active, version, updated_time " +
                "FROM gnosis_sample.sys_validation_flows WHERE is_active = TRUE";
        return jdbcTemplate.query(sql, new FlowRowMapper());
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
     * 保存流程配置（创建或更新）
     */
    @CacheEvict(value = "flowConfig", key = "#flow.flowId")
    public void save(ValidationFlow flow) {
        String sql = "INSERT INTO gnosis_sample.sys_validation_flows " +
                "(flow_id, flow_name, mode_type, el_expression, handler_code, component_config, is_active, version, updated_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                "ON CONFLICT (flow_id) DO UPDATE SET " +
                "flow_name = EXCLUDED.flow_name, " +
                "mode_type = EXCLUDED.mode_type, " +
                "el_expression = EXCLUDED.el_expression, " +
                "handler_code = EXCLUDED.handler_code, " +
                "component_config = EXCLUDED.component_config, " +
                "is_active = EXCLUDED.is_active, " +
                "version = sys_validation_flows.version + 1, " +
                "updated_time = CURRENT_TIMESTAMP";

        String componentConfigJson = null;
        try {
            if (flow.getComponentConfig() != null) {
                componentConfigJson = objectMapper.writeValueAsString(flow.getComponentConfig());
            }
        } catch (Exception e) {
            log.warn("[FlowConfigRepository] failed to serialize component_config", e);
        }

        jdbcTemplate.update(sql, 
                flow.getFlowId(),
                flow.getFlowName(),
                flow.getModeType(),
                flow.getElExpression(),
                flow.getHandlerCode(),
                componentConfigJson,
                flow.getIsActive(),
                flow.getVersion());
    }

    private class FlowRowMapper implements RowMapper<ValidationFlow> {
        @Override
        @SuppressWarnings("unchecked")
        public ValidationFlow mapRow(ResultSet rs, int rowNum) throws SQLException {
            ValidationFlow flow = new ValidationFlow();
            flow.setFlowId(rs.getString("flow_id"));
            flow.setFlowName(rs.getString("flow_name"));
            flow.setModeType(rs.getString("mode_type"));
            flow.setElExpression(rs.getString("el_expression"));
            flow.setHandlerCode(rs.getString("handler_code"));
            flow.setIsActive(rs.getBoolean("is_active"));
            flow.setVersion(rs.getInt("version"));

            String configJson = rs.getString("component_config");
            if (configJson != null && !configJson.isEmpty()) {
                try {
                    Map<String, Object> config = objectMapper.readValue(
                            configJson, new TypeReference<Map<String, Object>>() {});
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
