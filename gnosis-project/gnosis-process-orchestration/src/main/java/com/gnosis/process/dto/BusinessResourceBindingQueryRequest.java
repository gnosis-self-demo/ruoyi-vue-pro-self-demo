package com.gnosis.process.dto;

public class BusinessResourceBindingQueryRequest extends PageRequest {

    private static final long serialVersionUID = 1L;

    private String businessConfigId;
    private String resourceType;
    private String resourceId;
    private String resourceName;
    private String bindingType;
    private Integer bindingPriority;
    private Boolean isActive;
    private String createUserId;
    private String updateUserId;
    private String createTime;
    private String updateTime;

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

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
}
