package com.gnosis.notice.dto.inbox;

import com.gnosis.common.dto.BaseVO;
import java.util.Date;

/**
 * 站内信视图对象
 */
public class NoticeInboxVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    /**
     * 接收用户ID
     */
    private String userId;

    /**
     * 消息主题
     */
    private String subject;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 附件路径JSON
     */
    private String attachmentPaths;

    /**
     * 是否已读：0未读 1已读
     */
    private Integer isRead;

    /**
     * 读取时间
     */
    private Date readTime;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Date getReadTime() {
        return readTime;
    }

    public void setReadTime(Date readTime) {
        this.readTime = readTime;
    }
}
