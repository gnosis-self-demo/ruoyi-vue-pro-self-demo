package com.gnosis.distribute.queue.sdk;

/**
 * 分布式队列SDK门面接口
 * 整合所有队列管理功能，提供统一的服务入口
 */
public interface DistributedQueueSdk {

    /**
     * 获取任务管理服务
     * 
     * @return TaskManagementService实例
     */
    TaskManagementService getTaskManagementService();

    /**
     * 获取队列实例管理服务
     * 
     * @return QueueInstanceManagementService实例
     */
    QueueInstanceManagementService getQueueInstanceManagementService();

    /**
     * 获取队列配置管理服务
     * 
     * @return QueueConfigManagementService实例
     */
    QueueConfigManagementService getQueueConfigManagementService();
}