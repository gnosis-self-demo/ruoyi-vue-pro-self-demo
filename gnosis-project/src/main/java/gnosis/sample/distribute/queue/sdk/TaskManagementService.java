package gnosis.sample.distribute.queue.sdk;

import gnosis.sample.distribute.queue.dto.request.TaskSubmitRequest;
import gnosis.sample.distribute.queue.dto.response.CommonResponse;
import gnosis.sample.distribute.queue.dto.response.TaskSubmitResponse;
import gnosis.sample.distribute.queue.exception.ProcessingFailedException;
import gnosis.sample.distribute.queue.exception.QueueFullException;
import gnosis.sample.distribute.queue.exception.QueueNotFoundException;
import gnosis.sample.distribute.queue.exception.TimeoutException;

/**
 * 任务管理服务接口
 * 提供异步和同步任务提交功能
 */
public interface TaskManagementService {

    /**
     * 异步提交任务到指定队列
     * 
     * @param request 任务提交请求
     * @return 任务提交响应
     * @throws QueueNotFoundException 队列不存在异常
     * @throws QueueFullException 队列已满异常
     */
    TaskSubmitResponse submitTask(TaskSubmitRequest request) throws QueueNotFoundException, QueueFullException;

    /**
     * 同步提交任务并等待处理结果
     * 
     * @param request 任务提交请求
     * @return 任务提交响应
     * @throws QueueNotFoundException 队列不存在异常
     * @throws QueueFullException 队列已满异常
     * @throws TimeoutException 等待结果超时异常
     * @throws ProcessingFailedException 任务处理失败异常
     */
    TaskSubmitResponse submitTaskSync(TaskSubmitRequest request) 
        throws QueueNotFoundException, QueueFullException, TimeoutException, ProcessingFailedException;

    /**
     * 查询队列状态
     * 
     * @param queueName 队列名称
     * @return 队列状态响应
     */
    CommonResponse<CommonResponse.CommonData> getQueueStatus(String queueName);

    /**
     * 健康检查
     * 
     * @return 服务健康状态响应
     */
    CommonResponse<CommonResponse.CommonData> healthCheck();
}