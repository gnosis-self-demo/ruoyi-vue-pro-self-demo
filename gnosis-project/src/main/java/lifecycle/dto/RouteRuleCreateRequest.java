package lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 路由规则创建请求 DTO
 */
@ApiModel(value = "RouteRuleCreateRequest", description = "路由规则创建请求")
public class RouteRuleCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务类型 ID", required = true, example = "123456")
    private String businessTypeId;

    @ApiModelProperty(value = "事件类型", required = true, example = "ORDER_CREATED")
    private String eventType;

    @ApiModelProperty(value = "规则名称", required = true, example = "订单创建路由规则")
    private String ruleName;

    @ApiModelProperty(value = "规则编码", example = "ORDER_CREATED_ROUTE")
    private String ruleCode;

    @ApiModelProperty(value = "描述信息", example = "订单创建时的路由规则")
    private String description;

    @ApiModelProperty(value = "条件表达式", example = "action == 'CREATE' && node_id == 'start'")
    private String conditionExpression;

    @ApiModelProperty(value = "条件列表（支持 AND/OR 组合）")
    private List<ConditionConfig> conditions;

    @ApiModelProperty(value = "逻辑操作符 (AND/OR)", example = "AND")
    private String logicOperator;

    @ApiModelProperty(value = "优先级 (数字越大优先级越高)", required = true, example = "10")
    private Integer priority;

    @ApiModelProperty(value = "是否启用", example = "true")
    private Boolean isActive;

    @ApiModelProperty(value = "目标动作", example = "NEXT_NODE")
    private String targetAction;

    @ApiModelProperty(value = "目标节点 ID", example = "node_123")
    private String targetNodeId;

    public String getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public List<ConditionConfig> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionConfig> conditions) {
        this.conditions = conditions;
    }

    public String getLogicOperator() {
        return logicOperator;
    }

    public void setLogicOperator(String logicOperator) {
        this.logicOperator = logicOperator;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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

    /**
     * 条件配置
     */
    public static class ConditionConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "字段名", required = true, example = "action")
        private String field;

        @ApiModelProperty(value = "操作符", required = true, example = "==")
        private String operator;

        @ApiModelProperty(value = "字段值", required = true, example = "CREATE")
        private String value;

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
