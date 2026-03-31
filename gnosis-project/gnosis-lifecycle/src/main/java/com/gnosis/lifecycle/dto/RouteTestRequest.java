package com.gnosis.lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * 路由测试请求 DTO
 */
@ApiModel(value = "RouteTestRequest", description = "路由测试请求")
public class RouteTestRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务类型 ID", required = true, example = "123456")
    private String businessTypeId;

    @ApiModelProperty(value = "事件类型", required = true, example = "ORDER_CREATED")
    private String eventType;

    @ApiModelProperty(value = "当前状态编码", example = "PENDING_PAYMENT")
    private String currentStateCode;

    @ApiModelProperty(value = "测试数据 (JSON 格式)", required = true, example = "{\"action\":\"CREATE\",\"node_id\":\"start\",\"amount\":100}")
    private Map<String, Object> testData;

    @ApiModelProperty(value = "规则 ID（可选，指定特定规则进行测试）", example = "rule_123")
    private String ruleId;

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

    public String getCurrentStateCode() {
        return currentStateCode;
    }

    public void setCurrentStateCode(String currentStateCode) {
        this.currentStateCode = currentStateCode;
    }

    public Map<String, Object> getTestData() {
        return testData;
    }

    public void setTestData(Map<String, Object> testData) {
        this.testData = testData;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
}
