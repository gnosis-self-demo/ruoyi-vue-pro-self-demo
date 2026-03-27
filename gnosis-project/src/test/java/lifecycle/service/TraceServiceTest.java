package lifecycle.service;

import lifecycle.dto.*;
import lifecycle.service.GovernanceServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import lifecycle.domain.LifecycleInstanceTrace;
import lifecycle.domain.LifecycleMetadataDictionary;
import lifecycle.domain.LifecycleRouteRuleVersion;
import lifecycle.repository.LifecycleInstanceTraceMapper;
import lifecycle.repository.LifecycleMetadataDictionaryMapper;
import lifecycle.repository.LifecycleRouteRuleVersionMapper;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 追踪服务测试类
 */
@RunWith(MockitoJUnitRunner.class)
public class TraceServiceTest {

    @Mock
    private LifecycleMetadataDictionaryMapper metadataDictionaryMapper;

    @Mock
    private LifecycleRouteRuleVersionMapper ruleVersionMapper;

    @Mock
    private LifecycleInstanceTraceMapper instanceTraceMapper;

    @InjectMocks
    private GovernanceServiceImpl governanceService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * 测试按实例 ID 获取追踪
     */
    @Test
    public void testGetTraceByInstanceId() {
        // 准备测试数据
        String businessInstanceId = "test_instance_001";

        List<LifecycleInstanceTrace> traceList = new ArrayList<>();
        
        LifecycleInstanceTrace trace1 = new LifecycleInstanceTrace();
        trace1.setId("trace_001");
        trace1.setBusinessTypeId("biz_type_001");
        trace1.setBusinessInstanceId(businessInstanceId);
        trace1.setTraceType("STATE_TRANSITION");
        trace1.setSourceStateCode("PENDING");
        trace1.setTargetStateCode("APPROVED");
        trace1.setTraceStatus("SUCCESS");
        trace1.setCreateTime(new Date());
        traceList.add(trace1);

        LifecycleInstanceTrace trace2 = new LifecycleInstanceTrace();
        trace2.setId("trace_002");
        trace2.setBusinessTypeId("biz_type_001");
        trace2.setBusinessInstanceId(businessInstanceId);
        trace2.setTraceType("EVENT_TRIGGER");
        trace2.setEventType("ORDER_CREATE");
        trace2.setTraceStatus("SUCCESS");
        trace2.setCreateTime(new Date());
        traceList.add(trace2);

        // 配置 Mock 行为
        when(instanceTraceMapper.selectByBusinessInstanceId(businessInstanceId)).thenReturn(traceList);

        // 执行测试
        List<InstanceTraceVO> result = governanceService.getInstanceTrace(businessInstanceId);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertEquals("追踪列表大小应该匹配", 2, result.size());

        InstanceTraceVO vo1 = result.get(0);
        assertEquals("第一个追踪 ID 应该匹配", "trace_001", vo1.getId());
        assertEquals("第一个追踪类型应该匹配", "STATE_TRANSITION", vo1.getTraceType());
        assertEquals("第一个源状态应该匹配", "PENDING", vo1.getSourceStateCode());
        assertEquals("第一个目标状态应该匹配", "APPROVED", vo1.getTargetStateCode());

        InstanceTraceVO vo2 = result.get(1);
        assertEquals("第二个追踪 ID 应该匹配", "trace_002", vo2.getId());
        assertEquals("第二个追踪类型应该匹配", "EVENT_TRIGGER", vo2.getTraceType());
        assertEquals("第二个事件类型应该匹配", "ORDER_CREATE", vo2.getEventType());

        // 验证 Mock 调用
        verify(instanceTraceMapper, times(1)).selectByBusinessInstanceId(businessInstanceId);
    }

    /**
     * 测试获取看板数据
     */
    @Test
    public void testGetDashboardData() {
        // 配置 Mock 行为 - 返回空数据（因为实际实现中 TODO）
        // 这里主要测试方法调用和返回结构

        // 执行测试
        TraceDashboardVO result = governanceService.getTraceDashboard();

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        
        // 验证看板数据结构
        assertNotNull("总实例数不应为空", result.getTotalInstances());
        assertNotNull("今日新增实例数不应为空", result.getTodayInstances());
        assertNotNull("总事件数不应为空", result.getTotalEvents());
        assertNotNull("今日事件数不应为空", result.getTodayEvents());
        assertNotNull("状态统计列表不应为空", result.getStateStatistics());
        assertNotNull("事件统计列表不应为空", result.getEventStatistics());
        assertNotNull("流转统计列表不应为空", result.getTransitionStatistics());

        // 验证 Mock 调用
        // 注意：实际实现中可能没有调用 Mock 对象，因为都是 TODO
    }

