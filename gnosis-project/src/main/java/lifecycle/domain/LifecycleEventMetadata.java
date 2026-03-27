package lifecycle.domain;

import lifecycle.domain.base.BaseEntity;

/**
 * 事件元数据实体
 * 对应表：lifecycle_event_metadata
 */
public class LifecycleEventMetadata extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 事件类型
     */
    private String eventType;
    
    /**
     * 元数据键
     */
    private String metadataKey;
    
    /**
     * 元数据类型 (string/number/boolean/array/object)
     */
    private String metadataType;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 允许的值 (JSON 数组)
     */
    private String allowedValues;
    
    /**
     * 是否必填 (0-否 1-是)
     */
    private Integer isRequired;
    
    /**
     * 排序
     */
    private Integer sortOrder;
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getMetadataKey() {
        return metadataKey;
    }
    
    public void setMetadataKey(String metadataKey) {
        this.metadataKey = metadataKey;
    }
    
    public String getMetadataType() {
        return metadataType;
    }
    
    public void setMetadataType(String metadataType) {
        this.metadataType = metadataType;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAllowedValues() {
        return allowedValues;
    }
    
    public void setAllowedValues(String allowedValues) {
        this.allowedValues = allowedValues;
    }
    
    public Integer getIsRequired() {
        return isRequired;
    }
    
    public void setIsRequired(Integer isRequired) {
        this.isRequired = isRequired;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
