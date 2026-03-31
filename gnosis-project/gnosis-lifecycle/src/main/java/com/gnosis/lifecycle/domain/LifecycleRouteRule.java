package com.gnosis.lifecycle.domain;

import com.gnosis.lifecycle.domain.base.BaseEntity;

/**
 * 路由规则实体
 * 对应表：lifecycle_route_rule
 */
public class LifecycleRouteRule extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 业务类型 ID
     */
    private String businessTypeId;
    
    /**
     * 事件类型
     */
    private String eventType;
    
    /**
     * 规则名称
     */
    private String ruleName;
    
    /**
     * 条件表达式
     */
    private String conditionExpression;
    
    /**
     * 优先级 (数字越大优先级越高)
     */
    private Integer priority;
    
    /**
     * 是否启用 (0-否 1-是)
     */
    private Integer isActive;
    
    /**
     * 版本号
     */
    private Integer version;
    
    /**
     * 规则编码
     */
    private String ruleCode;
    
    /**
     * 规则描述
     */
    private String description;
    
    public String getBusinessTypeId() {
        return businessTypeId;
    }
    
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getRuleName() {
        return ruleName;
    }
    
    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
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
    
    public Integer getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public String getRuleCode() {
        return ruleCode;
    }
    
    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}
