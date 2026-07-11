package com.gnosis.notice.dto.send;

import java.io.Serializable;

/**
 * 消息发送响应
 */
public class NoticeSendResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 日志ID
     */
    private String logId;

    /**
     * 第三方请求ID
     */
    private String requestId;

    /**
     * 错误信息
     */
    private String errorMsg;

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}
