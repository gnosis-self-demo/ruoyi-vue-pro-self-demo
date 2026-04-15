package com.gnosis.process.domain;

import com.gnosis.process.domain.base.BaseEntity;

public class ProcessTransitionFlow extends BaseEntity {

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
    private String remarks;

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

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
