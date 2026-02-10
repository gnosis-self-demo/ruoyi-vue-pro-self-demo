package gnosis.sample.distribute.queue.sdk.impl;

import gnosis.sample.distribute.queue.sdk.*;
import org.springframework.stereotype.Service;

/**
 * 分布式队列SDK门面实现类
 * 整合所有队列管理功能的统一入口
 */
@Service
public class DistributedQueueSdkImpl implements DistributedQueueSdk {
    
    private final TaskManagementService taskManagementService;
    private final QueueInstanceManagementService queueInstanceManagementService;
    private final QueueConfigManagementService queueConfigManagementService;
    
    public DistributedQueueSdkImpl(
            TaskManagementService taskManagementService,
            QueueInstanceManagementService queueInstanceManagementService,
            QueueConfigManagementService queueConfigManagementService) {
        this.taskManagementService = taskManagementService;
        this.queueInstanceManagementService = queueInstanceManagementService;
        this.queueConfigManagementService = queueConfigManagementService;
    }

    public TaskManagementService getTaskManagementService() {
        return taskManagementService;
    }

    public QueueInstanceManagementService getQueueInstanceManagementService() {
        return queueInstanceManagementService;
    }

    public QueueConfigManagementService getQueueConfigManagementService() {
        return queueConfigManagementService;
    }
}