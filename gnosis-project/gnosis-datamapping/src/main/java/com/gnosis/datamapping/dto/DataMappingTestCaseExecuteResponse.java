package com.gnosis.datamapping.dto;

import com.alibaba.fastjson.JSONObject;
import java.util.List;

public class DataMappingTestCaseExecuteResponse {

    private boolean success;
    private String testCaseId;
    private String message;
    private Boolean isPassed;
    private JSONObject actualResult;
    private String diffInfo;
    private Integer executionTime;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(Boolean isPassed) {
        this.isPassed = isPassed;
    }

    public JSONObject getActualResult() {
        return actualResult;
    }

    public void setActualResult(JSONObject actualResult) {
        this.actualResult = actualResult;
    }

    public String getDiffInfo() {
        return diffInfo;
    }

    public void setDiffInfo(String diffInfo) {
        this.diffInfo = diffInfo;
    }

    public Integer getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Integer executionTime) {
        this.executionTime = executionTime;
    }
}
