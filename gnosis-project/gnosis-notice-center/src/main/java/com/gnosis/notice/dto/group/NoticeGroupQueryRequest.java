package com.gnosis.notice.dto.group;

import java.io.Serializable;

/**
 * 消息分组查询请求
 */
public class NoticeGroupQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分组编码 */
    private String groupCode;

    /** 分组名称 */
    private String groupName;

    /** 状态：1启用 0禁用 */
    private Integer status;

    /** 页码 */
    private Integer pageNum;

    /** 每页大小 */
    private Integer pageSize;

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
