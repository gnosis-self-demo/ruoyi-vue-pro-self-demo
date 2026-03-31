package com.gnosis.paramcheck.domain;

import java.io.Serializable;
import java.util.Map;

/**
 * 校验结果
 */
public class ValidationResult implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 是否成功 */
    private boolean success;

    /** 错误码 */
    private String errorCode;

    /** 错误消息 */
    private String errorMsg;

    /** 失败的节点 ID */
    private String failedNode;

    /** 错误详情 */
    private Map<String, Object> errorDetails;

    private ValidationResult() {}

    public static ValidationResult success() {
        ValidationResult result = new ValidationResult();
        result.success = true;
        return result;
    }

    public static ValidationResult fail(String errorCode, String errorMsg) {
        ValidationResult result = new ValidationResult();
        result.success = false;
        result.errorCode = errorCode;
        result.errorMsg = errorMsg;
        return result;
    }

    public static ValidationResult fail(String errorCode, String errorMsg, String failedNode) {
        ValidationResult result = fail(errorCode, errorMsg);
        result.failedNode = failedNode;
        return result;
    }

    public static ValidationResult fail(String errorCode, String errorMsg, String failedNode, Map<String, Object> errorDetails) {
        ValidationResult result = fail(errorCode, errorMsg, failedNode);
        result.errorDetails = errorDetails;
        return result;
    }

    // Getter and Setter methods
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getFailedNode() {
        return failedNode;
    }

    public void setFailedNode(String failedNode) {
        this.failedNode = failedNode;
    }

    public Map<String, Object> getErrorDetails() {
        return errorDetails;
    }

    public void setErrorDetails(Map<String, Object> errorDetails) {
        this.errorDetails = errorDetails;
    }
}
