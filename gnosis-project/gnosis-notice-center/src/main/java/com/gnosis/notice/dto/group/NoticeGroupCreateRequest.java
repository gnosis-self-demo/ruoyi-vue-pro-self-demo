package com.gnosis.notice.dto.group;

import java.io.Serializable;

/**
 * 消息分组创建请求
 */
public class NoticeGroupCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 分组编码 */
    private String groupCode;

    /** 分组名称 */
    private String groupName;

    /** 分组描述 */
    private String description;

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
}
