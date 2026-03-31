package com.gnosis.lifecycle.domain;

import com.gnosis.lifecycle.domain.base.BaseEntity;

/**
 * 事件发布记录实体类
 */
public class LifecycleEventPublishRecord extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 事件 ID
     */
    private String eventId;
    
    /**
     * 业务类型 ID
     */
    private String businessTypeId;
    
    /**
     * 业务实例 ID
     */
    private String businessInstanceId;
    
    /**
     * 发布类型：SYNC/ASYNC
     */
    private String publishType;
    
    /**
     * 目标类型：DOWNSTREAM/CUSTOM
     */
    private String targetType;
    
    /**
     * 目标配置（JSON）
     */
    private String targetConfig;
    
    /**
     * 消息内容（JSON）
     */
    private String payload;
    
    /**
     * 发布状态：PENDING/SUCCESS/FAILED
     */
    private String publishStatus;
    
    /**
     * 重试次数
     */
    private Integer retryCount;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 响应数据（JSON）
     */
    private String responseData;
    
    /**
     * 发布时间
     */
    private java.util.Date publishTime;
    
    /**
     * 备注
     */
    private String remark;
    
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getBusinessTypeId() {
        return businessTypeId;
    }
    
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
    
    public String getBusinessInstanceId() {
        return businessInstanceId;
    }
    
    public void setBusinessInstanceId(String businessInstanceId) {
        this.businessInstanceId = businessInstanceId;
    }
    
    public String getPublishType() {
        return publishType;
    }
    
    public void setPublishType(String publishType) {
        this.publishType = publishType;
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public String getTargetConfig() {
        return targetConfig;
    }
    
    public void setTargetConfig(String targetConfig) {
        this.targetConfig = targetConfig;
    }
    
    public String getPayload() {
        return payload;
    }
    
    public void setPayload(String payload) {
        this.payload = payload;
    }
    
    public String getPublishStatus() {
        return publishStatus;
    }
    
    public void setPublishStatus(String publishStatus) {
        this.publishStatus = publishStatus;
    }
    
    public Integer getRetryCount() {
        return retryCount;
    }
    
    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getResponseData() {
        return responseData;
    }
    
    public void setResponseData(String responseData) {
        this.responseData = responseData;
    }
    
    public java.util.Date getPublishTime() {
        return publishTime;
    }
    
    public void setPublishTime(java.util.Date publishTime) {
        this.publishTime = publishTime;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
