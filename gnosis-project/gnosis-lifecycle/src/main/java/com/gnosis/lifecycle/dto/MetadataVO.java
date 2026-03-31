package com.gnosis.lifecycle.dto;

import java.io.Serializable;

/**
 * 元数据 VO
 */
public class MetadataVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    private String id;
    
    /**
     * 业务类型 ID
     */
    private String businessTypeId;
    
    /**
     * 字段名称
     */
    private String fieldName;
    
    /**
     * 字段标签
     */
    private String fieldLabel;
    
    /**
     * 字段类型：STRING/NUMBER/BOOLEAN/DATE/ENUM
     */
    private String fieldType;
    
    /**
     * 字段长度
     */
    private Integer fieldLength;
    
    /**
     * 是否必填：0-否，1-是
     */
    private Integer isRequired;
    
    /**
     * 是否可搜索：0-否，1-是
     */
    private Integer isSearchable;
    
    /**
     * 是否展示：0-否，1-是
     */
    private Integer isDisplay;
    
    /**
     * 展示顺序
     */
    private Integer displayOrder;
    
    /**
     * 枚举值列表（JSON）
     */
    private String enumValues;
    
    /**
     * 校验规则（正则表达式）
     */
    private String validationRule;
    
    /**
     * 默认值
     */
    private String defaultValue;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建人 ID
     */
    private String createUserId;
    
    /**
     * 更新人 ID
     */
    private String updateUserId;
    
    /**
     * 创建时间
     */
    private java.util.Date createTime;
    
    /**
     * 更新时间
     */
    private java.util.Date updateTime;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getBusinessTypeId() {
        return businessTypeId;
    }
    
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
    
    public String getFieldLabel() {
        return fieldLabel;
    }
    
    public void setFieldLabel(String fieldLabel) {
        this.fieldLabel = fieldLabel;
    }
    
    public String getFieldType() {
        return fieldType;
    }
    
    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }
    
    public Integer getFieldLength() {
        return fieldLength;
    }
    
    public void setFieldLength(Integer fieldLength) {
        this.fieldLength = fieldLength;
    }
    
    public Integer getIsRequired() {
        return isRequired;
    }
    
    public void setIsRequired(Integer isRequired) {
        this.isRequired = isRequired;
    }
    
    public Integer getIsSearchable() {
        return isSearchable;
    }
    
    public void setIsSearchable(Integer isSearchable) {
        this.isSearchable = isSearchable;
    }
    
    public Integer getIsDisplay() {
        return isDisplay;
    }
    
    public void setIsDisplay(Integer isDisplay) {
        this.isDisplay = isDisplay;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public String getEnumValues() {
        return enumValues;
    }
    
    public void setEnumValues(String enumValues) {
        this.enumValues = enumValues;
    }
    
    public String getValidationRule() {
        return validationRule;
    }
    
    public void setValidationRule(String validationRule) {
        this.validationRule = validationRule;
    }
    
    public String getDefaultValue() {
        return defaultValue;
    }
    
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
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
    
    public java.util.Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }
    
    public java.util.Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(java.util.Date updateTime) {
        this.updateTime = updateTime;
    }
}
