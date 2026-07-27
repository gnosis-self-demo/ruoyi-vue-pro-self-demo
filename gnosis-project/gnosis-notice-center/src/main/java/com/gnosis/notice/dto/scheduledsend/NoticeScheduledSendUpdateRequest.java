package com.gnosis.notice.dto.scheduledsend;

import java.io.Serializable;
import java.util.Date;

/**
 * 定时发送更新请求
 */
public class NoticeScheduledSendUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID（必填） */
    private String id;

    /** 计划发送时间 */
    private Date scheduledTime;

    /** 发送状态：0待发送 1已发送 2失败 3已取消 */
    private Integer sendStatus;

    /** 消息主题 */
    private String subject;

    /** 消息内容 */
    private String content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
}
