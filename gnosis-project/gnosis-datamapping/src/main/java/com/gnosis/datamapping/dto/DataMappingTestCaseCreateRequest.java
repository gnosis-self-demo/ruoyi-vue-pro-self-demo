package com.gnosis.datamapping.dto;

public class DataMappingTestCaseCreateRequest {

    private String configId;
    private String configCode;
    private String caseName;
    private String caseCode;
    private String requestJson;
    private String expectedResultJson;
    private String description;
    private String createUserId;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getCaseCode() {
        return caseCode;
    }

    public void setCaseCode(String caseCode) {
        this.caseCode = caseCode;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }

    public String getExpectedResultJson() {
        return expectedResultJson;
    }

    public void setExpectedResultJson(String expectedResultJson) {
        this.expectedResultJson = expectedResultJson;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
}
