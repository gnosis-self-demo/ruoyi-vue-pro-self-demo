package com.gnosis.process.dto;

public class ProcessTransitionFlowQueryRequest extends PageRequest {

    private static final long serialVersionUID = 1L;

    private String orchestrationConfigId;
    private String eventConfigId;
    private String processDefKey;
    private String processDefUniqueKey;
    private String nodeDefKey;
    private String processInstanceId;
    private Integer processInstanceSequence;
    private String businessDescription;
    private String businessId;
    private String fromNode;
    private String toNode;
    private String transitionType;
    private String transitionResult;
    private String createUserId;
    private String updateUserId;
    private String createTime;
    private String updateTime;

    public String getOrchestrationConfigId() {
        return orchestrationConfigId;
    }

    public void setOrchestrationConfigId(String orchestrationConfigId) {
        this.orchestrationConfigId = orchestrationConfigId;
    }

    public String getEventConfigId() {
        return eventConfigId;
    }

    public void setEventConfigId(String eventConfigId) {
        this.eventConfigId = eventConfigId;
    }

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

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public Integer getProcessInstanceSequence() {
        return processInstanceSequence;
    }

    public void setProcessInstanceSequence(Integer processInstanceSequence) {
        this.processInstanceSequence = processInstanceSequence;
    }

    public String getBusinessDescription() {
        return businessDescription;
    }

    public void setBusinessDescription(String businessDescription) {
        this.businessDescription = businessDescription;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getFromNode() {
        return fromNode;
    }

    public void setFromNode(String fromNode) {
        this.fromNode = fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
    }

    public String getTransitionType() {
        return transitionType;
    }

    public void setTransitionType(String transitionType) {
        this.transitionType = transitionType;
    }

    public String getTransitionResult() {
        return transitionResult;
    }

    public void setTransitionResult(String transitionResult) {
        this.transitionResult = transitionResult;
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
