package com.gnosis.distribute.queue.sdk.simple;

import com.gnosis.distribute.queue.dto.request.TaskSubmitRequest;
import com.gnosis.distribute.queue.dto.response.CommonResponse;
import com.gnosis.distribute.queue.dto.response.TaskSubmitResponse;
import com.gnosis.distribute.queue.exception.ProcessingFailedException;
import com.gnosis.distribute.queue.exception.QueueFullException;
import com.gnosis.distribute.queue.exception.QueueNotFoundException;
import com.gnosis.distribute.queue.exception.TimeoutException;
import com.gnosis.distribute.queue.sdk.TaskManagementService;

/**
 * 简化的任务管理服务实现
 */
public class SimpleTaskManagementService implements TaskManagementService {
    
    @Override
    public TaskSubmitResponse submitTask(TaskSubmitRequest request) 
            throws QueueNotFoundException, QueueFullException {
        // 模拟实现
        return TaskSubmitResponse.asyncSuccess(request.getQueueName());
    }
    
    @Override
    public TaskSubmitResponse submitTaskSync(TaskSubmitRequest request) 
            throws QueueNotFoundException, QueueFullException, TimeoutException, ProcessingFailedException {
        // 模拟实现
        return TaskSubmitResponse.syncSuccess("req-" + System.currentTimeMillis(), 
            "处理完成: " + request.getPayload());
    }
    
    @Override
    public CommonResponse<CommonResponse.CommonData> getQueueStatus(String queueName) {
        CommonResponse.CommonData data = new CommonResponse.CommonData();
        data.setQueueName(queueName);
        data.setStatus("active");
        return CommonResponse.success("获取队列状态成功", data);
    }
    
    @Override
    public CommonResponse<CommonResponse.CommonData> healthCheck() {
        CommonResponse.CommonData data = new CommonResponse.CommonData();
        data.setStatus("UP");
        data.setService("分布式队列服务");
        return CommonResponse.success("服务健康", data);
    }
}