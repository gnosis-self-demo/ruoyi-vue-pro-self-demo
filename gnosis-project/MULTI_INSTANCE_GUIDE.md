# 多实例队列系统使用指南

## 🎯 架构说明

本系统采用工厂模式实现多实例队列管理，每个队列实例都有独立的配置和生命周期。

### 核心组件

1. **DistributedQueueFactory** - 队列工厂，负责创建和管理队列实例
2. **QueueInstance** - 队列实例包装类，包含服务、消费者、配置等
3. **RuntimeQueueConfig** - 运行时配置，每个实例可独立配置
4. **DistributedQueueService** - 队列核心服务
5. **QueueConsumer** - 消费者，支持动态启停

## 🚀 使用示例

### 1. 创建队列实例

```bash
# 创建SMS队列实例
curl -X POST http://localhost:8080/admin/queue-instances/sms_queue \
  -H "Content-Type: application/json" \
  -d '{
    "maxLength": 1000,
    "maxQps": 50,
    "pollInterval": 1000
  }'

# 创建Email队列实例
curl -X POST http://localhost:8080/admin/queue-instances/email_queue \
  -H "Content-Type: application/json" \
  -d '{
    "maxLength": 500,
    "maxQps": 30,
    "pollInterval": 2000
  }'
```

### 2. 批量创建队列实例

```bash
curl -X POST http://localhost:8080/admin/queue-instances/batch \
  -H "Content-Type: application/json" \
  -d '{
    "notification_queue": {
      "maxLength": 2000,
      "maxQps": 100,
      "pollInterval": 500
    },
    "data_process_queue": {
      "maxLength": 5000,
      "maxQps": 20,
      "pollInterval": 3000
    }
  }'
```

### 3. 查看队列实例

```bash
# 查看特定队列实例
curl -X GET http://localhost:8080/admin/queue-instances/sms_queue

# 查看所有队列实例
curl -X GET http://localhost:8080/admin/queue-instances
```

### 4. 更新队列配置

```bash
# 动态调整SMS队列配置
curl -X PUT http://localhost:8080/admin/queue-instances/sms_queue/config \
  -H "Content-Type: application/json" \
  -d '{
    "maxLength": 2000,
    "maxQps": 100
  }'
```

### 5. 使用队列实例提交任务

```bash
# 异步提交任务到SMS队列
curl -X POST http://localhost:8080/tasks/sms_queue \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","content":"验证码：123456"}'

# 同步提交任务到Email队列
curl -X POST "http://localhost:8080/tasks-sync/email_queue?timeoutMs=3000" \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","subject":"通知","content":"Hello"}'
```

### 6. 删除队列实例

```bash
curl -X DELETE http://localhost:8080/admin/queue-instances/sms_queue
```

## 📊 Java代码使用示例

```java
@Component
public class BusinessService {
    
    @Autowired
    private DistributedQueueFactory queueFactory;
    
    public void setupQueues() {
        // 创建不同用途的队列实例
        createHighPriorityQueue();
        createNormalQueue();
        createLowPriorityQueue();
    }
    
    private void createHighPriorityQueue() {
        RuntimeQueueConfig config = new RuntimeQueueConfig();
        config.setMaxQueueLength("high_priority", 100);
        config.setMaxQps("high_priority", 200);
        config.setEmptyPollIntervalMs(100);
        
        queueFactory.createQueueInstance("high_priority", config);
    }
    
    private void createNormalQueue() {
        RuntimeQueueConfig config = new RuntimeQueueConfig();
        config.setMaxQueueLength("normal", 1000);
        config.setMaxQps("normal", 50);
        config.setEmptyPollIntervalMs(1000);
        
        queueFactory.createQueueInstance("normal", config);
    }
    
    private void createLowPriorityQueue() {
        RuntimeQueueConfig config = new RuntimeQueueConfig();
        config.setMaxQueueLength("low_priority", 10000);
        config.setMaxQps("low_priority", 10);
        config.setEmptyPollIntervalMs(5000);
        
        queueFactory.createQueueInstance("low_priority", config);
    }
    
    public void submitTask(String priority, String taskData) throws Exception {
        String queueName = priority + "_priority";
        DistributedQueueFactory.QueueInstance instance = 
            queueFactory.getQueueInstance(queueName);
            
        if (instance != null) {
            instance.getService().enqueue(queueName, taskData);
        } else {
            throw new IllegalStateException("Queue instance not found: " + queueName);
        }
    }
}
```

## ⚙️ 配置参数说明

| 参数 | 类型 | 说明 | 默认值 |
|------|------|------|--------|
| `maxLength` | Integer | 队列最大长度 | -1(无限制) |
| `maxQps` | Integer | 每秒最大处理数 | -1(无限制) |
| `pollInterval` | Long | 空队列轮询间隔(ms) | 1000 |

## 🔧 最佳实践

### 1. 按业务场景分离队列
```java
// 不同业务使用不同的队列实例
queueFactory.createQueueInstance("user_registration", userRegConfig);
queueFactory.createQueueInstance("payment_processing", paymentConfig);
queueFactory.createQueueInstance("notification_send", notificationConfig);
```

### 2. 动态调整配置
```java
// 根据系统负载动态调整QPS
if (systemLoad > 0.8) {
    config.setMaxQps("critical_tasks", 200);
} else {
    config.setMaxQps("critical_tasks", 50);
}
```

### 3. 监控和告警
```java
// 定期检查队列状态
Map<String, Object> stats = queueFactory.getQueueStatistics();
for (String queueName : queueFactory.getAllQueueNames()) {
    QueueInstance instance = queueFactory.getQueueInstance(queueName);
    // 检查队列长度、处理延迟等指标
}
```

### 4. 优雅关闭
```java
// 应用关闭时清理资源
@PreDestroy
public void cleanup() {
    for (String queueName : queueFactory.getAllQueueNames()) {
        queueFactory.removeQueueInstance(queueName);
    }
}
```

## 🚨 注意事项

1. **队列名称唯一性**：每个队列实例必须有唯一的名称
2. **资源配置**：大量队列实例会消耗更多系统资源
3. **事务管理**：每个实例有自己的事务管理器
4. **线程安全**：工厂内部使用线程安全的并发集合
5. **异常处理**：记得处理队列不存在的异常情况

这个多实例架构让您能够灵活地为不同业务场景创建专门的队列配置！