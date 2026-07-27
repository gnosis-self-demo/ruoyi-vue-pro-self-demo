package com.gnosis.notice.dto.group;

import java.io.Serializable;

/**
 * 消息分组更新请求
 */
public class NoticeGroupUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID（必填） */
    private String id;

    /** 分组编码 */
    private String groupCode;

    /** 分组名称 */
    private String groupName;

    /** 分组描述 */
    private String description;

    /** 状态：1启用 0禁用 */
    private Integer status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
