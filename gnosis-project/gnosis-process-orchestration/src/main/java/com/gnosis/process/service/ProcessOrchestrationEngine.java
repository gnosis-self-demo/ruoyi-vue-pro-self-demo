package com.gnosis.process.service;

import com.gnosis.process.dto.BusinessResourceBindingVO;
import com.gnosis.process.dto.ProcessEventSubmitRequest;
import com.gnosis.process.dto.ProcessEventSubmitResult;
import com.gnosis.process.dto.ProcessOrchestrationConfigVO;
import com.gnosis.process.dto.ProcessTransitionFlowVO;
import com.gnosis.process.dto.ProcessTransitionQueryRequest;
import com.gnosis.process.dto.ProcessTriggerRequest;
import com.gnosis.process.dto.ProcessTriggerResult;

import java.util.List;
import java.util.Map;

/**
 * 流程编排引擎 Service 接口
 */
public interface ProcessOrchestrationEngine {

    /**
     * 触发流程
     *
     * @param request 流程触发请求参数
     * @return 流程触发结果
     */
    ProcessTriggerResult trigger(ProcessTriggerRequest request);

    /**
     * 提交事件
     *
     * @param request 事件提交请求参数
     * @return 事件提交结果
     */
    ProcessEventSubmitResult submitEvent(ProcessEventSubmitRequest request);

    /**
     * 查询流转记录
     *
     * @param request 流转查询请求参数
     * @return 流程流转记录VO列表
     */
    List<ProcessTransitionFlowVO> queryTransitions(ProcessTransitionQueryRequest request);

    /**
     * 查询业务资源绑定
     *
     * @param businessCode 业务编码
     * @param resourceType 资源类型
     * @return 业务资源绑定VO列表
     */
    List<BusinessResourceBindingVO> queryResources(String businessCode, String resourceType);

    /**
     * 查询下一节点
     *
     * @param businessCode 业务编码
     * @param currentProcessDefKey 当前流程定义Key
     * @param triggerEvent 触发事件
     * @param contextData 上下文数据
     * @return 流程编排配置VO列表
     */
    List<ProcessOrchestrationConfigVO> queryNextNodes(String businessCode, String currentProcessDefKey, String triggerEvent, Map<String, Object> contextData);
}
