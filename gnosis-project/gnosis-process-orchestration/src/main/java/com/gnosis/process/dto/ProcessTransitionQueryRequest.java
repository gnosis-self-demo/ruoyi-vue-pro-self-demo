package com.gnosis.process.dto;

import java.io.Serializable;

public class ProcessTransitionQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String processInstanceId;
    private String businessId;
    private String businessCode;

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public String getBusinessCode() {
        return businessCode;
    }

    public void setBusinessCode(String businessCode) {
        this.businessCode = businessCode;
    }
}
