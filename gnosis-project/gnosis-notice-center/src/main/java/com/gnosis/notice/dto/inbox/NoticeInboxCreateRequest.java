package com.gnosis.notice.dto.inbox;

import java.io.Serializable;
import java.util.List;

/**
 * 站内信创建请求
 */
public class NoticeInboxCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 接收用户ID列表
     */
    private List<String> userIds;

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

    public List<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(List<String> userIds) {
        this.userIds = userIds;
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
}
