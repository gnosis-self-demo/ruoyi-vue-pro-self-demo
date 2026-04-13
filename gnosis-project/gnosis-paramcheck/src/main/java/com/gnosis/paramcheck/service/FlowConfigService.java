package com.gnosis.paramcheck.service;

import com.gnosis.paramcheck.domain.ValidationFlow;
import com.gnosis.paramcheck.dto.PageRequest;
import com.gnosis.paramcheck.dto.PageResult;
import com.gnosis.paramcheck.mapper.ValidationFlowMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class FlowConfigService {

    private static final Logger log = LoggerFactory.getLogger(FlowConfigService.class);

    @Autowired
    private ValidationFlowMapper flowMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Cacheable(value = "flowConfig", key = "#flowId")
    public ValidationFlow findById(String flowId) {
        ValidationFlow flow = flowMapper.selectByFlowId(flowId);
        if (flow != null) {
            parseComponentConfig(flow);
        }
        return flow;
    }

    public List<ValidationFlow> findAllActive() {
        List<ValidationFlow> flows = flowMapper.selectActive();
        for (ValidationFlow flow : flows) {
            parseComponentConfig(flow);
        }
        return flows;
    }

    public List<ValidationFlow> findAll() {
        List<ValidationFlow> flows = flowMapper.selectAll();
        for (ValidationFlow flow : flows) {
            parseComponentConfig(flow);
        }
        return flows;
    }

    public PageResult<ValidationFlow> findPage(String flowId, String flowName, String businessType,
                                                String modeType, Boolean isActive, PageRequest pageRequest) {
        int total = flowMapper.countByCondition(flowId, flowName, businessType, modeType, isActive);
        List<ValidationFlow> records = flowMapper.selectByConditionWithPaging(
                flowId, flowName, businessType, modeType, isActive,
                pageRequest.getOffset(), pageRequest.getPageSize());
        for (ValidationFlow flow : records) {
            parseComponentConfig(flow);
        }
        return new PageResult<>(records, total, pageRequest.getPage(), pageRequest.getPageSize());
    }

    @CacheEvict(value = "flowConfig", key = "#flow.flowId")
    public void save(ValidationFlow flow) {
        serializeComponentConfig(flow);
        ValidationFlow existing = flowMapper.selectByFlowId(flow.getFlowId());
        if (existing != null) {
            flowMapper.updateByFlowId(flow);
        } else {
            if (flow.getIsActive() == null) {
                flow.setIsActive(true);
            }
            if (flow.getVersion() == null) {
                flow.setVersion(1);
            }
            if (flow.getCreateUserId() == null) {
                flow.setCreateUserId("admin");
            }
            if (flow.getUpdateUserId() == null) {
                flow.setUpdateUserId("admin");
            }
            flowMapper.insert(flow);
        }
    }

    @CacheEvict(value = "flowConfig", key = "#flowId")
    public void delete(String flowId) {
        flowMapper.deleteByFlowId(flowId);
    }

    public void batchDelete(List<String> flowIds) {
        if (flowIds != null && !flowIds.isEmpty()) {
            flowMapper.batchDelete(flowIds);
        }
    }

    @CacheEvict(value = "flowConfig", key = "#flowId")
    public void activate(String flowId) {
        flowMapper.activate(flowId);
    }

    @CacheEvict(value = "flowConfig", key = "#flowId")
    public void deactivate(String flowId) {
        flowMapper.deactivate(flowId);
    }

    public void batchActivate(List<String> flowIds) {
        if (flowIds != null && !flowIds.isEmpty()) {
            flowMapper.batchActivate(flowIds);
        }
    }

    public void batchDeactivate(List<String> flowIds) {
        if (flowIds != null && !flowIds.isEmpty()) {
            flowMapper.batchDeactivate(flowIds);
        }
    }

    @CacheEvict(value = "flowConfig", key = "#flowId")
    public int incrementVersion(String flowId) {
        return flowMapper.incrementVersion(flowId);
    }

    private void parseComponentConfig(ValidationFlow flow) {
        if (flow == null || flow.getComponentConfig() == null) return;
        try {
            String configJson = flow.getComponentConfig();
            String fixedConfigJson = configJson.replaceAll("\\s*:\\s*\\.", ": 0.");
            Map<String, Object> config = objectMapper.readValue(fixedConfigJson, new TypeReference<Map<String, Object>>() {});
            flow.setComponentConfigMap(config);
        } catch (Exception e) {
            log.warn("[FlowConfigService] failed to parse component_config for flow={}", flow.getFlowId(), e);
        }
    }

    private void serializeComponentConfig(ValidationFlow flow) {
        if (flow == null) return;
        if (flow.getComponentConfigMap() != null && flow.getComponentConfig() == null) {
            try {
                flow.setComponentConfig(objectMapper.writeValueAsString(flow.getComponentConfigMap()));
            } catch (Exception e) {
                log.warn("[FlowConfigService] failed to serialize component_config", e);
            }
        }
    }
}
