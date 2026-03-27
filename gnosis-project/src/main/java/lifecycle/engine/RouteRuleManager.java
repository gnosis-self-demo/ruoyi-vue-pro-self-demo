package lifecycle.engine;

import lifecycle.domain.LifecycleBusinessType;
import lifecycle.domain.LifecycleRouteRule;
import lifecycle.dto.RouteRuleCreateRequest;
import lifecycle.repository.LifecycleBusinessTypeMapper;
import lifecycle.repository.LifecycleRouteRuleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 路由规则管理器
 * 负责路由规则的 CRUD 操作
 */
@Component
public class RouteRuleManager {

    private static final Logger log = LoggerFactory.getLogger(RouteRuleManager.class);

    @Autowired
    private LifecycleRouteRuleMapper routeRuleMapper;

    @Autowired
    private LifecycleBusinessTypeMapper businessTypeMapper;

    /**
     * 创建路由规则
     *
     * @param request 创建请求
     * @return 创建的规则 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public String create(RouteRuleCreateRequest request) {
        log.info("[RouteRuleManager] 创建路由规则：businessTypeId={}, eventType={}, ruleName={}", 
                request.getBusinessTypeId(), request.getEventType(), request.getRuleName());

        // 校验业务类型是否存在
        LifecycleBusinessType businessType = businessTypeMapper.selectById(request.getBusinessTypeId());
        if (businessType == null) {
            throw new IllegalArgumentException("业务类型不存在：" + request.getBusinessTypeId());
        }

        String userId = getCurrentUserId();
        Date now = new Date();

        // 构建条件表达式
        String conditionExpression = buildConditionExpression(request);

        // 创建路由规则
        LifecycleRouteRule routeRule = new LifecycleRouteRule();
        routeRule.setId(java.util.UUID.randomUUID().toString().replace("-", ""));
        routeRule.setBusinessTypeId(request.getBusinessTypeId());
        routeRule.setEventType(request.getEventType());
        routeRule.setRuleName(request.getRuleName());
        routeRule.setRuleCode(request.getRuleCode());
        routeRule.setDescription(request.getDescription());
        routeRule.setConditionExpression(conditionExpression);
        routeRule.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        routeRule.setIsActive(request.getIsActive() != null ? (request.getIsActive() ? 1 : 0) : 1);
        routeRule.setVersion(1); // 初始版本为 1

        routeRule.setCreateUserId(userId);
        routeRule.setUpdateUserId(userId);
        routeRule.setCreateTime(now);
        routeRule.setUpdateTime(now);

        routeRuleMapper.insert(routeRule);

        log.info("[RouteRuleManager] 路由规则创建成功：ruleId={}", routeRule.getId());
        return routeRule.getId();
    }

    /**
     * 更新路由规则
     *
     * @param id 规则 ID
     * @param request 更新请求
     */
    @Transactional(rollbackFor = Exception.class)
    public void update(String id, RouteRuleCreateRequest request) {
        log.info("[RouteRuleManager] 更新路由规则：id={}", id);

        LifecycleRouteRule existing = routeRuleMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("路由规则不存在：" + id);
        }

        String userId = getCurrentUserId();
        Date now = new Date();

        // 构建条件表达式
        String conditionExpression = buildConditionExpression(request);

        // 更新字段
        if (request.getRuleName() != null) {
            existing.setRuleName(request.getRuleName());
        }
        if (request.getRuleCode() != null) {
            existing.setRuleCode(request.getRuleCode());
        }
        if (request.getDescription() != null) {
            existing.setDescription(request.getDescription());
        }
        if (conditionExpression != null) {
            existing.setConditionExpression(conditionExpression);
        }
        if (request.getPriority() != null) {
            existing.setPriority(request.getPriority());
        }
        if (request.getIsActive() != null) {
            existing.setIsActive(request.getIsActive() ? 1 : 0);
        }

        // 版本号 +1
        existing.setVersion(existing.getVersion() + 1);

        existing.setUpdateUserId(userId);
        existing.setUpdateTime(now);

        routeRuleMapper.updateById(existing);

