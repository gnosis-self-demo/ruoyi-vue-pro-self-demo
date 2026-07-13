package com.gnosis.signature.dto.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 签章流程创建请求 DTO
 */
@ApiModel(value = "SignatureProcessCreateRequest", description = "签章流程创建请求")
public class SignatureProcessCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "流程编码", required = true, example = "PROCESS_001")
    private String processCode;

    @ApiModelProperty(value = "流程名称", required = true, example = "合同会签流程")
    private String processName;

    @ApiModelProperty(value = "流程类型(会签/顺序签/并行签)", required = true, example = "会签")
    private String processType;

    @ApiModelProperty(value = "流程配置(JSON)", example = "{\"steps\":[]}")
    private String processConfig;

    @ApiModelProperty(value = "签署顺序", example = "1")
    private Integer signOrder;

    @ApiModelProperty(value = "描述", example = "合同会签流程描述")
    private String description;

    @ApiModelProperty(value = "状态(0-禁用 1-启用)", example = "1")
    private Integer status;

    @ApiModelProperty(value = "创建人ID", example = "user001")
    private String createUserId;

    @ApiModelProperty(value = "更新人ID", example = "user001")
    private String updateUserId;

    public String getProcessCode() {
        return processCode;
    }

    public void setProcessCode(String processCode) {
        this.processCode = processCode;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getProcessType() {
        return processType;
    }

    public void setProcessType(String processType) {
        this.processType = processType;
    }

    public String getProcessConfig() {
        return processConfig;
    }

    public void setProcessConfig(String processConfig) {
        this.processConfig = processConfig;
    }

    public Integer getSignOrder() {
        return signOrder;
    }

    public void setSignOrder(Integer signOrder) {
        this.signOrder = signOrder;
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
}
