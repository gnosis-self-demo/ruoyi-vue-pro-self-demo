好的！以下是**完全满足你所有要求的完整实现**，包括：

- ✅ 表名：`sys_distributed_queue` 和 `sys_distributed_queue_result`
- ✅ **数据库无任何枚举校验**（无 `CHECK`、无 `ENUM`）
- ✅ **队列最大长度 & QPS 为运行时动态配置**（通过 REST 接口实时修改，无需重启）
- ✅ 兼容 **Java 8**（无 Lambda / Stream）
- ✅ 包含：异步入队、同步等待结果、QPS 限流、队列长度限制、定期清理、消费者自动拉取
- ✅ 不包含主启动类（Main Class）

---

## 一、数据库建表语句（无约束）

```sql
-- 主队列表
CREATE TABLE sys_distributed_queue (
    id BIGINT NOT NULL PRIMARY KEY,
    queue_name VARCHAR(128) NOT NULL,
    message_body TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending' NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    consumer_id VARCHAR(128),
    attempt_count INT DEFAULT 0
);

CREATE INDEX idx_queue_status_id ON sys_distributed_queue (queue_name, status, id);

-- 结果表
CREATE TABLE sys_distributed_queue_result (
    request_id VARCHAR(64) NOT NULL PRIMARY KEY,
    result_status VARCHAR(20) NOT NULL,
    result_data TEXT,
    error_message TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

> 🔸 所有状态合法性由 Java 枚举保证，数据库不做校验。

---

## 二、Java 枚举：QueueMessageStatus

```java
// src/main/java/com/example/demo/enums/QueueMessageStatus.java
package com.example.demo.enums;

public enum QueueMessageStatus {
    PENDING("pending"),
    PROCESSING("processing"),
    DONE("done");

    private final String value;

    private QueueMessageStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static QueueMessageStatus fromValue(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Status value cannot be null or empty");
        }
        for (QueueMessageStatus status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid status value: " + value);
    }
}
```

---

## 三、消息模型

```java
// src/main/java/com/example/demo/model/QueueMessage.java
package com.example.demo.model;

import com.example.demo.enums.QueueMessageStatus;

public class QueueMessage {
    private final long id;
    private final String queueName;
    private final String messageBody;
    private final int attemptCount;
    private final QueueMessageStatus status;

    public QueueMessage(long id, String queueName, String messageBody, int attemptCount) {
        this(id, queueName, messageBody, attemptCount, null);
    }

    public QueueMessage(long id, String queueName, String messageBody, int attemptCount, QueueMessageStatus status) {
        this.id = id;
        this.queueName = queueName;
        this.messageBody = messageBody;
        this.attemptCount = attemptCount;
        this.status = status;
    }

    public long getId() { return id; }
    public String getQueueName() { return queueName; }
    public String getMessageBody() { return messageBody; }
    public int getAttemptCount() { return attemptCount; }
    public QueueMessageStatus getStatus() { return status; }
}
```

---

## 四、ID 生成器（Snowflake 简化版）

```java
// src/main/java/com/example/demo/util/IdGenerator.java
package com.example.demo.util;

public class IdGenerator {
    private static final long START_TIME = 1700000000000L; // 2023-11-15
    private static volatile long lastTimestamp = -1L;
    private static long sequence = 0L;
    private static final long MAX_SEQUENCE = 1023L;

    public static synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            try {
                Thread.sleep(lastTimestamp - timestamp);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            timestamp = System.currentTimeMillis();
        }
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;
        return ((timestamp - START_TIME) << 10) | sequence;
    }
}
```

---

## 五、自定义异常

```java
// src/main/java/com/example/demo/exception/QueueFullException.java
package com.example.demo.exception;

public class QueueFullException extends RuntimeException {
    private final String queueName;
    private final int currentSize;
    private final int maxSize;

    public QueueFullException(String queueName, int currentSize, int maxSize) {
        super("Queue '" + queueName + "' is full: " + currentSize + " >= " + maxSize);
        this.queueName = queueName;
        this.currentSize = currentSize;
        this.maxSize = maxSize;
    }

    public String getQueueName() { return queueName; }
    public int getCurrentSize() { return currentSize; }
    public int getMaxSize() { return maxSize; }
}
```

---

## 六、结果模型

```java
// src/main/java/com/example/demo/model/QueueResult.java
package com.example.demo.model;

public class QueueResult {
    private final String requestId;
    private final boolean success;
    private final String resultData;
    private final String errorMessage;

    public QueueResult(String requestId, boolean success, String resultData, String errorMessage) {
        this.requestId = requestId;
        this.success = success;
        this.resultData = resultData;
        this.errorMessage = errorMessage;
    }

