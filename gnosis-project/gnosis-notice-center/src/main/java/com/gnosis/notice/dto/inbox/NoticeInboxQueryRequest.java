package com.gnosis.notice.dto.inbox;

import java.io.Serializable;
import java.util.Date;

/**
 * 站内信查询请求
 */
public class NoticeInboxQueryRequest implements Serializable {

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
     * 是否已读：0未读 1已读
     */
    private Integer isRead;
    /** 消息优先级 */
    private Integer priority;
    /** 消息分组ID */
    private String groupId;
    /** 状态 */
    private Integer status;
    /** 开始时间 */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 每页大小
     */
    private Integer pageSize;

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

    public Integer getIsRead() {
        return isRead;
    }

    public void setIsRead(Integer isRead) {
        this.isRead = isRead;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
