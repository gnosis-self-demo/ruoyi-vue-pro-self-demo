package gnosis.sample.distribute.queue.sdk.simple;

import gnosis.sample.distribute.queue.sdk.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 简化版分布式队列SDK实现
 * 不依赖Spring框架，便于独立使用
 */
public class SimpleDistributedQueueSdk implements DistributedQueueSdk {
    
    private final TaskManagementService taskService;
    private final QueueInstanceManagementService instanceService;
    private final QueueConfigManagementService configService;
    
    public SimpleDistributedQueueSdk() {
        // 这里应该注入实际的服务实现
        // 为了演示，创建模拟实现
        this.taskService = new SimpleTaskManagementService();
        this.instanceService = new SimpleQueueInstanceManagementService();
        this.configService = new SimpleQueueConfigManagementService();
    }
    
    @Override
    public TaskManagementService getTaskManagementService() {
        return taskService;
    }
    
    @Override
    public QueueInstanceManagementService getQueueInstanceManagementService() {
        return instanceService;
    }
    
    @Override
    public QueueConfigManagementService getQueueConfigManagementService() {
        return configService;
    }
    

}