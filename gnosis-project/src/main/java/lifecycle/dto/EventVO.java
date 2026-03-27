package lifecycle.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * 事件 VO（视图对象）
 */
@ApiModel(value = "EventVO", description = "事件视图对象")
public class EventVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键 ID")
    private String id;

    @ApiModelProperty(value = "业务类型 ID")
    private String businessTypeId;

    @ApiModelProperty(value = "业务类型名称")
    private String businessTypeName;

    @ApiModelProperty(value = "事件类型")
    private String eventType;

    @ApiModelProperty(value = "业务实例 ID")
    private String businessInstanceId;

    @ApiModelProperty(value = "事件数据")
    private Map<String, Object> eventData;

    @ApiModelProperty(value = "扩展元数据")
    private Map<String, Object> metadata;

    @ApiModelProperty(value = "操作人 ID")
    private String operatorId;

    @ApiModelProperty(value = "备注信息")
    private String remark;

    @ApiModelProperty(value = "处理状态", example = "PENDING")
    private String processStatus;

    @ApiModelProperty(value = "处理结果")
    private String processResult;

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

    public String getBusinessInstanceId() {
        return businessInstanceId;
    }

    public void setBusinessInstanceId(String businessInstanceId) {
        this.businessInstanceId = businessInstanceId;
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

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public String getProcessResult() {
        return processResult;
    }

    public void setProcessResult(String processResult) {
        this.processResult = processResult;
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
}
