package com.gnosis.distribute.queue.sdk.impl;

import com.gnosis.distribute.queue.config.RuntimeQueueConfig;
import com.gnosis.distribute.queue.sdk.QueueConfigManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 队列配置管理服务实现类
 */
@Service
public class QueueConfigManagementServiceImpl implements QueueConfigManagementService {
    
    private static final Logger log = LoggerFactory.getLogger(QueueConfigManagementServiceImpl.class);
    
    private final RuntimeQueueConfig config;
    
    public QueueConfigManagementServiceImpl(RuntimeQueueConfig config) {
        this.config = config;
    }

    public Map<String, Object> getAllConfigs() {
        return config.getAllConfigs();
    }

    public Map<String, Object> getQueueConfig(String queueName) {
        Map<String, Object> queueConfig = new HashMap<>();
        queueConfig.put("queueName", queueName);
        queueConfig.put("maxLength", config.getMaxQueueLength(queueName));
        queueConfig.put("maxQps", config.getMaxQps(queueName));
        return queueConfig;
    }

    public Map<String, Object> setMaxLength(String queueName, int maxLength) 
            throws IllegalArgumentException, ConfigurationException {
        
        try {
            if (maxLength < 0) {
                throw new IllegalArgumentException("maxLength必须>=0");
            }
            config.setMaxQueueLength(queueName, maxLength);
            return createSuccessResponse("队列 '" + queueName + "' 最大长度设置为 " + maxLength);
        } catch (Exception e) {
            log.error("设置最大长度失败: {}", e.getMessage(), e);
            throw new ConfigurationException("设置最大长度失败: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> setMaxQps(String queueName, int maxQps) 
            throws IllegalArgumentException, ConfigurationException {
        
        try {
            if (maxQps <= 0) {
                throw new IllegalArgumentException("maxQps必须>0");
            }
            config.setMaxQps(queueName, maxQps);
            return createSuccessResponse("队列 '" + queueName + "' 最大QPS设置为 " + maxQps);
        } catch (Exception e) {
            log.error("设置最大QPS失败: {}", e.getMessage(), e);
            throw new ConfigurationException("设置最大QPS失败: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> setEmptyPollInterval(long intervalMs) 
            throws IllegalArgumentException, ConfigurationException {
        
        try {
            if (intervalMs <= 0) {
                throw new IllegalArgumentException("intervalMs必须>0");
            }
            config.setEmptyPollIntervalMs(intervalMs);
            return createSuccessResponse("空队列轮询间隔设置为 " + intervalMs + " ms");
        } catch (Exception e) {
            log.error("设置轮询间隔失败: {}", e.getMessage(), e);
            throw new ConfigurationException("设置轮询间隔失败: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> resetQueueConfig(String queueName) 
            throws ConfigurationException {
        
        try {
            config.resetQueueConfig(queueName);
            return createSuccessResponse("队列 '" + queueName + "' 配置已重置为默认值");
        } catch (Exception e) {
            log.error("重置配置失败: {}", e.getMessage(), e);
            throw new ConfigurationException("重置配置失败: " + e.getMessage(), e);
        }
    }

    public Map<String, Object> batchSetConfig(Map<String, Map<String, Integer>> configs) 
            throws ConfigurationException {
        
        try {
            StringBuilder result = new StringBuilder();
            for (Map.Entry<String, Map<String, Integer>> entry : configs.entrySet()) {
                String queueName = entry.getKey();
                Map<String, Integer> queueConfigs = entry.getValue();
                
                if (queueConfigs.containsKey("maxLength")) {
                    int maxLength = queueConfigs.get("maxLength");
                    if (maxLength >= 0) {
                        config.setMaxQueueLength(queueName, maxLength);
                        result.append("设置 ").append(queueName).append(" 最大长度为 ").append(maxLength).append("; ");
                    }
                }
                
                if (queueConfigs.containsKey("maxQps")) {
                    int maxQps = queueConfigs.get("maxQps");
                    if (maxQps > 0) {
                        config.setMaxQps(queueName, maxQps);
                        result.append("设置 ").append(queueName).append(" 最大QPS为 ").append(maxQps).append("; ");
                    }
                }
            }
            
            return createSuccessResponse("批量配置更新: " + result.toString());
        } catch (Exception e) {
            log.error("批量设置配置失败: {}", e.getMessage(), e);
            throw new ConfigurationException("批量设置配置失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}