package com.gnosis.process.dto;

import java.io.Serializable;

public class BusinessConfigUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String code;
    private String description;
    private String businessType;
    private String businessCategory;
    private String ruleEngineConfigId;
    private String ruleEngineType;
    private Boolean workflowEnabled;
    private String status;
    private String tenantId;
    private String updateUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessCategory() {
        return businessCategory;
    }

    public void setBusinessCategory(String businessCategory) {
        this.businessCategory = businessCategory;
    }

    public String getRuleEngineConfigId() {
        return ruleEngineConfigId;
    }

    public void setRuleEngineConfigId(String ruleEngineConfigId) {
        this.ruleEngineConfigId = ruleEngineConfigId;
    }

    public String getRuleEngineType() {
        return ruleEngineType;
    }

    public void setRuleEngineType(String ruleEngineType) {
        this.ruleEngineType = ruleEngineType;
    }

    public Boolean getWorkflowEnabled() {
        return workflowEnabled;
    }

    public void setWorkflowEnabled(Boolean workflowEnabled) {
        this.workflowEnabled = workflowEnabled;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
}
