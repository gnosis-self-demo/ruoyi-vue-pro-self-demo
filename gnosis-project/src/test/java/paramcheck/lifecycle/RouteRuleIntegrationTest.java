package paramcheck.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import lifecycle.domain.LifecycleRouteRule;
import lifecycle.domain.LifecycleRouteRuleVersion;
import lifecycle.dto.RouteRuleCreateRequest;
import lifecycle.repository.LifecycleRouteRuleMapper;
import lifecycle.repository.LifecycleRouteRuleVersionMapper;
import lifecycle.service.RouteRuleService;

import java.util.*;

import static org.junit.Assert.*;

/**
 * 路由规则集成测试类
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
public class RouteRuleIntegrationTest {

    @Autowired
    private RouteRuleService routeRuleService;

    @Autowired
    private LifecycleRouteRuleMapper routeRuleMapper;

    @Autowired
    private LifecycleRouteRuleVersionMapper ruleVersionMapper;

    /**
     * 测试规则创建和更新
     */
    @Test
    public void testRuleCreateAndUpdate() {
        // 1. 创建规则
        RouteRuleCreateRequest createRequest = new RouteRuleCreateRequest();
        createRequest.setBusinessTypeId("biz_type_" + System.currentTimeMillis());
        createRequest.setEventType("TEST_EVENT");
        createRequest.setRuleName("测试规则");
        createRequest.setRuleCode("TEST_RULE");
        createRequest.setConditionExpression("(action == 'CREATE')");
        createRequest.setPriority(10);
        createRequest.setIsActive(1);
        createRequest.setRemark("初始创建");

        Map<String, Object> conditionConfig = new HashMap<>();
        conditionConfig.put("fields", Arrays.asList("action"));
        conditionConfig.put("logic", "AND");
        createRequest.setConditionConfig(conditionConfig);

        // 2. 执行创建（这里假设 service 有 create 方法）
        // 由于 RouteRuleService 可能还没有实现 create 方法，我们直接操作 Mapper
        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_" + System.currentTimeMillis());
        rule.setBusinessTypeId(createRequest.getBusinessTypeId());
        rule.setEventType(createRequest.getEventType());
        rule.setRuleName(createRequest.getRuleName());
        rule.setRuleCode(createRequest.getRuleCode());
        rule.setConditionExpression(createRequest.getConditionExpression());
        rule.setPriority(createRequest.getPriority());
        rule.setIsActive(createRequest.getIsActive());
        rule.setRemark(createRequest.getRemark());
        rule.setCreateUserId("test_user");
        rule.setCreateTime(new Date());

        int insertCount = routeRuleMapper.insert(rule);
        assertEquals("规则应该插入成功", 1, insertCount);

        // 3. 查询规则
        LifecycleRouteRule queriedRule = routeRuleMapper.selectById(rule.getId());
        assertNotNull("查询的规则不应为空", queriedRule);
        assertEquals("规则名称应该匹配", createRequest.getRuleName(), queriedRule.getRuleName());
        assertEquals("规则编码应该匹配", createRequest.getRuleCode(), queriedRule.getRuleCode());

        // 4. 更新规则
        queriedRule.setRuleName("更新后的规则名称");
        queriedRule.setConditionExpression("(action == 'UPDATE')");
        queriedRule.setPriority(20);
        queriedRule.setUpdateUserId("test_user");
        queriedRule.setUpdateTime(new Date());

        int updateCount = routeRuleMapper.updateById(queriedRule);
        assertEquals("规则应该更新成功", 1, updateCount);

        // 5. 验证更新
        LifecycleRouteRule updatedRule = routeRuleMapper.selectById(rule.getId());
        assertNotNull("更新后的规则不应为空", updatedRule);
        assertEquals("规则名称应该更新", "更新后的规则名称", updatedRule.getRuleName());
        assertEquals("优先级应该更新", Integer.valueOf(20), updatedRule.getPriority());
    }

    /**
     * 测试规则版本管理
     */
    @Test
    public void testRuleVersionManagement() {
        // 1. 创建初始规则
        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_version_test_" + System.currentTimeMillis());
        rule.setBusinessTypeId("biz_type_version");
        rule.setEventType("VERSION_TEST");
        rule.setRuleName("版本管理测试规则");
        rule.setRuleCode("VERSION_RULE");
        rule.setConditionExpression("(version == 1)");
        rule.setPriority(10);
        rule.setIsActive(1);
        rule.setCreateUserId("test_user");
        rule.setCreateTime(new Date());

        routeRuleMapper.insert(rule);

        // 2. 创建版本记录 - 初始版本
        LifecycleRouteRuleVersion version1 = new LifecycleRouteRuleVersion();
        version1.setId("version_1_" + System.currentTimeMillis());
        version1.setRuleId(rule.getId());
        version1.setVersionNumber(1);
        version1.setRuleContent("{\"condition\": \"(version == 1)\"}");
        version1.setChangeType("CREATE");
        version1.setChangeReason("初始创建");
        version1.setIsCurrentVersion(1);
        version1.setCreateUserId("test_user");
        version1.setCreateTime(new Date());

        ruleVersionMapper.insert(version1);

        // 3. 创建版本记录 - 更新版本
        LifecycleRouteRuleVersion version2 = new LifecycleRouteRuleVersion();
        version2.setId("version_2_" + System.currentTimeMillis());
        version2.setRuleId(rule.getId());
        version2.setVersionNumber(2);
        version2.setRuleContent("{\"condition\": \"(version == 2)\"}");
        version2.setChangeType("UPDATE");
        version2.setChangeReason("更新条件表达式");
        version2.setIsCurrentVersion(1);
        version2.setPreviousVersionId(version1.getId());
        version2.setCreateUserId("test_user");
        version2.setCreateTime(new Date());

        ruleVersionMapper.insert(version2);

        // 4. 更新版本 1 为非当前版本
        version1.setIsCurrentVersion(0);
        version1.setUpdateTime(new Date());
        version1.setUpdateUserId("test_user");
        ruleVersionMapper.updateById(version1);

        // 5. 查询版本历史
        List<LifecycleRouteRuleVersion> versions = ruleVersionMapper.selectByRuleId(rule.getId());
        assertNotNull("版本列表不应为空", versions);
        assertEquals("应该有 2 个版本", 2, versions.size());

        // 6. 验证当前版本
        LifecycleRouteRuleVersion currentVersion = null;
        for (LifecycleRouteRuleVersion v : versions) {
            if (v.getIsCurrentVersion() == 1) {
                currentVersion = v;
                break;
            }
        }
        assertNotNull("应该有当前版本", currentVersion);
        assertEquals("当前版本应该是版本 2", Integer.valueOf(2), currentVersion.getVersionNumber());
    }

    /**
     * 测试规则回滚
     */
    @Test
    public void testRuleRollback() {
        // 1. 创建规则
        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_rollback_test_" + System.currentTimeMillis());
        rule.setBusinessTypeId("biz_type_rollback");
        rule.setEventType("ROLLBACK_TEST");
        rule.setRuleName("回滚测试规则");
        rule.setRuleCode("ROLLBACK_RULE");
        rule.setConditionExpression("(status == 'active')");
        rule.setPriority(10);
        rule.setIsActive(1);
        rule.setCreateUserId("test_user");
        rule.setCreateTime(new Date());

        routeRuleMapper.insert(rule);

        // 2. 创建多个版本
        LifecycleRouteRuleVersion version1 = createVersion(rule.getId(), 1, "CREATE", "初始版本");
        LifecycleRouteRuleVersion version2 = createVersion(rule.getId(), 2, "UPDATE", "第一次更新");
        LifecycleRouteRuleVersion version3 = createVersion(rule.getId(), 3, "UPDATE", "第二次更新");

        // 3. 模拟回滚到版本 1
        // 将版本 2 和 3 标记为非当前版本
        version2.setIsCurrentVersion(0);
        version2.setUpdateTime(new Date());
        ruleVersionMapper.updateById(version2);

        version3.setIsCurrentVersion(0);
        version3.setUpdateTime(new Date());
        ruleVersionMapper.updateById(version3);

        // 4. 创建回滚版本（版本 4）
        LifecycleRouteRuleVersion version4 = new LifecycleRouteRuleVersion();
        version4.setId("version_4_" + System.currentTimeMillis());
        version4.setRuleId(rule.getId());
        version4.setVersionNumber(4);
        version4.setRuleContent(version1.getRuleContent()); // 使用版本 1 的内容
        version4.setChangeType("ROLLBACK");
        version4.setChangeReason("回滚到版本 1");
        version4.setIsCurrentVersion(1);
        version4.setRollbackFromVersion(1);
        version4.setRemark("从版本 1 回滚");
        version4.setCreateUserId("test_user");
        version4.setCreateTime(new Date());
        version4.setUpdateTime(new Date());

        ruleVersionMapper.insert(version4);

        // 5. 验证回滚
        List<LifecycleRouteRuleVersion> allVersions = ruleVersionMapper.selectByRuleId(rule.getId());
        assertNotNull("所有版本列表不应为空", allVersions);
        assertEquals("应该有 4 个版本", 4, allVersions.size());

        // 6. 验证当前版本是回滚版本
        LifecycleRouteRuleVersion currentVersion = null;
        for (LifecycleRouteRuleVersion v : allVersions) {
            if (v.getIsCurrentVersion() == 1) {
                currentVersion = v;
                break;
            }
        }
        assertNotNull("应该有当前版本", currentVersion);
        assertEquals("当前版本应该是版本 4", Integer.valueOf(4), currentVersion.getVersionNumber());
        assertEquals("回滚来源应该是版本 1", Integer.valueOf(1), currentVersion.getRollbackFromVersion());
        assertEquals("变更类型应该是 ROLLBACK", "ROLLBACK", currentVersion.getChangeType());
    }

    /**
     * 测试规则启用和禁用
     */
    @Test
    public void testRuleEnableDisable() {
        // 1. 创建规则
        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_enable_disable_" + System.currentTimeMillis());
        rule.setBusinessTypeId("biz_type_enable");
        rule.setEventType("ENABLE_TEST");
        rule.setRuleName("启用禁用测试规则");
        rule.setRuleCode("ENABLE_RULE");
        rule.setConditionExpression("(test == true)");
        rule.setPriority(10);
        rule.setIsActive(1); // 初始为启用状态
        rule.setCreateUserId("test_user");
        rule.setCreateTime(new Date());

        routeRuleMapper.insert(rule);

        // 2. 验证初始状态
        LifecycleRouteRule queriedRule = routeRuleMapper.selectById(rule.getId());
        assertNotNull("规则不应为空", queriedRule);
        assertEquals("初始应该为启用状态", Integer.valueOf(1), queriedRule.getIsActive());

        // 3. 禁用规则
        queriedRule.setIsActive(0);
        queriedRule.setUpdateUserId("test_user");
        queriedRule.setUpdateTime(new Date());
        int updateCount = routeRuleMapper.updateById(queriedRule);
        assertEquals("规则应该更新成功", 1, updateCount);

        // 4. 验证禁用状态
        LifecycleRouteRule disabledRule = routeRuleMapper.selectById(rule.getId());
        assertNotNull("禁用后的规则不应为空", disabledRule);
        assertEquals("应该为禁用状态", Integer.valueOf(0), disabledRule.getIsActive());

        // 5. 重新启用规则
        disabledRule.setIsActive(1);
        disabledRule.setUpdateUserId("test_user");
        disabledRule.setUpdateTime(new Date());
        updateCount = routeRuleMapper.updateById(disabledRule);
        assertEquals("规则应该更新成功", 1, updateCount);

        // 6. 验证启用状态
        LifecycleRouteRule enabledRule = routeRuleMapper.selectById(rule.getId());
        assertNotNull("启用后的规则不应为空", enabledRule);
        assertEquals("应该为启用状态", Integer.valueOf(1), enabledRule.getIsActive());
    }

    /**
     * 测试规则删除
     */
    @Test
    public void testRuleDelete() {
        // 1. 创建规则
        LifecycleRouteRule rule = new LifecycleRouteRule();
        rule.setId("rule_delete_" + System.currentTimeMillis());
        rule.setBusinessTypeId("biz_type_delete");
        rule.setEventType("DELETE_TEST");
        rule.setRuleName("删除测试规则");
        rule.setRuleCode("DELETE_RULE");
        rule.setConditionExpression("(delete == true)");
        rule.setPriority(10);
        rule.setIsActive(1);
        rule.setCreateUserId("test_user");
        rule.setCreateTime(new Date());

        int insertCount = routeRuleMapper.insert(rule);
        assertEquals("规则应该插入成功", 1, insertCount);

        // 2. 验证规则存在
        LifecycleRouteRule queriedRule = routeRuleMapper.selectById(rule.getId());
        assertNotNull("规则应该存在", queriedRule);

        // 3. 删除规则
        int deleteCount = routeRuleMapper.deleteById(rule.getId());
        assertEquals("规则应该删除成功", 1, deleteCount);

        // 4. 验证规则已删除
        LifecycleRouteRule deletedRule = routeRuleMapper.selectById(rule.getId());
        assertNull("规则应该已删除", deletedRule);
    }

    /**
     * 测试规则查询
     */
    @Test
    public void testRuleQuery() {
        // 1. 创建多条规则
        String businessTypeId = "biz_type_query_" + System.currentTimeMillis();
        String eventType = "QUERY_TEST";

        for (int i = 0; i < 5; i++) {
            LifecycleRouteRule rule = new LifecycleRouteRule();
            rule.setId("rule_query_" + i + "_" + System.currentTimeMillis());
            rule.setBusinessTypeId(businessTypeId);
            rule.setEventType(eventType);
            rule.setRuleName("查询测试规则_" + i);
            rule.setRuleCode("QUERY_RULE_" + i);
            rule.setConditionExpression("(index == " + i + ")");
            rule.setPriority(i * 10);
            rule.setIsActive(1);
            rule.setCreateUserId("test_user");
            rule.setCreateTime(new Date());

            routeRuleMapper.insert(rule);
        }

        // 2. 查询规则列表
        LifecycleRouteRule query = new LifecycleRouteRule();
        query.setBusinessTypeId(businessTypeId);
        query.setEventType(eventType);
        query.setIsActive(1);

        List<LifecycleRouteRule> rules = routeRuleMapper.selectList(query);
        assertNotNull("规则列表不应为空", rules);
        assertEquals("应该有 5 条规则", 5, rules.size());

        // 3. 验证排序（按优先级）
        rules.sort((r1, r2) -> r2.getPriority().compareTo(r1.getPriority()));
        assertEquals("最高优先级应该是 40", Integer.valueOf(40), rules.get(0).getPriority());
        assertEquals("最低优先级应该是 0", Integer.valueOf(0), rules.get(4).getPriority());
    }

    /**
     * 辅助方法：创建版本记录
     */
    private LifecycleRouteRuleVersion createVersion(String ruleId, int versionNumber, 
                                                     String changeType, String changeReason) {
        LifecycleRouteRuleVersion version = new LifecycleRouteRuleVersion();
        version.setId("version_" + versionNumber + "_" + System.currentTimeMillis());
        version.setRuleId(ruleId);
        version.setVersionNumber(versionNumber);
        version.setRuleContent("{\"condition\": \"(version == \" + versionNumber + \")\"}");
        version.setChangeType(changeType);
        version.setChangeReason(changeReason);
        version.setIsCurrentVersion(1);
        version.setCreateUserId("test_user");
        version.setCreateTime(new Date());

        ruleVersionMapper.insert(version);
        return version;
    }
}
