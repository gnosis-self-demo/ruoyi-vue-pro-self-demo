package com.gnosis.process.dto;

import java.io.Serializable;

public class ProcessOrchestrationConfigUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
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
    private String updateUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
}
