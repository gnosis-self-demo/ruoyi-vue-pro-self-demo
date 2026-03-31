package com.gnosis.distribute.queue.controller;

import com.gnosis.distribute.queue.config.RuntimeQueueConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 队列配置管理控制器
 * 支持运行时动态调整队列配置
 */
@RestController
@RequiredArgsConstructor
public class QueueConfigController {
    
    private static final Logger log = LoggerFactory.getLogger(QueueConfigController.class);

    private final RuntimeQueueConfig config;

    /**
     * 获取所有队列配置
     */
    @GetMapping("/distribute-queue/admin/queue-config")
    public ResponseEntity<Map<String, Object>> getAllConfigs() {
        Map<String, Object> result = config.getAllConfigs();
        return ResponseEntity.ok(result);
    }

    /**
     * 获取指定队列的配置
     */
    @GetMapping("/distribute-queue/admin/queue-config/{queueName}")
    public ResponseEntity<?> getQueueConfig(@PathVariable String queueName) {
        Map<String, Object> queueConfig = new HashMap<>();
        queueConfig.put("queueName", queueName);
        queueConfig.put("maxLength", config.getMaxQueueLength(queueName));
        queueConfig.put("maxQps", config.getMaxQps(queueName));
        return ResponseEntity.ok(queueConfig);
    }

    /**
     * 设置队列最大长度
     */
    @PostMapping("/distribute-queue/admin/queue-config/{queueName}/max-length")
    public ResponseEntity<?> setMaxLength(@PathVariable String queueName,
                                          @RequestParam int maxLength) {
        try {
            if (maxLength < 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("maxLength必须>=0"));
            }
            config.setMaxQueueLength(queueName, maxLength);
            return ResponseEntity.ok(createSuccessResponse("队列 '" + queueName + "' 最大长度设置为 " + maxLength));
        } catch (Exception e) {
            log.error("设置最大长度失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(createErrorResponse("设置最大长度失败: " + e.getMessage()));
        }
    }

    /**
     * 设置队列最大QPS
     */
    @PostMapping("/distribute-queue/admin/queue-config/{queueName}/max-qps")
    public ResponseEntity<?> setMaxQps(@PathVariable String queueName,
                                       @RequestParam int maxQps) {
        try {
            if (maxQps <= 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("maxQps必须>0"));
            }
            config.setMaxQps(queueName, maxQps);
            return ResponseEntity.ok(createSuccessResponse("队列 '" + queueName + "' 最大QPS设置为 " + maxQps));
        } catch (Exception e) {
            log.error("设置最大QPS失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(createErrorResponse("设置最大QPS失败: " + e.getMessage()));
        }
    }

    /**
     * 设置空队列轮询间隔
     */
    @PostMapping("/distribute-queue/admin/queue-config/empty-poll-interval")
    public ResponseEntity<?> setEmptyPollInterval(@RequestParam long intervalMs) {
        try {
            if (intervalMs <= 0) {
                return ResponseEntity.badRequest().body(createErrorResponse("intervalMs必须>0"));
            }
            config.setEmptyPollIntervalMs(intervalMs);
            return ResponseEntity.ok(createSuccessResponse("空队列轮询间隔设置为 " + intervalMs + " ms"));
        } catch (Exception e) {
            log.error("设置轮询间隔失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(createErrorResponse("设置轮询间隔失败: " + e.getMessage()));
        }
    }

    /**
     * 重置指定队列的所有配置
     */
    @DeleteMapping("/distribute-queue/admin/queue-config/{queueName}")
    public ResponseEntity<?> resetQueueConfig(@PathVariable String queueName) {
        try {
            config.resetQueueConfig(queueName);
            return ResponseEntity.ok(createSuccessResponse("队列 '" + queueName + "' 配置已重置为默认值"));
        } catch (Exception e) {
            log.error("重置配置失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(createErrorResponse("重置配置失败: " + e.getMessage()));
        }
    }

    /**
     * 批量设置多个队列配置
     */
    @PostMapping("/distribute-queue/admin/queue-config/batch")
    public ResponseEntity<?> batchSetConfig(@RequestBody Map<String, Map<String, Integer>> configs) {
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
            
            return ResponseEntity.ok(createSuccessResponse("批量配置更新: " + result.toString()));
        } catch (Exception e) {
            log.error("批量设置配置失败: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(createErrorResponse("批量设置配置失败: " + e.getMessage()));
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

    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "error");
        response.put("message", message);
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}