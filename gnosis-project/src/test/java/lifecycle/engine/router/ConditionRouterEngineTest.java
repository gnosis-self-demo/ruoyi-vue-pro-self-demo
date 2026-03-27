package lifecycle.engine.router;

import lifecycle.engine.router.ConditionEvaluator;
import lifecycle.engine.router.ConditionRouterEngine;
import lifecycle.engine.router.RouteMatchResult;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import lifecycle.adapter.StandardEvent;
import lifecycle.domain.LifecycleRouteRule;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 条件路由引擎测试类
 */
@RunWith(MockitoJUnitRunner.class)
public class ConditionRouterEngineTest {

    @Mock
    private ConditionEvaluator conditionEvaluator;

    @InjectMocks
    private ConditionRouterEngine routerEngine;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * 测试简单条件匹配
     */
    @Test
    public void testSimpleConditionMatch() {
        // 准备测试数据
        StandardEvent event = createTestEvent();
        event.setMetadataValue("action", "CREATE");

        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_001");
        rule.setRuleName("简单条件规则");
        rule.setRuleCode("SIMPLE_RULE");
        rule.setConditionExpression("(action == 'CREATE')");
        rule.setPriority(10);

        List<LifecycleRouteRule> rules = Collections.singletonList(rule);

        // 配置 Mock 行为
        when(conditionEvaluator.getFieldValue(any(), eq("action"))).thenReturn("CREATE");
        when(conditionEvaluator.evaluate(eq("action"), eq("=="), eq("CREATE"), any())).thenReturn(true);

        // 执行测试
        List<RouteMatchResult> results = routerEngine.match(event, rules);

        // 验证结果
        assertNotNull("结果不应为空", results);
        assertEquals("应该返回 1 个结果", 1, results.size());

        RouteMatchResult result = results.get(0);
        assertTrue("规则应该匹配", result.isMatched());
        assertEquals("规则 ID 应该匹配", "rule_001", result.getRuleId());
        assertEquals("规则名称应该匹配", "简单条件规则", result.getRuleName());

        // 验证 Mock 调用
        verify(conditionEvaluator, times(1)).getFieldValue(any(), eq("action"));
        verify(conditionEvaluator, times(1)).evaluate(eq("action"), eq("=="), eq("CREATE"), any());
    }

    /**
     * 测试 AND 条件组合
     */
    @Test
    public void testAndConditionMatch() {
        // 准备测试数据
        StandardEvent event = createTestEvent();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", "UPDATE");
        metadata.put("priority", 10);
        event.setMetadata(metadata);

        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_002");
        rule.setRuleName("AND 条件规则");
        rule.setRuleCode("AND_RULE");
        rule.setConditionExpression("(action == 'UPDATE') AND (priority >= 5)");
        rule.setPriority(20);

        List<LifecycleRouteRule> rules = Collections.singletonList(rule);

        // 配置 Mock 行为
        when(conditionEvaluator.getFieldValue(any(), eq("action"))).thenReturn("UPDATE");
        when(conditionEvaluator.getFieldValue(any(), eq("priority"))).thenReturn(10);
        when(conditionEvaluator.evaluate(eq("action"), eq("=="), eq("UPDATE"), any())).thenReturn(true);
        when(conditionEvaluator.evaluate(eq("priority"), eq(">="), eq("5"), any())).thenReturn(true);

        // 执行测试
        List<RouteMatchResult> results = routerEngine.match(event, rules);

        // 验证结果
        assertNotNull("结果不应为空", results);
        assertEquals("应该返回 1 个结果", 1, results.size());

        RouteMatchResult result = results.get(0);
        assertTrue("规则应该匹配", result.isMatched());
        assertEquals("规则 ID 应该匹配", "rule_002", result.getRuleId());

        // 验证 Mock 调用 - 应该评估两个条件
        verify(conditionEvaluator, times(2)).getFieldValue(any(), anyString());
        verify(conditionEvaluator, times(2)).evaluate(anyString(), anyString(), anyString(), any());
    }

