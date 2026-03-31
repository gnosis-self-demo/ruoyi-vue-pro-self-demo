package com.gnosis.lifecycle.domain;

import com.gnosis.lifecycle.domain.base.BaseEntity;

/**
 * 状态节点实体
 * 对应表：lifecycle_state
 */
public class LifecycleState extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 业务类型 ID
     */
    private String businessTypeId;
    
    /**
     * 状态名称
     */
    private String stateName;
    
    /**
     * 状态编码
     */
    private String stateCode;
    
    /**
     * 状态类型 (initial/normal/final)
     */
    private String stateType;
    
    /**
     * 是否终态 (0-否 1-是)
     */
    private Integer isFinalState;
    
    /**
     * 位置 X 坐标
     */
    private Integer positionX;
    
    /**
     * 位置 Y 坐标
     */
    private Integer positionY;
    
    /**
     * 元数据 (JSON)
     */
    private String metadata;
    
    public String getBusinessTypeId() {
        return businessTypeId;
    }
    
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
    
    public String getStateName() {
        return stateName;
    }
    
    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
    
    public String getStateCode() {
        return stateCode;
    }
    
    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }
    
    public String getStateType() {
        return stateType;
    }
    
    public void setStateType(String stateType) {
        this.stateType = stateType;
    }
    
    public Integer getIsFinalState() {
        return isFinalState;
    }
    
    public void setIsFinalState(Integer isFinalState) {
        this.isFinalState = isFinalState;
    }
    
    public Integer getPositionX() {
        return positionX;
    }
    
    public void setPositionX(Integer positionX) {
        this.positionX = positionX;
    }
    
    public Integer getPositionY() {
        return positionY;
    }
    
    public void setPositionY(Integer positionY) {
        this.positionY = positionY;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
}
