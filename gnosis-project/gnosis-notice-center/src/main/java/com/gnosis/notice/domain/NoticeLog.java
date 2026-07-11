package com.gnosis.notice.domain;

import com.gnosis.common.domain.BaseEntity;
import java.util.Date;

/**
 * 消息发送日志实体
 */
public class NoticeLog extends BaseEntity {

    /**
     * 模板ID
     */
    private String templateId;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 通知类型
     */
    private String noticeType;

    /**
     * 接收人
     */
    private String receiver;

    /**
     * 消息主题
     */
    private String subject;

    /**
     * 实际发送内容
     */
    private String content;

    /**
     * 附件路径JSON
     */
    private String attachmentPaths;

    /**
     * 发送状态：0待发送 1发送中 2成功 3失败
     */
    private Integer sendStatus;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最大重试次数
     */
    private Integer maxRetry;

    /**
     * 第三方请求ID
     */
    private String requestId;

    /**
     * 第三方响应内容
     */
    private String thirdPartyResponse;

    /**
     * 发送时间
     */
    private Date sendTime;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachmentPaths() {
        return attachmentPaths;
    }

    public void setAttachmentPaths(String attachmentPaths) {
        this.attachmentPaths = attachmentPaths;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public Integer getMaxRetry() {
        return maxRetry;
    }

    public void setMaxRetry(Integer maxRetry) {
        this.maxRetry = maxRetry;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getThirdPartyResponse() {
        return thirdPartyResponse;
    }

    public void setThirdPartyResponse(String thirdPartyResponse) {
        this.thirdPartyResponse = thirdPartyResponse;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }
}
