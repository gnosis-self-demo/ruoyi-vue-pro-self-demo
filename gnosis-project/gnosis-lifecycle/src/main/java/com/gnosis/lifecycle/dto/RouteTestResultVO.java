package com.gnosis.lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 路由测试结果 VO
 */
@ApiModel(value = "RouteTestResultVO", description = "路由测试结果视图对象")
public class RouteTestResultVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "是否匹配成功", required = true, example = "true")
    private boolean matched;

    @ApiModelProperty(value = "匹配的规则列表")
    private List<MatchedRuleVO> matchedRules;

    @ApiModelProperty(value = "未匹配的规则列表")
    private List<UnmatchedRuleVO> unmatchedRules;

    @ApiModelProperty(value = "最终路由结果", example = "NEXT_NODE")
    private String finalRouteResult;

    @ApiModelProperty(value = "目标节点 ID", example = "node_123")
    private String targetNodeId;

    @ApiModelProperty(value = "执行消息", example = "匹配成功")
    private String message;

    @ApiModelProperty(value = "执行耗时（毫秒）", example = "15")
    private Long executionTimeMs;

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public List<MatchedRuleVO> getMatchedRules() {
        return matchedRules;
    }

    public void setMatchedRules(List<MatchedRuleVO> matchedRules) {
        this.matchedRules = matchedRules;
    }

    public List<UnmatchedRuleVO> getUnmatchedRules() {
        return unmatchedRules;
    }

    public void setUnmatchedRules(List<UnmatchedRuleVO> unmatchedRules) {
        this.unmatchedRules = unmatchedRules;
    }

    public String getFinalRouteResult() {
        return finalRouteResult;
    }

    public void setFinalRouteResult(String finalRouteResult) {
        this.finalRouteResult = finalRouteResult;
    }

    public String getTargetNodeId() {
        return targetNodeId;
    }

    public void setTargetNodeId(String targetNodeId) {
        this.targetNodeId = targetNodeId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(Long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    /**
     * 匹配的规则 VO
     */
    public static class MatchedRuleVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "规则 ID")
        private String ruleId;

        @ApiModelProperty(value = "规则名称")
        private String ruleName;

        @ApiModelProperty(value = "规则编码")
        private String ruleCode;

        @ApiModelProperty(value = "优先级")
        private Integer priority;

        @ApiModelProperty(value = "条件表达式")
        private String conditionExpression;

        @ApiModelProperty(value = "目标动作")
        private String targetAction;

        @ApiModelProperty(value = "目标节点 ID")
        private String targetNodeId;

        @ApiModelProperty(value = "匹配详情")
        private String matchDetail;

        public String getRuleId() {
            return ruleId;
        }

        public void setRuleId(String ruleId) {
            this.ruleId = ruleId;
        }

        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public String getRuleCode() {
            return ruleCode;
        }

        public void setRuleCode(String ruleCode) {
            this.ruleCode = ruleCode;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        public String getConditionExpression() {
            return conditionExpression;
        }

        public void setConditionExpression(String conditionExpression) {
            this.conditionExpression = conditionExpression;
        }

        public String getTargetAction() {
            return targetAction;
        }

        public void setTargetAction(String targetAction) {
            this.targetAction = targetAction;
        }

        public String getTargetNodeId() {
            return targetNodeId;
        }

        public void setTargetNodeId(String targetNodeId) {
            this.targetNodeId = targetNodeId;
        }

        public String getMatchDetail() {
            return matchDetail;
        }

        public void setMatchDetail(String matchDetail) {
            this.matchDetail = matchDetail;
        }
    }

    /**
     * 未匹配的规则 VO
     */
    public static class UnmatchedRuleVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "规则 ID")
        private String ruleId;

        @ApiModelProperty(value = "规则名称")
        private String ruleName;

        @ApiModelProperty(value = "规则编码")
        private String ruleCode;

        @ApiModelProperty(value = "优先级")
        private Integer priority;

        @ApiModelProperty(value = "条件表达式")
        private String conditionExpression;

        @ApiModelProperty(value = "未匹配原因", example = "条件不满足")
        private String unmatchReason;

        public String getRuleId() {
            return ruleId;
        }

        public void setRuleId(String ruleId) {
            this.ruleId = ruleId;
        }

        public String getRuleName() {
            return ruleName;
        }

        public void setRuleName(String ruleName) {
            this.ruleName = ruleName;
        }

        public String getRuleCode() {
            return ruleCode;
        }

        public void setRuleCode(String ruleCode) {
            this.ruleCode = ruleCode;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }

        public String getConditionExpression() {
            return conditionExpression;
        }

        public void setConditionExpression(String conditionExpression) {
            this.conditionExpression = conditionExpression;
        }

        public String getUnmatchReason() {
            return unmatchReason;
        }

        public void setUnmatchReason(String unmatchReason) {
            this.unmatchReason = unmatchReason;
        }
    }
}
