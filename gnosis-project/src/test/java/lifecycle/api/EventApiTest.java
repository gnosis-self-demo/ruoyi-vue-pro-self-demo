package lifecycle.api;

import com.alibaba.fastjson.JSON;
import lifecycle.dto.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import lifecycle.controller.EventController;
import lifecycle.service.EventService;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 事件 API 测试类
 */
@RunWith(MockitoJUnitRunner.class)
public class EventApiTest {

    private MockMvc mockMvc;

    @Mock
    private EventService eventService;

    @InjectMocks
    private EventController eventController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(eventController).build();
    }

    /**
     * 测试提交事件
     */
    @Test
    public void testSubmitEvent() throws Exception {
        // 准备测试数据
        EventSubmitRequest request = new EventSubmitRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setEventType("ORDER_CREATE");
        request.setBusinessInstanceId("ORDER_001");
        request.setCurrentStateCode("INIT");
        request.setOperatorId("user_001");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", "ORDER_001");
        eventData.put("amount", 1000);
        request.setEventData(eventData);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", "CREATE");
        request.setMetadata(metadata);

        EventVO responseVO = new EventVO();
        responseVO.setId("event_" + System.currentTimeMillis());
        responseVO.setBusinessTypeId(request.getBusinessTypeId());
        responseVO.setEventType(request.getEventType());
        responseVO.setBusinessInstanceId(request.getBusinessInstanceId());

        // 配置 Mock 行为
        when(eventService.submitEvent(any(EventSubmitRequest.class))).thenReturn(responseVO);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.businessTypeId").value("biz_type_001"))
                .andExpect(jsonPath("$.eventType").value("ORDER_CREATE"));

        // 验证 Mock 调用
        verify(eventService, times(1)).submitEvent(any(EventSubmitRequest.class));
    }

    /**
     * 测试获取事件列表
     */
    @Test
    public void testGetEventList() throws Exception {
        // 准备测试数据
        List<EventVO> mockList = new ArrayList<>();
        EventVO vo1 = new EventVO();
        vo1.setId("event_001");
        vo1.setBusinessTypeId("biz_type_001");
        vo1.setEventType("ORDER_CREATE");
        mockList.add(vo1);

        EventVO vo2 = new EventVO();
        vo2.setId("event_002");
        vo2.setBusinessTypeId("biz_type_001");
        vo2.setEventType("ORDER_UPDATE");
        mockList.add(vo2);

        // 配置 Mock 行为
        when(eventService.list(anyString(), anyString(), anyInt(), anyInt())).thenReturn(mockList);

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/event/list")
                .param("businessTypeId", "biz_type_001")
                .param("eventType", "")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value("event_001"))
                .andExpect(jsonPath("$[1].id").value("event_002"));

        // 验证 Mock 调用
        verify(eventService, times(1)).list(anyString(), anyString(), eq(1), eq(10));
    }

    /**
     * 测试路由规则测试
     */
    @Test
    public void testTestRouteRule() throws Exception {
        // 准备测试数据
        RouteTestRequest request = new RouteTestRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setEventType("ORDER_CREATE");
        request.setCurrentStateCode("INIT");

        Map<String, Object> testData = new HashMap<>();
        testData.put("action", "CREATE");
        testData.put("priority", 10);
        request.setTestData(testData);

        RouteTestResultVO resultVO = new RouteTestResultVO();
        resultVO.setMatched(true);
        resultVO.setMessage("匹配成功");
        resultVO.setExecutionTimeMs(50L);

        List<RouteTestResultVO.MatchedRuleVO> matchedRules = new ArrayList<>();
        RouteTestResultVO.MatchedRuleVO matchedRule = new RouteTestResultVO.MatchedRuleVO();
        matchedRule.setRuleId("rule_001");
        matchedRule.setRuleName("订单创建规则");
        matchedRule.setPriority(10);
        matchedRules.add(matchedRule);
        resultVO.setMatchedRules(matchedRules);

        // 配置 Mock 行为
        when(eventService.testRoute(any(RouteTestRequest.class))).thenReturn(resultVO);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/event/route/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.matched").value(true))
                .andExpect(jsonPath("$.message").value("匹配成功"))
                .andExpect(jsonPath("$.executionTimeMs").value(50))
                .andExpect(jsonPath("$.matchedRules.length()").value(1));

        // 验证 Mock 调用
        verify(eventService, times(1)).testRoute(any(RouteTestRequest.class));
    }

    /**
     * 测试执行流转
     */
    @Test
    public void testExecuteTransition() throws Exception {
        // 准备测试数据
        TransitionExecutionRequest request = new TransitionExecutionRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setBusinessInstanceId("instance_001");
        request.setTransitionId("transition_001");
        request.setTransitionCode("APPROVE");
        request.setFromStateId("state_pending");
        request.setToStateId("state_approved");
        request.setOperatorId("user_001");
        request.setRemark("审批通过");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "流转成功");
        response.put("newStateId", "state_approved");
        response.put("newStateCode", "APPROVED");

        // 配置 Mock 行为
        when(eventService.executeTransition(any(TransitionExecutionRequest.class))).thenReturn(response);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/event/transition/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("流转成功"))
                .andExpect(jsonPath("$.newStateCode").value("APPROVED"));

        // 验证 Mock 调用
        verify(eventService, times(1)).executeTransition(any(TransitionExecutionRequest.class));
    }

    /**
     * 测试获取事件详情
     */
    @Test
    public void testGetEventDetail() throws Exception {
        // 准备测试数据
        EventVO responseVO = new EventVO();
        responseVO.setId("event_001");
        responseVO.setBusinessTypeId("biz_type_001");
        responseVO.setEventType("ORDER_CREATE");
        responseVO.setBusinessInstanceId("ORDER_001");

        Map<String, Object> eventData = new HashMap<>();
        eventData.put("orderId", "ORDER_001");
        responseVO.setEventData(eventData);

        // 配置 Mock 行为
        when(eventService.getById("event_001")).thenReturn(responseVO);

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/event/{id}", "event_001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("event_001"))
                .andExpect(jsonPath("$.businessTypeId").value("biz_type_001"));

        // 验证 Mock 调用
        verify(eventService, times(1)).getById("event_001");
    }

    /**
     * 测试获取事件详情 - 事件不存在
     */
    @Test
    public void testGetEventDetail_NotFound() throws Exception {
        // 配置 Mock 行为
        when(eventService.getById("non_existent_event"))
                .thenThrow(new IllegalArgumentException("事件不存在"));

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/event/{id}", "non_existent_event"))
                .andExpect(status().isNotFound());

        // 验证 Mock 调用
        verify(eventService, times(1)).getById("non_existent_event");
    }

    /**
     * 测试提交事件 - 参数校验失败
     */
    @Test
    public void testSubmitEvent_ValidationFailure() throws Exception {
        // 准备测试数据 - 缺少必填字段
        EventSubmitRequest request = new EventSubmitRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setEventType("TEST_EVENT");
        // 缺少 businessInstanceId

        // 配置 Mock 行为
        when(eventService.submitEvent(any(EventSubmitRequest.class)))
                .thenThrow(new IllegalArgumentException("事件验证失败"));

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isBadRequest());

        // 验证 Mock 调用
        verify(eventService, times(1)).submitEvent(any(EventSubmitRequest.class));
    }

    /**
     * 测试路由测试 - 规则不存在
     */
    @Test
    public void testTestRouteRule_RuleNotFound() throws Exception {
        // 准备测试数据
        RouteTestRequest request = new RouteTestRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setEventType("TEST_EVENT");
        request.setRuleId("non_existent_rule");

        // 配置 Mock 行为
        when(eventService.testRoute(any(RouteTestRequest.class)))
                .thenThrow(new IllegalArgumentException("规则不存在"));

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/event/route/test")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isBadRequest());

        // 验证 Mock 调用
        verify(eventService, times(1)).testRoute(any(RouteTestRequest.class));
    }

    /**
     * 测试执行流转 - 流转失败
     */
    @Test
    public void testExecuteTransition_Failure() throws Exception {
        // 准备测试数据
        TransitionExecutionRequest request = new TransitionExecutionRequest();
        request.setBusinessInstanceId("instance_001");
        request.setTransitionCode("APPROVE");

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "流转失败：状态不匹配");
        response.put("errorCode", "STATE_MISMATCH");

        // 配置 Mock 行为
        when(eventService.executeTransition(any(TransitionExecutionRequest.class))).thenReturn(response);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/event/transition/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.errorCode").value("STATE_MISMATCH"));

        // 验证 Mock 调用
        verify(eventService, times(1)).executeTransition(any(TransitionExecutionRequest.class));
    }

    /**
     * 测试获取事件列表 - 无数据
     */
    @Test
    public void testGetEventList_Empty() throws Exception {
        // 配置 Mock 行为
        when(eventService.list(anyString(), anyString(), anyInt(), anyInt()))
                .thenReturn(new ArrayList<>());

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/event/list")
                .param("pageNum", "1")
                .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // 验证 Mock 调用
        verify(eventService, times(1)).list(anyString(), anyString(), anyInt(), anyInt());
    }

    /**
     * 测试复杂事件数据提交
     */
    @Test
    public void testSubmitEvent_ComplexData() throws Exception {
        // 准备测试数据 - 包含嵌套结构
        EventSubmitRequest request = new EventSubmitRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setEventType("COMPLEX_EVENT");
        request.setBusinessInstanceId("instance_001");
        request.setOperatorId("user_001");

        Map<String, Object> nestedData = new HashMap<>();
        nestedData.put("field1", "value1");
        nestedData.put("field2", 123);

        Map<String, Object> complexData = new HashMap<>();
        complexData.put("simple", "value");
        complexData.put("nested", nestedData);
        complexData.put("array", Arrays.asList("item1", "item2"));
        request.setEventData(complexData);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", "CREATE");
        request.setMetadata(metadata);

        EventVO responseVO = new EventVO();
        responseVO.setId("event_complex");
        responseVO.setBusinessTypeId(request.getBusinessTypeId());

        // 配置 Mock 行为
        when(eventService.submitEvent(any(EventSubmitRequest.class))).thenReturn(responseVO);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/event")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("event_complex"));

        // 验证 Mock 调用
        verify(eventService, times(1)).submitEvent(any(EventSubmitRequest.class));
    }
}
