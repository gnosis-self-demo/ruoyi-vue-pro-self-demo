package com.gnosis.notice.dto.blacklist;

import java.io.Serializable;

/**
 * 消息黑名单查询请求
 */
public class NoticeBlacklistQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private String userId;

    /** 通知类型 */
    private String noticeType;

    /** 模板编码 */
    private String templateCode;

    /** 状态：1生效 0失效 */
    private Integer status;

    /** 页码 */
    private Integer pageNum;

    /** 每页大小 */
    private Integer pageSize;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}
