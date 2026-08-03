package com.gnosis.notice.domain;

import com.gnosis.common.domain.BaseEntity;
import java.util.Date;

/**
 * 站内信实体
 */
public class NoticeInbox extends BaseEntity {

    private String userId;
    private String subject;
    private String content;
    private String attachmentPaths;
    /** 消息优先级：0普通 1重要 2紧急 */
    private Integer priority = 0;
    /** 消息分组ID */
    private String groupId;
    /** 是否已读：0未读 1已读 */
    private Integer isRead;
    private Date readTime;
    /** 状态：1正常 0禁用(归档) */
    private Integer status;

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
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
    public Integer getIsRead() { return isRead; }
    public void setIsRead(Integer isRead) { this.isRead = isRead; }
    public Date getReadTime() { return readTime; }
    public void setReadTime(Date readTime) { this.readTime = readTime; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
