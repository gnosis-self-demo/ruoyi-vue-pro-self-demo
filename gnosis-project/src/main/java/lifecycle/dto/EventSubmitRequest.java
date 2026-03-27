package lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Map;

/**
 * 事件提交请求 DTO
 */
@ApiModel(value = "EventSubmitRequest", description = "事件提交请求")
public class EventSubmitRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "业务类型 ID", required = true, example = "123456")
    private String businessTypeId;

    @ApiModelProperty(value = "事件类型", required = true, example = "ORDER_CREATED")
    private String eventType;

    @ApiModelProperty(value = "业务实例 ID", required = true, example = "ORDER_20260327001")
    private String businessInstanceId;

    @ApiModelProperty(value = "当前状态编码", example = "PENDING_PAYMENT")
    private String currentStateCode;

    @ApiModelProperty(value = "事件数据 (JSON 格式)", example = "{\"orderId\":\"123\",\"amount\":100}")
    private Map<String, Object> eventData;

    @ApiModelProperty(value = "扩展元数据", example = "{\"action\":\"CREATE\",\"node_id\":\"start\"}")
    private Map<String, Object> metadata;

    @ApiModelProperty(value = "操作人 ID", example = "user123")
    private String operatorId;

    @ApiModelProperty(value = "备注信息", example = "订单创建")
    private String remark;

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

    public String getBusinessInstanceId() {
        return businessInstanceId;
    }

    public void setBusinessInstanceId(String businessInstanceId) {
        this.businessInstanceId = businessInstanceId;
    }

    public String getCurrentStateCode() {
        return currentStateCode;
    }

    public void setCurrentStateCode(String currentStateCode) {
        this.currentStateCode = currentStateCode;
    }

    public Map<String, Object> getEventData() {
        return eventData;
    }

    public void setEventData(Map<String, Object> eventData) {
        this.eventData = eventData;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
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
