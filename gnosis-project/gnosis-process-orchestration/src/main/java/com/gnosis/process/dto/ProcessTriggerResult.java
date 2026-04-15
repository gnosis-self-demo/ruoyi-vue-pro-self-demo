package com.gnosis.process.dto;

import java.io.Serializable;

public class ProcessTriggerResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success;
    private String nextProcessDefKey;
    private String transitionFlowId;
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getNextProcessDefKey() {
        return nextProcessDefKey;
    }

    public void setNextProcessDefKey(String nextProcessDefKey) {
        this.nextProcessDefKey = nextProcessDefKey;
    }

    public String getTransitionFlowId() {
        return transitionFlowId;
    }

    public void setTransitionFlowId(String transitionFlowId) {
        this.transitionFlowId = transitionFlowId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
