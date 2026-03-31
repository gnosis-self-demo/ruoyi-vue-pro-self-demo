package com.gnosis.lifecycle.domain;

import com.gnosis.lifecycle.domain.base.BaseEntity;

/**
 * 终态配置实体类
 */
public class LifecycleFinalStateConfig extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 业务类型 ID
     */
    private String businessTypeId;
    
    /**
     * 状态 ID
     */
    private String stateId;
    
    /**
     * 状态编码
     */
    private String stateCode;
    
    /**
     * 状态名称
     */
    private String stateName;
    
    /**
     * 终态动作类型：ARCHIVE/CANCEL/COMPLETE
     */
    private String finalActionType;
    
    /**
     * 终态动作配置（JSON）
     */
    private String finalActionConfig;
    
    /**
     * 是否允许重新激活：0-否，1-是
     */
    private Integer allowReactivate;
    
    /**
     * 备注
     */
    private String remark;
    
    public String getBusinessTypeId() {
        return businessTypeId;
    }
    
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
    
    public String getStateId() {
        return stateId;
    }
    
    public void setStateId(String stateId) {
        this.stateId = stateId;
    }
    
    public String getStateCode() {
        return stateCode;
    }
    
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }
    
    public String getStateName() {
        return stateName;
    }
    
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
    
    public String getFinalActionType() {
        return finalActionType;
    }
    
    public void setFinalActionType(String finalActionType) {
        this.finalActionType = finalActionType;
    }
    
    public String getFinalActionConfig() {
        return finalActionConfig;
    }
    
    public void setFinalActionConfig(String finalActionConfig) {
        this.finalActionConfig = finalActionConfig;
    }
    
    public Integer getAllowReactivate() {
        return allowReactivate;
    }
    
    public void setAllowReactivate(Integer allowReactivate) {
        this.allowReactivate = allowReactivate;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
