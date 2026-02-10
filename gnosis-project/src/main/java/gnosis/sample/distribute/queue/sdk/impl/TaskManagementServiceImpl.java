package gnosis.sample.distribute.queue.sdk.impl;

import gnosis.sample.distribute.queue.exception.QueueFullException;
import gnosis.sample.distribute.queue.factory.DistributedQueueFactory;
import gnosis.sample.distribute.queue.model.QueueInstance;
import gnosis.sample.distribute.queue.model.QueueResult;
import gnosis.sample.distribute.queue.sdk.TaskManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务管理服务实现类
 */
@Slf4j
@Service
public class TaskManagementServiceImpl implements TaskManagementService {
    
    private final DistributedQueueFactory queueFactory;
    
    public TaskManagementServiceImpl(DistributedQueueFactory queueFactory) {
        this.queueFactory = queueFactory;
    }

    public Map<String, Object> submitTask(String queueName, String payload) 
            throws QueueNotFoundException, QueueFullException {
        
        QueueInstance instance = queueFactory.getQueueInstance(queueName);
        if (instance == null) {
            throw new QueueNotFoundException("队列 '" + queueName + "' 不存在，请先创建");
        }
        
        try {
            instance.getService().enqueue(queueName, payload);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "任务已加入队列");
            response.put("queueName", queueName);
            return response;
        } catch (gnosis.sample.distribute.queue.exception.QueueFullException e) {
            throw new QueueFullException(e.getQueueName(), e.getCurrentSize(), e.getMaxSize());
        } catch (SQLException e) {
            log.error("数据库操作失败: {}", e.getMessage(), e);
            throw new RuntimeException("数据库操作失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("内部服务器错误: {}", e.getMessage(), e);
            throw new RuntimeException("内部服务器错误: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> submitTaskSync(String queueName, String payload, long timeoutMs) 
            throws QueueNotFoundException, QueueFullException, TimeoutException, ProcessingFailedException {
        
        QueueInstance instance = queueFactory.getQueueInstance(queueName);
        if (instance == null) {
            throw new QueueNotFoundException("队列 '" + queueName + "' 不存在，请先创建");
        }
        
        try {
            // 入队并获取请求ID
            String requestId = instance.getService().enqueueWithResult(queueName, payload);
            
            // 等待处理结果
            QueueResult result = instance.getService().waitForResult(requestId, timeoutMs);

            if (result == null) {
                throw new TimeoutException(requestId, timeoutMs);
            }

            if (result.isSuccess()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("requestId", result.getRequestId());
                response.put("result", result.getResultData());
                return response;
            } else {
                throw new ProcessingFailedException(result.getRequestId(), result.getErrorMessage());
            }
        } catch (gnosis.sample.distribute.queue.exception.QueueFullException e) {
            throw new QueueFullException(e.getQueueName(), e.getCurrentSize(), e.getMaxSize());
        } catch (SQLException e) {
            log.error("数据库操作失败: {}", e.getMessage(), e);
            throw new RuntimeException("数据库操作失败: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("请求被中断", e);
        } catch (Exception e) {
            log.error("内部服务器错误: {}", e.getMessage(), e);
            throw new RuntimeException("内部服务器错误: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> getQueueStatus(String queueName) {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("queueName", queueName);
            status.put("timestamp", System.currentTimeMillis());
            return status;
        } catch (Exception e) {
            log.error("获取队列状态失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取队列状态失败: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "分布式队列服务");
        health.put("timestamp", System.currentTimeMillis());
        return health;
    }
}