    /**
     * 测试 OR 条件组合
     */
    @Test
    public void testOrConditionMatch() {
        // 准备测试数据
        StandardEvent event = createTestEvent();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", "DELETE");
        metadata.put("source", "API");
        event.setMetadata(metadata);

        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_003");
        rule.setRuleName("OR 条件规则");
        rule.setRuleCode("OR_RULE");
        rule.setConditionExpression("(action == 'DELETE') OR (source == 'WEB')");
        rule.setPriority(15);

        List<LifecycleRouteRule> rules = Collections.singletonList(rule);

        // 配置 Mock 行为
        when(conditionEvaluator.getFieldValue(any(), eq("action"))).thenReturn("DELETE");
        when(conditionEvaluator.evaluate(eq("action"), eq("=="), eq("DELETE"), any())).thenReturn(true);

        // 执行测试
        List<RouteMatchResult> results = routerEngine.match(event, rules);

        // 验证结果
        assertNotNull("结果不应为空", results);
        assertEquals("应该返回 1 个结果", 1, results.size());

        RouteMatchResult result = results.get(0);
        assertTrue("规则应该匹配", result.isMatched());
        assertEquals("规则 ID 应该匹配", "rule_003", result.getRuleId());

        // 验证 Mock 调用 - OR 条件第一个满足就应该返回
        verify(conditionEvaluator, atLeastOnce()).getFieldValue(any(), anyString());
    }

    /**
     * 测试优先级匹配（首条匹配生效）
     */
    @Test
    public void testPriorityMatch() {
        // 准备测试数据
        StandardEvent event = createTestEvent();
        event.setMetadataValue("action", "CREATE");

        // 创建多个规则，优先级不同
        LifecycleRouteRule rule1 = new LifecycleRouteRule();
        rule1.setId("rule_low");
        rule1.setRuleName("低优先级规则");
        rule1.setRuleCode("LOW_PRIORITY");
        rule1.setConditionExpression("(action == 'CREATE')");
        rule1.setPriority(5);

        LifecycleRouteRule rule2 = new LifecycleRouteRule();
        rule2.setId("rule_medium");
        rule2.setRuleName("中优先级规则");
        rule2.setRuleCode("MEDIUM_PRIORITY");
        rule2.setConditionExpression("(action == 'CREATE')");
        rule2.setPriority(10);

        LifecycleRouteRule rule3 = new LifecycleRouteRule();
        rule3.setId("rule_high");
        rule3.setRuleName("高优先级规则");
        rule3.setRuleCode("HIGH_PRIORITY");
        rule3.setConditionExpression("(action == 'CREATE')");
        rule3.setPriority(20);

        List<LifecycleRouteRule> rules = Arrays.asList(rule1, rule2, rule3);

        // 配置 Mock 行为
        when(conditionEvaluator.getFieldValue(any(), eq("action"))).thenReturn("CREATE");
        when(conditionEvaluator.evaluate(eq("action"), eq("=="), eq("CREATE"), any())).thenReturn(true);

        // 执行测试
        List<RouteMatchResult> results = routerEngine.match(event, rules);

        // 验证结果
        assertNotNull("结果不应为空", results);
        assertEquals("应该返回 3 个结果", 3, results.size());

        // 验证排序 - 优先级高的应该在前面
        assertEquals("第一个应该是高优先级规则", "rule_high", results.get(0).getRuleId());
        assertEquals("第二个应该是中优先级规则", "rule_medium", results.get(1).getRuleId());
        assertEquals("第三个应该是低优先级规则", "rule_low", results.get(2).getRuleId());

        // 所有规则都应该匹配
        for (RouteMatchResult result : results) {
            assertTrue("规则应该匹配", result.isMatched());
        }
    }

