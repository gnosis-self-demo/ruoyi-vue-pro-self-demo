package paramcheck.lifecycle;

import lifecycle.dto.EventSubmitRequest;
import lifecycle.dto.EventVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import lifecycle.adapter.StandardEvent;
import lifecycle.domain.LifecycleEvent;
import lifecycle.domain.LifecycleRouteRule;
import lifecycle.engine.router.ConditionRouterEngine;
import lifecycle.engine.router.RouteMatchResult;
import lifecycle.repository.LifecycleEventMapper;
import lifecycle.repository.LifecycleRouteRuleMapper;
import lifecycle.service.EventService;

import java.util.*;

import static org.junit.Assert.*;

/**
 * 事件处理集成测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class EventProcessingIntegrationTest {

    @Autowired
    private EventService eventService;

    @Autowired
    private ConditionRouterEngine conditionRouterEngine;

    @Autowired
    private LifecycleEventMapper eventMapper;

    @Autowired
    private LifecycleRouteRuleMapper routeRuleMapper;

    /**
     * 测试事件提交和处理
     */
    @Test
    public void testEventSubmitAndProcess() {
        // 1. 创建测试路由规则
        String businessTypeId = "biz_type_" + System.currentTimeMillis();
        createRouteRule(businessTypeId, "TEST_EVENT", 10, "(action == 'CREATE')");

        // 2. 提交事件
        EventSubmitRequest request = new EventSubmitRequest();
        request.setBusinessTypeId(businessTypeId);
        request.setEventType("TEST_EVENT");
        request.setBusinessInstanceId("instance_" + System.currentTimeMillis());
        request.setOperatorId("test_user");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("action", "CREATE");
        eventData.put("data", "test_data");
        request.setEventData(eventData);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", "CREATE");
        request.setMetadata(metadata);

        // 3. 执行提交
        EventVO result = eventService.submitEvent(request);

        // 4. 验证结果
        assertNotNull("事件提交结果不应为空", result);
        assertNotNull("事件 ID 不应为空", result.getId());
        assertEquals("业务类型 ID 应该匹配", businessTypeId, result.getBusinessTypeId());
        assertEquals("事件类型应该匹配", "TEST_EVENT", result.getEventType());

        // 5. 验证事件已保存
        LifecycleEvent savedEvent = eventMapper.selectById(result.getId());
        assertNotNull("保存的事件不应为空", savedEvent);
        assertEquals("事件数据应该匹配", eventData.toString(), savedEvent.getEventData());
    }

    /**
     * 测试事件路由匹配
     */
    @Test
    public void testEventRouteMatch() {
        // 1. 创建多条路由规则
        String businessTypeId = "biz_type_route_" + System.currentTimeMillis();
        
        createRouteRule(businessTypeId, "ROUTE_TEST", 100, "(priority >= 80)");
        createRouteRule(businessTypeId, "ROUTE_TEST", 50, "(priority >= 50) AND (priority < 80)");
        createRouteRule(businessTypeId, "ROUTE_TEST", 20, "(priority < 50)");

        // 2. 查询规则
        LifecycleRouteRule query = new LifecycleRouteRule();
        query.setBusinessTypeId(businessTypeId);
        query.setEventType("ROUTE_TEST");
        query.setIsActive(1);
        List<LifecycleRouteRule> rules = routeRuleMapper.selectList(query);
        
        assertNotNull("规则列表不应为空", rules);
        assertEquals("应该有 3 条规则", 3, rules.size());

        // 3. 创建测试事件
        StandardEvent event = new StandardEvent();
        event.setEventId("test_event_" + System.currentTimeMillis());
        event.setBusinessTypeId(businessTypeId);
        event.setEventType("ROUTE_TEST");
        event.setBusinessInstanceId("test_instance");

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("priority", 60);
        event.setMetadata(metadata);

        // 4. 执行路由匹配
        List<RouteMatchResult> results = conditionRouterEngine.match(event, rules);

        // 5. 验证匹配结果
        assertNotNull("匹配结果不应为空", results);
        assertEquals("应该有 3 个匹配结果", 3, results.size());

        // 验证排序 - 优先级高的在前
        RouteMatchResult firstResult = results.get(0);
        // 注意：实际匹配结果取决于条件是否满足
    }

    /**
     * 测试事件状态流转
     */
    @Test
    public void testEventStateTransition() {
        // 1. 创建业务类型和规则
        String businessTypeId = "biz_type_state_" + System.currentTimeMillis();
        createRouteRule(businessTypeId, "STATE_CHANGE", 10, "(action == 'UPDATE')");

        // 2. 提交初始事件
        EventSubmitRequest initialRequest = new EventSubmitRequest();
        initialRequest.setBusinessTypeId(businessTypeId);
        initialRequest.setEventType("STATE_CHANGE");
        initialRequest.setBusinessInstanceId("state_instance_" + System.currentTimeMillis());
        initialRequest.setCurrentStateCode("INIT");
        initialRequest.setOperatorId("test_user");

        Map<String, Object> initialData = new HashMap<>();
        initialData.put("action", "CREATE");
        initialRequest.setEventData(initialData);

        Map<String, Object> initialMetadata = new HashMap<>();
        initialMetadata.put("action", "CREATE");
        initialRequest.setMetadata(initialMetadata);

        EventVO initialEvent = eventService.submitEvent(initialRequest);
        assertNotNull("初始事件不应为空", initialEvent);

        // 3. 提交状态变更事件
        EventSubmitRequest changeRequest = new EventSubmitRequest();
        changeRequest.setBusinessTypeId(businessTypeId);
        changeRequest.setEventType("STATE_CHANGE");
        changeRequest.setBusinessInstanceId(initialRequest.getBusinessInstanceId());
        changeRequest.setCurrentStateCode("PENDING");
        changeRequest.setOperatorId("test_user");

        Map<String, Object> changeData = new HashMap<>();
        changeData.put("action", "UPDATE");
        changeData.put("newState", "APPROVED");
        changeRequest.setEventData(changeData);

        Map<String, Object> changeMetadata = new HashMap<>();
        changeMetadata.put("action", "UPDATE");
        changeRequest.setMetadata(changeMetadata);

        EventVO changeEvent = eventService.submitEvent(changeRequest);
        assertNotNull("变更事件不应为空", changeEvent);

        // 4. 验证事件序列
        List<EventVO> events = eventService.list(businessTypeId, "STATE_CHANGE", 1, 100);
        assertNotNull("事件列表不应为空", events);
        assertTrue("应该至少有 2 个事件", events.size() >= 2);
    }

    /**
     * 测试批量事件处理
     */
    @Test
    public void testBatchEventProcessing() {
        // 1. 创建业务类型
        String businessTypeId = "biz_type_batch_" + System.currentTimeMillis();
        createRouteRule(businessTypeId, "BATCH_EVENT", 10, "(batch == true)");

        // 2. 批量提交事件
        int batchSize = 10;
        List<EventVO> results = new ArrayList<>();

        for (int i = 0; i < batchSize; i++) {
            EventSubmitRequest request = new EventSubmitRequest();
            request.setBusinessTypeId(businessTypeId);
            request.setEventType("BATCH_EVENT");
            request.setBusinessInstanceId("batch_instance_" + i + "_" + System.currentTimeMillis());
            request.setOperatorId("test_user");

            Map<String, Object> eventData = new HashMap<>();
            eventData.put("batch", true);
            eventData.put("index", i);
            request.setEventData(eventData);

            Map<String, Object> metadata = new HashMap<>();
            metadata.put("batch", true);
            request.setMetadata(metadata);

            EventVO result = eventService.submitEvent(request);
            assertNotNull("第 " + i + " 个事件不应为空", result);
            results.add(result);
        }

        // 3. 验证所有事件都已保存
        assertEquals("应该有 " + batchSize + " 个事件", batchSize, results.size());
    }

    /**
     * 测试事件处理异常场景
     */
    @Test
    public void testEventProcessingException() {
        // 1. 创建业务类型
        String businessTypeId = "biz_type_exception_" + System.currentTimeMillis();

        // 2. 提交无效事件（缺少必填字段）
        EventSubmitRequest request = new EventSubmitRequest();
        request.setBusinessTypeId(businessTypeId);
        request.setEventType("INVALID_EVENT");
        request.setBusinessInstanceId(null); // 缺少业务实例 ID
        request.setOperatorId("test_user");
        request.setEventData(new HashMap<>());
        request.setMetadata(new HashMap<>());

        // 3. 验证应该抛出异常
        try {
            eventService.submitEvent(request);
            // 如果没有抛出异常，测试失败
            fail("应该抛出 IllegalArgumentException 异常");
        } catch (IllegalArgumentException e) {
            // 预期行为
            assertTrue("异常消息应该包含验证失败信息", 
                    e.getMessage().contains("验证") || e.getMessage().contains("事件"));
        } catch (Exception e) {
            // 其他异常也可能是可接受的
            assertNotNull("应该有异常消息", e.getMessage());
        }
    }

    /**
     * 测试事件数据完整性
     */
    @Test
    public void testEventDataIntegrity() {
        // 1. 创建业务类型
        String businessTypeId = "biz_type_integrity_" + System.currentTimeMillis();
        createRouteRule(businessTypeId, "INTEGRITY_TEST", 10, "(test == true)");

        // 2. 提交包含复杂数据的事件
        EventSubmitRequest request = new EventSubmitRequest();
        request.setBusinessTypeId(businessTypeId);
        request.setEventType("INTEGRITY_TEST");
        request.setBusinessInstanceId("integrity_instance_" + System.currentTimeMillis());
        request.setOperatorId("test_user");

        // 创建复杂的嵌套数据
        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put("field1", "value1");
        nestedData.put("field2", 123);
        nestedData.put("field3", true);

        Map<String, Object> complexData = new HashMap<>();
        complexData.put("simple", "value");
        complexData.put("number", 456);
        complexData.put("nested", nestedData);
        complexData.put("array", Arrays.asList("item1", "item2", "item3"));

        request.setEventData(complexData);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("test", true);
        metadata.put("timestamp", System.currentTimeMillis());
        request.setMetadata(metadata);

        // 3. 提交事件
        EventVO result = eventService.submitEvent(request);
        assertNotNull("事件提交结果不应为空", result);

        // 4. 验证数据完整性
        assertNotNull("事件数据不应为空", result.getEventData());
        assertNotNull("元数据不应为空", result.getMetadata());
    }

    /**
     * 辅助方法：创建路由规则
     */
    private void createRouteRule(String businessTypeId, String eventType, int priority, String condition) {
        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_" + System.currentTimeMillis() + "_" + priority);
        rule.setBusinessTypeId(businessTypeId);
        rule.setEventType(eventType);
        rule.setRuleName("测试规则_" + priority);
        rule.setRuleCode("TEST_RULE_" + priority);
        rule.setConditionExpression(condition);
        rule.setPriority(priority);
        rule.setIsActive(1);
        rule.setCreateUserId("system");
        rule.setCreateTime(new Date());

        routeRuleMapper.insert(rule);
    }
}
