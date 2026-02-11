# 死信队列设计文档

## 问题背景

在分布式队列系统中，当任务处于 `PROCESSING` 状态时，如果发生以下情况，任务状态将无法正常更新：
- 处理过程中发生异常
- 消费者进程意外退出
- 系统崩溃或重启
- 网络中断导致确认失败

这会导致任务永久处于处理中状态，形成"僵死任务"，影响系统正常运行。

## 解决方案

### 1. 状态扩展

扩展消息状态枚举，增加新的状态类型：

```java
public enum QueueMessageStatus {
    PENDING("pending"),      // 待处理
    PROCESSING("processing"), // 处理中
    DONE("done"),            // 处理完成
    FAILED("failed"),        // 处理失败
    DEAD_LETTER("dead_letter"); // 死信队列
}
```

### 2. 死信队列服务

创建 `DeadLetterQueueService` 服务，负责：

#### 2.1 超时检测
- 每5分钟检查一次处理中超时的任务（默认30分钟）
- 对超时任务进行重试或移至死信队列

#### 2.2 失败任务处理
- 每小时检查标记为失败的任务
- 对失败任务进行重试处理

#### 2.3 状态转换规则

```text
PROCESSING --(超时/失败)--> FAILED --(重试次数<3)--> PENDING
                                      --(重试次数>=3)--> DEAD_LETTER

FAILED --(重试)--> PENDING
DEAD_LETTER --(手动重试)--> PENDING
```

### 3. 消费者改进

修改 `QueueConsumer` 的异常处理逻辑：

```java
try {
    // 处理消息
    result = processorManager.processRequest(queueName, request);
    processingSuccess = true;
} catch (Exception e) {
    // 标记处理失败，保存错误信息
    markMessageAsFailed(msg.getId(), consumerId, "处理异常: " + e.getMessage());
    processingSuccess = false;
} finally {
    // 只有处理成功才确认消息
    if (processingSuccess) {
        queueService.ack(msg.getId(), consumerId);
    }
}
```

### 4. 管理接口

提供RESTful API进行死信队列管理：

#### 4.1 监控接口
- `GET /distribute-queue/admin/dead-letter/stats` - 获取死信队列统计
- `GET /distribute-queue/admin/dead-letter/config` - 获取配置信息

#### 4.2 管理接口
- `POST /distribute-queue/admin/dead-letter/retry/{messageId}` - 重试单个死信消息
- `DELETE /distribute-queue/admin/dead-letter/cleanup?daysToKeep=7` - 清理历史死信
- `POST /distribute-queue/admin/dead-letter/check` - 手动触发检查

## 配置参数

| 参数 | 默认值 | 说明 |
|------|--------|------|
| processingTimeoutMs | 1800000 (30分钟) | 处理超时时间 |
| maxRetryAttempts | 3 | 最大重试次数 |
| checkIntervalMs | 300000 (5分钟) | 检查间隔 |

## 数据库变更

### 新增字段
```sql
ALTER TABLE sys_distributed_queue ADD COLUMN error_message TEXT;
```

### 状态值扩展
- `pending`: 待处理
- `processing`: 处理中  
- `done`: 处理完成
- `failed`: 处理失败
- `dead_letter`: 死信队列

## 使用示例

### 1. 查看死信统计
```bash
curl http://localhost:8080/distribute-queue/admin/dead-letter/stats
```

### 2. 重试死信消息
```bash
curl -X POST http://localhost:8080/distribute-queue/admin/dead-letter/retry/12345
```

### 3. 清理历史死信
```bash
curl -X DELETE "http://localhost:8080/distribute-queue/admin/dead-letter/cleanup?daysToKeep=30"
```

## 监控告警

建议配置以下监控告警：

1. **死信数量告警**：当死信队列数量超过阈值时告警
2. **处理成功率告警**：处理成功率低于阈值时告警
3. **超时任务告警**：大量任务超时处理时告警

## 最佳实践

1. **合理设置超时时间**：根据业务特点设置合适的超时时间
2. **监控死信队列**：定期检查死信队列，分析失败原因
3. **及时清理**：定期清理历史死信消息，释放存储空间
4. **重试策略**：对于重要的业务消息，可以手动重试死信消息

## 故障排查

当遇到僵死任务问题时：

1. 检查死信队列统计：`GET /admin/dead-letter/stats`
2. 查看具体失败消息的错误信息
3. 分析失败原因并修复
4. 手动重试重要的死信消息
5. 调整相关配置参数