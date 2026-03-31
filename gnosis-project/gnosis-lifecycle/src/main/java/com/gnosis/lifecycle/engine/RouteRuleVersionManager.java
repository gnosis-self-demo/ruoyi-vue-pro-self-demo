package com.gnosis.lifecycle.engine;

import com.gnosis.lifecycle.domain.LifecycleRouteRule;
import com.gnosis.lifecycle.repository.LifecycleRouteRuleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 路由规则版本管理器
 * 负责路由规则的版本控制和历史管理
 */
@Component
public class RouteRuleVersionManager {

    private static final Logger log = LoggerFactory.getLogger(RouteRuleVersionManager.class);

    @Autowired
    private LifecycleRouteRuleMapper routeRuleMapper;

    /**
     * 创建新版本
     * 当规则更新时，可以选择创建新版本而不是直接覆盖
     *
     * @param sourceRuleId 源规则 ID
     * @param newRule 新规则数据
     * @return 新版本规则 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String createNewVersion(String sourceRuleId, LifecycleRouteRule newRule) {
        log.info("[RouteRuleVersionManager] 创建新版本：sourceRuleId={}", sourceRuleId);

        LifecycleRouteRule sourceRule = routeRuleMapper.selectById(sourceRuleId);
        if (sourceRule == null) {
            throw new IllegalArgumentException("源规则不存在：" + sourceRuleId);
        }

        String userId = getCurrentUserId();
        Date now = new Date();

        // 创建新版本规则
        LifecycleRouteRule newVersionRule = new LifecycleRouteRule();
        newVersionRule.setId(java.util.UUID.randomUUID().toString().replace("-", ""));
        newVersionRule.setBusinessTypeId(sourceRule.getBusinessTypeId());
        newVersionRule.setEventType(sourceRule.getEventType());
        newVersionRule.setRuleName(sourceRule.getRuleName());
        newVersionRule.setRuleCode(sourceRule.getRuleCode() + "_v" + (sourceRule.getVersion() + 1));
        newVersionRule.setDescription(sourceRule.getDescription());
        newVersionRule.setConditionExpression(newRule.getConditionExpression());
        newVersionRule.setPriority(newRule.getPriority() != null ? newRule.getPriority() : sourceRule.getPriority());
        newVersionRule.setIsActive(0); // 新版本默认禁用
        newVersionRule.setVersion(sourceRule.getVersion() + 1);

        newVersionRule.setCreateUserId(userId);
        newVersionRule.setUpdateUserId(userId);
        newVersionRule.setCreateTime(now);
        newVersionRule.setUpdateTime(now);

        routeRuleMapper.insert(newVersionRule);

        log.info("[RouteRuleVersionManager] 新版本创建成功：newRuleId={}, version={}", 
                newVersionRule.getId(), newVersionRule.getVersion());

        return newVersionRule.getId();
    }

    /**
     * 获取规则的所有版本
     *
     * @param businessTypeId 业务类型 ID
     * @param ruleCode 规则编码
     * @return 版本列表
     */
    public List<LifecycleRouteRule> getAllVersions(String businessTypeId, String ruleCode) {
        log.debug("[RouteRuleVersionManager] 获取规则所有版本：businessTypeId={}, ruleCode={}", businessTypeId, ruleCode);

        // 查询所有匹配的版本
        LifecycleRouteRule query = new LifecycleRouteRule();
        query.setBusinessTypeId(businessTypeId);
        // 这里需要查询所有以 ruleCode 开头的规则（包括_v1, _v2 等）
        // 由于 MyBatis 不支持 LIKE 查询，这里简化处理，直接返回列表

        List<LifecycleRouteRule> allRules = routeRuleMapper.selectList(query);
        List<LifecycleRouteRule> versions = new ArrayList<>();
        
        for (LifecycleRouteRule rule : allRules) {
            if (rule.getRuleCode() != null && rule.getRuleCode().startsWith(ruleCode)) {
                versions.add(rule);
            }
        }

        // 按版本号排序
        versions.sort((r1, r2) -> r2.getVersion().compareTo(r1.getVersion()));

        return versions;
    }

    /**
     * 获取最新版本
     *
     * @param businessTypeId 业务类型 ID
     * @param ruleCode 规则编码
     * @return 最新版本规则
     */
    public LifecycleRouteRule getLatestVersion(String businessTypeId, String ruleCode) {
        log.debug("[RouteRuleVersionManager] 获取最新版本：businessTypeId={}, ruleCode={}", businessTypeId, ruleCode);

        List<LifecycleRouteRule> versions = getAllVersions(businessTypeId, ruleCode);
        if (versions.isEmpty()) {
            return null;
        }

        return versions.get(0);
    }

    /**
     * 回滚到指定版本
     *
     * @param targetRuleId 目标版本规则 ID
     * @return 回滚后的规则 ID（会创建一个新版本）
     */
    @Transactional(rollbackFor = Exception.class)
    public String rollbackToVersion(String targetRuleId) {
        log.info("[RouteRuleVersionManager] 回滚到指定版本：targetRuleId={}", targetRuleId);

        LifecycleRouteRule targetRule = routeRuleMapper.selectById(targetRuleId);
        if (targetRule == null) {
            throw new IllegalArgumentException("目标版本规则不存在：" + targetRuleId);
        }

        // 创建新版本，使用目标版本的配置
        return createNewVersion(targetRuleId, targetRule);
    }

    /**
     * 删除旧版本
     * 保留最近的 N 个版本
     *
     * @param businessTypeId 业务类型 ID
     * @param ruleCode 规则编码
     * @param keepCount 保留的版本数量
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteOldVersions(String businessTypeId, String ruleCode, int keepCount) {
        log.info("[RouteRuleVersionManager] 删除旧版本：businessTypeId={}, ruleCode={}, keepCount={}", 
                businessTypeId, ruleCode, keepCount);

        List<LifecycleRouteRule> versions = getAllVersions(businessTypeId, ruleCode);
        if (versions.size() <= keepCount) {
            log.debug("[RouteRuleVersionManager] 版本数量未超过限制，无需删除");
            return;
        }

        // 删除旧版本（保留最新的 keepCount 个）
        for (int i = keepCount; i < versions.size(); i++) {
            LifecycleRouteRule oldVersion = versions.get(i);
            try {
                routeRuleMapper.deleteById(oldVersion.getId());
                log.debug("[RouteRuleVersionManager] 删除旧版本：ruleId={}, version={}", 
                        oldVersion.getId(), oldVersion.getVersion());
            } catch (Exception e) {
                log.error("[RouteRuleVersionManager] 删除旧版本失败：ruleId={}", oldVersion.getId(), e);
            }
        }

        log.info("[RouteRuleVersionManager] 旧版本删除完成");
    }

    /**
     * 获取当前用户 ID
     */
    private String getCurrentUserId() {
        return "system";
    }
}
