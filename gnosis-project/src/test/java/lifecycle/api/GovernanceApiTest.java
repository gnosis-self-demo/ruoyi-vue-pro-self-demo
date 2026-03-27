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
import lifecycle.controller.GovernanceController;
import lifecycle.service.GovernanceService;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 治理 API 测试类
 */
@RunWith(MockitoJUnitRunner.class)
public class GovernanceApiTest {

    private MockMvc mockMvc;

    @Mock
    private GovernanceService governanceService;

    @InjectMocks
    private GovernanceController governanceController;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(governanceController).build();
    }

    /**
     * 测试获取元数据列表
     */
    @Test
    public void testGetMetadataList() throws Exception {
        // 准备测试数据
        List<MetadataVO> mockList = new ArrayList<>();
        MetadataVO metadata1 = new MetadataVO();
        metadata1.setId("meta_001");
        metadata1.setFieldName("field_1");
        metadata1.setFieldLabel("字段 1");
        metadata1.setFieldType("String");
        mockList.add(metadata1);

        MetadataVO metadata2 = new MetadataVO();
        metadata2.setId("meta_002");
        metadata2.setFieldName("field_2");
        metadata2.setFieldLabel("字段 2");
        metadata2.setFieldType("Integer");
        mockList.add(metadata2);

        // 配置 Mock 行为
        when(governanceService.listMetadata("biz_type_001")).thenReturn(mockList);

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/governance/metadata/list")
                .param("businessTypeId", "biz_type_001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].fieldName").value("field_1"))
                .andExpect(jsonPath("$.data[1].fieldName").value("field_2"))
                .andExpect(jsonPath("$.total").value(2));

        // 验证 Mock 调用
        verify(governanceService, times(1)).listMetadata("biz_type_001");
    }

    /**
     * 测试获取规则版本历史
     */
    @Test
    public void testGetRuleVersionHistory() throws Exception {
        // 准备测试数据
        List<RuleVersionHistoryVO> mockList = new ArrayList<>();
        RuleVersionHistoryVO version1 = new RuleVersionHistoryVO();
        version1.setId("version_001");
        version1.setRuleId("rule_001");
        version1.setVersionNumber(1);
        version1.setChangeType("CREATE");
        version1.setChangeReason("初始创建");
        mockList.add(version1);

        RuleVersionHistoryVO version2 = new RuleVersionHistoryVO();
        version2.setId("version_002");
        version2.setRuleId("rule_001");
        version2.setVersionNumber(2);
        version2.setChangeType("UPDATE");
        version2.setChangeReason("更新条件");
        mockList.add(version2);

        // 配置 Mock 行为
        when(governanceService.getRuleVersionHistory("rule_001")).thenReturn(mockList);

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/governance/rule-version/{ruleId}", "rule_001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].versionNumber").value(1))
                .andExpect(jsonPath("$.data[1].versionNumber").value(2))
                .andExpect(jsonPath("$.total").value(2));

        // 验证 Mock 调用
        verify(governanceService, times(1)).getRuleVersionHistory("rule_001");
    }

    /**
     * 测试回滚规则版本
     */
    @Test
    public void testRollbackRuleVersion() throws Exception {
        // 准备测试数据
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "规则版本回滚成功");
        response.put("newVersionId", "version_003");
        response.put("newVersionNumber", 3);

        // 配置 Mock 行为
        when(governanceService.rollbackRuleVersion(anyString(), anyString())).thenReturn(response);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/governance/rule-version/{id}/rollback", "version_001")
                .header("X-Operator-Id", "test_operator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("规则版本回滚成功"))
                .andExpect(jsonPath("$.newVersionId").value("version_003"));

        // 验证 Mock 调用
        verify(governanceService, times(1)).rollbackRuleVersion(anyString(), anyString());
    }

    /**
     * 测试获取追踪
     */
    @Test
    public void testGetTrace() throws Exception {
        // 准备测试数据
        List<InstanceTraceVO> mockList = new ArrayList<>();
        InstanceTraceVO trace1 = new InstanceTraceVO();
        trace1.setId("trace_001");
        trace1.setBusinessInstanceId("instance_001");
        trace1.setTraceType("STATE_TRANSITION");
        trace1.setSourceStateCode("PENDING");
        trace1.setTargetStateCode("APPROVED");
        trace1.setTraceStatus("SUCCESS");
        mockList.add(trace1);

        InstanceTraceVO trace2 = new InstanceTraceVO();
        trace2.setId("trace_002");
        trace2.setBusinessInstanceId("instance_001");
        trace2.setTraceType("EVENT_TRIGGER");
        trace2.setEventType("ORDER_CREATE");
        trace2.setTraceStatus("SUCCESS");
        mockList.add(trace2);

        // 配置 Mock 行为
        when(governanceService.getInstanceTrace("instance_001")).thenReturn(mockList);

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/governance/trace/{businessInstanceId}", "instance_001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].traceType").value("STATE_TRANSITION"))
                .andExpect(jsonPath("$.data[1].traceType").value("EVENT_TRIGGER"))
                .andExpect(jsonPath("$.total").value(2));

        // 验证 Mock 调用
        verify(governanceService, times(1)).getInstanceTrace("instance_001");
    }

    /**
     * 测试获取看板
     */
    @Test
    public void testGetDashboard() throws Exception {
        // 准备测试数据
        TraceDashboardVO dashboard = new TraceDashboardVO();
        dashboard.setTotalInstances(100);
        dashboard.setTodayInstances(10);
        dashboard.setTotalEvents(500);
        dashboard.setTodayEvents(50);

        List<StateStatistics> stateStats = new ArrayList<>();
        StateStatistics stateStat = new StateStatistics();
        stateStat.setStateCode("APPROVED");
        stateStat.setCount(50);
        stateStats.add(stateStat);
        dashboard.setStateStatistics(stateStats);

        List<EventStatistics> eventStats = new ArrayList<>();
        EventStatistics eventStat = new EventStatistics();
        eventStat.setEventType("ORDER_CREATE");
        eventStat.setCount(200);
        eventStats.add(eventStat);
        dashboard.setEventStatistics(eventStats);

        // 配置 Mock 行为
        when(governanceService.getTraceDashboard()).thenReturn(dashboard);

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/governance/trace/dashboard"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalInstances").value(100))
                .andExpect(jsonPath("$.data.todayInstances").value(10))
                .andExpect(jsonPath("$.data.totalEvents").value(500))
                .andExpect(jsonPath("$.data.todayEvents").value(50));

        // 验证 Mock 调用
        verify(governanceService, times(1)).getTraceDashboard();
    }

    /**
     * 测试创建元数据
     */
    @Test
    public void testCreateMetadata() throws Exception {
        // 准备测试数据
        MetadataCreateRequest request = new MetadataCreateRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setFieldName("test_field");
        request.setFieldLabel("测试字段");
        request.setFieldType("String");
        request.setFieldLength(255);
        request.setIsRequired(0);
        request.setIsSearchable(1);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "元数据创建成功");
        response.put("id", "meta_001");

        // 配置 Mock 行为
        when(governanceService.createMetadata(any(MetadataCreateRequest.class), anyString()))
                .thenReturn(response);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/governance/metadata")
                .header("X-Operator-Id", "test_operator")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("元数据创建成功"))
                .andExpect(jsonPath("$.id").value("meta_001"));

        // 验证 Mock 调用
        verify(governanceService, times(1)).createMetadata(any(MetadataCreateRequest.class), anyString());
    }

    /**
     * 测试创建元数据 - 字段已存在
     */
    @Test
    public void testCreateMetadata_FieldAlreadyExists() throws Exception {
        // 准备测试数据
        MetadataCreateRequest request = new MetadataCreateRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setFieldName("existing_field");
        request.setFieldLabel("已存在字段");

        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "字段已存在：existing_field");

        // 配置 Mock 行为
        when(governanceService.createMetadata(any(MetadataCreateRequest.class), anyString()))
                .thenReturn(response);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/governance/metadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("字段已存在"));

        // 验证 Mock 调用
        verify(governanceService, times(1)).createMetadata(any(MetadataCreateRequest.class), anyString());
    }

    /**
     * 测试回滚规则版本 - 版本不存在
     */
    @Test
    public void testRollbackRuleVersion_VersionNotFound() throws Exception {
        // 准备测试数据
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", "版本不存在");

        // 配置 Mock 行为
        when(governanceService.rollbackRuleVersion(anyString(), anyString())).thenReturn(response);

        // 执行请求
        mockMvc.perform(post("/api/lifecycle/governance/rule-version/{id}/rollback", "non_existent_version")
                .header("X-Operator-Id", "test_operator"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("版本不存在"));

        // 验证 Mock 调用
        verify(governanceService, times(1)).rollbackRuleVersion(anyString(), anyString());
    }

    /**
     * 测试获取追踪 - 无数据
     */
    @Test
    public void testGetTrace_Empty() throws Exception {
        // 配置 Mock 行为
        when(governanceService.getInstanceTrace("non_existent_instance"))
                .thenReturn(new ArrayList<>());

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/governance/trace/{businessInstanceId}", "non_existent_instance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.total").value(0));

        // 验证 Mock 调用
        verify(governanceService, times(1)).getInstanceTrace("non_existent_instance");
    }

    /**
     * 测试获取元数据列表 - 无业务类型 ID
     */
    @Test
    public void testGetMetadataList_NoBusinessTypeId() throws Exception {
        // 配置 Mock 行为
        when(governanceService.listMetadata(null)).thenReturn(new ArrayList<>());

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/governance/metadata/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0));

        // 验证 Mock 调用
        verify(governanceService, times(1)).listMetadata(null);
    }

    /**
     * 测试获取规则版本历史 - 无数据
     */
    @Test
    public void testGetRuleVersionHistory_Empty() throws Exception {
        // 配置 Mock 行为
        when(governanceService.getRuleVersionHistory("rule_001")).thenReturn(new ArrayList<>());

        // 执行请求
        mockMvc.perform(get("/api/lifecycle/governance/rule-version/{ruleId}", "rule_001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(0))
                .andExpect(jsonPath("$.total").value(0));

        // 验证 Mock 调用
        verify(governanceService, times(1)).getRuleVersionHistory("rule_001");
    }

    /**
     * 测试创建元数据 - 缺少操作人
     */
    @Test
    public void testCreateMetadata_NoOperator() throws Exception {
        // 准备测试数据
        MetadataCreateRequest request = new MetadataCreateRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setFieldName("test_field");

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "元数据创建成功");

        // 配置 Mock 行为
        when(governanceService.createMetadata(any(MetadataCreateRequest.class), eq("system")))
                .thenReturn(response);

        // 执行请求（不提供 X-Operator-Id）
        mockMvc.perform(post("/api/lifecycle/governance/metadata")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 验证 Mock 调用 - 应该使用默认操作人"system"
        verify(governanceService, times(1)).createMetadata(any(MetadataCreateRequest.class), eq("system"));
    }
}
