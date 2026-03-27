package lifecycle.engine.router;

import java.io.Serializable;

/**
 * 路由匹配结果
 */
public class RouteMatchResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否匹配
     */
    private boolean matched;

    /**
     * 匹配的规则 ID
     */
    private String ruleId;

    /**
     * 匹配的规则名称
     */
    private String ruleName;

    /**
     * 匹配的规则编码
     */
    private String ruleCode;

    /**
     * 优先级
     */
    private Integer priority;

    /**
     * 目标动作
     */
    private String targetAction;

    /**
     * 目标节点 ID
     */
    private String targetNodeId;

    /**
     * 匹配详情
     */
    private String matchDetail;

    /**
     * 未匹配原因
     */
    private String unmatchReason;

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

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

    public String getUnmatchReason() {
        return unmatchReason;
    }

    public void setUnmatchReason(String unmatchReason) {
        this.unmatchReason = unmatchReason;
    }

    /**
     * 创建匹配成功的结果
     */
    public static RouteMatchResult success(String ruleId, String ruleName, String ruleCode, 
                                           Integer priority, String targetAction, String targetNodeId, 
                                           String matchDetail) {
        RouteMatchResult result = new RouteMatchResult();
        result.setMatched(true);
        result.setRuleId(ruleId);
        result.setRuleName(ruleName);
        result.setRuleCode(ruleCode);
        result.setPriority(priority);
        result.setTargetAction(targetAction);
        result.setTargetNodeId(targetNodeId);
        result.setMatchDetail(matchDetail);
        return result;
    }

    /**
     * 创建匹配失败的结果
     */
    public static RouteMatchResult failure(String ruleId, String ruleName, String unmatchReason) {
        RouteMatchResult result = new RouteMatchResult();
        result.setMatched(false);
        result.setRuleId(ruleId);
        result.setRuleName(ruleName);
        result.setUnmatchReason(unmatchReason);
        return result;
    }
}