        log.info("[RouteRuleManager] 路由规则更新成功：id={}, version={}", id, existing.getVersion());
    }

    /**
     * 根据 ID 查询路由规则
     *
     * @param id 规则 ID
     * @return 路由规则实体
     */
    public LifecycleRouteRule getById(String id) {
        log.debug("[RouteRuleManager] 查询路由规则：id={}", id);
        return routeRuleMapper.selectById(id);
    }

    /**
     * 查询路由规则列表
     *
     * @param businessTypeId 业务类型 ID
     * @param eventType 事件类型
     * @return 路由规则列表
     */
    public List<LifecycleRouteRule> list(String businessTypeId, String eventType) {
        log.debug("[RouteRuleManager] 查询路由规则列表：businessTypeId={}, eventType={}", businessTypeId, eventType);

        LifecycleRouteRule query = new LifecycleRouteRule();
        query.setBusinessTypeId(businessTypeId);
        query.setEventType(eventType);

        return routeRuleMapper.selectList(query);
    }

    /**
     * 删除路由规则
     *
     * @param id 规则 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        log.info("[RouteRuleManager] 删除路由规则：id={}", id);

        LifecycleRouteRule existing = routeRuleMapper.selectById(id);
        if (existing == null) {
            throw new IllegalArgumentException("路由规则不存在：" + id);
        }

        routeRuleMapper.deleteById(id);

        log.info("[RouteRuleManager] 路由规则删除成功：id={}", id);
    }

    /**
     * 批量删除路由规则
     *
     * @param ids 规则 ID 数组
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(String[] ids) {
        log.info("[RouteRuleManager] 批量删除路由规则，数量：{}", ids.length);
        int count = routeRuleMapper.deleteByIds(ids);
        log.info("[RouteRuleManager] 批量删除成功，删除数量：{}", count);
    }

    /**
     * 批量启用路由规则
     *
     * @param ids 规则 ID 数组
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchEnable(String[] ids) {
        log.info("[RouteRuleManager] 批量启用路由规则，数量：{}", ids.length);

        String userId = getCurrentUserId();
        Date now = new Date();

        for (String id : ids) {
            try {
                LifecycleRouteRule routeRule = routeRuleMapper.selectById(id);
                if (routeRule != null) {
                    routeRule.setIsActive(1);
                    routeRule.setUpdateUserId(userId);
                    routeRule.setUpdateTime(now);
                    routeRuleMapper.updateById(routeRule);
                }
            } catch (Exception e) {
                log.error("[RouteRuleManager] 启用路由规则失败：{}", id, e);
            }
        }

        log.info("[RouteRuleManager] 批量启用完成");
    }

    /**
     * 批量禁用路由规则
     *
     * @param ids 规则 ID 数组
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDisable(String[] ids) {
        log.info("[RouteRuleManager] 批量禁用路由规则，数量：{}", ids.length);

        String userId = getCurrentUserId();
        Date now = new Date();

        for (String id : ids) {
            try {
                LifecycleRouteRule routeRule = routeRuleMapper.selectById(id);
                if (routeRule != null) {
                    routeRule.setIsActive(0);
                    routeRule.setUpdateUserId(userId);
                    routeRule.setUpdateTime(now);
                    routeRuleMapper.updateById(routeRule);
                }
            } catch (Exception e) {
                log.error("[RouteRuleManager] 禁用路由规则失败：{}", id, e);
            }
        }

        log.info("[RouteRuleManager] 批量禁用完成");
    }

    /**
     * 构建条件表达式
     */
    private String buildConditionExpression(RouteRuleCreateRequest request) {
        // 如果直接提供了条件表达式，优先使用
        if (request.getConditionExpression() != null && !request.getConditionExpression().trim().isEmpty()) {
            return request.getConditionExpression();
        }

        // 否则根据条件列表构建
        if (request.getConditions() == null || request.getConditions().isEmpty()) {
            return null;
        }

        List<RouteRuleCreateRequest.ConditionConfig> conditions = request.getConditions();
        String logicOperator = request.getLogicOperator() != null ? request.getLogicOperator() : "AND";

        StringBuilder expression = new StringBuilder();
        for (int i = 0; i < conditions.size(); i++) {
            RouteRuleCreateRequest.ConditionConfig condition = conditions.get(i);
            String field = condition.getField();
            String operator = condition.getOperator();
            String value = condition.getValue();

            // 处理字符串值的引号
            String formattedValue = value;
            if (!isNumeric(value)) {
                formattedValue = "'" + value + "'";
            }

            expression.append("(").append(field).append(" ").append(operator).append(" ").append(formattedValue).append(")");

            if (i < conditions.size() - 1) {
                expression.append(" ").append(logicOperator).append(" ");
            }
        }

        return expression.toString();
    }

    /**
     * 判断是否为数字
     */
    private boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * 获取当前用户 ID
     */
    private String getCurrentUserId() {
        return "system";
    }
}
