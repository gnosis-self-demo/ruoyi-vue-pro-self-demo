package com.gnosis.lifecycle.domain;

import com.gnosis.lifecycle.domain.base.BaseEntity;

/**
 * 下游触发配置实体类
 */
public class LifecycleDownstreamTriggerConfig extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 终态配置 ID
     */
    private String finalStateId;
    
    /**
     * 触发类型：HTTP/MQ/EMAIL/SMS
     */
    private String triggerType;
    
    /**
     * 触发 URL（HTTP 类型）
     */
    private String triggerUrl;
    
    /**
     * 触发方法：GET/POST/PUT/DELETE
     */
    private String triggerMethod;
    
    /**
     * 请求头配置（JSON）
     */
    private String triggerHeaders;
    
    /**
     * 请求体模板（JSON）
     */
    private String triggerPayload;
    
    /**
     * MQ 主题
     */
    private String mqTopic;
    
    /**
     * MQ 标签
     */
    private String mqTag;
    
    /**
     * MQ 消息模板（JSON）
     */
    private String mqMessageTemplate;
    
    /**
     * 邮件接收人列表（逗号分隔）
     */
    private String emailRecipients;
    
    /**
     * 邮件模板编码
     */
    private String emailTemplate;
    
    /**
     * 短信接收人列表（逗号分隔）
     */
    private String smsRecipients;
    
    /**
     * 短信模板编码
     */
    private String smsTemplate;
    
    /**
     * 重试次数
     */
    private Integer retryTimes;
    
    /**
     * 重试间隔（秒）
     */
    private Integer retryInterval;
    
    /**
     * 超时时间（秒）
     */
    private Integer timeoutSeconds;
    
    /**
     * 是否异步：0-否，1-是
     */
    private Integer isAsync;
    
    /**
     * 是否启用：0-否，1-是
     */
    private Integer isActive;
    
    /**
     * 触发条件（SpEL 表达式）
     */
    private String triggerCondition;
    
    /**
     * 备注
     */
    private String remark;
    
    public String getFinalStateId() {
        return finalStateId;
    }
    
    public void setFinalStateId(String finalStateId) {
        this.finalStateId = finalStateId;
    }
    
    public String getTriggerType() {
        return triggerType;
    }
    
    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }
    
    public String getTriggerUrl() {
        return triggerUrl;
    }
    
    public void setTriggerUrl(String triggerUrl) {
        this.triggerUrl = triggerUrl;
    }
    
    public String getTriggerMethod() {
        return triggerMethod;
    }
    
    public void setTriggerMethod(String triggerMethod) {
        this.triggerMethod = triggerMethod;
    }
    
    public String getTriggerHeaders() {
        return triggerHeaders;
    }
    
    public void setTriggerHeaders(String triggerHeaders) {
        this.triggerHeaders = triggerHeaders;
    }
    
    public String getTriggerPayload() {
        return triggerPayload;
    }
    
    public void setTriggerPayload(String triggerPayload) {
        this.triggerPayload = triggerPayload;
    }
    
    public String getMqTopic() {
        return mqTopic;
    }
    
    public void setMqTopic(String mqTopic) {
        this.mqTopic = mqTopic;
    }
    
    public String getMqTag() {
        return mqTag;
    }
    
    public void setMqTag(String mqTag) {
        this.mqTag = mqTag;
    }
    
    public String getMqMessageTemplate() {
        return mqMessageTemplate;
    }
    
    public void setMqMessageTemplate(String mqMessageTemplate) {
        this.mqMessageTemplate = mqMessageTemplate;
    }
    
    public String getEmailRecipients() {
        return emailRecipients;
    }
    
    public void setEmailRecipients(String emailRecipients) {
        this.emailRecipients = emailRecipients;
    }
    
    public String getEmailTemplate() {
        return emailTemplate;
    }
    
    public void setEmailTemplate(String emailTemplate) {
        this.emailTemplate = emailTemplate;
    }
    
    public String getSmsRecipients() {
        return smsRecipients;
    }
    
    public void setSmsRecipients(String smsRecipients) {
        this.smsRecipients = smsRecipients;
    }
    
    public String getSmsTemplate() {
        return smsTemplate;
    }
    
    public void setSmsTemplate(String smsTemplate) {
        this.smsTemplate = smsTemplate;
    }
    
    public Integer getRetryTimes() {
        return retryTimes;
    }
    
    public void setRetryTimes(Integer retryTimes) {
        this.retryTimes = retryTimes;
    }
    
    public Integer getRetryInterval() {
        return retryInterval;
    }
    
    public void setRetryInterval(Integer retryInterval) {
        this.retryInterval = retryInterval;
    }
    
    public Integer getTimeoutSeconds() {
        return timeoutSeconds;
    }
    
    public void setTimeoutSeconds(Integer timeoutSeconds) {
        this.timeoutSeconds = timeoutSeconds;
    }
    
    public Integer getIsAsync() {
        return isAsync;
    }
    
    public void setIsAsync(Integer isAsync) {
        this.isAsync = isAsync;
    }
    
    public Integer getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
    
    public String getTriggerCondition() {
        return triggerCondition;
    }
    
    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
