package com.gnosis.signature.dto.audit;

import java.io.Serializable;

/**
 * 审计日志创建请求
 */
public class SignatureAuditLogCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 操作类型 */
    private String operationType;

    /** 操作模块 */
    private String operationModule;

    /** 操作描述 */
    private String operationDesc;

    /** 请求参数 */
    private String requestParams;

    /** 响应结果 */
    private String responseResult;

    /** 操作IP */
    private String operationIp;

    /** 操作结果(0-失败 1-成功) */
    private Integer operationResult;

    /** 错误信息 */
    private String errorMsg;

    /** 状态(0-禁用 1-启用) */
    private Integer status;

    /** 创建人ID */
    private String createUserId;

    /** 更新人ID */
    private String updateUserId;

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public String getOperationModule() {
        return operationModule;
    }

    public void setOperationModule(String operationModule) {
        this.operationModule = operationModule;
    }

    public String getOperationDesc() {
        return operationDesc;
    }

    public void setOperationDesc(String operationDesc) {
        this.operationDesc = operationDesc;
    }

    public String getRequestParams() {
        return requestParams;
    }

    public void setRequestParams(String requestParams) {
        this.requestParams = requestParams;
    }

    public String getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(String responseResult) {
        this.responseResult = responseResult;
    }

    public String getOperationIp() {
        return operationIp;
    }

    public void setOperationIp(String operationIp) {
        this.operationIp = operationIp;
    }

    public Integer getOperationResult() {
        return operationResult;
    }

    public void setOperationResult(Integer operationResult) {
        this.operationResult = operationResult;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
}
