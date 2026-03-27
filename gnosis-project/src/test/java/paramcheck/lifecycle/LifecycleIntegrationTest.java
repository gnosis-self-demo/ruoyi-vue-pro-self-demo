package paramcheck.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import lifecycle.domain.LifecycleEvent;
import lifecycle.domain.LifecycleRouteRule;
import lifecycle.dto.BusinessTypeCreateRequest;
import lifecycle.dto.BusinessTypeVO;
import lifecycle.dto.EventSubmitRequest;
import lifecycle.dto.EventVO;
import lifecycle.entry.EntryService;
import lifecycle.repository.LifecycleBusinessTypeMapper;
import lifecycle.repository.LifecycleEventMapper;
import lifecycle.repository.LifecycleRouteRuleMapper;
import lifecycle.service.EventService;

import java.util.*;

import static org.junit.Assert.*;

/**
 * 生命周期集成测试类
 * 测试完整的生命周期流程
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class LifecycleIntegrationTest {

    @Autowired
    private EntryService entryService;

    @Autowired
    private EventService eventService;

    @Autowired
    private LifecycleBusinessTypeMapper businessTypeMapper;

    @Autowired
    private LifecycleEventMapper eventMapper;

    @Autowired
    private LifecycleRouteRuleMapper routeRuleMapper;

    /**
     * 测试完整的生命周期流程
     */
    @Test
    public void testCompleteLifecycleFlow() {
        // 1. 创建业务类型（入口注册）
        BusinessTypeCreateRequest createRequest = new BusinessTypeCreateRequest();
        createRequest.setName("订单生命周期");
        createRequest.setCode("ORDER_LIFECYCLE");
        createRequest.setDescription("订单从创建到完成的完整生命周期");

        BusinessTypeVO businessType = entryService.register(createRequest);
        assertNotNull("业务类型创建不应为空", businessType);
        assertNotNull("业务类型 ID 不应为空", businessType.getId());
        assertEquals("业务类型名称应该匹配", "订单生命周期", businessType.getName());
        assertEquals("业务类型编码应该匹配", "ORDER_LIFECYCLE", businessType.getCode());

        // 2. 创建路由规则
        LifecycleRouteRule routeRule = new LifecycleRouteRule();
        routeRule.setId("route_rule_test_" + System.currentTimeMillis());
        routeRule.setBusinessTypeId(businessType.getId());
        routeRule.setEventType("ORDER_CREATE");
        routeRule.setRuleName("订单创建规则");
        routeRule.setRuleCode("ORDER_CREATE_RULE");
        routeRule.setConditionExpression("(action == 'CREATE')");
        routeRule.setPriority(10);
        routeRule.setIsActive(1);
        routeRule.setCreateUserId("system");
        routeRule.setCreateTime(new Date());

        int insertCount = routeRuleMapper.insert(routeRule);
        assertEquals("路由规则应该插入成功", 1, insertCount);

        // 3. 提交事件
        EventSubmitRequest eventRequest = new EventSubmitRequest();
        eventRequest.setBusinessTypeId(businessType.getId());
        eventRequest.setEventType("ORDER_CREATE");
        eventRequest.setBusinessInstanceId("ORDER_" + System.currentTimeMillis());
        eventRequest.setCurrentStateCode("INIT");
        eventRequest.setOperatorId("test_user");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", "ORDER_001");
        eventData.put("amount", 1000);
        eventData.put("action", "CREATE");
        eventRequest.setEventData(eventData);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", "CREATE");
        metadata.put("node_id", "node_start");
        eventRequest.setMetadata(metadata);

        EventVO event = eventService.submitEvent(eventRequest);
        assertNotNull("事件提交不应为空", event);
        assertNotNull("事件 ID 不应为空", event.getId());
        assertEquals("业务类型 ID 应该匹配", businessType.getId(), event.getBusinessTypeId());
        assertEquals("事件类型应该匹配", "ORDER_CREATE", event.getEventType());

        // 4. 验证事件已保存
        LifecycleEvent savedEvent = eventMapper.selectById(event.getId());
        assertNotNull("保存的事件不应为空", savedEvent);
        assertEquals("事件 ID 应该匹配", event.getId(), savedEvent.getId());
        assertEquals("业务实例 ID 应该匹配", eventRequest.getBusinessInstanceId(), savedEvent.getBusinessInstanceId());

        // 5. 查询路由规则
        LifecycleRouteRule query = new LifecycleRouteRule();
        query.setBusinessTypeId(businessType.getId());
        query.setEventType("ORDER_CREATE");
        List<LifecycleRouteRule> rules = routeRuleMapper.selectList(query);
        assertNotNull("规则列表不应为空", rules);
        assertTrue("应该至少有一条规则", rules.size() > 0);
    }

    /**
     * 测试从入口到出口的全流程
     */
    @Test
    public void testEntryToExit() {
        // 1. 注册业务入口
        BusinessTypeCreateRequest request = new BusinessTypeCreateRequest();
        request.setName("请假申请流程");
        request.setCode("LEAVE_APPLICATION");
        request.setDescription("员工请假申请的生命周期管理");

        BusinessTypeVO entry = entryService.register(request);
        assertNotNull("入口注册不应为空", entry);

        // 2. 更新入口配置
        BusinessTypeVO updateRequest = new BusinessTypeVO();
        updateRequest.setId(entry.getId());
        updateRequest.setName("请假申请流程 - 已更新");
        updateRequest.setDescription("更新后的描述");
        
        // 3. 提交多个事件模拟完整流程
        String businessInstanceId = "LEAVE_" + System.currentTimeMillis();
        
        // 事件 1: 提交申请
        submitTestEvent(entry.getId(), "APPLICATION_SUBMIT", businessInstanceId, "INIT", "employee_001");
        
        // 事件 2: 主管审批
        submitTestEvent(entry.getId(), "MANAGER_APPROVE", businessInstanceId, "PENDING_MANAGER", "manager_001");
        
        // 事件 3: HR 确认
        submitTestEvent(entry.getId(), "HR_CONFIRM", businessInstanceId, "PENDING_HR", "hr_001");
        
        // 事件 4: 流程完成
        submitTestEvent(entry.getId(), "PROCESS_COMPLETE", businessInstanceId, "APPROVED", "system");

        // 4. 查询所有相关事件
        List<EventVO> events = eventService.list(entry.getId(), null, 1, 100);
        assertNotNull("事件列表不应为空", events);
        assertTrue("应该有多个事件", events.size() >= 4);
    }

    /**
     * 测试事件处理流程
     */
    @Test
    public void testEventProcessing() {
        // 1. 创建业务类型
        BusinessTypeCreateRequest createRequest = new BusinessTypeCreateRequest();
        createRequest.setName("事件处理测试");
        createRequest.setCode("EVENT_PROCESSING_TEST");
        BusinessTypeVO businessType = entryService.register(createRequest);

        // 2. 创建多条路由规则
        createRouteRule(businessType.getId(), "HIGH_PRIORITY_EVENT", 100, "(priority >= 50)");
        createRouteRule(businessType.getId(), "MEDIUM_PRIORITY_EVENT", 50, "(priority >= 20) AND (priority < 50)");
        createRouteRule(businessType.getId(), "LOW_PRIORITY_EVENT", 10, "(priority < 20)");

        // 3. 提交高优先级事件
        EventSubmitRequest highPriorityEvent = createEventRequest(
                businessType.getId(), 
                "HIGH_PRIORITY_EVENT", 
                "INSTANCE_HIGH_" + System.currentTimeMillis(),
                80
        );
        EventVO highResult = eventService.submitEvent(highPriorityEvent);
        assertNotNull("高优先级事件不应为空", highResult);

        // 4. 提交中优先级事件
        EventSubmitRequest mediumPriorityEvent = createEventRequest(
                businessType.getId(), 
                "MEDIUM_PRIORITY_EVENT", 
                "INSTANCE_MEDIUM_" + System.currentTimeMillis(),
                30
        );
        EventVO mediumResult = eventService.submitEvent(mediumPriorityEvent);
        assertNotNull("中优先级事件不应为空", mediumResult);

        // 5. 提交低优先级事件
        EventSubmitRequest lowPriorityEvent = createEventRequest(
                businessType.getId(), 
                "LOW_PRIORITY_EVENT", 
                "INSTANCE_LOW_" + System.currentTimeMillis(),
                10
        );
        EventVO lowResult = eventService.submitEvent(lowPriorityEvent);
        assertNotNull("低优先级事件不应为空", lowResult);

        // 6. 验证所有事件都已保存
        List<LifecycleEvent> allEvents = eventMapper.selectList(new LifecycleEvent());
        assertNotNull("所有事件列表不应为空", allEvents);
        assertTrue("应该至少有 3 个事件", allEvents.size() >= 3);
    }

    /**
     * 测试并发事件处理
     */
    @Test
    public void testConcurrentEventProcessing() throws InterruptedException {
        // 1. 创建业务类型
        BusinessTypeCreateRequest createRequest = new BusinessTypeCreateRequest();
        createRequest.setName("并发测试");
        createRequest.setCode("CONCURRENT_TEST");
        BusinessTypeVO businessType = entryService.register(createRequest);

        // 2. 创建多个线程并发提交事件
        final int threadCount = 10;
        final List<EventVO> results = Collections.synchronizedList(new ArrayList<>());
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                try {
                    EventSubmitRequest request = createEventRequest(
                            businessType.getId(),
                            "CONCURRENT_EVENT",
                            "CONCURRENT_INSTANCE_" + index + "_" + System.currentTimeMillis(),
                            50
                    );
                    EventVO result = eventService.submitEvent(request);
                    if (result != null) {
                        results.add(result);
                    }
                } catch (Exception e) {
                    fail("线程 " + index + " 执行失败：" + e.getMessage());
                }
            });
            threads[i].start();
        }

        // 3. 等待所有线程完成
        for (Thread thread : threads) {
            thread.join();
        }

        // 4. 验证所有事件都已处理
        assertEquals("所有线程应该都成功提交事件", threadCount, results.size());
    }

    /**
     * 测试事件状态流转
     */
    @Test
    public void testEventStateTransition() {
        // 1. 创建业务类型
        BusinessTypeCreateRequest createRequest = new BusinessTypeCreateRequest();
        createRequest.setName("状态流转测试");
        createRequest.setCode("STATE_TRANSITION_TEST");
        BusinessTypeVO businessType = entryService.register(createRequest);

        // 2. 提交初始事件
        EventSubmitRequest initialEvent = new EventSubmitRequest();
        initialEvent.setBusinessTypeId(businessType.getId());
        initialEvent.setEventType("PROCESS_START");
        initialEvent.setBusinessInstanceId("PROCESS_" + System.currentTimeMillis());
        initialEvent.setCurrentStateCode("INIT");
        initialEvent.setOperatorId("test_user");
        initialEvent.setEventData(new HashMap<>());
        initialEvent.setMetadata(new HashMap<>());

        EventVO event = eventService.submitEvent(initialEvent);
        assertNotNull("初始事件不应为空", event);

        // 3. 模拟状态流转（这里应该调用状态流转服务）
        // 实际项目中应该验证状态是否正确流转
    }

    /**
     * 辅助方法：创建路由规则
     */
    private void createRouteRule(String businessTypeId, String eventType, int priority, String condition) {
        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_" + System.currentTimeMillis() + "_" + priority);
        rule.setBusinessTypeId(businessTypeId);
        rule.setEventType(eventType);
        rule.setRuleName("规则_" + priority);
        rule.setRuleCode("RULE_" + priority);
        rule.setConditionExpression(condition);
        rule.setPriority(priority);
        rule.setIsActive(1);
        rule.setCreateUserId("system");
        rule.setCreateTime(new Date());

        routeRuleMapper.insert(rule);
    }

    /**
     * 辅助方法：创建事件请求
     */
    private EventSubmitRequest createEventRequest(String businessTypeId, String eventType, 
                                                   String businessInstanceId, int priority) {
        EventSubmitRequest request = new EventSubmitRequest();
        request.setBusinessTypeId(businessTypeId);
        request.setEventType(eventType);
        request.setBusinessInstanceId(businessInstanceId);
        request.setCurrentStateCode("INIT");
        request.setOperatorId("test_user");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("priority", priority);
        eventData.put("timestamp", System.currentTimeMillis());
        request.setEventData(eventData);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("priority", priority);
        metadata.put("action", "TEST");
        request.setMetadata(metadata);

        return request;
    }

    /**
     * 辅助方法：提交测试事件
     */
    private EventVO submitTestEvent(String businessTypeId, String eventType, 
                                    String businessInstanceId, String stateCode, String operatorId) {
        EventSubmitRequest request = new EventSubmitRequest();
        request.setBusinessTypeId(businessTypeId);
        request.setEventType(eventType);
        request.setBusinessInstanceId(businessInstanceId);
        request.setCurrentStateCode(stateCode);
        request.setOperatorId(operatorId);
        request.setEventData(new HashMap<>());
        request.setMetadata(new HashMap<>());

        return eventService.submitEvent(request);
    }
}
