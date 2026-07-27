package com.gnosis.notice.dto.blacklist;

import com.gnosis.common.dto.BaseVO;

/**
 * 消息黑名单视图对象
 */
public class NoticeBlacklistVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private String userId;

    /** 通知类型 */
    private String noticeType;

    /** 模板编码（空=退订该类型所有通知） */
    private String templateCode;

    /** 退订原因 */
    private String reason;

    /** 状态：1生效 0失效 */
    private Integer status;

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
