package lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 状态模型创建请求 DTO
 */
@ApiModel(value = "StateModelCreateRequest", description = "状态模型创建请求")
public class StateModelCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务类型 ID", required = true, example = "123456")
    private String businessTypeId;

    @ApiModelProperty(value = "状态模型名称", required = true, example = "订单状态模型")
    private String modelName;

    @ApiModelProperty(value = "状态模型编码", required = true, example = "ORDER_STATE_MODEL")
    private String modelCode;

    @ApiModelProperty(value = "描述信息", example = "订单业务的状态模型")
    private String description;

    @ApiModelProperty(value = "状态节点列表")
    private List<StateNodeConfig> states;

    @ApiModelProperty(value = "状态流转列表")
    private List<StateTransitionConfig> transitions;

    public String getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getModelCode() {
        return modelCode;
    }

    public void setModelCode(String modelCode) {
        this.modelCode = modelCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<StateNodeConfig> getStates() {
        return states;
    }

    public void setStates(List<StateNodeConfig> states) {
        this.states = states;
    }

    public List<StateTransitionConfig> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<StateTransitionConfig> transitions) {
        this.transitions = transitions;
    }

    /**
     * 状态节点配置
     */
    public static class StateNodeConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "状态名称", required = true, example = "待支付")
        private String stateName;

        @ApiModelProperty(value = "状态编码", required = true, example = "PENDING_PAYMENT")
        private String stateCode;

        @ApiModelProperty(value = "状态类型 (initial/normal/final)", required = true, example = "initial")
        private String stateType;

        @ApiModelProperty(value = "位置 X 坐标", example = "100")
        private Integer positionX;

        @ApiModelProperty(value = "位置 Y 坐标", example = "200")
        private Integer positionY;

        @ApiModelProperty(value = "元数据 (JSON)", example = "{\"color\":\"#1890ff\"}")
        private String metadata;

        public String getStateName() {
            return stateName;
        }

        public void setStateName(String stateName) {
            this.stateName = stateName;
        }

        public String getStateCode() {
            return stateCode;
        }

        public void setStateCode(String stateCode) {
            this.stateCode = stateCode;
        }

        public String getStateType() {
            return stateType;
        }

        public void setStateType(String stateType) {
            this.stateType = stateType;
        }

        public Integer getPositionX() {
            return positionX;
        }

        public void setPositionX(Integer positionX) {
            this.positionX = positionX;
        }

        public Integer getPositionY() {
            return positionY;
        }

        public void setPositionY(Integer positionY) {
            this.positionY = positionY;
        }

        public String getMetadata() {
            return metadata;
        }

        public void setMetadata(String metadata) {
            this.metadata = metadata;
        }
    }

    /**
     * 状态流转配置
     */
    public static class StateTransitionConfig implements Serializable {
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "流转名称", required = true, example = "支付成功")
        private String transitionName;

        @ApiModelProperty(value = "流转编码", required = true, example = "PAY_SUCCESS")
        private String transitionCode;

        @ApiModelProperty(value = "源状态编码", required = true, example = "PENDING_PAYMENT")
        private String fromStateCode;

        @ApiModelProperty(value = "目标状态编码", required = true, example = "PAID")
        private String toStateCode;

        @ApiModelProperty(value = "条件表达式", example = "amount > 0")
        private String conditionExpression;

        @ApiModelProperty(value = "优先级 (数字越大优先级越高)", example = "10")
        private Integer priority;

        public String getTransitionName() {
            return transitionName;
        }

        public void setTransitionName(String transitionName) {
            this.transitionName = transitionName;
        }

        public String getTransitionCode() {
            return transitionCode;
        }

        public void setTransitionCode(String transitionCode) {
            this.transitionCode = transitionCode;
        }

        public String getFromStateCode() {
            return fromStateCode;
        }

        public void setFromStateCode(String fromStateCode) {
            this.fromStateCode = fromStateCode;
        }

        public String getToStateCode() {
            return toStateCode;
        }

        public void setToStateCode(String toStateCode) {
            this.toStateCode = toStateCode;
        }

        public String getConditionExpression() {
            return conditionExpression;
        }

        public void setConditionExpression(String conditionExpression) {
            this.conditionExpression = conditionExpression;
        }

        public Integer getPriority() {
            return priority;
        }

        public void setPriority(Integer priority) {
            this.priority = priority;
        }
    }
}
