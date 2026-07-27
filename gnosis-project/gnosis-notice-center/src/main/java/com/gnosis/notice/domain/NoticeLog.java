package com.gnosis.notice.domain;

import com.gnosis.common.domain.BaseEntity;
import java.util.Date;

/**
 * 消息发送日志实体
 */
public class NoticeLog extends BaseEntity {

    private String templateId;
    private String templateCode;
    private String noticeType;
    private String receiver;
    private String subject;
    private String content;
    private String attachmentPaths;
    /** 消息优先级：0普通 1重要 2紧急 */
    private Integer priority;
    /** 消息分组ID */
    private String groupId;
    /** 发送状态：0待发送 1发送中 2成功 3失败 */
    private Integer sendStatus;
    private String errorMsg;
    private Integer retryCount;
    private Integer maxRetry;
    private String requestId;
    private String thirdPartyResponse;
    private Date sendTime;

    public String getTemplateId() { return templateId; }
    public void setTemplateId(String templateId) { this.templateId = templateId; }
    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getNoticeType() { return noticeType; }
    public void setNoticeType(String noticeType) { this.noticeType = noticeType; }
    public String getReceiver() { return receiver; }
    public void setReceiver(String receiver) { this.receiver = receiver; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAttachmentPaths() { return attachmentPaths; }
    public void setAttachmentPaths(String attachmentPaths) { this.attachmentPaths = attachmentPaths; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public Integer getSendStatus() { return sendStatus; }
    public void setSendStatus(Integer sendStatus) { this.sendStatus = sendStatus; }
    public String getErrorMsg() { return errorMsg; }
    public void setErrorMsg(String errorMsg) { this.errorMsg = errorMsg; }
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    public Integer getMaxRetry() { return maxRetry; }
    public void setMaxRetry(Integer maxRetry) { this.maxRetry = maxRetry; }
    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
    public String getThirdPartyResponse() { return thirdPartyResponse; }
    public void setThirdPartyResponse(String thirdPartyResponse) { this.thirdPartyResponse = thirdPartyResponse; }
    public Date getSendTime() { return sendTime; }
    public void setSendTime(Date sendTime) { this.sendTime = sendTime; }
}
