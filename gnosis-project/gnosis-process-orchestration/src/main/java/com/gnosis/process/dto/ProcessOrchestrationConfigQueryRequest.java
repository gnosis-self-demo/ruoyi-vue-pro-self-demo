package com.gnosis.process.dto;

public class ProcessOrchestrationConfigQueryRequest extends PageRequest {

    private static final long serialVersionUID = 1L;

    private String businessConfigId;
    private String currentProcessDefKey;
    private String currentProcessDefUniqueKey;
    private String currentProcessSystem;
    private String nextProcessDefKey;
    private String nextProcessDefUniqueKey;
    private String nextProcessSystem;
    private String triggerEvent;
    private String conditionExpression;
    private Integer priority;
    private Boolean isActive;
    private String createUserId;
    private String updateUserId;
    private String createTime;
    private String updateTime;

    public String getBusinessConfigId() {
        return businessConfigId;
    }

    public void setBusinessConfigId(String businessConfigId) {
        this.businessConfigId = businessConfigId;
    }

    public String getCurrentProcessDefKey() {
        return currentProcessDefKey;
    }

    public void setCurrentProcessDefKey(String currentProcessDefKey) {
        this.currentProcessDefKey = currentProcessDefKey;
    }

    public String getCurrentProcessDefUniqueKey() {
        return currentProcessDefUniqueKey;
    }

    public void setCurrentProcessDefUniqueKey(String currentProcessDefUniqueKey) {
        this.currentProcessDefUniqueKey = currentProcessDefUniqueKey;
    }

    public String getCurrentProcessSystem() {
        return currentProcessSystem;
    }

    public void setCurrentProcessSystem(String currentProcessSystem) {
        this.currentProcessSystem = currentProcessSystem;
    }

    public String getNextProcessDefKey() {
        return nextProcessDefKey;
    }

    public void setNextProcessDefKey(String nextProcessDefKey) {
        this.nextProcessDefKey = nextProcessDefKey;
    }

    public String getNextProcessDefUniqueKey() {
        return nextProcessDefUniqueKey;
    }

    public void setNextProcessDefUniqueKey(String nextProcessDefUniqueKey) {
        this.nextProcessDefUniqueKey = nextProcessDefUniqueKey;
    }

    public String getNextProcessSystem() {
        return nextProcessSystem;
    }

    public void setNextProcessSystem(String nextProcessSystem) {
        this.nextProcessSystem = nextProcessSystem;
    }

    public String getTriggerEvent() {
        return triggerEvent;
    }

    public void setTriggerEvent(String triggerEvent) {
        this.triggerEvent = triggerEvent;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
