package com.gnosis.notice.domain;

import com.gnosis.common.domain.BaseEntity;

import java.util.Date;

/**
 * 定时发送实体
 */
public class NoticeScheduledSend extends BaseEntity {

    /** 模板编码 */
    private String templateCode;

    /** 通知类型 */
    private String noticeType;

    /** 接收人 */
    private String receiver;

    /** 消息主题 */
    private String subject;

    /** 消息内容 */
    private String content;

    /** 模板变量参数JSON */
    private String params;

    /** 附件路径 */
    private String attachmentPaths;

    /** 消息优先级 */
    private Integer priority = 0;

    /** 分组ID */
    private String groupId;

    /** 计划发送时间 */
    private Date scheduledTime;

    /** 发送状态：0待发送 1已发送 2失败 3已取消 */
    private Integer sendStatus = 0;

    /** 发送后回填的日志ID */
    private String logId;

    /** 错误信息 */
    private String errorMsg;

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

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getAttachmentPaths() {
        return attachmentPaths;
    }

    public void setAttachmentPaths(String attachmentPaths) {
        this.attachmentPaths = attachmentPaths;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Integer getSendStatus() {
        return sendStatus;
    }

    public void setSendStatus(Integer sendStatus) {
        this.sendStatus = sendStatus;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
