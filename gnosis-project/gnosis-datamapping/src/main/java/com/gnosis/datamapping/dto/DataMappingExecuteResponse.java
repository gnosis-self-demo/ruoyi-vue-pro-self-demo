package com.gnosis.datamapping.dto;

import com.gnosis.datamapping.engine.ErrorDetail;
import java.util.List;

public class DataMappingExecuteResponse {

    private Boolean success;
    private Object resultData;
    private List<ErrorDetail> errors;
    private List<ErrorDetail> validationErrors;
    private Integer executionTime;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public Object getResultData() {
        return resultData;
    }

    public void setResultData(Object resultData) {
        this.resultData = resultData;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDetail> errors) {
        this.errors = errors;
    }

    public List<ErrorDetail> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(List<ErrorDetail> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public Integer getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }
}
