# 基于openGauss的分布式队列系统

这是一个基于openGauss数据库实现的高性能分布式队列系统，支持**多实例管理**、异步和同步消息处理、动态配置管理、QPS限流等功能。

> 📚 **多实例使用指南**：请查看 [MULTI_INSTANCE_GUIDE.md](MULTI_INSTANCE_GUIDE.md)

## 🚀 主要特性

- ✅ **多实例支持**：可动态创建和管理多个独立队列实例
- ✅ **灵活配置**：每个队列实例可独立配置参数
- ✅ **双模式入队**：支持异步入队和同步等待结果
- ✅ **动态配置**：运行时可动态调整队列长度和QPS限制
- ✅ **QPS限流**：支持每队列独立的QPS控制
- ✅ **队列长度限制**：防止队列无限增长
- ✅ **自动清理**：定时清理已完成的消息
- ✅ **多队列支持**：可同时管理多个不同类型队列
- ✅ **Java 8兼容**：不使用Lambda表达式和Stream API
- ✅ **Spring Boot集成**：完整的RESTful API接口

## 📁 项目结构

```
src/main/java/com/example/demo/
├── config/                 # 配置管理
│   └── RuntimeQueueConfig.java
├── controller/             # 控制器
│   ├── TaskController.java
│   └── QueueConfigController.java
├── consumer/               # 消费者
│   └── QueueConsumer.java
├── enums/                  # 枚举类
│   └── QueueMessageStatus.java
├── exception/              # 自定义异常
│   └── QueueFullException.java
├── model/                  # 数据模型
│   ├── QueueMessage.java
│   └── QueueResult.java
├── service/                # 核心服务
│   └── DistributedQueueService.java
├── task/                   # 定时任务
│   └── CleanupTask.java
├── util/                   # 工具类
│   └── IdGenerator.java
└── DistributedQueueApplication.java  # 启动类

src/main/resources/
├── db/
│   └── schema.sql          # 数据库建表脚本
└── application.properties  # 应用配置文件
```

## 🛠️ 快速开始

### 1. 环境准备

- Java 8+
- Maven 3.6+
- openGauss数据库

### 2. 数据库配置

在openGauss中创建数据库并执行建表脚本：

```sql
CREATE DATABASE distributed_queue_db;
\c distributed_queue_db
-- 执行 src/main/resources/db/schema.sql 中的内容
```

### 3. 修改配置

编辑 `src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:opengauss://localserver.gnosis:5432/gnosis_sample
spring.datasource.username=gaussdb
spring.datasource.password=Enmotech@123
```

### 4. 编译运行

```bash
# 编译项目
mvn clean compile

# 运行主应用
mvn spring-boot:run

# 或运行测试应用（带交互界面）
mvn exec:java -Dexec.mainClass="gnosis.sample.distribute.queue.test.QueueTestApplication"

# 打包运行
mvn clean package
java -jar target/gnosis-project-1.0.0-SNAPSHOT.jar
```

## 📡 API接口

### 任务提交接口

#### 异步入队
```http
POST /distribute-queue/tasks/{queueName}
Content-Type: application/json

{
  "message": "your task data"
}
```

#### 同步入队（等待结果）
```http
POST /distribute-queue/tasks-sync/{queueName}?timeoutMs=5000
Content-Type: application/json

{
  "message": "your task data"
}
```

### 配置管理接口

#### 查看所有配置
```http
GET /distribute-queue/admin/queue-config
```

#### 设置队列最大长度
```http
POST /distribute-queue/admin/queue-config/{queueName}/max-length?maxLength=1000
```

#### 设置队列最大QPS
```http
POST /distribute-queue/admin/queue-config/{queueName}/max-qps?maxQps=100
```

#### 重置队列配置
```http
DELETE /distribute-queue/admin/queue-config/{queueName}
```

### 系统监控接口

#### 健康检查
```http
GET /distribute-queue/health
```

#### 队列状态
```http
GET /distribute-queue/queues/{queueName}/status
```

## 🎯 使用示例

### Java客户端示例

```java
// 异步提交任务
RestTemplate restTemplate = new RestTemplate();
String url = "http://localhost:8080/tasks/sms_queue";
String payload = "{\"phone\":\"13800138000\",\"content\":\"Hello World\"}";

ResponseEntity<String> response = restTemplate.postForEntity(url, payload, String.class);
System.out.println("任务已提交: " + response.getBody());

// 同步提交任务并等待结果
String syncUrl = "http://localhost:8080/tasks-sync/email_queue?timeoutMs=3000";
ResponseEntity<String> syncResponse = restTemplate.postForEntity(syncUrl, payload, String.class);
System.out.println("任务处理结果: " + syncResponse.getBody());
```

### Curl命令示例

```bash
# 异步提交任务
curl -X POST http://localhost:8080/distribute-queue/tasks/sms_queue \
  -H "Content-Type: application/json" \
  -d '{"phone":"13800138000","content":"验证码：123456"}'

# 同步提交任务
curl -X POST "http://localhost:8080/distribute-queue/tasks-sync/email_queue?timeoutMs=5000" \
  -H "Content-Type: application/json" \
  -d '{"email":"user@example.com","subject":"通知","content":"Hello"}'

# 动态配置队列
curl -X POST "http://localhost:8080/distribute-queue/admin/queue-config/sms_queue/max-length?maxLength=1000"
curl -X POST "http://localhost:8080/distribute-queue/admin/queue-config/sms_queue/max-qps?maxQps=50"
```

## ⚙️ 配置说明

### 核心配置项

| 配置项 | 说明 | 默认值 |
|--------|------|--------|
| `spring.datasource.*` | 数据库连接配置 | - |
| `server.port` | 服务端口 | 8080 |
| `logging.level.*` | 日志级别配置 | INFO/DEBUG |

### 运行时配置

通过REST API动态调整：
- 队列最大长度限制
- 队列QPS限制
- 空队列轮询间隔

## 🔧 性能优化建议

1. **数据库连接池调优**：根据并发量调整连接池大小
2. **索引优化**：确保关键查询字段有合适的索引
3. **JVM参数调优**：适当增加堆内存和调整GC策略
4. **队列配置**：合理设置队列长度和QPS限制

## 📊 监控指标

系统提供以下监控信息：
- 队列长度统计
- 处理成功率
- 平均处理延迟
- 系统健康状态

通过Actuator端点访问：
```http
GET /actuator/health
GET /actuator/metrics
```

## 🤝 贡献指南

欢迎提交Issue和Pull Request来改进这个项目！

## 📄 许可证

MIT License

---
**注意**：请确保在生产环境中修改默认的用户名密码配置，并根据实际需求调整各项参数。