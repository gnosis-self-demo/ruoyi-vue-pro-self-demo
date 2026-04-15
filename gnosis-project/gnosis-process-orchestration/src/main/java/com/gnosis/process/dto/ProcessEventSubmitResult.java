package com.gnosis.process.dto;

import java.io.Serializable;

public class ProcessEventSubmitResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private Boolean success;
    private String changedStatus;
    private String eventConfigId;
    private String message;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getChangedStatus() {
        return changedStatus;
    }

    public void setChangedStatus(String changedStatus) {
        this.changedStatus = changedStatus;
    }

    public String getEventConfigId() {
        return eventConfigId;
    }

    public void setEventConfigId(String eventConfigId) {
        this.eventConfigId = eventConfigId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
