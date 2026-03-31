package com.gnosis.lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * 状态流转执行请求 DTO
 */
@ApiModel(value = "TransitionExecutionRequest", description = "状态流转执行请求")
public class TransitionExecutionRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务类型 ID", required = true, example = "123456")
    private String businessTypeId;

    @ApiModelProperty(value = "业务实例 ID", required = true, example = "ORDER_20260327001")
    private String businessInstanceId;

    @ApiModelProperty(value = "流转 ID", required = true, example = "transition_123")
    private String transitionId;

    @ApiModelProperty(value = "流转编码", example = "PAY_SUCCESS")
    private String transitionCode;

    @ApiModelProperty(value = "源状态 ID", example = "state_1")
    private String fromStateId;

    @ApiModelProperty(value = "源状态编码", example = "PENDING_PAYMENT")
    private String fromStateCode;

    @ApiModelProperty(value = "目标状态 ID", example = "state_2")
    private String toStateId;

    @ApiModelProperty(value = "目标状态编码", example = "PAID")
    private String toStateCode;

    @ApiModelProperty(value = "流转数据 (JSON 格式)", example = "{\"paymentTime\":\"2026-03-27 10:00:00\"}")
    private Map<String, Object> transitionData;

    @ApiModelProperty(value = "操作人 ID", example = "user123")
    private String operatorId;

    @ApiModelProperty(value = "备注信息", example = "支付成功")
    private String remark;

    public String getBusinessTypeId() {
        return businessTypeId;
    }

    public void setBusinessTypeId(String businessTypeId) {
        this.businessTypeId = businessTypeId;
    }

    public String getBusinessInstanceId() {
        return businessInstanceId;
    }

    public void setBusinessInstanceId(String businessInstanceId) {
        this.businessInstanceId = businessInstanceId;
    }

    public String getTransitionId() {
        return transitionId;
    }

    public void setTransitionId(String transitionId) {
        this.transitionId = transitionId;
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

    public Map<String, Object> getTransitionData() {
        return transitionData;
    }

    public void setTransitionData(Map<String, Object> transitionData) {
        this.transitionData = transitionData;
    }

    public String getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