    public String getRequestId() { return requestId; }
    public boolean isSuccess() { return success; }
    public String getResultData() { return resultData; }
    public String getErrorMessage() { return errorMessage; }
}
```

---

## 七、运行时配置管理器（核心：动态配置）

```java
// src/main/java/com/example/demo/config/RuntimeQueueConfig.java
package com.example.demo.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RuntimeQueueConfig {

    private final ConcurrentHashMap<String, Integer> maxLengthMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Integer> maxQpsMap = new ConcurrentHashMap<>();
    private volatile long emptyPollIntervalMs = 1000L;

    public int getMaxQueueLength(String queueName) {
        Integer max = maxLengthMap.get(queueName);
        return max != null ? max : -1;
    }

    public void setMaxQueueLength(String queueName, int maxLength) {
        if (maxLength < 0) {
            maxLengthMap.remove(queueName);
        } else {
            maxLengthMap.put(queueName, maxLength);
        }
    }

    public Map<String, Integer> getMaxLengthMap() {
        return new ConcurrentHashMap<>(maxLengthMap);
    }

    public int getMaxQps(String queueName) {
        Integer qps = maxQpsMap.get(queueName);
        return qps != null ? qps : -1;
    }

    public void setMaxQps(String queueName, int maxQps) {
        if (maxQps <= 0) {
            maxQpsMap.remove(queueName);
        } else {
            maxQpsMap.put(queueName, maxQps);
        }
    }

    public Map<String, Integer> getMaxQpsMap() {
        return new ConcurrentHashMap<>(maxQpsMap);
    }

    public long getSleepMsAfterProcess(String queueName) {
        int qps = getMaxQps(queueName);
        if (qps <= 0) {
            return 0L;
        }
        return (long) Math.ceil(1000.0 / qps);
    }

    public long getEmptyPollIntervalMs() {
        return emptyPollIntervalMs;
    }

    public void setEmptyPollIntervalMs(long intervalMs) {
        if (intervalMs > 0) {
            this.emptyPollIntervalMs = intervalMs;
        }
    }
}
```

---

## 八、核心服务：DistributedQueueService

```java
// src/main/java/com/example/demo/service/DistributedQueueService.java
package com.example.demo.service;

