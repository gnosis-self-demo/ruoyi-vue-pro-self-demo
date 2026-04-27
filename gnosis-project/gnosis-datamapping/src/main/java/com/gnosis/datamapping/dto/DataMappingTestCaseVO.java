package com.gnosis.datamapping.dto;

import com.gnosis.common.dto.BaseVO;

public class DataMappingTestCaseVO extends BaseVO {

    private String configId;
    private String configCode;
    private String caseName;
    private String caseCode;
    private String requestJson;
    private String expectedResultJson;
    private String actualResultJson;
    private Integer isPassed;
    private String diffInfo;
    private String description;
    private Integer status;

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

    public String getActualResultJson() {
        return actualResultJson;
    }

    public void setActualResultJson(String actualResultJson) {
        this.actualResultJson = actualResultJson;
    }

    public Integer getIsPassed() {
        return isPassed;
    }

    public void setIsPassed(Integer isPassed) {
        this.isPassed = isPassed;
    }

    public String getDiffInfo() {
        return diffInfo;
    }

    public void setDiffInfo(String diffInfo) {
        this.diffInfo = diffInfo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
