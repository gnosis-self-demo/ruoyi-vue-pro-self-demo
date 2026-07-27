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
    /** 消息优先级：0普通 1重要 2紧急 */
    private Integer priority;
    /** 消息分组ID */
    private String groupId;

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

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
}