import com.example.demo.config.RuntimeQueueConfig;
import com.example.demo.enums.QueueMessageStatus;
import com.example.demo.exception.QueueFullException;
import com.example.demo.model.QueueMessage;
import com.example.demo.model.QueueResult;
import com.example.demo.util.IdGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DistributedQueueService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RuntimeQueueConfig runtimeConfig;

    private final ConcurrentHashMap<String, QueueResult> resultCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> waiters = new ConcurrentHashMap<>();

    // ------------------ 异步入队 ------------------
    public void enqueue(String queueName, String messageBody) throws SQLException {
        Objects.requireNonNull(queueName, "queueName cannot be null");
        Objects.requireNonNull(messageBody, "messageBody cannot be null");

        int maxLen = runtimeConfig.getMaxQueueLength(queueName);
        if (maxLen > 0) {
            int currentLen = getCurrentQueueLength(queueName);
            if (currentLen >= maxLen) {
                throw new QueueFullException(queueName, currentLen, maxLen);
            }
        }

        long id = IdGenerator.nextId();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "INSERT INTO sys_distributed_queue (id, queue_name, message_body, status) VALUES (?, ?, ?, ?)");
            ps.setLong(1, id);
            ps.setString(2, queueName);
            ps.setString(3, messageBody);
            ps.setString(4, QueueMessageStatus.PENDING.getValue());
            ps.executeUpdate();
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    // ------------------ 同步入队 ------------------
    public String enqueueWithResult(String queueName, String payload) throws SQLException {
        String requestId = UUID.randomUUID().toString().replace("-", "");
        String wrappedBody = "{\"requestId\":\"" + requestId + "\",\"payload\":" + payload + "}";
        enqueue(queueName, wrappedBody);
        return requestId;
    }

    // ------------------ 阻塞等待结果 ------------------
    public QueueResult waitForResult(String requestId, long timeoutMs) throws InterruptedException {
        long start = System.currentTimeMillis();
        long remaining = timeoutMs;

        QueueResult cached = resultCache.get(requestId);
        if (cached != null) {
            return cached;
        }

        Object lock = new Object();
        waiters.put(requestId, lock);

        try {
            while (remaining > 0) {
                synchronized (lock) {
                    cached = resultCache.get(requestId);
                    if (cached != null) {
                        return cached;
                    }
                    lock.wait(remaining);
                    remaining = timeoutMs - (System.currentTimeMillis() - start);
                }
            }
            return null;
        } finally {
            waiters.remove(requestId);
        }
    }

    // ------------------ 保存结果 ------------------
    public void saveResult(String requestId, boolean success, String resultData, String errorMessage) throws SQLException {
        QueueResult result = new QueueResult(requestId, success, resultData, errorMessage);
        resultCache.put(requestId, result);

        Object lock = waiters.get(requestId);
        if (lock != null) {
            synchronized (lock) {
                lock.notifyAll();
            }
        }

        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "INSERT INTO sys_distributed_queue_result (request_id, result_status, result_data, error_message) VALUES (?, ?, ?, ?)");
            ps.setString(1, requestId);
            ps.setString(2, success ? "SUCCESS" : "FAILED");
            ps.setString(3, resultData);
            ps.setString(4, errorMessage);
            ps.executeUpdate();
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    // ------------------ 出队 ------------------
    public QueueMessage dequeue(String queueName, String consumerId) throws SQLException {
        Objects.requireNonNull(queueName, "queueName cannot be null");
        Objects.requireNonNull(consumerId, "consumerId cannot be null");

        Long candidateId = null;
        String body = null;
        int attempts = 0;

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "SELECT id, message_body, attempt_count FROM sys_distributed_queue " +
                "WHERE queue_name = ? AND status = ? ORDER BY id ASC LIMIT 1");
            ps.setString(1, queueName);
            ps.setString(2, QueueMessageStatus.PENDING.getValue());
            rs = ps.executeQuery();
            if (rs.next()) {
                candidateId = rs.getLong("id");
                body = rs.getString("message_body");
                attempts = rs.getInt("attempt_count");
            } else {
                return null;
            }
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }

        if (candidateId == null) {
            return null;
        }

        conn = null;
        ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "UPDATE sys_distributed_queue SET status = ?, consumer_id = ?, " +
                "attempt_count = attempt_count + 1, updated_at = CURRENT_TIMESTAMP " +
                "WHERE id = ? AND status = ?");
            ps.setString(1, QueueMessageStatus.PROCESSING.getValue());
            ps.setString(2, consumerId);
            ps.setLong(3, candidateId);
            ps.setString(4, QueueMessageStatus.PENDING.getValue());
            return ps.executeUpdate() == 1 ? new QueueMessage(candidateId, queueName, body, attempts + 1) : null;
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    // ------------------ ACK/NACK ------------------
    public void ack(long messageId, String consumerId) throws SQLException {
        executeUpdate(
            "UPDATE sys_distributed_queue SET status = ?, updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = ? AND consumer_id = ?",
            QueueMessageStatus.DONE.getValue(), messageId, consumerId);
    }

    public void nack(long messageId, String consumerId) throws SQLException {
        executeUpdate(
            "UPDATE sys_distributed_queue SET status = ?, consumer_id = NULL, updated_at = CURRENT_TIMESTAMP " +
            "WHERE id = ? AND consumer_id = ?",
            QueueMessageStatus.PENDING.getValue(), messageId, consumerId);
    }

    // ------------------ 清理 ------------------
    public void cleanupDoneMessages(int keepDays) throws SQLException {
        Timestamp cutoff = new Timestamp(System.currentTimeMillis() - (long) keepDays * 24 * 3600 * 1000L);
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "DELETE FROM sys_distributed_queue WHERE status = ? AND updated_at < ?");
            ps.setString(1, QueueMessageStatus.DONE.getValue());
            ps.setTimestamp(2, cutoff);
            int deleted = ps.executeUpdate();
            System.out.println("Cleaned up " + deleted + " done messages older than " + keepDays + " days.");
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    public long getEmptyPollIntervalMs() {
        return runtimeConfig.getEmptyPollIntervalMs();
    }

    // ------------------ 工具方法 ------------------
    private int getCurrentQueueLength(String queueName) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(
                "SELECT COUNT(*) FROM sys_distributed_queue WHERE queue_name = ? AND status IN (?, ?)");
            ps.setString(1, queueName);
            ps.setString(2, QueueMessageStatus.PENDING.getValue());
            ps.setString(3, QueueMessageStatus.PROCESSING.getValue());
            rs = ps.executeQuery();
            return rs.next() ? rs.getInt(1) : 0;
        } finally {
            closeQuietly(rs);
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    private void executeUpdate(String sql, Object... params) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                Object param = params[i];
                if (param instanceof Long) {
                    ps.setLong(i + 1, (Long) param);
                } else if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                }
            }
            ps.executeUpdate();
        } finally {
            closeQuietly(ps);
            closeQuietly(conn);
        }
    }

    private void closeQuietly(java.sql.Statement stmt) {
        if (stmt != null) {
            try { stmt.close(); } catch (SQLException ignored) {}
        }
    }

    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try { conn.close(); } catch (SQLException ignored) {}
        }
    }

    private void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try { rs.close(); } catch (SQLException ignored) {}
        }
    }
}
```

---

## 九、消费者逻辑

```java
// src/main/java/com/example/demo/consumer/QueueConsumer.java
package com.example.demo.consumer;

