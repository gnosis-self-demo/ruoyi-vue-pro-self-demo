package gnosis.sample.distribute.queue.dto.response;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务提交响应对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class TaskSubmitResponse extends CommonResponse<TaskSubmitResponse.TaskData> {
    
    @Data
    public static class TaskData {
        /**
         * 队列名称
         */
        private String queueName;
        
        /**
         * 请求ID（同步提交时返回）
         */
        private String requestId;
        
        /**
         * 处理结果数据（同步提交时返回）
         */
        private String result;
        
        public TaskData() {}
        
        public TaskData(String queueName) {
            this.queueName = queueName;
        }
        
        public TaskData(String queueName, String requestId, String result) {
            this.queueName = queueName;
            this.requestId = requestId;
            this.result = result;
        }
    }
    
    public TaskSubmitResponse() {
        super();
    }
    
    public TaskSubmitResponse(String status, String message) {
        super(status, message);
    }
    
    public TaskSubmitResponse(String status, String message, TaskData data) {
        super(status, message, data);
    }
    
    /**
     * 创建异步提交成功响应
     */
    public static TaskSubmitResponse asyncSuccess(String queueName) {
        TaskData data = new TaskData(queueName);
        TaskSubmitResponse response = new TaskSubmitResponse("success", "任务已加入队列", data);
        return response;
    }
    
    /**
     * 创建同步提交成功响应
     */
    public static TaskSubmitResponse syncSuccess(String requestId, String result) {
        TaskData data = new TaskData(null, requestId, result);
        TaskSubmitResponse response = new TaskSubmitResponse("success", "任务处理完成", data);
        return response;
    }
    
    /**
     * 创建队列不存在错误响应
     */
    public static TaskSubmitResponse queueNotFound(String queueName) {
        return new TaskSubmitResponse("error", "队列 '" + queueName + "' 不存在，请先创建");
    }
    
    /**
     * 创建队列已满错误响应
     */
    public static TaskSubmitResponse queueFull(String queueName, int currentSize, int maxSize) {
        TaskData data = new TaskData();
        data.setQueueName(queueName);
        TaskSubmitResponse response = new TaskSubmitResponse("error", 
            String.format("队列 '%s' 已满，当前大小: %d, 最大大小: %d", queueName, currentSize, maxSize), 
            data);
        return response;
    }
    
    /**
     * 创建超时错误响应
     */
    public static TaskSubmitResponse timeout(String requestId, long timeoutMs) {
        TaskData data = new TaskData();
        data.setRequestId(requestId);
        TaskSubmitResponse response = new TaskSubmitResponse("error", 
            String.format("等待结果超时: %dms", timeoutMs), 
            data);
        return response;
    }
    
    /**
     * 创建处理失败错误响应
     */
    public static TaskSubmitResponse processingFailed(String requestId, String errorMessage) {
        TaskData data = new TaskData();
        data.setRequestId(requestId);
        TaskSubmitResponse response = new TaskSubmitResponse("error", errorMessage, data);
        return response;
    }
}