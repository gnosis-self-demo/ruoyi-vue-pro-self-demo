package com.gnosis.notice.dto.inbox;

import com.gnosis.common.dto.BaseVO;
import java.util.Date;

/**
 * 站内信视图对象 - 修复版: 新增priority/groupId/status/updateUserId/updateTime
 */
public class NoticeInboxVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String subject;
    private String content;
    private String attachmentPaths;
    private Integer priority;
    private String groupId;
    private Integer isRead;
    private Date readTime;
    private Integer status;
    private String updateUserId;
    private Date updateTime;

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
    public String getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(String updateUserId) { this.updateUserId = updateUserId; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
