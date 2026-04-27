package com.gnosis.datamapping.dto;

import java.util.List;

public class DataMappingBatchExecuteResponse {

    private boolean success;
    private String message;
    private int total;
    private int passed;
    private int failed;
    private List<DataMappingTestCaseExecuteResponse> results;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPassed() {
        return passed;
    }

    public void setPassed(int passed) {
        this.passed = passed;
    }

    public int getFailed() {
        return failed;
    }

    public void setFailed(int failed) {
        this.failed = failed;
    }

    public List<DataMappingTestCaseExecuteResponse> getResults() {
        return results;
    }

    public void setResults(List<DataMappingTestCaseExecuteResponse> results) {
        this.results = results;
    }
}
