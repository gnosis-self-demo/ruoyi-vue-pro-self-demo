package com.gnosis.paramcheck.service;

import com.gnosis.paramcheck.domain.*;
import com.gnosis.paramcheck.exception.ValidationException;
import com.gnosis.paramcheck.handler.HandlerRegistry;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.slot.DefaultContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class DynamicValidationService {

    private static final Logger log = LoggerFactory.getLogger(DynamicValidationService.class);

    @Autowired
    private FlowConfigService flowConfigService;

    @Autowired
    private HandlerRegistry handlerRegistry;

    @Autowired
    private FlowExecutor flowExecutor;

    @Autowired
    private ValidationLogService validationLogService;

    public ValidationResult execute(String flowId, Object requestData) {
        ValidationFlow flow = flowConfigService.findById(flowId);
        if (flow == null) {
            throw new IllegalArgumentException("[DynamicValidationService] Flow not found: " + flowId);
        }

        if (!Boolean.TRUE.equals(flow.getIsActive())) {
            log.info("[DynamicValidationService] flow inactive, skipping, flowId={}", flowId);
            return ValidationResult.success();
        }

        ValidationContext context = new ValidationContext();
        context.setData("rawRequest", requestData);
        context.setData("flowId", flowId);

        injectComponentConfig(requestData, flow);

        String mode = flow.getModeType();
        log.info("[DynamicValidationService] executing, flowId={}, mode={}", flowId, mode);

        try {
            ValidationResult result;
            switch (mode) {
                case "HANDLER":
                    result = executeHandler(flow.getHandlerCode(), context);
                    break;
                case "FLOW":
                    result = executeLiteFlow(flowId, requestData);
                    break;
                case "HYBRID":
                    ValidationResult flowResult = executeLiteFlow(flowId, requestData);
                    if (!flowResult.isSuccess()) {
                        logFailure(flowId, context, flowResult.getErrorMsg());
                        return flowResult;
                    }
                    result = executeHandler(flow.getHandlerCode(), context);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown mode type: " + mode);
            }

            if (!result.isSuccess()) {
                logFailure(flowId, context, result.getErrorMsg());
            } else {
                log.info("[DynamicValidationService] validation passed, flowId={}", flowId);
            }

            return result;
        } catch (ValidationException e) {
            log.warn("[DynamicValidationService] validation failed, flowId={}, msg={}",
                    flowId, e.getMessage());
            logFailure(flowId, context, e.getMessage());
            return ValidationResult.fail(e.getErrorCode(), e.getMessage(), e.getFailedNode());
        } catch (Exception e) {
            log.error("[DynamicValidationService] unexpected error, flowId=" + flowId, e);
            logFailure(flowId, context, e.getMessage());
            return ValidationResult.fail("SYSTEM_ERROR", e.getMessage());
        }
    }

    private ValidationResult executeHandler(String handlerCode, ValidationContext context) {
        if (handlerCode == null || handlerCode.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "[DynamicValidationService] handler_code is required for HANDLER/HYBRID mode");
        }
        return handlerRegistry.getHandler(handlerCode).validate(context);
    }

    private ValidationResult executeLiteFlow(String flowId, Object requestData) {
        try {
            LiteflowResponse response = flowExecutor.execute2Resp(flowId, requestData, DefaultContext.class);

            if (!response.isSuccess()) {
                String errMsg = response.getMessage();
                String failedStep = response.getExecuteStepStr();
                log.warn("[DynamicValidationService] LiteFlow failed, flowId={}, msg={}, steps={}",
                        flowId, errMsg, failedStep);
                return ValidationResult.fail("FLOW_ERROR",
                        errMsg != null ? errMsg : "Unknown error", failedStep);
            }

            return ValidationResult.success();
        } catch (Exception e) {
            throw new RuntimeException("[DynamicValidationService] LiteFlow execution error: "
                    + e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private void injectComponentConfig(Object requestData, ValidationFlow flow) {
        if (flow.getComponentConfigMap() == null || requestData == null) return;
        if (!(requestData instanceof Map)) return;

        Map<String, Object> data = (Map<String, Object>) requestData;
        Map<String, Object> config = flow.getComponentConfigMap();

        if (config.containsKey("check_format")) {
            Map<String, Object> checkFormat = (Map<String, Object>) config.get("check_format");
            if (checkFormat.containsKey("rules")) {
                data.put("check_format_rules", checkFormat.get("rules"));
            }
        }

        if (config.containsKey("check_db_user")) {
            Map<String, Object> dbCheck = (Map<String, Object>) config.get("check_db_user");
            data.put("db_sql", dbCheck.get("sql"));
            data.put("db_param_path", dbCheck.get("param_path"));
            data.put("db_error_msg", dbCheck.get("msg"));
        }

        if (config.containsKey("parse_param")) {
            Map<String, Object> parseParam = (Map<String, Object>) config.get("parse_param");
            data.put("parse_json_path", parseParam.get("json_path"));
        }
    }

    private void logFailure(String flowId, ValidationContext context, String msg) {
        try {
            ValidationLog logEntry = new ValidationLog();
            logEntry.setFlowId(flowId);
            logEntry.setRequestId(context.getRequestId());
            logEntry.setModeType("SYSTEM");
            if (context.getData("rawRequest") != null) {
                try {
                    logEntry.setInputSnapshot(com.alibaba.fastjson.JSON.toJSONString(context.getData("rawRequest")));
                } catch (Exception e) {
                    logEntry.setInputSnapshot(String.valueOf(context.getData("rawRequest")));
                }
            }
            logEntry.setErrorMsg(msg);
            validationLogService.saveLogAsync(logEntry);
        } catch (Exception e) {
            log.error("[DynamicValidationService] save log failed", e);
        }
    }
}
