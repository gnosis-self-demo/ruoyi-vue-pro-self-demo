package lifecycle.domain;

import lifecycle.domain.base.BaseEntity;

/**
 * 事件记录实体
 * 对应表：lifecycle_event
 */
public class LifecycleEvent extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 业务类型 ID
     */
    private String businessTypeId;
    
    /**
     * 业务 ID
     */
    private String businessId;
    
    /**
     * 事件类型
     */
    private String eventType;
    
    /**
     * 事件数据 (JSON)
     */
    private String eventData;
    
    /**
     * 元数据快照 (JSON)
     */
    private String metadataSnapshot;
    
    /**
     * 来源系统
     */
    private String sourceSystem;
    
    /**
     * 状态 (PENDING/PROCESSING/SUCCESS/FAILED)
     */
    private String status;
    
    /**
     * 业务实例 ID
     */
    private String businessInstanceId;
    
    /**
     * 元数据
     */
    private String metadata;
    
    /**
     * 操作人 ID
     */
    private String operatorId;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 处理状态
     */
    private String processStatus;
    
    public String getBusinessTypeId() {
        return businessTypeId;
    }
    
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
    
    public String getBusinessId() {
        return businessId;
    }
    
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
    
    public String getEventType() {
        return eventType;
    }
    
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    
    public String getEventData() {
        return eventData;
    }
    
    public void setEventData(String eventData) {
        this.eventData = eventData;
    }
    
    public String getMetadataSnapshot() {
        return metadataSnapshot;
    }
    
    public void setMetadataSnapshot(String metadataSnapshot) {
        this.metadataSnapshot = metadataSnapshot;
    }
    
    public String getSourceSystem() {
        return sourceSystem;
    }
    
    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getBusinessInstanceId() {
        return businessInstanceId;
    }
    
    public void setBusinessInstanceId(String businessInstanceId) {
        this.businessInstanceId = businessInstanceId;
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }
    
    public String getOperatorId() {
        return operatorId;
    }
    
    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
    
    public String getProcessStatus() {
        return processStatus;
    }
    
    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }
}