import com.example.demo.config.RuntimeQueueConfig;
import com.example.demo.model.QueueMessage;
import com.example.demo.service.DistributedQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

@Component
public class QueueConsumer {

    @Autowired
    private DistributedQueueService queueService;

    @Autowired
    private RuntimeQueueConfig runtimeConfig;

    @PostConstruct
    public void startConsumers() {
        Set<String> allQueues = new HashSet<String>();
        allQueues.addAll(runtimeConfig.getMaxLengthMap().keySet());
        allQueues.addAll(runtimeConfig.getMaxQpsMap().keySet());

        // 可扩展：从 DB 或注册中心获取活跃队列名
        String[] defaultQueues = {"sms_queue", "email_queue"};
        for (String q : defaultQueues) {
            allQueues.add(q);
        }

        for (final String queueName : allQueues) {
            final String consumerId = queueName + "-worker-" + System.currentTimeMillis();
            Thread t = new Thread(new Runnable() {
                public void run() {
                    consumeLoop(queueName, consumerId);
                }
            });
            t.setDaemon(true);
            t.setName("QueueConsumer-" + queueName);
            t.start();
            System.out.println("Started consumer for queue: " + queueName);
        }
    }

    private void consumeLoop(String queueName, String consumerId) {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                QueueMessage msg = queueService.dequeue(queueName, consumerId);
                if (msg != null) {
                    String requestId = null;
                    String actualPayload = msg.getMessageBody();
                    boolean hasRequestId = false;

                    if (msg.getMessageBody() != null && msg.getMessageBody().startsWith("{\"requestId\":")) {
                        int start = msg.getMessageBody().indexOf("\"requestId\":\"") + 13;
                        int end = msg.getMessageBody().indexOf("\"", start);
                        if (start > 13 && end > start) {
                            requestId = msg.getMessageBody().substring(start, end);
                            int payloadStart = msg.getMessageBody().indexOf("\"payload\":", end);
                            if (payloadStart > 0) {
                                actualPayload = msg.getMessageBody().substring(payloadStart + 10);
                                if (actualPayload.endsWith("}")) {
                                    actualPayload = actualPayload.substring(0, actualPayload.length() - 1);
                                }
                            }
                            hasRequestId = true;
                        }
                    }

                    boolean success = false;
                    String resultData = null;
                    String errorMsg = null;

                    try {
                        if ("sms_queue".equals(queueName)) {
                            resultData = sendSms(actualPayload);
                        } else if ("email_queue".equals(queueName)) {
                            resultData = sendEmail(actualPayload);
                        } else {
                            resultData = "Processed: " + actualPayload;
                        }
                        success = true;
                    } catch (Exception e) {
                        errorMsg = e.getMessage();
                        throw e;
                    } finally {
                        if (hasRequestId && requestId != null) {
                            queueService.saveResult(requestId, success, resultData, errorMsg);
                        }
                    }

                    queueService.ack(msg.getId(), consumerId);
                } else {
                    Thread.sleep(queueService.getEmptyPollIntervalMs());
                }

                long sleepMs = runtimeConfig.getSleepMsAfterProcess(queueName);
                if (sleepMs > 0) {
                    Thread.sleep(sleepMs);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                e.printStackTrace();
                try { Thread.sleep(1000); } catch (InterruptedException ie) { Thread.currentThread().interrupt(); break; }
            }
        }
    }

    private String sendSms(String body) throws Exception {
        Thread.sleep(100);
        return "SMS_SENT_TO_" + body;
    }

    private String sendEmail(String body) throws Exception {
        Thread.sleep(80);
        return "EMAIL_SENT_TO_" + body;
    }
}
```

---

## 十、任务提交 Controller

```java
// src/main/java/com/example/demo/controller/TaskController.java
package com.example.demo.controller;

