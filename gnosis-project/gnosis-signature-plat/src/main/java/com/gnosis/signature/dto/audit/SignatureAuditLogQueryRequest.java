package com.gnosis.signature.dto.audit;

/**
 * 审计日志查询请求
 */
public class SignatureAuditLogQueryRequest {

    /** 操作类型 */
    private String operationType;

    /** 操作模块 */
    private String operationModule;

    /** 操作描述 */
    private String operationDesc;

    /** 操作IP */
    private String operationIp;

    /** 操作结果(0-失败 1-成功) */
    private Integer operationResult;

    /** 状态(0-禁用 1-启用) */
    private Integer status;

    /** 页码 */
    private Integer pageNum;

    /** 每页大小 */
    private Integer pageSize;

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

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }
}
