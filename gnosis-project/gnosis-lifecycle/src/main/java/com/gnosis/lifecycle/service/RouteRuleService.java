package com.gnosis.lifecycle.service;

import com.gnosis.lifecycle.domain.LifecycleBusinessType;
import com.gnosis.lifecycle.domain.LifecycleRouteRule;
import com.gnosis.lifecycle.dto.RouteRuleCreateRequest;
import com.gnosis.lifecycle.dto.RouteRuleVO;
import com.gnosis.lifecycle.repository.LifecycleBusinessTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.gnosis.lifecycle.engine.RouteRuleManager;
import com.gnosis.lifecycle.repository.LifecycleRouteRuleMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * 路由规则服务
 */
@Service
public class RouteRuleService {

    private static final Logger log = LoggerFactory.getLogger(RouteRuleService.class);

    @Autowired
    private RouteRuleManager routeRuleManager;

    @Autowired
    private LifecycleRouteRuleMapper routeRuleMapper;

    @Autowired
    private LifecycleBusinessTypeMapper businessTypeMapper;

    /**
     * 创建路由规则
     *
     * @param request 创建请求
     * @return 路由规则 VO
     */
    @Transactional(rollbackFor = Exception.class)
    public RouteRuleVO create(RouteRuleCreateRequest request) {
        log.info("[RouteRuleService] 创建路由规则：businessTypeId={}, eventType={}, ruleName={}", 
                request.getBusinessTypeId(), request.getEventType(), request.getRuleName());

        String ruleId = routeRuleManager.create(request);

        return getById(ruleId);
    }

    /**
     * 更新路由规则
     *
     * @param id 规则 ID
     * @param request 更新请求
     * @return 路由规则 VO
     */
    @Transactional(rollbackFor = Exception.class)
    public RouteRuleVO update(String id, RouteRuleCreateRequest request) {
        log.info("[RouteRuleService] 更新路由规则：id={}", id);

        routeRuleManager.update(id, request);

        return getById(id);
    }

    /**
     * 根据 ID 查询路由规则
     *
     * @param id 规则 ID
     * @return 路由规则 VO
     */
    public RouteRuleVO getById(String id) {
        log.debug("[RouteRuleService] 查询路由规则：id={}", id);

        LifecycleRouteRule routeRule = routeRuleManager.getById(id);
        if (routeRule == null) {
            throw new IllegalArgumentException("路由规则不存在：" + id);
        }

        return convertToVO(routeRule);
    }

    /**
     * 查询路由规则列表
     *
     * @param businessTypeId 业务类型 ID
     * @param eventType 事件类型
     * @return 路由规则列表 VO
     */
    public List<RouteRuleVO> list(String businessTypeId, String eventType) {
        log.debug("[RouteRuleService] 查询路由规则列表：businessTypeId={}, eventType={}", businessTypeId, eventType);

        List<LifecycleRouteRule> routeRules = routeRuleManager.list(businessTypeId, eventType);

        List<RouteRuleVO> voList = new ArrayList<>();
        for (LifecycleRouteRule routeRule : routeRules) {
            voList.add(convertToVO(routeRule));
        }

        log.debug("[RouteRuleService] 路由规则列表获取成功，数量：{}", voList.size());
        return voList;
    }

    /**
     * 删除路由规则
     *
     * @param id 规则 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void delete(String id) {
        log.info("[RouteRuleService] 删除路由规则：id={}", id);
        routeRuleManager.delete(id);
        log.info("[RouteRuleService] 路由规则删除成功：id={}", id);
    }

    /**
     * 批量删除路由规则
     *
     * @param ids 规则 ID 数组
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(String[] ids) {
        log.info("[RouteRuleService] 批量删除路由规则，数量：{}", ids.length);
        routeRuleManager.batchDelete(ids);
        log.info("[RouteRuleService] 批量删除成功");
    }

    /**
     * 批量启用路由规则
     *
     * @param ids 规则 ID 数组
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchEnable(String[] ids) {
        log.info("[RouteRuleService] 批量启用路由规则，数量：{}", ids.length);
        routeRuleManager.batchEnable(ids);
        log.info("[RouteRuleService] 批量启用成功");
    }

    /**
     * 批量禁用路由规则
     *
     * @param ids 规则 ID 数组
     */
    @Transactional(rollbackFor = Exception.class)
    public void batchDisable(String[] ids) {
        log.info("[RouteRuleService] 批量禁用路由规则，数量：{}", ids.length);
        routeRuleManager.batchDisable(ids);
        log.info("[RouteRuleService] 批量禁用成功");
    }

    /**
     * 转换为 VO
     */
    private RouteRuleVO convertToVO(LifecycleRouteRule routeRule) {
        RouteRuleVO vo = new RouteRuleVO();
        BeanUtils.copyProperties(routeRule, vo);

        // 获取业务类型名称
        if (routeRule.getBusinessTypeId() != null) {
            LifecycleBusinessType businessType = businessTypeMapper.selectById(routeRule.getBusinessTypeId());
            if (businessType != null) {
                vo.setBusinessTypeName(businessType.getName());
            }
        }

        // 解析条件表达式为条件列表
        if (routeRule.getConditionExpression() != null) {
            try {
                List<RouteRuleVO.ConditionVO> conditions = parseConditions(routeRule.getConditionExpression());
                vo.setConditions(conditions);
            } catch (Exception e) {
                log.warn("[RouteRuleService] 解析条件表达式失败：{}", routeRule.getConditionExpression(), e);
            }
        }

        return vo;
    }

    /**
     * 解析条件表达式
     */
    private List<RouteRuleVO.ConditionVO> parseConditions(String expression) {
        List<RouteRuleVO.ConditionVO> conditions = new ArrayList<>();
        
        if (expression == null || expression.trim().isEmpty()) {
            return conditions;
        }

        // 简单的解析逻辑，实际项目可能需要更复杂的解析器
        // 假设表达式格式为：(field1 == 'value1') AND (field2 > 100)
        String[] parts = expression.split("\\s+(AND|OR)\\s+");
        for (String part : parts) {
            // 去除括号
            part = part.trim().replaceAll("^\\(|\\)$", "");
            
            // 解析 field operator value
            String[] tokens = part.split("\\s*(==|!=|>=|<=|>|<)\\s*");
            if (tokens.length >= 2) {
                RouteRuleVO.ConditionVO condition = new RouteRuleVO.ConditionVO();
                condition.setField(tokens[0].trim());
                condition.setOperator(tokens[1].trim());
                if (tokens.length > 2) {
                    condition.setValue(tokens[2].trim().replaceAll("^'|'$", ""));
                }
                conditions.add(condition);
            }
        }

        return conditions;
    }
}
