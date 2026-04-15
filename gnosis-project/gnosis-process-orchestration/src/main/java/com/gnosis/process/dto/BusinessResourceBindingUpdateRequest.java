package com.gnosis.process.dto;

import java.io.Serializable;

public class BusinessResourceBindingUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String businessConfigId;
    private String resourceType;
    private String resourceId;
    private String resourceName;
    private String bindingType;
    private Integer bindingPriority;
    private Boolean isActive;
    private String metadata;
    private String updateUserId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessConfigId() {
        return businessConfigId;
    }

    public void setBusinessConfigId(String businessConfigId) {
        this.businessConfigId = businessConfigId;
    }

    public String getResourceType() {
        return resourceType;
    }

    public void setResourceType(String resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getBindingType() {
        return bindingType;
    }

    public void setBindingType(String bindingType) {
        this.bindingType = bindingType;
    }

    public Integer getBindingPriority() {
        return bindingPriority;
    }

    public void setBindingPriority(Integer bindingPriority) {
        this.bindingPriority = bindingPriority;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
}
