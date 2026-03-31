package com.gnosis.paramcheck.service;

import com.gnosis.paramcheck.domain.*;
import com.gnosis.paramcheck.exception.ValidationException;
import com.gnosis.paramcheck.handler.HandlerRegistry;
import com.gnosis.paramcheck.repository.FlowConfigRepository;
import com.gnosis.paramcheck.repository.LogRepository;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import com.yomahub.liteflow.slot.DefaultContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 动态校验服务 — 三种模式统一入口
 *
 * 路由策略:
 * - FLOW:     加载 DB 中的 EL 表达式，通过 LiteFlow 引擎编排执行
 * - HANDLER:  直接调用注册的 IValidationHandler 实现类
 * - HYBRID:   先执行 LiteFlow 流程，流程成功后再执行 Handler
 */
@Service
public class DynamicValidationService {

    private static final Logger log = LoggerFactory.getLogger(DynamicValidationService.class);

    @Autowired
    private FlowConfigRepository flowConfigRepository;

    @Autowired
    private HandlerRegistry handlerRegistry;

    @Autowired
    private FlowExecutor flowExecutor;

    @Autowired
    private LogRepository logRepository;

    /**
     * 执行指定流程的校验（带缓存）
     *
     * @param flowId      流程 ID
     * @param requestData 待校验的请求数据 (支持 Map/JSON String/任意对象)
     * @return 校验结果
     */
    @Transactional(rollbackFor = Exception.class)
    @Cacheable(value = "validationResult", key = "#flowId + '_' + #requestData.hashCode()")
    public ValidationResult execute(String flowId, Object requestData) {
        ValidationFlow flow = flowConfigRepository.findById(flowId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "[DynamicValidationService] Flow not found: " + flowId));

        if (!Boolean.TRUE.equals(flow.getIsActive())) {
            log.info("[DynamicValidationService] flow inactive, skipping, flowId={}", flowId);
            return ValidationResult.success();
        }

        ValidationContext context = new ValidationContext();
        context.setData("rawRequest", requestData);
        context.setData("flowId", flowId);

        // 将 component_config 注入到请求数据中（供 LiteFlow 组件通过 getRequestData() 读取）
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
                    // 混合模式：先 FLOW 后 HANDLER
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

    /**
     * HANDLER 模式: 调用自定义处理器
     */
    private ValidationResult executeHandler(String handlerCode, ValidationContext context) {
        if (handlerCode == null || handlerCode.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "[DynamicValidationService] handler_code is required for HANDLER/HYBRID mode");
        }
        return handlerRegistry.getHandler(handlerCode).validate(context);
    }

    /**
     * FLOW 模式: 执行 LiteFlow 流程
     *
     * 注意: 组件配置的规则通过 injectComponentConfig() 已注入到 requestData 中，
     *       组件通过 getRequestData() 获取（包含所有配置数据）
     */
    private ValidationResult executeLiteFlow(String flowId, Object requestData) {
        try {
            // LiteFlow 2.11.3: execute2Resp(chainName, requestData, contextClass)
            // requestData 中已包含 check_format_rules / db_sql 等配置
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

    /**
     * 将 component_config 中的规则注入到请求数据对象中，
     * 供 LiteFlow 组件通过 getRequestData() 读取
     */
    @SuppressWarnings("unchecked")
    private void injectComponentConfig(Object requestData, ValidationFlow flow) {
        if (flow.getComponentConfig() == null || requestData == null) return;
        if (!(requestData instanceof Map)) return;

        Map<String, Object> data = (Map<String, Object>) requestData;
        Map<String, Object> config = flow.getComponentConfig();

        // 注入 check_format 规则
        if (config.containsKey("check_format")) {
            Map<String, Object> checkFormat = (Map<String, Object>) config.get("check_format");
            if (checkFormat.containsKey("rules")) {
                data.put("check_format_rules", checkFormat.get("rules"));
            }
        }

        // 注入 DB 查询配置
        if (config.containsKey("check_db_user")) {
            Map<String, Object> dbCheck = (Map<String, Object>) config.get("check_db_user");
            data.put("db_sql", dbCheck.get("sql"));
            data.put("db_param_path", dbCheck.get("param_path"));
            data.put("db_error_msg", dbCheck.get("msg"));
        }

        // 注入 parse_param 配置
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
            logEntry.setInputSnapshot(context.getData("rawRequest"));
            logEntry.setErrorMsg(msg);
            logEntry.setCreatedTime(OffsetDateTime.now());
            logRepository.saveLogAsync(logEntry);
        } catch (Exception e) {
            log.error("[DynamicValidationService] save log failed", e);
        }
    }
}
