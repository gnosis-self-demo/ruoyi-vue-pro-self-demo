package lifecycle.dto;

import java.io.Serializable;
import java.util.Map;

/**
 * 事件发布请求 DTO
 */
public class EventPublishRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 事件 ID
     */
    private String eventId;
    
    /**
     * 业务类型 ID
     */
    private String businessTypeId;
    
    /**
     * 业务实例 ID
     */
    private String businessInstanceId;
    
    /**
     * 发布类型：SYNC/ASYNC
     */
    private String publishType;
    
    /**
     * 目标类型：DOWNSTREAM/CUSTOM
     */
    private String targetType;
    
    /**
     * 目标配置（JSON）
     */
    private Map<String, Object> targetConfig;
    
    /**
     * 消息内容
     */
    private Map<String, Object> payload;
    
    /**
     * 备注
     */
    private String remark;
    
    public String getEventId() {
        return eventId;
    }
    
    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
    
    public String getBusinessTypeId() {
        return businessTypeId;
    }
    
    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }
    
    public String getBusinessInstanceId() {
        return businessInstanceId;
    }
    
    public void setBusinessInstanceId(String businessInstanceId) {
        this.businessInstanceId = businessInstanceId;
    }
    
    public String getPublishType() {
        return publishType;
    }
    
    public void setPublishType(String publishType) {
        this.publishType = publishType;
    }
    
    public String getTargetType() {
        return targetType;
    }
    
    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }
    
    public Map<String, Object> getTargetConfig() {
        return targetConfig;
    }
    
    public void setTargetConfig(Map<String, Object> targetConfig) {
        this.targetConfig = targetConfig;
    }
    
    public Map<String, Object> getPayload() {
        return payload;
    }
    
    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
