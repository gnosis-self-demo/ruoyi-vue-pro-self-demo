package com.gnosis.lifecycle.domain;

import com.gnosis.lifecycle.domain.base.BaseEntity;

/**
 * 实例追踪实体类
 */
public class LifecycleInstanceTrace extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 业务类型 ID
     */
    private String businessTypeId;
    
    /**
     * 业务实例 ID
     */
    private String businessInstanceId;
    
    /**
     * 追踪类型：STATE_TRANSITION/EVENT_TRIGGER/ACTION_EXECUTE
     */
    private String traceType;
    
    /**
     * 追踪数据（JSON）
     */
    private String traceData;
    
    /**
     * 源状态 ID（状态流转时使用）
     */
    private String sourceStateId;
    
    /**
     * 源状态编码
     */
    private String sourceStateCode;
    
    /**
     * 目标状态 ID（状态流转时使用）
     */
    private String targetStateId;
    
    /**
     * 目标状态编码
     */
    private String targetStateCode;
    
    /**
     * 事件 ID（事件触发时使用）
     */
    private String eventId;
    
    /**
     * 事件类型
     */
    private String eventType;
    
    /**
     * 动作类型（动作执行时使用）
     */
    private String actionType;
    
    /**
     * 执行结果（JSON）
     */
    private String actionResult;
    
    /**
     * 追踪状态：SUCCESS/FAILED
     */
    private String traceStatus;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 执行耗时（毫秒）
     */
    private Long executionTimeMs;
    
    /**
     * 备注
     */
    private String remark;
    
    public String getBusinessTypeId() {
        return businessTypeId;
    }
    
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
    
    public String getBusinessInstanceId() {
        return businessInstanceId;
    }
    
    public void setBusinessInstanceId(String businessInstanceId) {
        this.businessInstanceId = businessInstanceId;
    }
    
    public String getTraceType() {
        return traceType;
    }
    
    public void setTraceType(String traceType) {
        this.traceType = traceType;
    }
    
    public String getTraceData() {
        return traceData;
    }
    
    public void setTraceData(String traceData) {
        this.traceData = traceData;
    }
    
    public String getSourceStateId() {
        return sourceStateId;
    }
    
    public void setSourceStateId(String sourceStateId) {
        this.sourceStateId = sourceStateId;
    }
    
    public String getSourceStateCode() {
        return sourceStateCode;
    }
    
    public void setSourceStateCode(String sourceStateCode) {
        this.sourceStateCode = sourceStateCode;
    }
    
    public String getTargetStateId() {
        return targetStateId;
    }
    
    public void setTargetStateId(String targetStateId) {
        this.targetStateId = targetStateId;
    }
    
    public String getTargetStateCode() {
        return targetStateCode;
    }
    
    public void setTargetStateCode(String targetStateCode) {
        this.targetStateCode = targetStateCode;
    }
    
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getActionType() {
        return actionType;
    }
    
    public void setActionType(String actionType) {
        this.actionType = actionType;
    }
    
    public String getActionResult() {
        return actionResult;
    }
    
    public void setActionResult(String actionResult) {
        this.actionResult = actionResult;
    }
    
    public String getTraceStatus() {
        return traceStatus;
    }
    
    public void setTraceStatus(String traceStatus) {
        this.traceStatus = traceStatus;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }
    
    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
