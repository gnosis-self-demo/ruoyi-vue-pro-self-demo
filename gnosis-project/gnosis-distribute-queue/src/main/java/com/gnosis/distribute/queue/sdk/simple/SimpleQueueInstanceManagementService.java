package com.gnosis.distribute.queue.sdk.simple;

import com.gnosis.distribute.queue.sdk.QueueInstanceManagementService;

import java.util.HashMap;
import java.util.Map;

/**
 * 简化的队列实例管理服务实现
 */
public class SimpleQueueInstanceManagementService implements QueueInstanceManagementService {
    @Override
    public Map<String, Object> createQueueInstance(String queueName, Map<String, Object> configParams) 
            throws QueueAlreadyExistsException, CreationFailedException {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "success");
        result.put("message", "队列实例创建成功: " + queueName);
        result.put("queueName", queueName);
        result.put("config", configParams != null ? configParams : new HashMap<String, Object>());
        return result;
    }
    
    @Override
    public Map<String, Object> getQueueInstance(String queueName) {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("queueName", queueName);
        result.put("status", "active");
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("maxLength", 1000);
        config.put("maxQps", 100);
        result.put("config", config);
        return result;
    }
    
    @Override
    public Map<String, Object> getAllQueueInstances() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("total", 1);
        result.put("queues", new String[]{"default-queue"});
        return result;
    }
    
    @Override
    public Map<String, Object> updateQueueConfig(String queueName, Map<String, Object> configUpdates) 
            throws QueueNotFoundException, UpdateFailedException {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "success");
        result.put("message", "配置更新成功: " + queueName);
        result.put("updatedConfig", configUpdates);
        return result;
    }
    
    @Override
    public boolean deleteQueueInstance(String queueName) {
        // 模拟删除成功
        return true;
    }
    
    @Override
    public Map<String, Object> createMultipleInstances(Map<String, Map<String, Object>> batchConfig) 
            throws BatchCreationFailedException {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "success");
        result.put("message", "批量队列实例创建成功");
        result.put("createdQueues", batchConfig.keySet());
        return result;
    }
}