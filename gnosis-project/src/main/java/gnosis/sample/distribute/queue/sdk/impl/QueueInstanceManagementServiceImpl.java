package gnosis.sample.distribute.queue.sdk.impl;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.factory.DistributedQueueFactory;
import gnosis.sample.distribute.queue.model.QueueInstance;
import gnosis.sample.distribute.queue.sdk.QueueInstanceManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 队列实例管理服务实现类
 */
@Slf4j
@Service
public class QueueInstanceManagementServiceImpl implements QueueInstanceManagementService {
    
    private final DistributedQueueFactory queueFactory;
    
    public QueueInstanceManagementServiceImpl(DistributedQueueFactory queueFactory) {
        this.queueFactory = queueFactory;
    }

    public Map<String, Object> createQueueInstance(String queueName, Map<String, Object> configParams) 
            throws QueueAlreadyExistsException, CreationFailedException {
        
        try {
            RuntimeQueueConfig config = new RuntimeQueueConfig();
            
            // 解析配置参数
            if (configParams != null) {
                if (configParams.containsKey("maxLength")) {
                    config.setMaxQueueLength(queueName, (Integer) configParams.get("maxLength"));
                }
                if (configParams.containsKey("maxQps")) {
                    config.setMaxQps(queueName, (Integer) configParams.get("maxQps"));
                }
                if (configParams.containsKey("pollInterval")) {
                    config.setEmptyPollIntervalMs(((Number) configParams.get("pollInterval")).longValue());
                }
            }
            
            QueueInstance instance = queueFactory.createQueueInstance(queueName, config);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "队列实例创建成功");
            response.put("queueName", queueName);
            response.put("config", getConfigSummary(instance.getConfig(), queueName));
            
            return response;
            
        } catch (IllegalArgumentException e) {
            throw new QueueAlreadyExistsException(e.getMessage());
        } catch (Exception e) {
            log.error("创建队列实例失败: {}", e.getMessage(), e);
            throw new CreationFailedException(e.getMessage(), e);
        }
    }

    public Map<String, Object> getQueueInstance(String queueName) {
        QueueInstance instance = queueFactory.getQueueInstance(queueName);
        
        if (instance == null) {
            return null;
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("queueName", instance.getQueueName());
        response.put("config", getConfigSummary(instance.getConfig(), queueName));
        response.put("status", "active");
        
        return response;
    }

    public Map<String, Object> getAllQueueInstances() {
        return queueFactory.getQueueStatistics();
    }

    public Map<String, Object> updateQueueConfig(String queueName, Map<String, Object> configUpdates) 
            throws QueueNotFoundException, UpdateFailedException {
        
        QueueInstance instance = queueFactory.getQueueInstance(queueName);
        if (instance == null) {
            throw new QueueNotFoundException("队列 '" + queueName + "' 不存在");
        }
        
        try {
            RuntimeQueueConfig config = instance.getConfig();
            
            // 更新配置
            if (configUpdates.containsKey("maxLength")) {
                config.setMaxQueueLength(queueName, (Integer) configUpdates.get("maxLength"));
            }
            if (configUpdates.containsKey("maxQps")) {
                config.setMaxQps(queueName, (Integer) configUpdates.get("maxQps"));
            }
            if (configUpdates.containsKey("pollInterval")) {
                config.setEmptyPollIntervalMs(((Number) configUpdates.get("pollInterval")).longValue());
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "配置更新成功");
            response.put("queueName", queueName);
            response.put("updatedConfig", getConfigSummary(config, queueName));
            
            return response;
            
        } catch (Exception e) {
            log.error("更新队列配置失败: {}", e.getMessage(), e);
            throw new UpdateFailedException(e.getMessage(), e);
        }
    }

    public boolean deleteQueueInstance(String queueName) {
        return queueFactory.removeQueueInstance(queueName);
    }

    public Map<String, Object> createMultipleInstances(Map<String, Map<String, Object>> batchConfig) 
            throws BatchCreationFailedException {
        
        try {
            Map<String, RuntimeQueueConfig> configs = new HashMap<String, RuntimeQueueConfig>();
            
            for (Map.Entry<String, Map<String, Object>> entry : batchConfig.entrySet()) {
                String queueName = entry.getKey();
                Map<String, Object> configParams = entry.getValue();
                
                RuntimeQueueConfig config = new RuntimeQueueConfig();
                if (configParams.containsKey("maxLength")) {
                    config.setMaxQueueLength(queueName, (Integer) configParams.get("maxLength"));
                }
                if (configParams.containsKey("maxQps")) {
                    config.setMaxQps(queueName, (Integer) configParams.get("maxQps"));
                }
                if (configParams.containsKey("pollInterval")) {
                    config.setEmptyPollIntervalMs(((Number) configParams.get("pollInterval")).longValue());
                }
                
                configs.put(queueName, config);
            }
            
            queueFactory.createMultipleQueueInstances(configs);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "批量队列实例创建成功");
            response.put("createdQueues", configs.keySet());
            
            return response;
            
        } catch (Exception e) {
            log.error("批量创建队列实例失败: {}", e.getMessage(), e);
            throw new BatchCreationFailedException(e.getMessage(), e);
        }
    }

    /**
     * 获取配置摘要
     */
    private Map<String, Object> getConfigSummary(RuntimeQueueConfig config, String queueName) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("maxLength", config.getMaxQueueLength(queueName));
        summary.put("maxQps", config.getMaxQps(queueName));
        summary.put("pollInterval", config.getEmptyPollIntervalMs());
        return summary;
    }
}