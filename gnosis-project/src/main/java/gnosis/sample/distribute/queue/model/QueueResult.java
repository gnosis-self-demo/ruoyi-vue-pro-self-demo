package gnosis.sample.distribute.queue.model;

/**
 * 队列处理结果模型
 */
public class QueueResult {
    private final String requestId;
    private final boolean success;
    private final String resultData;
    private final String errorMessage;

    public QueueResult(String requestId, boolean success, String resultData, String errorMessage) {
        this.requestId = requestId;
        this.success = success;
        this.resultData = resultData;
        this.errorMessage = errorMessage;
    }

    public String getRequestId() { 
        return requestId; 
    }
    
    public boolean isSuccess() { 
        return success; 
    }
    
    public String getResultData() { 
        return resultData; 
    }
    
    public String getErrorMessage() { 
        return errorMessage; 
    }

    @Override
    public String toString() {
        return "QueueResult{" +
                "requestId='" + requestId + '\'' +
                ", success=" + success +
                ", resultData='" + resultData + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}