package com.gnosis.datamapping.dto;

public class DataMappingExecuteRequest {

    private String configId;
    private String requestJson;

    public String getConfigId() {
        return configId;
    }

    public void setConfigId(String configId) {
        this.configId = configId;
    }

    public String getRequestJson() {
        return requestJson;
    }

    public void setRequestJson(String requestJson) {
        this.requestJson = requestJson;
    }
}
