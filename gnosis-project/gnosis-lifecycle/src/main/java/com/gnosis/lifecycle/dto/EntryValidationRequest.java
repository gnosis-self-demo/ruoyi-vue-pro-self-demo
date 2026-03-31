package com.gnosis.lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * 入口校验请求 DTO
 */
@ApiModel(value = "EntryValidationRequest", description = "入口校验请求")
public class EntryValidationRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务类型 ID", required = true, example = "123456")
    private String businessTypeId;

    @ApiModelProperty(value = "用户 ID", required = true, example = "user123")
    private String userId;

    @ApiModelProperty(value = "用户角色列表", example = "[\"ADMIN\", \"USER\"]")
    private java.util.List<String> userRoles;

    @ApiModelProperty(value = "校验数据 (JSON 格式)", example = "{\"field1\":\"value1\",\"field2\":\"value2\"}")
    private Map<String, Object> validationData;

    public String getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public java.util.List<String> getUserRoles() {
        return userRoles;
    }

    public void setUserRoles(java.util.List<String> userRoles) {
        this.userRoles = userRoles;
    }

    public Map<String, Object> getValidationData() {
        return validationData;
    }

    public void setValidationData(Map<String, Object> validationData) {
        this.validationData = validationData;
    }
}
