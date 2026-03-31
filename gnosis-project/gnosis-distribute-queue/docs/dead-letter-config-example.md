# 死信队列配置管理示例

## 配置参数说明

### 队列配置参数

| 参数名 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `maxLength` | Integer | 1000 | 队列最大长度 |
| `maxQps` | Integer | 50 | 每秒最大处理数 |
| `pollInterval` | Long | 1000 | 空队列轮询间隔(毫秒) |
| `maxRetryAttempts` | Integer | 3 | 最大重试次数 |
| `processingTimeoutMs` | Long | 1800000 | 处理超时时间(毫秒，默认30分钟) |
| `deadLetterCheckIntervalMs` | Long | 300000 | 死信检查间隔(毫秒，默认5分钟) |

## 配置方式

### 1. 启动时配置文件配置

在 `application.properties` 中配置：

```properties
# SMS队列配置
distributed.queue.sms.max-length=1000
distributed.queue.sms.max-qps=50
distributed.queue.sms.poll-interval=1000
distributed.queue.sms.max-retry-attempts=5
distributed.queue.sms.processing-timeout-ms=3600000
distributed.queue.sms.dead-letter-check-interval-ms=600000

# Email队列配置
distributed.queue.email.max-length=500
distributed.queue.email.max-qps=30
distributed.queue.email.poll-interval=2000
distributed.queue.email.max-retry-attempts=3
distributed.queue.email.processing-timeout-ms=1800000
distributed.queue.email.dead-letter-check-interval-ms=300000

# 通知队列配置
distributed.queue.notification.max-length=2000
distributed.queue.notification.max-qps=100
distributed.queue.notification.poll-interval=500
distributed.queue.notification.max-retry-attempts=2
distributed.queue.notification.processing-timeout-ms=900000
distributed.queue.notification.dead-letter-check-interval-ms=150000
```

### 2. 运行时API配置

#### 获取队列死信配置
```bash
curl -X GET http://localhost:8080/distribute-queue/admin/dead-letter/config/sms_queue
```

响应示例：
```json
{
  "status": "success",
  "queueName": "sms_queue",
  "config": {
    "maxRetryAttempts": 5,
    "processingTimeoutMs": 3600000,
    "checkIntervalMs": 600000
  }
}
```

#### 更新队列死信配置
```bash
curl -X PUT http://localhost:8080/distribute-queue/admin/dead-letter/config/sms_queue \
  -H "Content-Type: application/json" \
  -d '{
    "maxRetryAttempts": 3,
    "processingTimeoutMs": 1800000,
    "checkIntervalMs": 300000
  }'
```

#### 获取全局配置信息
```bash
curl -X GET http://localhost:8080/distribute-queue/admin/dead-letter/config
```

### 3. Java代码配置示例

```java
@Component
public class QueueConfigurationService {
    
    @Autowired
    private RuntimeQueueConfig runtimeConfig;
    
    @Autowired
    private DeadLetterQueueService deadLetterService;
    
    /**
     * 初始化高优先级队列配置
     */
    public void initHighPriorityQueueConfig() {
        String queueName = "high_priority_queue";
        
        // 基础配置
        runtimeConfig.setMaxQueueLength(queueName, 100);
        runtimeConfig.setMaxQps(queueName, 200);
        runtimeConfig.setEmptyPollIntervalMs(100);
        
        // 死信队列配置
        runtimeConfig.setMaxRetryAttempts(queueName, 2);           // 只重试2次
        runtimeConfig.setProcessingTimeoutMs(queueName, 600000);   // 10分钟超时
        runtimeConfig.setDeadLetterCheckIntervalMs(queueName, 60000); // 1分钟检查
        
        System.out.println("高优先级队列配置完成");
    }
    
    /**
     * 初始化批处理队列配置
     */
    public void initBatchQueueConfig() {
        String queueName = "batch_processing_queue";
        
        // 基础配置
        runtimeConfig.setMaxQueueLength(queueName, 10000);
        runtimeConfig.setMaxQps(queueName, 10);
        runtimeConfig.setEmptyPollIntervalMs(5000);
        
        // 死信队列配置
        runtimeConfig.setMaxRetryAttempts(queueName, 5);           // 重试5次
        runtimeConfig.setProcessingTimeoutMs(queueName, 7200000);  // 2小时超时
        runtimeConfig.setDeadLetterCheckIntervalMs(queueName, 600000); // 10分钟检查
        
        System.out.println("批处理队列配置完成");
    }
    
    /**
     * 动态调整配置示例
     */
    public void adjustConfigBasedOnLoad() {
        String queueName = "dynamic_queue";
        
        // 根据系统负载动态调整
        double systemLoad = getSystemLoad();
        
        if (systemLoad > 0.8) {
            // 高负载时减少重试次数和增加超时时间
            runtimeConfig.setMaxRetryAttempts(queueName, 1);
            runtimeConfig.setProcessingTimeoutMs(queueName, 3600000); // 1小时
        } else if (systemLoad > 0.5) {
            // 中等负载时使用默认配置
            runtimeConfig.setMaxRetryAttempts(queueName, 3);
            runtimeConfig.setProcessingTimeoutMs(queueName, 1800000); // 30分钟
        } else {
            // 低负载时增加重试次数
            runtimeConfig.setMaxRetryAttempts(queueName, 5);
            runtimeConfig.setProcessingTimeoutMs(queueName, 900000);  // 15分钟
        }
        
        System.out.println("动态配置调整完成，当前负载: " + systemLoad);
    }
    
    private double getSystemLoad() {
        // 模拟获取系统负载
        return Math.random(); // 实际应该从监控系统获取
    }
}
```

