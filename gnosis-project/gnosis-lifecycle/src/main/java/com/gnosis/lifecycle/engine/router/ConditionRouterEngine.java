package com.gnosis.lifecycle.engine.router;

import com.gnosis.lifecycle.domain.LifecycleRouteRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.gnosis.lifecycle.adapter.StandardEvent;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 条件路由引擎
 * 核心路由匹配引擎，支持 AND/OR 条件组合
 */
@Component
public class ConditionRouterEngine {

    private static final Logger log = LoggerFactory.getLogger(ConditionRouterEngine.class);

    @Autowired
    private ConditionEvaluator conditionEvaluator;

    // 条件表达式解析正则
    private static final Pattern CONDITION_PATTERN = Pattern.compile(
            "\\(?(\\w+)\\s*(==|!=|>=|<=|>|<|contains|in|notin)\\s*([^)]+)\\)?"
    );

    /**
     * 匹配路由规则
     *
     * @param event 标准事件
     * @param routeRules 路由规则列表
     * @return 匹配结果列表
     */
    public List<RouteMatchResult> match(StandardEvent event, List<LifecycleRouteRule> routeRules) {
        log.info("[ConditionRouterEngine] 开始匹配路由规则，规则数量：{}", routeRules.size());

        List<RouteMatchResult> results = new ArrayList<>();

        for (LifecycleRouteRule rule : routeRules) {
            RouteMatchResult result = matchRule(event, rule);
            results.add(result);

            if (result.isMatched()) {
                log.info("[ConditionRouterEngine] 规则匹配成功：ruleId={}, ruleName={}", 
                        rule.getId(), rule.getRuleName());
            } else {
                log.debug("[ConditionRouterEngine] 规则匹配失败：ruleId={}, reason={}", 
                        rule.getId(), result.getUnmatchReason());
            }
        }

        // 按优先级排序
        results.sort((r1, r2) -> {
            if (r1.isMatched() && !r2.isMatched()) {
                return -1;
            }
            if (!r1.isMatched() && r2.isMatched()) {
                return 1;
            }
            if (r1.getPriority() == null) {
                return 1;
            }
            if (r2.getPriority() == null) {
                return -1;
            }
            return r2.getPriority().compareTo(r1.getPriority());
        });

        return results;
    }

    /**
     * 匹配单个规则
     */
    private RouteMatchResult matchRule(StandardEvent event, LifecycleRouteRule rule) {
        log.debug("[ConditionRouterEngine] 匹配规则：ruleId={}, ruleName={}", rule.getId(), rule.getRuleName());

        String conditionExpression = rule.getConditionExpression();

        // 如果没有条件表达式，默认匹配成功
        if (conditionExpression == null || conditionExpression.trim().isEmpty()) {
            return RouteMatchResult.success(
                    rule.getId(),
                    rule.getRuleName(),
                    rule.getRuleCode(),
                    rule.getPriority(),
                    null,
                    null,
                    "无条件表达式，默认匹配"
            );
        }

        try {
            boolean matched = evaluateExpression(event, conditionExpression);
            
            if (matched) {
                return RouteMatchResult.success(
                        rule.getId(),
                        rule.getRuleName(),
                        rule.getRuleCode(),
                        rule.getPriority(),
                        null,
                        null,
                        "所有条件满足"
                );
            } else {
                return RouteMatchResult.failure(
                        rule.getId(),
                        rule.getRuleName(),
                        "条件不满足"
                );
            }
        } catch (Exception e) {
            log.error("[ConditionRouterEngine] 条件表达式评估失败：ruleId={}, expression={}", 
                    rule.getId(), conditionExpression, e);
            return RouteMatchResult.failure(
                    rule.getId(),
                    rule.getRuleName(),
                    "条件评估异常：" + e.getMessage()
            );
        }
    }

    /**
     * 评估条件表达式
     */
    private boolean evaluateExpression(StandardEvent event, String expression) {
        log.debug("[ConditionRouterEngine] 评估条件表达式：{}", expression);

        // 解析 AND/OR 逻辑
        // 先处理 OR，再处理 AND
        String[] orParts = splitByOperator(expression, "OR");
        
        if (orParts.length > 1) {
            // OR 逻辑：只要有一个为 true 即可
            for (String orPart : orParts) {
                if (evaluateAndExpression(event, orPart.trim())) {
                    return true;
                }
            }
            return false;
        } else {
            // 没有 OR，处理 AND
            return evaluateAndExpression(event, expression);
        }
    }

    /**
     * 评估 AND 表达式
     */
    private boolean evaluateAndExpression(StandardEvent event, String expression) {
        String[] andParts = splitByOperator(expression, "AND");
        
        for (String andPart : andParts) {
            if (!evaluateSingleCondition(event, andPart.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 评估单个条件
     */
    private boolean evaluateSingleCondition(StandardEvent event, String condition) {
        log.debug("[ConditionRouterEngine] 评估单个条件：{}", condition);

        // 去除括号
        condition = condition.trim().replaceAll("^\\(|\\)$", "");

        // 使用正则解析
        Matcher matcher = CONDITION_PATTERN.matcher(condition);
        if (!matcher.matches()) {
            log.warn("[ConditionRouterEngine] 条件格式不正确：{}", condition);
            return false;
        }

        String field = matcher.group(1);
        String operator = matcher.group(2);
        String valueStr = matcher.group(3).trim();

        // 去除引号
        if (valueStr.startsWith("'") && valueStr.endsWith("'")) {
            valueStr = valueStr.substring(1, valueStr.length() - 1);
        } else if (valueStr.startsWith("\"") && valueStr.endsWith("\"")) {
            valueStr = valueStr.substring(1, valueStr.length() - 1);
        }

        // 获取实际值
        Object actualValue = conditionEvaluator.getFieldValue(event, field);
        
        // 评估条件
        return conditionEvaluator.evaluate(field, operator, valueStr, actualValue);
    }

    /**
     * 按操作符分割表达式
     */
    private String[] splitByOperator(String expression, String operator) {
        List<String> parts = new ArrayList<>();
        int parenCount = 0;
        int lastSplit = 0;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                parenCount++;
            } else if (c == ')') {
                parenCount--;
            } else if (parenCount == 0 && isOperatorAt(expression, operator, i)) {
                parts.add(expression.substring(lastSplit, i).trim());
                lastSplit = i + operator.length();
                i += operator.length() - 1;
            }
        }
        
        parts.add(expression.substring(lastSplit).trim());
        return parts.toArray(new String[0]);
    }

    /**
     * 检查操作符是否在指定位置
     */
    private boolean isOperatorAt(String expression, String operator, int position) {
        if (position + operator.length() > expression.length()) {
            return false;
        }
        
        String sub = expression.substring(position, position + operator.length());
        return operator.equals(sub);
    }
}
