package lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 业务类型 VO（视图对象）
 */
@ApiModel(value = "BusinessTypeVO", description = "业务类型视图对象")
public class BusinessTypeVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 ID")
    private String id;

    @ApiModelProperty(value = "业务类型名称")
    private String name;

    @ApiModelProperty(value = "业务类型编码")
    private String code;

    @ApiModelProperty(value = "描述信息")
    private String description;

    @ApiModelProperty(value = "入口权限配置 (JSON 格式)")
    private String entryPermissionConfig;

    @ApiModelProperty(value = "创建人 ID")
    private String createUserId;

    @ApiModelProperty(value = "更新人 ID")
    private String updateUserId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntryPermissionConfig() {
        return entryPermissionConfig;
    }

    public void setEntryPermissionConfig(String entryPermissionConfig) {
        this.entryPermissionConfig = entryPermissionConfig;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
