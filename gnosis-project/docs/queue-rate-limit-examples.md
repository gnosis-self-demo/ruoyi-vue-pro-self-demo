# 队列限流示例说明

## 📋 概述

本示例演示了如何使用分布式队列系统来控制两种常见的限流场景：

1. **外部API调用限流** - 控制本系统调用外部接口的最大QPS
2. **第三方系统接入限流** - 控制外部系统调用本系统的最大QPS

## 🎯 示例文件说明

### 1. ExternalApiRateLimitExample.java
**用途**: 控制调用外部接口的最大QPS

**核心功能**:
- 创建专门的限流队列 (`external-api-rate-limit`)
- 限制对外部API的最大调用频率为50 QPS
- 支持支付网关、短信服务等外部接口调用
- 提供动态调整限流参数的能力

**主要方法**:
```java
// 初始化限流队列
initializeExternalApiQueue()

// 发送支付请求到限流队列
sendPaymentRequest(String orderId, double amount)

// 发送短信请求到限流队列
sendSmsRequest(String phone, String message)

// 批量测试限流效果
batchTestExternalApiLimit()

// 动态调整限流参数
adjustExternalApiRate(int newMaxQps)
```

### 2. ThirdPartyIngressControlExample.java
**用途**: 控制外部系统调用本系统的最大QPS

**核心功能**:
- 创建第三方接入限流队列 (`thirdparty-ingress-control`)
- 限制第三方系统接入的最大QPS为200
- 支持订单同步、库存更新、用户数据同步等场景
- 提供紧急降级和恢复机制

**主要方法**:
```java
// 初始化接入限流队列
initializeThirdPartyIngressQueue()

// 处理电商平台订单同步
handleEcommerceOrderSync(String partnerId, String batchId, int orderCount)

// 处理库存更新请求（同步处理）
handleInventoryUpdate(String productId, int newStock, String sourceSystem)

// 处理用户数据同步
handleUserDataSync(String partnerId, int userCount)

// 紧急降级处理
emergencyDegradation()

// 恢复正常限流策略
recoveryToNormal()
```

### 3. QueueRateLimitDemo.java
**用途**: 演示程序主入口，整合两个限流示例

**运行方式**:
```bash
# 启动Spring Boot应用后自动运行演示
# 或者直接调用run方法
```

## 🚀 使用示例

### 场景一：控制调用外部支付网关

```java
@Autowired
private ExternalApiRateLimitExample externalApiExample;

// 1. 初始化限流队列
externalApiExample.initializeExternalApiQueue();

// 2. 发送支付请求（受QPS限制）
externalApiExample.sendPaymentRequest("ORDER_12345", 299.99);
externalApiExample.sendPaymentRequest("ORDER_12346", 199.99);

// 3. 动态调整限流参数（根据外部服务商策略）
externalApiExample.adjustExternalApiRate(30); // 降低到30 QPS
```

### 场景二：控制第三方电商系统接入

```java
@Autowired
private ThirdPartyIngressControlExample thirdPartyExample;

// 1. 初始化接入限流队列
thirdPartyExample.initializeThirdPartyIngressQueue();

// 2. 处理第三方订单同步请求
thirdPartyExample.handleEcommerceOrderSync("TAOBAO_PARTNER", "TB_BATCH_001", 150);

// 3. 处理重要库存更新（同步处理）
thirdPartyExample.handleInventoryUpdate("PRODUCT_001", 999, "WMS_SYSTEM");

// 4. 系统压力大时紧急降级
thirdPartyExample.emergencyDegradation();

// 5. 系统恢复后恢复正常策略
thirdPartyExample.recoveryToNormal();
```

## ⚙️ 配置参数说明

### 外部API限流队列配置
```java
private static final String EXTERNAL_API_QUEUE = "external-api-rate-limit";
private static final int MAX_EXTERNAL_QPS = 50;      // 最大50 QPS
private static final int QUEUE_MAX_LENGTH = 1000;    // 队列最大长度1000
```

### 第三方接入限流队列配置
```java
private static final String THIRD_PARTY_INGRESS_QUEUE = "thirdparty-ingress-control";
private static final int MAX_INGRESS_QPS = 200;      // 最大200 QPS
private static final int QUEUE_MAX_LENGTH = 5000;    // 队列最大长度5000
```

## 📊 限流效果说明

### 外部API调用限流效果
- **正常情况**: 每秒最多处理50个外部API调用请求
- **队列满时**: 新的请求会被拒绝，避免系统过载
- **动态调整**: 可根据外部服务商的限流策略实时调整

### 第三方系统接入限流效果
- **正常情况**: 每秒最多处理200个第三方系统请求
- **高负载时**: 可降级到50 QPS保护核心服务
- **紧急情况**: 自动触发降级机制，确保系统稳定性

## 🔧 最佳实践建议

1. **合理设置QPS参数**: 根据实际业务需求和系统承载能力设置
2. **监控队列状态**: 定期检查队列长度和处理延迟
3. **建立告警机制**: 在达到限流阈值时及时通知运维人员
4. **准备应急预案**: 包括备用队列和降级策略
5. **记录操作日志**: 便于问题追溯和性能分析

## 🎯 运行测试

```bash
# 运行单元测试
mvn test -Dtest=QueueRateLimitTest

# 运行完整演示
# 启动Spring Boot应用后，QueueRateLimitDemo会自动运行演示程序
```

## 📝 注意事项

1. 所有示例代码均使用Java 1.8语法编写
2. 队列名称需要全局唯一
3. 动态调整限流参数时要考虑业务影响
4. 建议在生产环境中逐步调整参数并观察效果