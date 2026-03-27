package lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 业务类型创建请求 DTO
 */
@ApiModel(value = "BusinessTypeCreateRequest", description = "业务类型创建请求")
public class BusinessTypeCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务类型名称", required = true, example = "订单管理")
    private String name;

    @ApiModelProperty(value = "业务类型编码", required = true, example = "ORDER_MANAGE")
    private String code;

    @ApiModelProperty(value = "描述信息", example = "订单管理业务类型")
    private String description;

    @ApiModelProperty(value = "入口权限配置 (JSON 格式)", example = "{\"roles\":[\"ADMIN\",\"USER\"]}")
    private String entryPermissionConfig;

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
}