## 配置最佳实践

### 1. 按业务场景配置

```java
// 关键业务 - 严格配置
runtimeConfig.setMaxRetryAttempts("payment_queue", 1);
runtimeConfig.setProcessingTimeoutMs("payment_queue", 300000); // 5分钟

// 通知业务 - 宽松配置
runtimeConfig.setMaxRetryAttempts("notification_queue", 5);
runtimeConfig.setProcessingTimeoutMs("notification_queue", 3600000); // 1小时

// 批处理业务 - 长时间处理
runtimeConfig.setMaxRetryAttempts("batch_queue", 3);
runtimeConfig.setProcessingTimeoutMs("batch_queue", 86400000); // 24小时
```

### 2. 监控和告警配置

```java
public class QueueMonitoringService {
    
    @Autowired
    private DeadLetterQueueService deadLetterService;
    
    /**
     * 检查队列健康状况
     */
    public void checkQueueHealth(String queueName) {
        Map<String, Object> config = deadLetterService.getDeadLetterConfig(queueName);
        
        int maxRetries = (Integer) config.get("maxRetryAttempts");
        long timeoutMs = (Long) config.get("processingTimeoutMs");
        
        // 根据配置设置不同的告警阈值
        if (maxRetries <= 2) {
            // 严格配置，低阈值告警
            setAlertThreshold(queueName, AlertLevel.HIGH);
        } else if (timeoutMs > 3600000) {
            // 长超时，中等阈值告警
            setAlertThreshold(queueName, AlertLevel.MEDIUM);
        } else {
            // 宽松配置，高阈值告警
            setAlertThreshold(queueName, AlertLevel.LOW);
        }
    }
    
    private void setAlertThreshold(String queueName, AlertLevel level) {
        // 实现告警阈值设置逻辑
        System.out.println("为队列 " + queueName + " 设置 " + level + " 级别告警");
    }
    
    enum AlertLevel {
        HIGH, MEDIUM, LOW
    }
}
```

### 3. 配置备份和恢复

```java
@Service
public class QueueConfigBackupService {
    
    @Autowired
    private RuntimeQueueConfig runtimeConfig;
    
    /**
     * 备份所有队列配置
     */
    public void backupAllConfigs() {
        Map<String, Object> backup = new HashMap<>();
        
        // 备份基础配置
        backup.put("max_length", runtimeConfig.getMaxLengthMap());
        backup.put("max_qps", runtimeConfig.getMaxQpsMap());
        backup.put("poll_interval", runtimeConfig.getEmptyPollIntervalMs());
        
        // 备份死信配置
        backup.put("max_retry_attempts", runtimeConfig.getMaxRetryAttemptsMap());
        backup.put("processing_timeout", runtimeConfig.getProcessingTimeoutMap());
        backup.put("check_interval", runtimeConfig.getDeadLetterCheckIntervalMap());
        
        // 保存到文件或数据库
        saveBackupToFile(backup);
        System.out.println("队列配置备份完成");
    }
    
    /**
     * 恢复队列配置
     */
    public void restoreConfigs(Map<String, Object> backup) {
        // 恢复基础配置
        Map<String, Integer> maxLengthMap = (Map<String, Integer>) backup.get("max_length");
        maxLengthMap.forEach(runtimeConfig::setMaxQueueLength);
        
        Map<String, Integer> maxQpsMap = (Map<String, Integer>) backup.get("max_qps");
        maxQpsMap.forEach(runtimeConfig::setMaxQps);
        
        Long pollInterval = (Long) backup.get("poll_interval");
        runtimeConfig.setEmptyPollIntervalMs(pollInterval);
        
        // 恢复死信配置
        Map<String, Integer> retryMap = (Map<String, Integer>) backup.get("max_retry_attempts");
        retryMap.forEach(runtimeConfig::setMaxRetryAttempts);
        
        Map<String, Long> timeoutMap = (Map<String, Long>) backup.get("processing_timeout");
        timeoutMap.forEach(runtimeConfig::setProcessingTimeoutMs);
        
        Map<String, Long> intervalMap = (Map<String, Long>) backup.get("check_interval");
        intervalMap.forEach(runtimeConfig::setDeadLetterCheckIntervalMs);
        
        System.out.println("队列配置恢复完成");
    }
    
    private void saveBackupToFile(Map<String, Object> backup) {
        // 实现备份保存逻辑
    }
}
```

## API使用示例

### 完整的配置管理流程

```bash
# 1. 查看当前配置
curl http://localhost:8080/distribute-queue/admin/dead-letter/config/payment_queue

# 2. 更新配置
curl -X PUT http://localhost:8080/distribute-queue/admin/dead-letter/config/payment_queue \
  -H "Content-Type: application/json" \
  -d '{
    "maxRetryAttempts": 2,
    "processingTimeoutMs": 600000,
    "checkIntervalMs": 120000
  }'

# 3. 验证更新结果
curl http://localhost:8080/distribute-queue/admin/dead-letter/config/payment_queue

# 4. 查看死信统计
curl http://localhost:8080/distribute-queue/admin/dead-letter/stats

# 5. 清理历史死信（保留7天）
curl -X DELETE "http://localhost:8080/distribute-queue/admin/dead-letter/cleanup?daysToKeep=7"
```

这样的配置化管理使得死信队列的行为可以根据不同的业务需求进行灵活调整。