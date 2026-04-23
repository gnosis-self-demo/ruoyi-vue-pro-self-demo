package com.gnosis.process.service.impl;

import com.gnosis.process.domain.BusinessConfig;
import com.gnosis.process.domain.BusinessResourceBinding;
import com.gnosis.process.domain.ProcessEventConfig;
import com.gnosis.process.domain.ProcessOrchestrationConfig;
import com.gnosis.process.domain.ProcessTransitionFlow;
import com.gnosis.process.dto.*;
import com.gnosis.process.mapper.BusinessConfigMapper;
import com.gnosis.process.mapper.BusinessResourceBindingMapper;
import com.gnosis.process.mapper.ProcessEventConfigMapper;
import com.gnosis.process.mapper.ProcessOrchestrationConfigMapper;
import com.gnosis.process.mapper.ProcessTransitionFlowMapper;
import com.gnosis.process.service.ProcessOrchestrationEngine;
import com.gnosis.process.util.ConditionExpressionEvaluator;
import com.gnosis.process.util.IdGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ProcessOrchestrationEngineImpl implements ProcessOrchestrationEngine {

    private static final Logger log = LoggerFactory.getLogger(ProcessOrchestrationEngineImpl.class);

    @Autowired
    private BusinessConfigMapper businessConfigMapper;

    @Autowired
    private BusinessResourceBindingMapper resourceBindingMapper;

    @Autowired
    private ProcessOrchestrationConfigMapper orchestrationConfigMapper;

    @Autowired
    private ProcessEventConfigMapper eventConfigMapper;

    @Autowired
    private ProcessTransitionFlowMapper transitionFlowMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessTriggerResult trigger(ProcessTriggerRequest request) {
        log.info("[ProcessOrchestrationEngine] trigger: businessCode={}, processDefKey={}, triggerEvent={}",
                request.getBusinessCode(), request.getProcessDefKey(), request.getTriggerEvent());

        ProcessTriggerResult result = new ProcessTriggerResult();
        result.setSuccess(false);

        try {
            BusinessConfig businessConfig = businessConfigMapper.selectByCode(request.getBusinessCode());
            if (businessConfig == null) {
                result.setMessage("业务配置不存在: " + request.getBusinessCode());
                return result;
            }

            if (!"active".equals(businessConfig.getStatus())) {
                result.setMessage("业务配置未启用: " + request.getBusinessCode());
                return result;
            }

            List<ProcessOrchestrationConfig> configs = orchestrationConfigMapper
                    .selectByCurrentProcessAndEvent(businessConfig.getId(), request.getProcessDefKey(), request.getTriggerEvent());
            if (configs == null || configs.isEmpty()) {
                result.setMessage("未找到匹配的编排配置");
                return result;
            }

            ProcessOrchestrationConfig matchedConfig = null;
            for (ProcessOrchestrationConfig config : configs) {
                if (config.getConditionExpression() == null || config.getConditionExpression().trim().isEmpty()) {
                    matchedConfig = config;
                    break;
                }
                Map<String, Object> context = request.getContextData();
                if (context == null) {
                    context = new HashMap<>();
                }
                boolean evalResult = ConditionExpressionEvaluator.evaluate(config.getConditionExpression(), context);
                if (evalResult) {
                    matchedConfig = config;
                    break;
                }
            }

            if (matchedConfig == null) {
                result.setMessage("条件表达式均不匹配");
                return result;
            }

            ProcessTransitionFlow flow = new ProcessTransitionFlow();
            flow.setId(IdGenerator.nextId());
            flow.setOrchestrationConfigId(matchedConfig.getId());
            flow.setProcessDefKey(matchedConfig.getNextProcessDefKey());
            flow.setProcessDefUniqueKey(matchedConfig.getNextProcessDefUniqueKey());
            flow.setProcessInstanceId(request.getBusinessId() != null ? request.getBusinessId() : UUID.randomUUID().toString().replace("-", ""));
            flow.setProcessInstanceSequence(1);
            flow.setBusinessId(request.getBusinessId());
            flow.setBusinessDescription("流程触发: " + request.getProcessDefKey() + " -> " + matchedConfig.getNextProcessDefKey());
            flow.setFromNode(request.getProcessDefKey());
            flow.setToNode(matchedConfig.getNextProcessDefKey());
            flow.setTransitionType("auto");
            flow.setTransitionResult("success");
            flow.setRemarks("triggerEvent=" + request.getTriggerEvent());
            flow.setCreateUserId(request.getOperatorId() != null ? request.getOperatorId() : "system");
            flow.setUpdateUserId(request.getOperatorId() != null ? request.getOperatorId() : "system");
            flow.setCreateTime(new Date());
            flow.setUpdateTime(new Date());
            transitionFlowMapper.insert(flow);

            result.setSuccess(true);
            result.setNextProcessDefKey(matchedConfig.getNextProcessDefKey());
            result.setTransitionFlowId(flow.getId());
            result.setMessage("流程触发成功");

            log.info("[ProcessOrchestrationEngine] trigger success: nextProcessDefKey={}, flowId={}",
                    matchedConfig.getNextProcessDefKey(), flow.getId());
        } catch (Exception e) {
            log.error("[ProcessOrchestrationEngine] trigger error", e);
            result.setMessage("流程触发异常: " + e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProcessEventSubmitResult submitEvent(ProcessEventSubmitRequest request) {
        log.info("[ProcessOrchestrationEngine] submitEvent: processDefKey={}, nodeDefKey={}, submitAction={}",
                request.getProcessDefKey(), request.getNodeDefKey(), request.getSubmitAction());

        ProcessEventSubmitResult result = new ProcessEventSubmitResult();
        result.setSuccess(false);

        try {
            ProcessEventConfig eventConfig = eventConfigMapper.selectByProcessAndNodeAndAction(
                    request.getProcessDefKey(), request.getNodeDefKey(),
                    request.getSubmitAction(), request.getBusinessCode());

            if (eventConfig == null) {
                result.setMessage("未找到匹配的事件配置");
                return result;
            }

            ProcessTransitionFlow flow = new ProcessTransitionFlow();
            flow.setId(IdGenerator.nextId());
            flow.setEventConfigId(eventConfig.getId());
            flow.setProcessDefKey(request.getProcessDefKey());
            flow.setProcessDefUniqueKey(eventConfig.getProcessDefUniqueKey());
            flow.setNodeDefKey(request.getNodeDefKey());
            flow.setProcessInstanceId(request.getProcessInstanceId() != null ? request.getProcessInstanceId() : UUID.randomUUID().toString().replace("-", ""));
            flow.setProcessInstanceSequence(1);
            flow.setBusinessDescription("事件提交: " + request.getProcessDefKey() + " - " + request.getNodeDefKey());
            flow.setBusinessId(request.getBusinessId());
            flow.setFromNode(request.getNodeDefKey());
            flow.setToNode(eventConfig.getChangeBusinessStatus());
            flow.setTransitionType("manual");
            flow.setTransitionResult("success");
            flow.setRemarks("submitAction=" + request.getSubmitAction());
            flow.setCreateUserId(request.getOperatorId() != null ? request.getOperatorId() : "system");
            flow.setUpdateUserId(request.getOperatorId() != null ? request.getOperatorId() : "system");
            flow.setCreateTime(new Date());
            flow.setUpdateTime(new Date());
            transitionFlowMapper.insert(flow);

            result.setSuccess(true);
            result.setChangedStatus(eventConfig.getChangeBusinessStatus());
            result.setEventConfigId(eventConfig.getId());
            result.setMessage("事件提交成功");

            log.info("[ProcessOrchestrationEngine] submitEvent success: changedStatus={}", eventConfig.getChangeBusinessStatus());
        } catch (Exception e) {
            log.error("[ProcessOrchestrationEngine] submitEvent error", e);
            result.setMessage("事件提交异常: " + e.getMessage());
        }

        return result;
    }

    @Override
    public List<ProcessTransitionFlowVO> queryTransitions(ProcessTransitionQueryRequest request) {
        log.info("[ProcessOrchestrationEngine] queryTransitions: processInstanceId={}, businessId={}, businessCode={}",
                request.getProcessInstanceId(), request.getBusinessId(), request.getBusinessCode());

        List<ProcessTransitionFlow> flows = new ArrayList<>();

        if (request.getProcessInstanceId() != null && !request.getProcessInstanceId().isEmpty()) {
            flows = transitionFlowMapper.selectByProcessInstanceId(request.getProcessInstanceId());
        } else if (request.getBusinessId() != null && !request.getBusinessId().isEmpty()) {
            flows = transitionFlowMapper.selectByBusinessId(request.getBusinessId());
        } else if (request.getBusinessCode() != null && !request.getBusinessCode().isEmpty()) {
            BusinessConfig config = businessConfigMapper.selectByCode(request.getBusinessCode());
            if (config != null) {
                ProcessTransitionFlowQueryRequest queryRequest = new ProcessTransitionFlowQueryRequest();
                queryRequest.setProcessInstanceId(config.getCode());
                flows = transitionFlowMapper.selectByBusinessId(request.getBusinessCode());
                if (flows.isEmpty()) {
                    queryRequest.setBusinessId(null);
                    queryRequest.setPageNum(1);
                    queryRequest.setPageSize(Integer.MAX_VALUE);
                    flows = transitionFlowMapper.selectAllForExport(queryRequest);
                }
            }
        }

        List<ProcessTransitionFlowVO> vos = new ArrayList<>();
        for (ProcessTransitionFlow flow : flows) {
            ProcessTransitionFlowVO vo = new ProcessTransitionFlowVO();
            BeanUtils.copyProperties(flow, vo);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<BusinessResourceBindingVO> queryResources(String businessCode, String resourceType) {
        log.info("[ProcessOrchestrationEngine] queryResources: businessCode={}, resourceType={}", businessCode, resourceType);

        List<BusinessResourceBinding> bindings = resourceBindingMapper.selectByBusinessCodeAndType(businessCode, resourceType);

        List<BusinessResourceBindingVO> vos = new ArrayList<>();
        for (BusinessResourceBinding binding : bindings) {
            BusinessResourceBindingVO vo = new BusinessResourceBindingVO();
            BeanUtils.copyProperties(binding, vo);
            vos.add(vo);
        }
        return vos;
    }

    @Override
    public List<ProcessOrchestrationConfigVO> queryNextNodes(String businessCode, String currentProcessDefKey, String triggerEvent, Map<String, Object> contextData) {
        log.info("[ProcessOrchestrationEngine] queryNextNodes: businessCode={}, currentProcessDefKey={}, triggerEvent={}",
                businessCode, currentProcessDefKey, triggerEvent);

        BusinessConfig businessConfig = businessConfigMapper.selectByCode(businessCode);
        if (businessConfig == null) {
            return new ArrayList<>();
        }

        List<ProcessOrchestrationConfig> configs = orchestrationConfigMapper
                .selectByCurrentProcessAndEvent(businessConfig.getId(), currentProcessDefKey, triggerEvent);

        List<ProcessOrchestrationConfigVO> matchedVos = new ArrayList<>();
        for (ProcessOrchestrationConfig config : configs) {
            if (config.getConditionExpression() != null && !config.getConditionExpression().trim().isEmpty()) {
                Map<String, Object> context = contextData;
                if (context == null) {
                    context = new HashMap<>();
                }
                boolean evalResult = ConditionExpressionEvaluator.evaluate(config.getConditionExpression(), context);
                if (!evalResult) {
                    continue;
                }
            }
            ProcessOrchestrationConfigVO vo = new ProcessOrchestrationConfigVO();
            BeanUtils.copyProperties(config, vo);
            matchedVos.add(vo);
        }
        return matchedVos;
    }
}
