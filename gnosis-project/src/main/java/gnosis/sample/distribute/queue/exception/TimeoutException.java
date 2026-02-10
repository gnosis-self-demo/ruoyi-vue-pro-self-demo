package gnosis.sample.distribute.queue.exception;

/**
 * 等待结果超时异常
 */
public class TimeoutException extends Exception {
    private final String requestId;
    private final long timeoutMs;

    public TimeoutException(String requestId, long timeoutMs) {
        super(String.format("等待结果超时: %dms, 请求ID: %s", timeoutMs, requestId));
        this.requestId = requestId;
        this.timeoutMs = timeoutMs;
    }

    public String getRequestId() { 
        return requestId; 
    }
    
    public long getTimeoutMs() { 
        return timeoutMs; 
    }
}