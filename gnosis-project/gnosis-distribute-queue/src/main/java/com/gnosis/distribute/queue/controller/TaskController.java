package com.gnosis.distribute.queue.controller;

import com.gnosis.distribute.queue.exception.QueueFullException;
import com.gnosis.distribute.queue.factory.DistributedQueueFactory;
import com.gnosis.distribute.queue.model.QueueInstance;
import com.gnosis.distribute.queue.model.QueueResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * 任务提交控制器
 * 提供异步和同步两种入队方式
 */
@Slf4j
@RestController
@RequiredArgsConstructor
public class TaskController {

    private final DistributedQueueFactory queueFactory;

    /**
     * 异步提交任务
     */
    @PostMapping("/distribute-queue/tasks/{queueName}")
    public ResponseEntity<?> submitTask(@PathVariable String queueName, @RequestBody String payload) {
        QueueInstance instance = queueFactory.getQueueInstance(queueName);
        if (instance == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "QUEUE_NOT_FOUND");
            error.put("message", "队列 '" + queueName + "' 不存在，请先创建");
            return ResponseEntity.status(404).body(error);
        }
        
        try {
            instance.getService().enqueue(queueName, payload);
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "任务已加入队列");
            response.put("queueName", queueName);
            return ResponseEntity.accepted().body(response);
        } catch (QueueFullException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "QUEUE_FULL");
            error.put("message", e.getMessage());
            error.put("queue", e.getQueueName());
            error.put("current", e.getCurrentSize());
            error.put("max", e.getMaxSize());
            return ResponseEntity.status(429).body(error);
        } catch (SQLException e) {
            log.error("数据库操作失败: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "DATABASE_ERROR");
            error.put("message", "数据库操作失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        } catch (Exception e) {
            log.error("内部服务器错误: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", "内部服务器错误: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 同步提交任务并等待结果
     */
    @PostMapping("/distribute-queue/tasks-sync/{queueName}")
    public ResponseEntity<?> submitTaskSync(@PathVariable String queueName,
                                            @RequestBody String payload,
                                            @RequestParam(defaultValue = "5000") long timeoutMs) {
        QueueInstance instance = queueFactory.getQueueInstance(queueName);
        if (instance == null) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "QUEUE_NOT_FOUND");
            error.put("message", "队列 '" + queueName + "' 不存在，请先创建");
            return ResponseEntity.status(404).body(error);
        }
        
        try {
            // 入队并获取请求ID
            String requestId = instance.getService().enqueueWithResult(queueName, payload);
            
            // 等待处理结果
            QueueResult result = instance.getService().waitForResult(requestId, timeoutMs);

            if (result == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "TIMEOUT");
                error.put("message", "等待结果超时: " + timeoutMs + "ms");
                error.put("requestId", requestId);
                return ResponseEntity.status(504).body(error);
            }

            if (result.isSuccess()) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "success");
                response.put("requestId", result.getRequestId());
                response.put("result", result.getResultData());
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "PROCESSING_FAILED");
                error.put("message", result.getErrorMessage());
                error.put("requestId", result.getRequestId());
                return ResponseEntity.status(500).body(error);
            }
        } catch (QueueFullException e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "QUEUE_FULL");
            error.put("message", e.getMessage());
            error.put("queue", e.getQueueName());
            error.put("current", e.getCurrentSize());
            error.put("max", e.getMaxSize());
            return ResponseEntity.status(429).body(error);
        } catch (SQLException e) {
            log.error("数据库操作失败: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "DATABASE_ERROR");
            error.put("message", "数据库操作失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERRUPTED");
            error.put("message", "请求被中断");
            return ResponseEntity.status(500).body(error);
        } catch (Exception e) {
            log.error("内部服务器错误: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", "内部服务器错误: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 查询队列状态
     */
    @GetMapping("/distribute-queue/queues/{queueName}/status")
    public ResponseEntity<?> getQueueStatus(@PathVariable String queueName) {
        try {
            Map<String, Object> status = new HashMap<>();
            status.put("queueName", queueName);
            status.put("timestamp", System.currentTimeMillis());
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            log.error("获取队列状态失败: {}", e.getMessage(), e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "INTERNAL_ERROR");
            error.put("message", "获取队列状态失败: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 健康检查接口
     */
    @GetMapping("/distribute-queue/health")
    public ResponseEntity<?> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "分布式队列服务");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }
}