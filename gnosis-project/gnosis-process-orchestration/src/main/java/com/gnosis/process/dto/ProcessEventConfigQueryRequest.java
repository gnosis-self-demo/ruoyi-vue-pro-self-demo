package com.gnosis.process.dto;

public class ProcessEventConfigQueryRequest extends PageRequest {

    private static final long serialVersionUID = 1L;

    private String processDefKey;
    private String processDefUniqueKey;
    private String nodeDefKey;
    private String submitAction;
    private String matchBusiness;
    private String changeBusinessStatus;
    private String eventType;
    private String eventHandler;
    private String handlerConfig;
    private Boolean isActive;
    private String createUserId;
    private String updateUserId;
    private String createTime;
    private String updateTime;

    public String getProcessDefKey() {
        return processDefKey;
    }

    public void setProcessDefKey(String processDefKey) {
        this.processDefKey = processDefKey;
    }

    public String getProcessDefUniqueKey() {
        return processDefUniqueKey;
    }

    public void setProcessDefUniqueKey(String processDefUniqueKey) {
        this.processDefUniqueKey = processDefUniqueKey;
    }

    public String getNodeDefKey() {
        return nodeDefKey;
    }

    public void setNodeDefKey(String nodeDefKey) {
        this.nodeDefKey = nodeDefKey;
    }

    public String getSubmitAction() {
        return submitAction;
    }

    public void setSubmitAction(String submitAction) {
        this.submitAction = submitAction;
    }

    public String getMatchBusiness() {
        return matchBusiness;
    }

    public void setMatchBusiness(String matchBusiness) {
        this.matchBusiness = matchBusiness;
    }

    public String getChangeBusinessStatus() {
        return changeBusinessStatus;
    }

    public void setChangeBusinessStatus(String changeBusinessStatus) {
        this.changeBusinessStatus = changeBusinessStatus;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventHandler() {
        return eventHandler;
    }

    public void setEventHandler(String eventHandler) {
        this.eventHandler = eventHandler;
    }

    public String getHandlerConfig() {
        return handlerConfig;
    }

    public void setHandlerConfig(String handlerConfig) {
        this.handlerConfig = handlerConfig;
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
