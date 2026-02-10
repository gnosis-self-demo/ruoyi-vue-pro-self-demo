package gnosis.sample.distribute.queue.sdk.simple;

import gnosis.sample.distribute.queue.sdk.QueueConfigManagementService;

import java.util.HashMap;
import java.util.Map;

/**
 * 简化的队列配置管理服务实现
 */
public class SimpleQueueConfigManagementService implements QueueConfigManagementService {
    @Override
    public Map<String, Object> getAllConfigs() {
        Map<String, Object> configs = new HashMap<String, Object>();
        Map<String, Object> defaultConfig = new HashMap<String, Object>();
        defaultConfig.put("maxLength", 1000);
        defaultConfig.put("maxQps", 100);
        configs.put("default", defaultConfig);
        return configs;
    }
    
    @Override
    public Map<String, Object> getQueueConfig(String queueName) {
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("queueName", queueName);
        config.put("maxLength", 1000);
        config.put("maxQps", 100);
        return config;
    }
    
    @Override
    public Map<String, Object> setMaxLength(String queueName, int maxLength) 
            throws IllegalArgumentException, ConfigurationException {
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength必须>=0");
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "success");
        result.put("message", "队列 '" + queueName + "' 最大长度设置为 " + maxLength);
        return result;
    }
    
    @Override
    public Map<String, Object> setMaxQps(String queueName, int maxQps) 
            throws IllegalArgumentException, ConfigurationException {
        if (maxQps <= 0) {
            throw new IllegalArgumentException("maxQps必须>0");
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "success");
        result.put("message", "队列 '" + queueName + "' 最大QPS设置为 " + maxQps);
        return result;
    }
    
    @Override
    public Map<String, Object> setEmptyPollInterval(long intervalMs) 
            throws IllegalArgumentException, ConfigurationException {
        if (intervalMs <= 0) {
            throw new IllegalArgumentException("intervalMs必须>0");
        }
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "success");
        result.put("message", "空队列轮询间隔设置为 " + intervalMs + " ms");
        return result;
    }
    
    @Override
    public Map<String, Object> resetQueueConfig(String queueName) 
            throws ConfigurationException {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "success");
        result.put("message", "队列 '" + queueName + "' 配置已重置为默认值");
        return result;
    }
    
    @Override
    public Map<String, Object> batchSetConfig(Map<String, Map<String, Integer>> configs) 
            throws ConfigurationException {
        StringBuilder resultMsg = new StringBuilder();
        for (String queueName : configs.keySet()) {
            resultMsg.append("设置 ").append(queueName).append(" 配置; ");
        }
        
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("status", "success");
        result.put("message", "批量配置更新: " + resultMsg.toString());
        return result;
    }
}