import com.example.demo.exception.QueueFullException;
import com.example.demo.model.QueueResult;
import com.example.demo.service.DistributedQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class TaskController {

    @Autowired
    private DistributedQueueService queueService;

    @PostMapping("/tasks/{queueName}")
    public ResponseEntity<?> submitTask(@PathVariable String queueName, @RequestBody String payload) {
        try {
            queueService.enqueue(queueName, payload);
            return ResponseEntity.accepted().body("Queued");
        } catch (QueueFullException e) {
            Map<String, Object> error = new HashMap<String, Object>();
            error.put("error", "QUEUE_FULL");
            error.put("queue", e.getQueueName());
            error.put("current", e.getCurrentSize());
            error.put("max", e.getMaxSize());
            return ResponseEntity.status(429).body(error);
        } catch (SQLException e) {
            return ResponseEntity.status(500).body("Database error");
        }
    }

    @PostMapping("/tasks-sync/{queueName}")
    public ResponseEntity<?> submitTaskSync(@PathVariable String queueName,
                                            @RequestBody String payload,
                                            @RequestParam(defaultValue = "5000") long timeoutMs) {
        try {
            String requestId = queueService.enqueueWithResult(queueName, payload);
            QueueResult result = queueService.waitForResult(requestId, timeoutMs);

            if (result == null) {
                return ResponseEntity.status(504).body("Timeout waiting for result");
            }

            if (result.isSuccess()) {
                return ResponseEntity.ok(result.getResultData());
            } else {
                Map<String, String> error = new HashMap<String, String>();
                error.put("error", "PROCESSING_FAILED");
                error.put("message", result.getErrorMessage());
                return ResponseEntity.status(500).body(error);
            }
        } catch (QueueFullException e) {
            Map<String, Object> error = new HashMap<String, Object>();
            error.put("error", "QUEUE_FULL");
            error.put("queue", e.getQueueName());
            error.put("current", e.getCurrentSize());
            error.put("max", e.getMaxSize());
            return ResponseEntity.status(429).body(error);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Internal error: " + e.getMessage());
        }
    }
}
```

---

## 十一、动态配置管理 Controller

```java
// src/main/java/com/example/demo/controller/QueueConfigController.java
package com.example.demo.controller;

import com.example.demo.config.RuntimeQueueConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/admin/queue-config")
public class QueueConfigController {

    @Autowired
    private RuntimeQueueConfig config;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllConfigs() {
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("max_length", config.getMaxLengthMap());
        result.put("max_qps", config.getMaxQpsMap());
        result.put("empty_poll_interval_ms", config.getEmptyPollIntervalMs());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{queueName}/max-length")
    public ResponseEntity<?> setMaxLength(@PathVariable String queueName,
                                          @RequestParam int maxLength) {
        if (maxLength < 0) {
            return ResponseEntity.badRequest().body("maxLength must be >= 0");
        }
        config.setMaxQueueLength(queueName, maxLength);
        return ResponseEntity.ok("Max length for queue '" + queueName + "' set to " + maxLength);
    }

    @PostMapping("/{queueName}/max-qps")
    public ResponseEntity<?> setMaxQps(@PathVariable String queueName,
                                       @RequestParam int maxQps) {
        if (maxQps <= 0) {
            return ResponseEntity.badRequest().body("maxQps must be > 0");
        }
        config.setMaxQps(queueName, maxQps);
        return ResponseEntity.ok("Max QPS for queue '" + queueName + "' set to " + maxQps);
    }

    @PostMapping("/empty-poll-interval")
    public ResponseEntity<?> setEmptyPollInterval(@RequestParam long intervalMs) {
        if (intervalMs <= 0) {
            return ResponseEntity.badRequest().body("intervalMs must be > 0");
        }
        config.setEmptyPollIntervalMs(intervalMs);
        return ResponseEntity.ok("Empty poll interval set to " + intervalMs + " ms");
    }

    @DeleteMapping("/{queueName}")
    public ResponseEntity<?> resetQueueConfig(@PathVariable String queueName) {
        config.setMaxQueueLength(queueName, -1);
        config.setMaxQps(queueName, -1);
        return ResponseEntity.ok("Config for queue '" + queueName + "' reset");
    }
}
```

---

## 十二、定时清理任务

```java
// src/main/java/com/example/demo/task/CleanupTask.java
package com.example.demo.task;

import com.example.demo.service.DistributedQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CleanupTask {

    @Autowired
    private DistributedQueueService queueService;

    @Scheduled(cron = "0 0 2 * * ?")
    public void cleanup() throws Exception {
        queueService.cleanupDoneMessages(7);
    }
}
```

---

✅ **全部特性已集成**：

- 表名正确 ✅
- 数据库无枚举校验 ✅
- 运行时动态配置 ✅
- Java 8 兼容 ✅
- 功能完整 ✅

如需 `pom.xml`、`application.yml` 示例或部署说明，请继续告知！
