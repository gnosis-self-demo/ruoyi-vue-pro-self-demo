package lifecycle.engine.sandbox;

import lifecycle.domain.LifecycleRouteRule;
import lifecycle.dto.RouteRuleCreateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import lifecycle.adapter.StandardEvent;
import lifecycle.engine.router.ConditionRouterEngine;
import lifecycle.engine.router.RouteMatchResult;

import java.util.*;

/**
 * 规则测试沙箱
 * 用于在安全环境中测试路由规则
 */
@Component
public class RuleTestSandbox {

    private static final Logger log = LoggerFactory.getLogger(RuleTestSandbox.class);

    @Autowired
    private ConditionRouterEngine conditionRouterEngine;

    /**
     * 模拟测试路由规则
     *
     * @param event 测试事件
     * @param rules 待测试的规则列表
     * @return 模拟测试结果
     */
    public TestSimulationResult simulate(StandardEvent event, List<LifecycleRouteRule> rules) {
        log.info("[RuleTestSandbox] 开始模拟测试，规则数量：{}", rules.size());

        long startTime = System.currentTimeMillis();
        TestSimulationResult result = new TestSimulationResult();

        try {
            // 执行路由匹配
            List<RouteMatchResult> matchResults = conditionRouterEngine.match(event, rules);

            // 分析匹配结果
            List<TestSimulationResult.MatchedRuleDetail> matchedDetails = new ArrayList<>();
            List<TestSimulationResult.UnmatchedRuleDetail> unmatchedDetails = new ArrayList<>();

            for (RouteMatchResult matchResult : matchResults) {
                if (matchResult.isMatched()) {
                    TestSimulationResult.MatchedRuleDetail detail = new TestSimulationResult.MatchedRuleDetail();
                    detail.setRuleId(matchResult.getRuleId());
                    detail.setRuleName(matchResult.getRuleName());
                    detail.setPriority(matchResult.getPriority());
                    detail.setConditionExpression(matchResult.getMatchDetail());
                    detail.setMatchDetail(matchResult.getMatchDetail());
                    matchedDetails.add(detail);
                } else {
                    TestSimulationResult.UnmatchedRuleDetail detail = new TestSimulationResult.UnmatchedRuleDetail();
                    detail.setRuleId(matchResult.getRuleId());
                    detail.setRuleName(matchResult.getRuleName());
                    detail.setPriority(matchResult.getPriority());
                    detail.setConditionExpression(matchResult.getUnmatchReason());
                    detail.setUnmatchReason(matchResult.getUnmatchReason());
                    unmatchedDetails.add(detail);
                }
            }

            // 设置结果
            result.setSuccess(true);
            result.setMatchedCount(matchedDetails.size());
            result.setMatchedRules(matchedDetails);
            result.setUnmatchedRules(unmatchedDetails);

            if (!matchedDetails.isEmpty()) {
                RouteMatchResult firstMatch = matchedDetails.stream()
                        .max(Comparator.comparingInt(TestSimulationResult.MatchedRuleDetail::getPriority))
                        .map(d -> {
                            RouteMatchResult r = new RouteMatchResult();
                            r.setRuleId(d.getRuleId());
                            r.setTargetNodeId(null);
                            return r;
                        })
                        .orElse(null);
                
                if (firstMatch != null) {
                    result.setFinalRouteResult("MATCHED");
                } else {
                    result.setFinalRouteResult("NO_MATCH");
                }
                result.setMessage("匹配成功，共匹配 " + matchedDetails.size() + " 条规则");
            } else {
                result.setFinalRouteResult("NO_MATCH");
                result.setMessage("未匹配到任何规则");
            }

        } catch (Exception e) {
            log.error("[RuleTestSandbox] 模拟测试失败", e);
            result.setSuccess(false);
            result.setErrorMessage("模拟测试异常：" + e.getMessage());
            result.setMessage("测试失败");
        }

        long endTime = System.currentTimeMillis();
        result.setExecutionTimeMs(endTime - startTime);

        log.info("[RuleTestSandbox] 模拟测试完成，耗时：{}ms, 匹配数量：{}", 
                result.getExecutionTimeMs(), result.getMatchedCount());

        return result;
    }

    /**
     * 测试单条规则
     *
     * @param event 测试事件
     * @param rule 待测试的规则
     * @return 模拟测试结果
     */
    public TestSimulationResult testSingleRule(StandardEvent event, LifecycleRouteRule rule) {
        log.info("[RuleTestSandbox] 测试单条规则：ruleId={}, ruleName={}", rule.getId(), rule.getRuleName());

        List<LifecycleRouteRule> rules = new ArrayList<>();
        rules.add(rule);

        return simulate(event, rules);
    }

    /**
     * 测试新规则（未保存的规则）
     *
     * @param event 测试事件
     * @param request 新规则配置
     * @return 模拟测试结果
     */
    public TestSimulationResult testNewRule(StandardEvent event, RouteRuleCreateRequest request) {
        log.info("[RuleTestSandbox] 测试新规则：ruleName={}", request.getRuleName());

        // 构建临时规则对象
        LifecycleRouteRule tempRule = new LifecycleRouteRule();
        tempRule.setId("TEMP_" + System.currentTimeMillis());
        tempRule.setBusinessTypeId(request.getBusinessTypeId());
        tempRule.setEventType(request.getEventType());
        tempRule.setRuleName(request.getRuleName());
        tempRule.setRuleCode(request.getRuleCode());
        
        // 构建条件表达式
        if (request.getConditionExpression() != null) {
            tempRule.setConditionExpression(request.getConditionExpression());
        } else if (request.getConditions() != null) {
            tempRule.setConditionExpression(buildConditionExpression(request));
        }
        
        tempRule.setPriority(request.getPriority() != null ? request.getPriority() : 0);
        tempRule.setIsActive(1);
        tempRule.setVersion(1);

        List<LifecycleRouteRule> rules = new ArrayList<>();
        rules.add(tempRule);

        return simulate(event, rules);
    }

    /**
     * 构建条件表达式
     */
    private String buildConditionExpression(RouteRuleCreateRequest request) {
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
}
