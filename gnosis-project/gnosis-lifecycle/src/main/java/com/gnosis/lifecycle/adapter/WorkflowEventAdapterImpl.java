package com.gnosis.lifecycle.adapter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 工作流事件适配器实现类
 */
@Component
public class WorkflowEventAdapterImpl implements WorkflowEventAdapter {

    private static final Logger log = LoggerFactory.getLogger(WorkflowEventAdapterImpl.class);

    /**
     * 支持的元数据字段定义
     */
    private static final Map<String, EventMetadataDictionary.MetadataField> METADATA_FIELDS = new HashMap<>();

    static {
        // 初始化元数据字段定义
        addMetadataField("action", "String", true, "动作类型，如 CREATE, UPDATE, DELETE, APPROVE, REJECT 等", "CREATE");
        addMetadataField("node_id", "String", true, "节点 ID，工作流中的当前节点标识", "node_123");
        addMetadataField("node_type", "String", false, "节点类型，如 START, TASK, GATEWAY, END 等", "TASK");
        addMetadataField("priority", "Integer", false, "优先级，数字越大优先级越高", "10");
        addMetadataField("source", "String", false, "事件来源，如 WEB, APP, API 等", "WEB");
        addMetadataField("tenant_id", "String", false, "租户 ID，多租户场景使用", "tenant_001");
        addMetadataField("operator_type", "String", false, "操作人类型，如 USER, ADMIN, SYSTEM 等", "USER");
    }

    private static void addMetadataField(String name, String type, boolean required, String description, String example) {
        EventMetadataDictionary.MetadataField field = new EventMetadataDictionary.MetadataField();
        field.setName(name);
        field.setType(type);
        field.setRequired(required);
        field.setDescription(description);
        field.setExample(example);
        METADATA_FIELDS.put(name, field);
    }

    @Override
    public StandardEvent adaptToStandardEvent(Object sourceEvent) {
        log.debug("[WorkflowEventAdapter] 转换原始事件为标准事件");

        if (sourceEvent instanceof StandardEvent) {
            return (StandardEvent) sourceEvent;
        }

        if (sourceEvent instanceof Map) {
            return convertMapToStandardEvent((Map<String, Object>) sourceEvent);
        }

        // 其他类型的事件，可以通过反射等方式转换
        // 这里简化处理，抛出异常
        throw new IllegalArgumentException("不支持的事件类型：" + sourceEvent.getClass().getName());
    }

    @Override
    public <T> T adaptFromStandardEvent(StandardEvent standardEvent, Class<T> targetType) {
        log.debug("[WorkflowEventAdapter] 将标准事件转换为目标类型：{}", targetType.getName());

        if (targetType == Map.class) {
            return targetType.cast(convertStandardEventToMap(standardEvent));
        }

        if (targetType == StandardEvent.class) {
            return targetType.cast(standardEvent);
        }

        // 其他类型，可以通过反射等方式转换
        throw new IllegalArgumentException("不支持的目标类型：" + targetType.getName());
    }

    @Override
    public boolean validateEvent(StandardEvent standardEvent) {
        log.debug("[WorkflowEventAdapter] 验证事件有效性");

        if (standardEvent == null) {
            log.warn("[WorkflowEventAdapter] 事件为空");
            return false;
        }

        // 校验必填字段
        if (standardEvent.getBusinessTypeId() == null || standardEvent.getBusinessTypeId().trim().isEmpty()) {
            log.warn("[WorkflowEventAdapter] 业务类型 ID 为空");
            return false;
        }

        if (standardEvent.getEventType() == null || standardEvent.getEventType().trim().isEmpty()) {
            log.warn("[WorkflowEventAdapter] 事件类型为空");
            return false;
        }

        if (standardEvent.getBusinessInstanceId() == null || standardEvent.getBusinessInstanceId().trim().isEmpty()) {
            log.warn("[WorkflowEventAdapter] 业务实例 ID 为空");
            return false;
        }

        // 校验元数据必填字段
        Map<String, Object> metadata = standardEvent.getMetadata();
        if (metadata != null) {
            for (EventMetadataDictionary.MetadataField field : METADATA_FIELDS.values()) {
                if (field.isRequired() && !metadata.containsKey(field.getName())) {
                    log.warn("[WorkflowEventAdapter] 缺少必填元数据字段：{}", field.getName());
                    return false;
                }
            }
        } else {
            // 检查是否有必填字段
            for (EventMetadataDictionary.MetadataField field : METADATA_FIELDS.values()) {
                if (field.isRequired()) {
                    log.warn("[WorkflowEventAdapter] 缺少必填元数据字段：{}", field.getName());
                    return false;
                }
            }
        }

        log.debug("[WorkflowEventAdapter] 事件验证通过");
        return true;
    }

    @Override
    public EventMetadataDictionary getMetadataDictionary() {
        EventMetadataDictionary dictionary = new EventMetadataDictionary();
        dictionary.setFields(METADATA_FIELDS.values().toArray(new EventMetadataDictionary.MetadataField[0]));
        return dictionary;
    }

    /**
     * 将 Map 转换为标准事件
     */
    private StandardEvent convertMapToStandardEvent(Map<String, Object> map) {
        StandardEvent event = new StandardEvent();

        event.setEventId(getStringValue(map, "eventId"));
        event.setBusinessTypeId(getStringValue(map, "businessTypeId"));
        event.setEventType(getStringValue(map, "eventType"));
        event.setBusinessInstanceId(getStringValue(map, "businessInstanceId"));
        event.setCurrentStateCode(getStringValue(map, "currentStateCode"));
        event.setOperatorId(getStringValue(map, "operatorId"));
        event.setRemark(getStringValue(map, "remark"));

        // 处理 eventData
        Object eventDataObj = map.get("eventData");
        if (eventDataObj instanceof Map) {
            event.setEventData((Map<String, Object>) eventDataObj);
        }

        // 处理 metadata
        Object metadataObj = map.get("metadata");
        if (metadataObj instanceof Map) {
            event.setMetadata((Map<String, Object>) metadataObj);
        }

        // 处理 eventTime
        Object eventTimeObj = map.get("eventTime");
        if (eventTimeObj instanceof Date) {
            event.setEventTime((Date) eventTimeObj);
        } else if (eventTimeObj instanceof String) {
            try {
                // 尝试解析字符串时间
                event.setEventTime(new Date()); // 简化处理
            } catch (Exception e) {
                event.setEventTime(new Date());
            }
        } else {
            event.setEventTime(new Date());
        }

        return event;
    }

    /**
     * 将标准事件转换为 Map
     */
    private Map<String, Object> convertStandardEventToMap(StandardEvent standardEvent) {
        Map<String, Object> map = new HashMap<>();

        map.put("eventId", standardEvent.getEventId());
        map.put("businessTypeId", standardEvent.getBusinessTypeId());
        map.put("eventType", standardEvent.getEventType());
        map.put("businessInstanceId", standardEvent.getBusinessInstanceId());
        map.put("currentStateCode", standardEvent.getCurrentStateCode());
        map.put("operatorId", standardEvent.getOperatorId());
        map.put("remark", standardEvent.getRemark());
        map.put("eventData", standardEvent.getEventData());
        map.put("metadata", standardEvent.getMetadata());
        map.put("eventTime", standardEvent.getEventTime());

        return map;
    }

    /**
     * 从 Map 中获取字符串值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
