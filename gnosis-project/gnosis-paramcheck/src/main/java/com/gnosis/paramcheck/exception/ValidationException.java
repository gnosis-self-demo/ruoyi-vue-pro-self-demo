package com.gnosis.paramcheck.exception;

import java.util.Map;

/**
 * 校验失败异常
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final String errorCode;
    private final String failedNode;
    private final Map<String, Object> errorDetails;

    public ValidationException(String errorCode, String errorMsg) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.failedNode = null;
        this.errorDetails = null;
    }

    public ValidationException(String errorCode, String errorMsg, String failedNode) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.failedNode = failedNode;
        this.errorDetails = null;
    }

    public ValidationException(String errorCode, String errorMsg, String failedNode, Map<String, Object> errorDetails) {
        super(errorMsg);
        this.errorCode = errorCode;
        this.failedNode = failedNode;
        this.errorDetails = errorDetails;
    }

    public ValidationException(String errorMsg) {
        super(errorMsg);
        this.errorCode = "VALIDATION_ERROR";
        this.failedNode = null;
        this.errorDetails = null;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getFailedNode() {
        return failedNode;
    }

    public Map<String, Object> getErrorDetails() {
        return errorDetails;
    }

    /**
     * 预定义错误码
     */
    public static class ErrorCodes {
        public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
        public static final String FLOW_ERROR = "FLOW_ERROR";
        public static final String HANDLER_ERROR = "HANDLER_ERROR";
        public static final String DB_ERROR = "DB_ERROR";
        public static final String PARAM_ERROR = "PARAM_ERROR";
        public static final String SYSTEM_ERROR = "SYSTEM_ERROR";
    }
}
