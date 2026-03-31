package com.gnosis.distribute.queue.exception;

/**
 * 任务处理失败异常
 */
public class ProcessingFailedException extends Exception {
    private final String requestId;

    public ProcessingFailedException(String requestId, String message) {
        super(message);
        this.requestId = requestId;
    }

    public ProcessingFailedException(String requestId, String message, Throwable cause) {
        super(message, cause);
        this.requestId = requestId;
    }

    public String getRequestId() { 
        return requestId; 
    }
}