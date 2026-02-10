package gnosis.sample.distribute.queue.controller;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.factory.DistributedQueueFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 队列实例管理控制器
 * 支持动态创建、配置和管理多个队列实例
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class QueueInstanceController {

    private final DistributedQueueFactory queueFactory;

    /**
     * 创建新的队列实例
     */
    @PostMapping("/distribute-queue/admin/queue-instances/{queueName}")
    public ResponseEntity<?> createQueueInstance(
            @PathVariable String queueName,
            @RequestBody(required = false) Map<String, Object> configParams) {
        
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
            
            DistributedQueueFactory.QueueInstance instance = queueFactory.createQueueInstance(queueName, config);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "队列实例创建成功");
            response.put("queueName", queueName);
            response.put("config", getConfigSummary(instance.getConfig(), queueName));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(createErrorResponse("QUEUE_EXISTS", e.getMessage()));
        } catch (Exception e) {
            log.error("创建队列实例失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                .body(createErrorResponse("CREATION_FAILED", e.getMessage()));
        }
    }

    /**
     * 获取队列实例信息
     */
    @GetMapping("/distribute-queue/admin/queue-instances/{queueName}")
    public ResponseEntity<?> getQueueInstance(@PathVariable String queueName) {
        DistributedQueueFactory.QueueInstance instance = queueFactory.getQueueInstance(queueName);
        
        if (instance == null) {
            return ResponseEntity.notFound().build();
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("queueName", instance.getQueueName());
        response.put("config", getConfigSummary(instance.getConfig(), queueName));
        response.put("status", "active");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取所有队列实例
     */
    @GetMapping("/distribute-queue/admin/queue-instances")
    public ResponseEntity<?> getAllQueueInstances() {
        Map<String, Object> response = queueFactory.getQueueStatistics();
        return ResponseEntity.ok(response);
    }

    /**
     * 更新队列实例配置
     */
    @PutMapping("/distribute-queue/admin/queue-instances/{queueName}/config")
    public ResponseEntity<?> updateQueueConfig(
            @PathVariable String queueName,
            @RequestBody Map<String, Object> configUpdates) {
        
        DistributedQueueFactory.QueueInstance instance = queueFactory.getQueueInstance(queueName);
        if (instance == null) {
            return ResponseEntity.notFound().build();
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
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("更新队列配置失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                .body(createErrorResponse("UPDATE_FAILED", e.getMessage()));
        }
    }

    /**
     * 删除队列实例
     */
    @DeleteMapping("/distribute-queue/admin/queue-instances/{queueName}")
    public ResponseEntity<?> deleteQueueInstance(@PathVariable String queueName) {
        boolean removed = queueFactory.removeQueueInstance(queueName);
        
        if (removed) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "队列实例删除成功");
            response.put("queueName", queueName);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 批量创建队列实例
     */
    @PostMapping("/distribute-queue/admin/queue-instances/batch")
    public ResponseEntity<?> createMultipleInstances(@RequestBody Map<String, Map<String, Object>> batchConfig) {
        try {
            Map<String, RuntimeQueueConfig> configs = new HashMap<>();
            
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
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("批量创建队列实例失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500)
                .body(createErrorResponse("BATCH_CREATION_FAILED", e.getMessage()));
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

    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String errorCode, String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", errorCode);
        error.put("message", message);
        error.put("timestamp", System.currentTimeMillis());
        return error;
    }
}