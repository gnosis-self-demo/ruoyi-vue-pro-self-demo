package com.gnosis.notice.dto.group;

import com.gnosis.common.dto.BaseVO;

/**
 * 消息分组视图对象
 */
public class NoticeGroupVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    /** 分组编码 */
    private String groupCode;

    /** 分组名称 */
    private String groupName;

    /** 分组描述 */
    private String description;

    /** 状态：1启用 0禁用 */
    private Integer status;

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
