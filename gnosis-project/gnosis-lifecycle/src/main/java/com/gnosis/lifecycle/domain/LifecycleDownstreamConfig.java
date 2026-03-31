package com.gnosis.lifecycle.domain;

import com.gnosis.lifecycle.domain.base.BaseEntity;

/**
 * 下游配置实体
 * 对应表：lifecycle_downstream_config
 */
public class LifecycleDownstreamConfig extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 终态 ID
     */
    private String finalStateId;
    
    /**
     * 下游业务类型
     */
    private String downstreamBusinessType;
    
    /**
     * 触发条件
     */
    private String triggerCondition;
    
    /**
     * 触发模式 (AUTO/MANUAL)
     */
    private String triggerMode;
    
    /**
     * 配置数据 (JSON)
     */
    private String configData;
    
    /**
     * 是否启用 (0-否 1-是)
     */
    private Integer isActive;
    
    public String getFinalStateId() {
        return finalStateId;
    }
    
    public void setFinalStateId(String finalStateId) {
        this.finalStateId = finalStateId;
    }
    
    public String getDownstreamBusinessType() {
        return downstreamBusinessType;
    }
    
    public void setDownstreamBusinessType(String downstreamBusinessType) {
        this.downstreamBusinessType = downstreamBusinessType;
    }
    
    public String getTriggerCondition() {
        return triggerCondition;
    }
    
    public void setTriggerCondition(String triggerCondition) {
        this.triggerCondition = triggerCondition;
    }
    
    public String getTriggerMode() {
        return triggerMode;
    }
    
    public void setTriggerMode(String triggerMode) {
        this.triggerMode = triggerMode;
    }
    
    public String getConfigData() {
        return configData;
    }
    
    public void setConfigData(String configData) {
        this.configData = configData;
    }
    
    public Integer getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }
}
