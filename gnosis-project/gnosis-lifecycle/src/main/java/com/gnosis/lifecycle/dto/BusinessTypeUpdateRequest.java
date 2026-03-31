package com.gnosis.lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 业务类型更新请求 DTO
 */
@ApiModel(value = "BusinessTypeUpdateRequest", description = "业务类型更新请求")
public class BusinessTypeUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务类型 ID", required = true, example = "123456")
    private String id;

    @ApiModelProperty(value = "业务类型名称", example = "订单管理")
    private String name;

    @ApiModelProperty(value = "业务类型编码", example = "ORDER_MANAGE")
    private String code;

    @ApiModelProperty(value = "描述信息", example = "订单管理业务类型")
    private String description;

    @ApiModelProperty(value = "入口权限配置 (JSON 格式)", example = "{\"roles\":[\"ADMIN\",\"USER\"]}")
    private String entryPermissionConfig;

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
}
