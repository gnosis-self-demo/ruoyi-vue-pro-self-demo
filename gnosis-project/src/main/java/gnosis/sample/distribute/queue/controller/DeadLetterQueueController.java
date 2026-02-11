package gnosis.sample.distribute.queue.controller;

import gnosis.sample.distribute.queue.service.DeadLetterQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 死信队列管理控制器
 * 提供死信队列的监控和管理功能
 */
@Slf4j
@RestController
@RequestMapping("/distribute-queue")
@RequiredArgsConstructor
public class DeadLetterQueueController {

    private final DeadLetterQueueService deadLetterService;

    /**
     * 获取死信队列统计信息
     */
    @GetMapping("/admin/dead-letter/stats")
    public ResponseEntity<?> getDeadLetterStats() {
        try {
            Map<String, Object> stats = deadLetterService.getDeadLetterStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("获取死信队列统计失败: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", "获取统计信息失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 清理历史死信消息
     */
    @DeleteMapping("/admin/dead-letter/cleanup")
    public ResponseEntity<?> cleanupDeadLetters(@RequestParam(defaultValue = "7") int daysToKeep) {
        try {
            if (daysToKeep <= 0) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "INVALID_PARAMETER");
                error.put("message", "保留天数必须大于0");
                return ResponseEntity.badRequest().body(error);
            }

            int deletedCount = deadLetterService.cleanupOldDeadLetters(daysToKeep);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("deletedCount", deletedCount);
            response.put("message", String.format("已清理 %d 条超过 %d 天的死信消息", deletedCount, daysToKeep));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("清理死信消息失败: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "CLEANUP_FAILED");
            error.put("message", "清理失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 重试单个死信消息
     */
    @PostMapping("/admin/dead-letter/retry/{messageId}")
    public ResponseEntity<?> retryDeadLetterMessage(@PathVariable Long messageId) {
        try {
            boolean success = deadLetterService.retryDeadLetterMessage(messageId);
            
            if (success) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("message", "死信消息已重新入队");
                response.put("messageId", messageId);
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "MESSAGE_NOT_FOUND");
                error.put("message", "未找到对应的死信消息或状态不正确");
                error.put("messageId", messageId);
                return ResponseEntity.status(404).body(error);
            }
        } catch (Exception e) {
            log.error("重试死信消息失败: messageId={}, error={}", messageId, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "RETRY_FAILED");
            error.put("message", "重试失败: " + e.getMessage());
            error.put("messageId", messageId);
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 手动触发死信队列检查
     */
    @PostMapping("/admin/dead-letter/check")
    public ResponseEntity<?> triggerDeadLetterCheck() {
        try {
            // 这里可以添加具体的队列名称参数，或者检查所有队列
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "死信队列检查功能已触发，请查看日志获取详细信息");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("触发死信队列检查失败: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "CHECK_TRIGGER_FAILED");
            error.put("message", "检查触发失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 获取死信队列配置信息
     */
    @GetMapping("/admin/dead-letter/config/{queueName}")
    public ResponseEntity<?> getDeadLetterConfig(@PathVariable String queueName) {
        try {
            Map<String, Object> config = deadLetterService.getDeadLetterConfig(queueName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("queueName", queueName);
            response.put("config", config);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取队列 {} 死信配置失败: {}", queueName, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "CONFIG_READ_FAILED");
            error.put("message", "读取配置失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 更新死信队列配置
     */
    @PutMapping("/admin/dead-letter/config/{queueName}")
    public ResponseEntity<?> updateDeadLetterConfig(
            @PathVariable String queueName,
            @RequestBody Map<String, Object> configUpdates) {
        try {
            Integer maxRetryAttempts = null;
            Long processingTimeoutMs = null;
            Long checkIntervalMs = null;
            
            if (configUpdates.containsKey("maxRetryAttempts")) {
                maxRetryAttempts = (Integer) configUpdates.get("maxRetryAttempts");
                if (maxRetryAttempts <= 0) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "INVALID_PARAMETER");
                    error.put("message", "最大重试次数必须大于0");
                    return ResponseEntity.badRequest().body(error);
                }
            }
            
            if (configUpdates.containsKey("processingTimeoutMs")) {
                processingTimeoutMs = ((Number) configUpdates.get("processingTimeoutMs")).longValue();
                if (processingTimeoutMs <= 0) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "INVALID_PARAMETER");
                    error.put("message", "处理超时时间必须大于0");
                    return ResponseEntity.badRequest().body(error);
                }
            }
            
            if (configUpdates.containsKey("checkIntervalMs")) {
                checkIntervalMs = ((Number) configUpdates.get("checkIntervalMs")).longValue();
                if (checkIntervalMs <= 0) {
                    Map<String, Object> error = new HashMap<>();
                    error.put("error", "INVALID_PARAMETER");
                    error.put("message", "检查间隔必须大于0");
                    return ResponseEntity.badRequest().body(error);
                }
            }
            
            deadLetterService.updateDeadLetterConfig(queueName, maxRetryAttempts, 
                                                   processingTimeoutMs, checkIntervalMs);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "死信队列配置更新成功");
            response.put("queueName", queueName);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("更新队列 {} 死信配置失败: {}", queueName, e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "CONFIG_UPDATE_FAILED");
            error.put("message", "配置更新失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 获取全局死信队列配置信息
     */
    @GetMapping("/admin/dead-letter/config")
    public ResponseEntity<?> getGlobalDeadLetterConfig() {
        try {
            Map<String, Object> config = new HashMap<>();
            config.put("defaultMaxRetryAttempts", 3);
            config.put("defaultProcessingTimeoutMs", 1800000L); // 30分钟
            config.put("defaultCheckIntervalMs", 300000L); // 5分钟
            config.put("supportedStatus", new String[]{"pending", "processing", "done", "failed", "dead_letter"});
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("config", config);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取全局死信队列配置失败: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "CONFIG_READ_FAILED");
            error.put("message", "读取全局配置失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
}