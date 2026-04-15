package com.gnosis.process.controller;

import com.gnosis.process.dto.BusinessResourceBindingVO;
import com.gnosis.process.dto.CommonResponse;
import com.gnosis.process.dto.ProcessEventSubmitRequest;
import com.gnosis.process.dto.ProcessEventSubmitResult;
import com.gnosis.process.dto.ProcessOrchestrationConfigVO;
import com.gnosis.process.dto.ProcessTransitionFlowVO;
import com.gnosis.process.dto.ProcessTransitionQueryRequest;
import com.gnosis.process.dto.ProcessTriggerRequest;
import com.gnosis.process.dto.ProcessTriggerResult;
import com.gnosis.process.service.ProcessOrchestrationEngine;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 流程编排内部API Controller
 */
@RestController
@RequestMapping("/api/process-orchestration/internal")
@Api(tags = "流程编排内部API")
public class ProcessOrchestrationApiController {

    @Autowired
    private ProcessOrchestrationEngine processOrchestrationEngine;

    @PostMapping("/trigger")
    @ApiOperation(value = "触发流程")
    public CommonResponse<ProcessTriggerResult> trigger(@RequestBody ProcessTriggerRequest request) {
        ProcessTriggerResult result = processOrchestrationEngine.trigger(request);
        return CommonResponse.success(result);
    }

    @PostMapping("/event/submit")
    @ApiOperation(value = "提交事件")
    public CommonResponse<ProcessEventSubmitResult> submitEvent(@RequestBody ProcessEventSubmitRequest request) {
        ProcessEventSubmitResult result = processOrchestrationEngine.submitEvent(request);
        return CommonResponse.success(result);
    }

    @PostMapping("/transition/query")
    @ApiOperation(value = "查询流转记录")
    public CommonResponse<List<ProcessTransitionFlowVO>> queryTransitions(@RequestBody ProcessTransitionQueryRequest request) {
        List<ProcessTransitionFlowVO> result = processOrchestrationEngine.queryTransitions(request);
        return CommonResponse.success(result);
    }

    @PostMapping("/resource/query")
    @ApiOperation(value = "查询业务资源绑定")
    public CommonResponse<List<BusinessResourceBindingVO>> queryResources(@RequestBody Map<String, String> request) {
        String businessCode = request.get("businessCode");
        String resourceType = request.get("resourceType");
        List<BusinessResourceBindingVO> result = processOrchestrationEngine.queryResources(businessCode, resourceType);
        return CommonResponse.success(result);
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/next-node/query")
    @ApiOperation(value = "查询下一节点")
    public CommonResponse<List<ProcessOrchestrationConfigVO>> queryNextNodes(@RequestBody Map<String, Object> request) {
        String businessCode = (String) request.get("businessCode");
        String currentProcessDefKey = (String) request.get("currentProcessDefKey");
        String triggerEvent = (String) request.get("triggerEvent");
        Map<String, Object> contextData = (Map<String, Object>) request.get("contextData");
        List<ProcessOrchestrationConfigVO> result = processOrchestrationEngine.queryNextNodes(businessCode, currentProcessDefKey, triggerEvent, contextData);
        return CommonResponse.success(result);
    }
}
