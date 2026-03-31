package com.gnosis.lifecycle.domain;

import com.gnosis.lifecycle.domain.base.BaseEntity;

/**
 * 状态流转实体
 * 对应表：lifecycle_state_transition
 */
public class LifecycleStateTransition extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 业务类型 ID
     */
    private String businessTypeId;
    
    /**
     * 源状态 ID
     */
    private String fromStateId;
    
    /**
     * 目标状态 ID
     */
    private String toStateId;
    
    /**
     * 流转名称
     */
    private String transitionName;
    
    /**
     * 流转编码
     */
    private String transitionCode;
    
    /**
     * 条件表达式
     */
    private String conditionExpression;
    
    /**
     * 优先级 (数字越大优先级越高)
     */
    private Integer priority;
    
    public String getBusinessTypeId() {
        return businessTypeId;
    }
    
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
    
    public String getFromStateId() {
        return fromStateId;
    }
    
    public void setFromStateId(String fromStateId) {
        this.fromStateId = fromStateId;
    }
    
    public String getToStateId() {
        return toStateId;
    }
    
    public void setToStateId(String toStateId) {
        this.toStateId = toStateId;
    }
    
    public String getTransitionName() {
        return transitionName;
    }
    
    public void setTransitionName(String transitionName) {
        this.transitionName = transitionName;
    }
    
    public String getTransitionCode() {
        return transitionCode;
    }
    
    public void setTransitionCode(String transitionCode) {
        this.transitionCode = transitionCode;
    }
    
    public String getConditionExpression() {
        return conditionExpression;
    }
    
    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }
    
    public Integer getPriority() {
        return priority;
    }
    
    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