    /**
     * 测试无匹配情况
     */
    @Test
    public void testNoMatch() {
        // 准备测试数据
        StandardEvent event = createTestEvent();
        event.setMetadataValue("action", "QUERY");

        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_004");
        rule.setRuleName("不匹配规则");
        rule.setRuleCode("NO_MATCH_RULE");
        rule.setConditionExpression("(action == 'CREATE')");
        rule.setPriority(10);

        List<LifecycleRouteRule> rules = Collections.singletonList(rule);

        // 配置 Mock 行为
        when(conditionEvaluator.getFieldValue(any(), eq("action"))).thenReturn("QUERY");
        when(conditionEvaluator.evaluate(eq("action"), eq("=="), eq("CREATE"), any())).thenReturn(false);

        // 执行测试
        List<RouteMatchResult> results = routerEngine.match(event, rules);

        // 验证结果
        assertNotNull("结果不应为空", results);
        assertEquals("应该返回 1 个结果", 1, results.size());

        RouteMatchResult result = results.get(0);
        assertFalse("规则应该不匹配", result.isMatched());
        assertEquals("规则 ID 应该匹配", "rule_004", result.getRuleId());
        assertNotNull("应该有不匹配原因", result.getUnmatchReason());

        // 验证 Mock 调用
        verify(conditionEvaluator, times(1)).getFieldValue(any(), eq("action"));
        verify(conditionEvaluator, times(1)).evaluate(eq("action"), eq("=="), eq("CREATE"), any());
    }

    /**
     * 测试无条件表达式（默认匹配）
     */
    @Test
    public void testNoConditionExpression() {
        // 准备测试数据
        StandardEvent event = createTestEvent();

        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_005");
        rule.setRuleName("无条件规则");
        rule.setRuleCode("NO_CONDITION_RULE");
        rule.setConditionExpression(null); // 无条件表达式
        rule.setPriority(10);

        List<LifecycleRouteRule> rules = Collections.singletonList(rule);

        // 执行测试
        List<RouteMatchResult> results = routerEngine.match(event, rules);

        // 验证结果
        assertNotNull("结果不应为空", results);
        assertEquals("应该返回 1 个结果", 1, results.size());

        RouteMatchResult result = results.get(0);
        assertTrue("规则应该默认匹配", result.isMatched());
        assertEquals("规则 ID 应该匹配", "rule_005", result.getRuleId());
    }

    /**
     * 测试复杂条件组合
     */
    @Test
    public void testComplexCondition() {
        // 准备测试数据
        StandardEvent event = createTestEvent();
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("action", "APPROVE");
        metadata.put("priority", 15);
        metadata.put("source", "WEB");
        event.setMetadata(metadata);

        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_006");
        rule.setRuleName("复杂条件规则");
        rule.setRuleCode("COMPLEX_RULE");
        rule.setConditionExpression("((action == 'APPROVE') AND (priority >= 10)) OR (source == 'ADMIN')");
        rule.setPriority(25);

        List<LifecycleRouteRule> rules = Collections.singletonList(rule);

        // 配置 Mock 行为
        when(conditionEvaluator.getFieldValue(any(), eq("action"))).thenReturn("APPROVE");
        when(conditionEvaluator.getFieldValue(any(), eq("priority"))).thenReturn(15);
        when(conditionEvaluator.getFieldValue(any(), eq("source"))).thenReturn("WEB");
        when(conditionEvaluator.evaluate(eq("action"), eq("=="), eq("APPROVE"), any())).thenReturn(true);
        when(conditionEvaluator.evaluate(eq("priority"), eq(">="), eq("10"), any())).thenReturn(true);

        // 执行测试
        List<RouteMatchResult> results = routerEngine.match(event, rules);

        // 验证结果
        assertNotNull("结果不应为空", results);
        assertEquals("应该返回 1 个结果", 1, results.size());

        RouteMatchResult result = results.get(0);
        assertTrue("规则应该匹配", result.isMatched());
        assertEquals("规则 ID 应该匹配", "rule_006", result.getRuleId());
    }

    /**
     * 创建测试事件
     */
    private StandardEvent createTestEvent() {
        StandardEvent event = new StandardEvent();
        event.setEventId("test_event_" + System.currentTimeMillis());
        event.setBusinessTypeId("test_biz_type");
        event.setEventType("TEST_EVENT");
        event.setBusinessInstanceId("test_instance");
        event.setMetadata(new HashMap<>());
        return event;
    }
}
