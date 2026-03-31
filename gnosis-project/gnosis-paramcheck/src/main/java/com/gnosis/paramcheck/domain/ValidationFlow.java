package com.gnosis.paramcheck.domain;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 校验流程配置实体 (映射 sys_validation_flows)
 */
public class ValidationFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 流程标识 */
    private String flowId;

    /** 流程名称 */
    private String flowName;

    /** 模式类型: FLOW / HANDLER / HYBRID */
    private String modeType;

    /** LiteFlow EL 表达式 (FLOW/HYBRID 必填) */
    private String elExpression;

    /** 自定义处理器编码 (HANDLER/HYBRID 必填) */
    private String handlerCode;

    /** 组件配置 (JSONPath/正则/SQL 等) */
    private Map<String, Object> componentConfig;

    /** 业务类型（多个，逗号分隔） */
    private String businessTypes;

    /** 是否激活 */
    private Boolean isActive;

    /** 版本号 */
    private Integer version;

    /** 创建人ID */
    private String createUserId;

    /** 更新人ID */
    private String updateUserId;

    /** 创建时间 */
    private OffsetDateTime createTime;

    /** 更新时间 */
    private OffsetDateTime updatedTime;

    // Getter and Setter methods
    public String getFlowId() {
        return flowId;
    }

    public void setFlowId(String flowId) {
        this.flowId = flowId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getModeType() {
        return modeType;
    }

    public void setModeType(String modeType) {
        this.modeType = modeType;
    }

    public String getElExpression() {
        return elExpression;
    }

    public void setElExpression(String elExpression) {
        this.elExpression = elExpression;
    }

    public String getHandlerCode() {
        return handlerCode;
    }

    public void setHandlerCode(String handlerCode) {
        this.handlerCode = handlerCode;
    }

    public Map<String, Object> getComponentConfig() {
        return componentConfig;
    }

    public void setComponentConfig(Map<String, Object> componentConfig) {
        this.componentConfig = componentConfig;
    }

    public String getBusinessTypes() {
        return businessTypes;
    }

    public void setBusinessTypes(String businessTypes) {
        this.businessTypes = businessTypes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public OffsetDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(OffsetDateTime createTime) {
        this.createTime = createTime;
    }

    public OffsetDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(OffsetDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
