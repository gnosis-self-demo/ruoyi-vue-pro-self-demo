package com.gnosis.notice.domain;

import com.gnosis.common.domain.BaseEntity;

/**
 * 消息分组实体
 */
public class NoticeGroup extends BaseEntity {

    /** 分组编码 */
    private String groupCode;

    /** 分组名称 */
    private String groupName;

    /** 分组描述 */
    private String description;

    /** 状态：1启用 0禁用 */
    private Integer status = 1;

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
