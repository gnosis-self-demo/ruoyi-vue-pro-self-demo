package com.gnosis.notice.dto.inbox;

import java.io.Serializable;

/**
 * 站内信更新请求
 */
public class NoticeInboxUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 站内信ID（必填） */
    private String id;
    /** 消息主题 */
    private String subject;
    /** 消息内容 */
    private String content;
    /** 消息优先级：0普通 1重要 2紧急 */
    private Integer priority;
    /** 消息分组ID */
    private String groupId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
}