    /**
     * 测试创建元数据
     */
    @Test
    public void testCreateMetadata() {
        // 准备测试数据
        MetadataCreateRequest request = new MetadataCreateRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setFieldName("test_field");
        request.setFieldLabel("测试字段");
        request.setFieldType("String");
        request.setFieldLength(255);
        request.setIsRequired(0);
        request.setIsSearchable(1);
        request.setIsDisplay(1);
        request.setDisplayOrder(1);
        request.setRemark("测试备注");

        String operatorId = "test_operator";

        // 配置 Mock 行为 - 字段不存在
        when(metadataDictionaryMapper.selectByBusinessTypeId("biz_type_001"))
                .thenReturn(new ArrayList<>());
        when(metadataDictionaryMapper.insert(any(LifecycleMetadataDictionary.class)))
                .thenReturn(1);

        // 执行测试
        Map<String, Object> result = governanceService.createMetadata(request, operatorId);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertTrue("创建应该成功", (Boolean) result.get("success"));
        assertEquals("消息应该匹配", "元数据创建成功", result.get("message"));
        assertNotNull("ID 不应为空", result.get("id"));

        // 验证 Mock 调用
        verify(metadataDictionaryMapper, times(1)).selectByBusinessTypeId("biz_type_001");
        verify(metadataDictionaryMapper, times(1)).insert(any(LifecycleMetadataDictionary.class));
    }

    /**
     * 测试创建元数据 - 字段已存在
     */
    @Test
    public void testCreateMetadata_FieldAlreadyExists() {
        // 准备测试数据
        MetadataCreateRequest request = new MetadataCreateRequest();
        request.setBusinessTypeId("biz_type_001");
        request.setFieldName("existing_field");
        request.setFieldLabel("已存在字段");
        request.setFieldType("String");

        String operatorId = "test_operator";

        // 创建已存在的字段
        LifecycleMetadataDictionary existingField = new LifecycleMetadataDictionary();
        existingField.setFieldName("existing_field");

        List<LifecycleMetadataDictionary> existingFields = new ArrayList<>();
        existingFields.add(existingField);

        // 配置 Mock 行为 - 字段已存在
        when(metadataDictionaryMapper.selectByBusinessTypeId("biz_type_001"))
                .thenReturn(existingFields);

        // 执行测试
        Map<String, Object> result = governanceService.createMetadata(request, operatorId);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertFalse("创建应该失败", (Boolean) result.get("success"));
        assertTrue("消息应该包含字段已存在", result.get("message").toString().contains("字段已存在"));

        // 验证 Mock 调用 - 不应该执行插入
        verify(metadataDictionaryMapper, times(1)).selectByBusinessTypeId("biz_type_001");
        verify(metadataDictionaryMapper, never()).insert(any(LifecycleMetadataDictionary.class));
    }

    /**
     * 测试获取元数据列表
     */
    @Test
    public void testListMetadata() {
        // 准备测试数据
        String businessTypeId = "biz_type_001";

        List<LifecycleMetadataDictionary> metadataList = new ArrayList<>();
        
        LifecycleMetadataDictionary metadata1 = new LifecycleMetadataDictionary();
        metadata1.setId("meta_001");
        metadata1.setBusinessTypeId(businessTypeId);
        metadata1.setFieldName("field_1");
        metadata1.setFieldLabel("字段 1");
        metadata1.setFieldType("String");
        metadataList.add(metadata1);

        LifecycleMetadataDictionary metadata2 = new LifecycleMetadataDictionary();
        metadata2.setId("meta_002");
        metadata2.setBusinessTypeId(businessTypeId);
        metadata2.setFieldName("field_2");
        metadata2.setFieldLabel("字段 2");
        metadata2.setFieldType("Integer");
        metadataList.add(metadata2);

        // 配置 Mock 行为
        when(metadataDictionaryMapper.selectByBusinessTypeId(businessTypeId)).thenReturn(metadataList);

        // 执行测试
        List<MetadataVO> result = governanceService.listMetadata(businessTypeId);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertEquals("元数据列表大小应该匹配", 2, result.size());

        MetadataVO vo1 = result.get(0);
        assertEquals("第一个字段名称应该匹配", "field_1", vo1.getFieldName());
        assertEquals("第一个字段标签应该匹配", "字段 1", vo1.getFieldLabel());
        assertEquals("第一个字段类型应该匹配", "String", vo1.getFieldType());

        MetadataVO vo2 = result.get(1);
        assertEquals("第二个字段名称应该匹配", "field_2", vo2.getFieldName());
        assertEquals("第二个字段类型应该匹配", "Integer", vo2.getFieldType());

        // 验证 Mock 调用
        verify(metadataDictionaryMapper, times(1)).selectByBusinessTypeId(businessTypeId);
    }

