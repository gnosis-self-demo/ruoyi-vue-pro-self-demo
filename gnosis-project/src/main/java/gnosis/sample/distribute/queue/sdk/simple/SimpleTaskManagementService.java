package gnosis.sample.distribute.queue.sdk.simple;

import gnosis.sample.distribute.queue.sdk.TaskManagementService;

import java.util.HashMap;
import java.util.Map;

/**
 * 简化的任务管理服务实现
 */
public class SimpleTaskManagementService implements TaskManagementService {
    @Override
    public Map<String, Object> submitTask(String queueName, String payload) 
            throws QueueNotFoundException, QueueFullException {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "success");
        result.put("message", "任务已加入队列: " + queueName);
        result.put("queueName", queueName);
        return result;
    }
    
    @Override
    public Map<String, Object> submitTaskSync(String queueName, String payload, long timeoutMs) 
            throws QueueNotFoundException, QueueFullException, TimeoutException, ProcessingFailedException {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "success");
        result.put("requestId", "req-" + System.currentTimeMillis());
        result.put("result", "处理完成: " + payload);
        return result;
    }
    
    @Override
    public Map<String, Object> getQueueStatus(String queueName) {
        Map<String, Object> status = new HashMap<String, Object>();
        status.put("queueName", queueName);
        status.put("timestamp", System.currentTimeMillis());
        status.put("status", "active");
        return status;
    }
    
    @Override
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<String, Object>();
        health.put("status", "UP");
        health.put("service", "分布式队列服务");
        health.put("timestamp", System.currentTimeMillis());
        return health;
    }
}