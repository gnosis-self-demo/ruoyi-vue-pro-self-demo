package gnosis.sample.distribute.queue.model;

import lombok.Data;

/**
 * 队列处理结果模型
 */
@Data
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
}