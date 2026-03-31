package com.gnosis.lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 状态模型 VO（视图对象）
 */
@ApiModel(value = "StateModelVO", description = "状态模型视图对象")
public class StateModelVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 ID")
    private String id;

    @ApiModelProperty(value = "业务类型 ID")
    private String businessTypeId;

    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @ApiModelProperty(value = "状态模型名称")
    private String modelName;

    @ApiModelProperty(value = "状态模型编码")
    private String modelCode;

    @ApiModelProperty(value = "描述信息")
    private String description;

    @ApiModelProperty(value = "状态节点列表")
    private List<StateNodeVO> states;

    @ApiModelProperty(value = "状态流转列表")
    private List<StateTransitionVO> transitions;

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

    public List<StateNodeVO> getStates() {
        return states;
    }

    public void setStates(List<StateNodeVO> states) {
        this.states = states;
    }

    public List<StateTransitionVO> getTransitions() {
        return transitions;
    }

    public void setTransitions(List<StateTransitionVO> transitions) {
        this.transitions = transitions;
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
     * 状态节点 VO
     */
    public static class StateNodeVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "状态 ID")
        private String id;

        @ApiModelProperty(value = "状态名称")
        private String stateName;

        @ApiModelProperty(value = "状态编码")
        private String stateCode;

        @ApiModelProperty(value = "状态类型")
        private String stateType;

        @ApiModelProperty(value = "是否终态")
        private Integer isFinalState;

        @ApiModelProperty(value = "位置 X 坐标")
        private Integer positionX;

        @ApiModelProperty(value = "位置 Y 坐标")
        private Integer positionY;

        @ApiModelProperty(value = "元数据")
        private String metadata;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

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

        public Integer getIsFinalState() {
            return isFinalState;
        }

        public void setIsFinalState(Integer isFinalState) {
            this.isFinalState = isFinalState;
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
     * 状态流转 VO
     */
    public static class StateTransitionVO implements Serializable {
        private static final long serialVersionUID = 1L;

        @ApiModelProperty(value = "流转 ID")
        private String id;

        @ApiModelProperty(value = "流转名称")
        private String transitionName;

        @ApiModelProperty(value = "流转编码")
        private String transitionCode;

        @ApiModelProperty(value = "源状态 ID")
        private String fromStateId;

        @ApiModelProperty(value = "源状态编码")
        private String fromStateCode;

        @ApiModelProperty(value = "目标状态 ID")
        private String toStateId;

        @ApiModelProperty(value = "目标状态编码")
        private String toStateCode;

        @ApiModelProperty(value = "条件表达式")
        private String conditionExpression;

        @ApiModelProperty(value = "优先级")
        private Integer priority;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

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

        public String getFromStateId() {
            return fromStateId;
        }

        public void setFromStateId(String fromStateId) {
            this.fromStateId = fromStateId;
        }

        public String getFromStateCode() {
            return fromStateCode;
        }

        public void setFromStateCode(String fromStateCode) {
            this.fromStateCode = fromStateCode;
        }

        public String getToStateId() {
            return toStateId;
        }

        public void setToStateId(String toStateId) {
            this.toStateId = toStateId;
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
