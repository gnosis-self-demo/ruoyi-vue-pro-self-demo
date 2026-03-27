package lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 路由规则 VO（视图对象）
 */
@ApiModel(value = "RouteRuleVO", description = "路由规则视图对象")
public class RouteRuleVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 ID")
    private String id;

    @ApiModelProperty(value = "业务类型 ID")
    private String businessTypeId;

    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @ApiModelProperty(value = "事件类型")
    private String eventType;

    @ApiModelProperty(value = "规则名称")
    private String ruleName;

    @ApiModelProperty(value = "规则编码")
    private String ruleCode;

    @ApiModelProperty(value = "描述信息")
    private String description;

    @ApiModelProperty(value = "条件表达式")
    private String conditionExpression;

    @ApiModelProperty(value = "条件列表")
    private List<ConditionVO> conditions;

    @ApiModelProperty(value = "逻辑操作符")
    private String logicOperator;

    @ApiModelProperty(value = "优先级")
    private Integer priority;

    @ApiModelProperty(value = "是否启用")
    private Integer isActive;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "目标动作")
    private String targetAction;

    @ApiModelProperty(value = "目标节点 ID")
    private String targetNodeId;

    @ApiModelProperty(value = "创建人 ID")
    private String createUserId;

    @ApiModelProperty(value = "更新人 ID")
    private String updateUserId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public String getBusinessTypeName() {
        return businessTypeName;
    }

    public void setBusinessTypeName(String businessTypeName) {
        this.businessTypeName = businessTypeName;
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

    public List<ConditionVO> getConditions() {
        return conditions;
    }

    public void setConditions(List<ConditionVO> conditions) {
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

    public Integer getIsActive() {
        return isActive;
    }

    public void setIsActive(Integer isActive) {
        this.isActive = isActive;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
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

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * 条件 VO
     */
    public static class ConditionVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "字段名")
        private String field;

        @ApiModelProperty(value = "操作符")
        private String operator;

        @ApiModelProperty(value = "字段值")
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