    /**
     * 测试获取规则版本历史
     */
    @Test
    public void testGetRuleVersionHistory() {
        // 准备测试数据
        String ruleId = "rule_001";

        List<LifecycleRouteRuleVersion> versionList = new ArrayList<>();
        
        LifecycleRouteRuleVersion version1 = new LifecycleRouteRuleVersion();
        version1.setId("version_001");
        version1.setRuleId(ruleId);
        version1.setVersionNumber(1);
        version1.setRuleContent("{\"condition\": \"test1\"}");
        version1.setChangeType("CREATE");
        version1.setCreateTime(new Date());
        versionList.add(version1);

        LifecycleRouteRuleVersion version2 = new LifecycleRouteRuleVersion();
        version2.setId("version_002");
        version2.setRuleId(ruleId);
        version2.setVersionNumber(2);
        version2.setRuleContent("{\"condition\": \"test2\"}");
        version2.setChangeType("UPDATE");
        version2.setCreateTime(new Date());
        versionList.add(version2);

        // 配置 Mock 行为
        when(ruleVersionMapper.selectByRuleId(ruleId)).thenReturn(versionList);

        // 执行测试
        List<RuleVersionHistoryVO> result = governanceService.getRuleVersionHistory(ruleId);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertEquals("版本历史列表大小应该匹配", 2, result.size());

        RuleVersionHistoryVO vo1 = result.get(0);
        assertEquals("第一个版本 ID 应该匹配", "version_001", vo1.getId());
        assertEquals("第一个版本号应该匹配", 1, vo1.getVersionNumber().intValue());
        assertEquals("第一个变更类型应该匹配", "CREATE", vo1.getChangeType());

        RuleVersionHistoryVO vo2 = result.get(1);
        assertEquals("第二个版本 ID 应该匹配", "version_002", vo2.getId());
        assertEquals("第二个版本号应该匹配", 2, vo2.getVersionNumber().intValue());

        // 验证 Mock 调用
        verify(ruleVersionMapper, times(1)).selectByRuleId(ruleId);
    }

    /**
     * 测试回滚规则版本
     */
    @Test
    public void testRollbackRuleVersion() {
        // 准备测试数据
        String versionId = "version_001";
        String operatorId = "test_operator";

        LifecycleRouteRuleVersion targetVersion = new LifecycleRouteRuleVersion();
        targetVersion.setId(versionId);
        targetVersion.setRuleId("rule_001");
        targetVersion.setVersionNumber(1);
        targetVersion.setRuleContent("{\"condition\": \"rollback_target\"}");

        // 配置 Mock 行为
        when(ruleVersionMapper.selectById(versionId)).thenReturn(targetVersion);
        when(ruleVersionMapper.insert(any(LifecycleRouteRuleVersion.class))).thenReturn(1);

        // 执行测试
        Map<String, Object> result = governanceService.rollbackRuleVersion(versionId, operatorId);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertTrue("回滚应该成功", (Boolean) result.get("success"));
        assertEquals("消息应该匹配", "规则版本回滚成功", result.get("message"));
        assertNotNull("新版本 ID 不应为空", result.get("newVersionId"));
        assertNotNull("新版本号不应为空", result.get("newVersionNumber"));

        // 验证 Mock 调用
        verify(ruleVersionMapper, times(1)).selectById(versionId);
        verify(ruleVersionMapper, times(1)).insert(any(LifecycleRouteRuleVersion.class));
    }

    /**
     * 测试回滚规则版本 - 版本不存在
     */
    @Test
    public void testRollbackRuleVersion_VersionNotFound() {
        // 准备测试数据
        String versionId = "non_existent_version";
        String operatorId = "test_operator";

        // 配置 Mock 行为 - 版本不存在
        when(ruleVersionMapper.selectById(versionId)).thenReturn(null);

        // 执行测试
        Map<String, Object> result = governanceService.rollbackRuleVersion(versionId, operatorId);

        // 验证结果
        assertNotNull("返回结果不应为空", result);
        assertFalse("回滚应该失败", (Boolean) result.get("success"));
        assertEquals("消息应该匹配", "版本不存在", result.get("message"));

        // 验证 Mock 调用 - 不应该执行插入
        verify(ruleVersionMapper, times(1)).selectById(versionId);
        verify(ruleVersionMapper, never()).insert(any(LifecycleRouteRuleVersion.class));
    }
}
