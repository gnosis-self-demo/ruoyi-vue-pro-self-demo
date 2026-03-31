package com.gnosis.distribute.queue.sdk;

import java.util.Map;

/**
 * 队列实例管理服务接口
 * 支持动态创建、配置和管理多个队列实例
 */
public interface QueueInstanceManagementService {

    /**
     * 创建新的队列实例
     * 
     * @param queueName 队列名称
     * @param configParams 配置参数（可选）
     * @return 创建结果，包含队列信息和配置
     * @throws QueueAlreadyExistsException 队列已存在异常
     * @throws CreationFailedException 创建失败异常
     */
    Map<String, Object> createQueueInstance(String queueName, Map<String, Object> configParams) 
        throws QueueAlreadyExistsException, CreationFailedException;

    /**
     * 获取队列实例信息
     * 
     * @param queueName 队列名称
     * @return 队列实例信息，如果不存在返回null
     */
    Map<String, Object> getQueueInstance(String queueName);

    /**
     * 获取所有队列实例统计信息
     * 
     * @return 所有队列实例的信息
     */
    Map<String, Object> getAllQueueInstances();

    /**
     * 更新队列实例配置
     * 
     * @param queueName 队列名称
     * @param configUpdates 配置更新内容
     * @return 更新结果
     * @throws QueueNotFoundException 队列不存在异常
     * @throws UpdateFailedException 更新失败异常
     */
    Map<String, Object> updateQueueConfig(String queueName, Map<String, Object> configUpdates) 
        throws QueueNotFoundException, UpdateFailedException;

    /**
     * 删除队列实例
     * 
     * @param queueName 队列名称
     * @return 删除结果，true表示删除成功，false表示队列不存在
     */
    boolean deleteQueueInstance(String queueName);

    /**
     * 批量创建队列实例
     * 
     * @param batchConfig 批量配置，key为队列名称，value为配置参数
     * @return 创建结果
     * @throws BatchCreationFailedException 批量创建失败异常
     */
    Map<String, Object> createMultipleInstances(Map<String, Map<String, Object>> batchConfig) 
        throws BatchCreationFailedException;

    // 自定义异常类
    class QueueAlreadyExistsException extends Exception {
        public QueueAlreadyExistsException(String message) {
            super(message);
        }
    }

    class QueueNotFoundException extends Exception {
        public QueueNotFoundException(String message) {
            super(message);
        }
    }

    class CreationFailedException extends Exception {
        public CreationFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    class UpdateFailedException extends Exception {
        public UpdateFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    class BatchCreationFailedException extends Exception {
        public BatchCreationFailedException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}