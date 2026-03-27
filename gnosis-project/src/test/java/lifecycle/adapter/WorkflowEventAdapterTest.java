package lifecycle.adapter;

import lifecycle.adapter.StandardEvent;
import lifecycle.adapter.WorkflowEventAdapter;
import lifecycle.adapter.WorkflowEventAdapterImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import lifecycle.dto.RouteTestResultVO;

import java.util.*;

import static org.junit.Assert.*;

/**
 * 工作流事件适配器测试类
 */
@RunWith(MockitoJUnitRunner.class)
public class WorkflowEventAdapterTest {

    @InjectMocks
    private WorkflowEventAdapterImpl workflowEventAdapter;

    @Before
    public void setUp() {
        // 无需特殊初始化
    }

    /**
     * 测试工作流事件适配
     */
    @Test
    public void testAdaptWorkflowEvent() {
        // 准备测试数据 - Map 类型事件
        Map<String, Object> sourceEvent = new HashMap<>();
        sourceEvent.put("eventId", "test_event_123");
        sourceEvent.put("businessTypeId", "test_biz_type");
        sourceEvent.put("eventType", "CREATE_ORDER");
        sourceEvent.put("businessInstanceId", "order_456");
        sourceEvent.put("currentStateCode", "PENDING");
        sourceEvent.put("operatorId", "user_789");
        sourceEvent.put("remark", "测试事件");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", "ORDER_001");
        eventData.put("amount", 1000);
        sourceEvent.put("eventData", eventData);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", "CREATE");
        metadata.put("node_id", "node_001");
        sourceEvent.put("metadata", metadata);

        // 执行测试
        StandardEvent standardEvent = workflowEventAdapter.adaptToStandardEvent(sourceEvent);

        // 验证结果
        assertNotNull("转换后的事件不应为空", standardEvent);
        assertEquals("事件 ID 应该匹配", "test_event_123", standardEvent.getEventId());
        assertEquals("业务类型 ID 应该匹配", "test_biz_type", standardEvent.getBusinessTypeId());
        assertEquals("事件类型应该匹配", "CREATE_ORDER", standardEvent.getEventType());
        assertEquals("业务实例 ID 应该匹配", "order_456", standardEvent.getBusinessInstanceId());
        assertEquals("当前状态应该匹配", "PENDING", standardEvent.getCurrentStateCode());
        assertEquals("操作人 ID 应该匹配", "user_789", standardEvent.getOperatorId());
        assertEquals("备注应该匹配", "测试事件", standardEvent.getRemark());

        // 验证事件数据
        assertNotNull("事件数据不应为空", standardEvent.getEventData());
        assertEquals("订单 ID 应该匹配", "ORDER_001", standardEvent.getEventData().get("orderId"));
        assertEquals("金额应该匹配", 1000, standardEvent.getEventData().get("amount"));

        // 验证元数据
        assertNotNull("元数据不应为空", standardEvent.getMetadata());
        assertEquals("动作应该匹配", "CREATE", standardEvent.getMetadata().get("action"));
        assertEquals("节点 ID 应该匹配", "node_001", standardEvent.getMetadata().get("node_id"));
    }

    /**
     * 测试元数据提取
     */
    @Test
    public void testExtractMetadata() {
        // 准备测试数据
        StandardEvent event = new StandardEvent();
        event.setEventId("test_event");
        event.setBusinessTypeId("test_biz_type");
        event.setEventType("TEST_EVENT");
        event.setBusinessInstanceId("test_instance");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", "UPDATE");
        metadata.put("node_id", "node_002");
        metadata.put("node_type", "TASK");
        metadata.put("priority", 10);
        metadata.put("source", "WEB");
        metadata.put("tenant_id", "tenant_001");
        metadata.put("operator_type", "ADMIN");
        event.setMetadata(metadata);

        // 执行测试 - 获取元数据字典
        WorkflowEventAdapter.EventMetadataDictionary dictionary = workflowEventAdapter.getMetadataDictionary();

        // 验证结果
        assertNotNull("元数据字典不应为空", dictionary);
        assertNotNull("字段列表不应为空", dictionary.getFields());
        assertTrue("字段列表应该包含多个字段", dictionary.getFields().length > 0);

        // 验证必填字段
        boolean hasActionField = false;
        for (EventMetadataDictionary.MetadataField field : dictionary.getFields()) {
            if ("action".equals(field.getName())) {
                hasActionField = true;
                assertTrue("action 字段应该是必填的", field.isRequired());
                assertEquals("action 字段类型应该是 String", "String", field.getType());
                assertNotNull("action 字段应该有描述", field.getDescription());
            }
        }
        assertTrue("应该包含 action 字段", hasActionField);
    }

