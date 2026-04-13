package com.gnosis.paramcheck.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class ValidationFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    private String flowId;
    private String flowName;
    private String modeType;
    private String elExpression;
    private String handlerCode;
    private String componentConfig;
    private String businessType;
    private Boolean isActive;
    private Integer version;
    private String createUserId;
    private String updateUserId;
    private Date createTime;
    private Date updateTime;

    private transient Map<String, Object> componentConfigMap;

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

    public String getComponentConfig() {
        return componentConfig;
    }

    public void setComponentConfig(String componentConfig) {
        this.componentConfig = componentConfig;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
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

    public Map<String, Object> getComponentConfigMap() {
        return componentConfigMap;
    }

    public void setComponentConfigMap(Map<String, Object> componentConfigMap) {
        this.componentConfigMap = componentConfigMap;
    }
}
