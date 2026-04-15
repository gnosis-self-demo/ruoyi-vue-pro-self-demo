package com.gnosis.process.dto;

import java.io.Serializable;
import java.util.Map;

public class ProcessEventSubmitRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String processDefKey;
    private String nodeDefKey;
    private String submitAction;
    private String businessCode;
    private String businessId;
    private Map<String, Object> contextData;
    private String operatorId;

    public String getProcessDefKey() {
        return processDefKey;
    }

    public void setProcessDefKey(String processDefKey) {
        this.processDefKey = processDefKey;
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

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public Map<String, Object> getContextData() {
        return contextData;
    }

    public void setContextData(Map<String, Object> contextData) {
        this.contextData = contextData;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
}