    /**
     * 测试标准事件格式
     */
    @Test
    public void testStandardEventFormat() {
        // 准备测试数据
        Map<String, Object> sourceMap = new HashMap<>();
        sourceMap.put("eventId", "formatted_event");
        sourceMap.put("businessTypeId", "biz_type_001");
        sourceMap.put("eventType", "FORMAT_TEST");
        sourceMap.put("businessInstanceId", "instance_001");
        sourceMap.put("currentStateCode", "INIT");
        sourceMap.put("operatorId", "operator_001");
        sourceMap.put("remark", "格式测试");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("field1", "value1");
        eventData.put("field2", 123);
        sourceMap.put("eventData", eventData);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", "CREATE");
        metadata.put("node_id", "node_start");
        sourceMap.put("metadata", metadata);

        // 执行测试
        StandardEvent standardEvent = workflowEventAdapter.adaptToStandardEvent(sourceMap);

        // 验证标准事件格式
        assertNotNull("事件不应为空", standardEvent);
        assertNotNull("事件 ID 不应为空", standardEvent.getEventId());
        assertNotNull("业务类型 ID 不应为空", standardEvent.getBusinessTypeId());
        assertNotNull("事件类型不应为空", standardEvent.getEventType());
        assertNotNull("业务实例 ID 不应为空", standardEvent.getBusinessInstanceId());
        assertNotNull("事件时间不应为空", standardEvent.getEventTime());
        assertTrue("事件时间应该是当前时间附近", 
                Math.abs(System.currentTimeMillis() - standardEvent.getEventTime().getTime()) < 10000);

        // 验证事件可以转换回 Map
        Map<String, Object> resultMap = workflowEventAdapter.adaptFromStandardEvent(standardEvent, Map.class);
        assertNotNull("转换后的 Map 不应为空", resultMap);
        assertEquals("事件 ID 应该匹配", standardEvent.getEventId(), resultMap.get("eventId"));
        assertEquals("业务类型 ID 应该匹配", standardEvent.getBusinessTypeId(), resultMap.get("businessTypeId"));
        assertEquals("事件类型应该匹配", standardEvent.getEventType(), resultMap.get("eventType"));
    }

    /**
     * 测试事件验证 - 有效事件
     */
    @Test
    public void testValidateEvent_Valid() {
        // 准备测试数据
        StandardEvent event = new StandardEvent();
        event.setBusinessTypeId("test_biz_type");
        event.setEventType("TEST_EVENT");
        event.setBusinessInstanceId("test_instance");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", "CREATE");
        event.setMetadata(metadata);

        // 执行测试
        boolean isValid = workflowEventAdapter.validateEvent(event);

        // 验证结果
        assertTrue("事件应该有效", isValid);
    }

    /**
     * 测试事件验证 - 缺少必填字段
     */
    @Test
    public void testValidateEvent_MissingRequiredFields() {
        // 准备测试数据 - 缺少业务类型 ID
        StandardEvent event = new StandardEvent();
        event.setBusinessTypeId(null); // 缺少必填字段
        event.setEventType("TEST_EVENT");
        event.setBusinessInstanceId("test_instance");

        // 执行测试
        boolean isValid = workflowEventAdapter.validateEvent(event);

        // 验证结果
        assertFalse("事件应该无效", isValid);
    }

    /**
     * 测试事件验证 - 空事件
     */
    @Test
    public void testValidateEvent_NullEvent() {
        // 执行测试
        boolean isValid = workflowEventAdapter.validateEvent(null);

        // 验证结果
        assertFalse("空事件应该无效", isValid);
    }

    /**
     * 测试 StandardEvent 转换为 StandardEvent（直接返回）
     */
    @Test
    public void testAdaptStandardEventToStandardEvent() {
        // 准备测试数据
        StandardEvent originalEvent = new StandardEvent();
        originalEvent.setEventId("direct_event");
        originalEvent.setBusinessTypeId("biz_type");
        originalEvent.setEventType("DIRECT_TEST");

        // 执行测试
        StandardEvent resultEvent = workflowEventAdapter.adaptToStandardEvent(originalEvent);

        // 验证结果 - 应该返回同一个对象
        assertSame("应该返回同一个对象", originalEvent, resultEvent);
    }

    /**
     * 测试不支持的事件类型
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAdaptUnsupportedEventType() {
        // 准备测试数据 - 不支持的类型
        String unsupportedEvent = "unsupported_event_string";

        // 执行测试 - 应该抛出异常
        workflowEventAdapter.adaptToStandardEvent(unsupportedEvent);
    }

    /**
     * 测试不支持的目标类型
     */
    @Test(expected = IllegalArgumentException.class)
    public void testAdaptFromStandardEventUnsupportedType() {
        // 准备测试数据
        StandardEvent event = new StandardEvent();
        event.setEventId("test_event");

        // 执行测试 - 应该抛出异常
        workflowEventAdapter.adaptFromStandardEvent(event, RouteTestResultVO.class);
    }

    /**
     * 测试元数据字典完整性
     */
    @Test
    public void testMetadataDictionaryCompleteness() {
        // 执行测试
        WorkflowEventAdapter.EventMetadataDictionary dictionary = workflowEventAdapter.getMetadataDictionary();

        // 验证结果
        assertNotNull("字典不应为空", dictionary);
        assertNotNull("字段数组不应为空", dictionary.getFields());

        // 验证所有预定义的字段都存在
        Map<String, EventMetadataDictionary.MetadataField> fieldMap = new HashMap<>();
        for (EventMetadataDictionary.MetadataField field : dictionary.getFields()) {
            fieldMap.put(field.getName(), field);
        }

        // 验证关键字段存在
        assertTrue("应该包含 action 字段", fieldMap.containsKey("action"));
        assertTrue("应该包含 node_id 字段", fieldMap.containsKey("node_id"));
        assertTrue("应该包含 priority 字段", fieldMap.containsKey("priority"));
        assertTrue("应该包含 source 字段", fieldMap.containsKey("source"));

        // 验证字段属性
        EventMetadataDictionary.MetadataField actionField = fieldMap.get("action");
        assertTrue("action 字段应该是必填的", actionField.isRequired());
        assertEquals("action 字段类型应该是 String", "String", actionField.getType());

        EventMetadataDictionary.MetadataField priorityField = fieldMap.get("priority");
        assertFalse("priority 字段应该是可选的", priorityField.isRequired());
        assertEquals("priority 字段类型应该是 Integer", "Integer", priorityField.getType());
    }
}
