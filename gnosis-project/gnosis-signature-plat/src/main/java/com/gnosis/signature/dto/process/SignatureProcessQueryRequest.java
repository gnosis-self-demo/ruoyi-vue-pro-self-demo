package com.gnosis.signature.dto.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * 签章流程查询请求 DTO
 */
@ApiModel(value = "SignatureProcessQueryRequest", description = "签章流程查询请求")
public class SignatureProcessQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "流程编码", example = "PROCESS_001")
    private String processCode;

    @ApiModelProperty(value = "流程名称", example = "合同会签流程")
    private String processName;

    @ApiModelProperty(value = "流程类型(会签/顺序签/并行签)", example = "会签")
    private String processType;

    @ApiModelProperty(value = "状态(0-禁用 1-启用)", example = "1")
    private Integer status;

    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum;

    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer pageSize;

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
