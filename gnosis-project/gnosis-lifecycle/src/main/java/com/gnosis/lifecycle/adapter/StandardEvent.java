package com.gnosis.lifecycle.adapter;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 标准事件对象
 * 统一的事件数据结构
 */
public class StandardEvent implements Serializable {

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
     * 事件类型
     */
    private String eventType;

    /**
     * 业务实例 ID
     */
    private String businessInstanceId;

    /**
     * 当前状态编码
     */
    private String currentStateCode;

    /**
     * 事件数据
     */
    private Map<String, Object> eventData;

    /**
     * 扩展元数据（包含 action, node_id 等）
     */
    private Map<String, Object> metadata;

    /**
     * 操作人 ID
     */
    private String operatorId;

    /**
     * 备注信息
     */
    private String remark;

    /**
     * 事件发生时间
     */
    private Date eventTime;

    public StandardEvent() {
        this.eventTime = new Date();
    }

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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getBusinessInstanceId() {
        return businessInstanceId;
    }

    public void setBusinessInstanceId(String businessInstanceId) {
        this.businessInstanceId = businessInstanceId;
    }

    public String getCurrentStateCode() {
        return currentStateCode;
    }

    public void setCurrentStateCode(String currentStateCode) {
        this.currentStateCode = currentStateCode;
    }

    public Map<String, Object> getEventData() {
        return eventData;
    }

    public void setEventData(Map<String, Object> eventData) {
        this.eventData = eventData;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
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

    public Date getEventTime() {
        return eventTime;
    }

    public void setEventTime(Date eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * 获取元数据中的指定字段
     */
    public Object getMetadataValue(String key) {
        return metadata != null ? metadata.get(key) : null;
    }

    /**
     * 获取事件数据中的指定字段
     */
    public Object getEventDataValue(String key) {
        return eventData != null ? eventData.get(key) : null;
    }
}
