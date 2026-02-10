package gnosis.sample.distribute.queue.sdk;

import java.util.Map;

/**
 * 任务管理服务接口
 * 提供异步和同步任务提交功能
 */
public interface TaskManagementService {

    /**
     * 异步提交任务到指定队列
     * 
     * @param queueName 队列名称
     * @param payload 任务负载数据
     * @return 操作结果，包含状态和消息
     * @throws QueueNotFoundException 队列不存在异常
     * @throws QueueFullException 队列已满异常
     */
    Map<String, Object> submitTask(String queueName, String payload) throws QueueNotFoundException, QueueFullException;

    /**
     * 同步提交任务并等待处理结果
     * 
     * @param queueName 队列名称
     * @param payload 任务负载数据
     * @param timeoutMs 超时时间(毫秒)
     * @return 处理结果，包含请求ID和结果数据
     * @throws QueueNotFoundException 队列不存在异常
     * @throws QueueFullException 队列已满异常
     * @throws TimeoutException 等待结果超时异常
     * @throws ProcessingFailedException 任务处理失败异常
     */
    Map<String, Object> submitTaskSync(String queueName, String payload, long timeoutMs) 
        throws QueueNotFoundException, QueueFullException, TimeoutException, ProcessingFailedException;

    /**
     * 查询队列状态
     * 
     * @param queueName 队列名称
     * @return 队列状态信息
     */
    Map<String, Object> getQueueStatus(String queueName);

    /**
     * 健康检查
     * 
     * @return 服务健康状态
     */
    Map<String, Object> healthCheck();

    // 自定义异常类
    class QueueNotFoundException extends Exception {
        public QueueNotFoundException(String message) {
            super(message);
        }
    }

    class QueueFullException extends Exception {
        private final String queueName;
        private final int currentSize;
        private final int maxSize;

        public QueueFullException(String queueName, int currentSize, int maxSize) {
            super(String.format("队列 '%s' 已满，当前大小: %d, 最大大小: %d", queueName, currentSize, maxSize));
            this.queueName = queueName;
            this.currentSize = currentSize;
            this.maxSize = maxSize;
        }

        public String getQueueName() { return queueName; }
        public int getCurrentSize() { return currentSize; }
        public int getMaxSize() { return maxSize; }
    }

    class TimeoutException extends Exception {
        private final String requestId;
        private final long timeoutMs;

        public TimeoutException(String requestId, long timeoutMs) {
            super(String.format("等待结果超时: %dms, 请求ID: %s", timeoutMs, requestId));
            this.requestId = requestId;
            this.timeoutMs = timeoutMs;
        }

        public String getRequestId() { return requestId; }
        public long getTimeoutMs() { return timeoutMs; }
    }

    class ProcessingFailedException extends Exception {
        private final String requestId;

        public ProcessingFailedException(String requestId, String message) {
            super(message);
            this.requestId = requestId;
        }

        public String getRequestId() { return requestId; }
    }
}