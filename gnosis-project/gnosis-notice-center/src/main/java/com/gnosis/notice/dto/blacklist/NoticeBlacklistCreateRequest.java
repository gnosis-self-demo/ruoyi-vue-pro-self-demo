package com.gnosis.notice.dto.blacklist;

import java.io.Serializable;

/**
 * 消息黑名单创建请求
 */
public class NoticeBlacklistCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private String userId;

    /** 通知类型 */
    private String noticeType;

    /** 模板编码（空=退订该类型所有通知） */
    private String templateCode;

    /** 退订原因 */
    private String reason;

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
}
