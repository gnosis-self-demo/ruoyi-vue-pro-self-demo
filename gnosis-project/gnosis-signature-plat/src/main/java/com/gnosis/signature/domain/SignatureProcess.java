package com.gnosis.signature.domain;

import com.gnosis.common.domain.BaseEntity;

/**
 * 签章流程实体
 */
public class SignatureProcess extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 流程编码 */
    private String processCode;

    /** 流程名称 */
    private String processName;

    /** 流程类型(会签/顺序签/并行签) */
    private String processType;

    /** 流程配置(JSON) */
    private String processConfig;

    /** 签署顺序 */
    private Integer signOrder;

    /** 描述 */
    private String description;

    /** 状态(0-禁用 1-启用) */
    private Integer status;

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
}
