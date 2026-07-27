package com.gnosis.notice.dto.blacklist;

import java.io.Serializable;

/**
 * 消息黑名单更新请求
 */
public class NoticeBlacklistUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID（必填） */
    private String id;

    /** 用户ID */
    private String userId;

    /** 通知类型 */
    private String noticeType;

    /** 模板编码 */
    private String templateCode;

    /** 退订原因 */
    private String reason;

    /** 状态：1生效 0失效 */
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
