package com.gnosis.lifecycle.engine.sandbox;

import java.io.Serializable;
import java.util.List;

/**
 * 模拟测试结果
 */
public class TestSimulationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 匹配的规则数量
     */
    private int matchedCount;

    /**
     * 匹配的规则详情
     */
    private List<MatchedRuleDetail> matchedRules;

    /**
     * 未匹配的规则详情
     */
    private List<UnmatchedRuleDetail> unmatchedRules;

    /**
     * 最终路由结果
     */
    private String finalRouteResult;

    /**
     * 执行消息
     */
    private String message;

    /**
     * 执行耗时（毫秒）
     */
    private Long executionTimeMs;

    /**
     * 错误信息
     */
    private String errorMessage;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getMatchedCount() {
        return matchedCount;
    }

    public void setMatchedCount(int matchedCount) {
        this.matchedCount = matchedCount;
    }

    public List<MatchedRuleDetail> getMatchedRules() {
        return matchedRules;
    }

    public void setMatchedRules(List<MatchedRuleDetail> matchedRules) {
        this.matchedRules = matchedRules;
    }

    public List<UnmatchedRuleDetail> getUnmatchedRules() {
        return unmatchedRules;
    }

    public void setUnmatchedRules(List<UnmatchedRuleDetail> unmatchedRules) {
        this.unmatchedRules = unmatchedRules;
    }

    public String getFinalRouteResult() {
        return finalRouteResult;
    }

    public void setFinalRouteResult(String finalRouteResult) {
        this.finalRouteResult = finalRouteResult;
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

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * 匹配的规则详情
     */
    public static class MatchedRuleDetail implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 规则 ID
         */
        private String ruleId;

        /**
         * 规则名称
         */
        private String ruleName;

        /**
         * 优先级
         */
        private Integer priority;

        /**
         * 条件表达式
         */
        private String conditionExpression;

        /**
         * 匹配详情
         */
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

        public String getMatchDetail() {
            return matchDetail;
        }

        public void setMatchDetail(String matchDetail) {
            this.matchDetail = matchDetail;
        }
    }

    /**
     * 未匹配的规则详情
     */
    public static class UnmatchedRuleDetail implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * 规则 ID
         */
        private String ruleId;

        /**
         * 规则名称
         */
        private String ruleName;

        /**
         * 优先级
         */
        private Integer priority;

        /**
         * 条件表达式
         */
        private String conditionExpression;

        /**
         * 未匹配原因
         */
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
