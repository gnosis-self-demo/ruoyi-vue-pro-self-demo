package gnosis.sample.distribute.queue.sdk.impl;

import gnosis.sample.distribute.queue.dto.request.TaskSubmitRequest;
import gnosis.sample.distribute.queue.dto.response.CommonResponse;
import gnosis.sample.distribute.queue.dto.response.TaskSubmitResponse;
import gnosis.sample.distribute.queue.exception.ProcessingFailedException;
import gnosis.sample.distribute.queue.exception.QueueFullException;
import gnosis.sample.distribute.queue.exception.QueueNotFoundException;
import gnosis.sample.distribute.queue.exception.TimeoutException;
import gnosis.sample.distribute.queue.factory.DistributedQueueFactory;
import gnosis.sample.distribute.queue.model.QueueInstance;
import gnosis.sample.distribute.queue.model.QueueResult;
import gnosis.sample.distribute.queue.sdk.TaskManagementService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

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

    @Override
    public TaskSubmitResponse submitTask(TaskSubmitRequest request) 
            throws QueueNotFoundException, QueueFullException {
        
        QueueInstance instance = queueFactory.getQueueInstance(request.getQueueName());
        if (instance == null) {
            return TaskSubmitResponse.queueNotFound(request.getQueueName());
        }
        
        try {
            instance.getService().enqueue(request.getQueueName(), request.getPayload());
            return TaskSubmitResponse.asyncSuccess(request.getQueueName());
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

    @Override
    public TaskSubmitResponse submitTaskSync(TaskSubmitRequest request) 
            throws QueueNotFoundException, QueueFullException, TimeoutException, ProcessingFailedException {
        
        QueueInstance instance = queueFactory.getQueueInstance(request.getQueueName());
        if (instance == null) {
            return TaskSubmitResponse.queueNotFound(request.getQueueName());
        }
        
        long timeoutMs = request.getTimeoutMs() != null ? request.getTimeoutMs() : 5000L;
        
        try {
            // 入队并获取请求ID
            String requestId = instance.getService().enqueueWithResult(request.getQueueName(), request.getPayload());
            
            // 等待处理结果
            QueueResult result = instance.getService().waitForResult(requestId, timeoutMs);

            if (result == null) {
                throw new TimeoutException(requestId, timeoutMs);
            }

            if (result.isSuccess()) {
                return TaskSubmitResponse.syncSuccess(result.getRequestId(), result.getResultData());
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

    @Override
    public CommonResponse<CommonResponse.CommonData> getQueueStatus(String queueName) {
        try {
            CommonResponse.CommonData data = new CommonResponse.CommonData();
            data.setQueueName(queueName);
            data.setTimestamp(System.currentTimeMillis());
            return CommonResponse.success("获取队列状态成功", data);
        } catch (Exception e) {
            log.error("获取队列状态失败: {}", e.getMessage(), e);
            return CommonResponse.error("获取队列状态失败: " + e.getMessage());
        }
    }

    @Override
    public CommonResponse<CommonResponse.CommonData> healthCheck() {
        CommonResponse.CommonData data = new CommonResponse.CommonData();
        data.setStatus("UP");
        data.setService("分布式队列服务");
        data.setTimestamp(System.currentTimeMillis());
        return CommonResponse.success("服务健康", data);
    }
}