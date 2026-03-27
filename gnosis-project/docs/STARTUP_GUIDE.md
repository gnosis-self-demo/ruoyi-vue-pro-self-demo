# 启动与测试指南

## 1. 启动方式

### 方式一：直接运行启动类

```bash
cd C:\works\project\ruoyi-vue-pro-self-demo\gnosis-project

# 方式1: 运行 main 方法
mvn spring-boot:run -Dspring-boot.mainClass=paramcheck.ParamCheckApplication

# 方式2: 先编译再运行
mvn compile
java -cp target/classes;target/dependency/* paramcheck.ParamCheckApplication
```

### 方式二：打包后运行

```bash
# 打包
mvn clean package -DskipTests

# 运行 JAR
java -jar target/gnosis-project-1.0.0-SNAPSHOT.jar
```

---

## 2. 接口测试

启动成功后，使用 Postman 或 curl 测试：

### 2.1 测试 FLOW 模式（简单登录校验）

```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "phone": "13812345678"}'
```

**预期响应**：
```json
{"success": true, "token": "token_xxx", "message": "登录成功"}
```

**失败示例（邮箱格式错误）**：
```bash
curl -X POST http://localhost:8080/api/login \
  -H "Content-Type: application/json" \
  -d '{"email": "invalid-email", "phone": "13812345678"}'
```

**预期响应**（throwOnFail=false）：
```json
{"success": false, "errorCode": "FORMAT_ERROR", "errorMsg": "邮箱格式错误"}
```

---

### 2.2 测试 HANDLER 模式（用户注册）

```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username": "newuser", "password": "Password123", "email": "test@example.com"}'
```

**预期响应**：
```json
{"success": true, "userId": "Uxxx", "message": "用户注册成功"}
```

**失败示例（密码强度不足）**：
```bash
curl -X POST http://localhost:8080/api/user/register \
  -H "Content-Type: application/json" \
  -d '{"username": "test", "password": "123", "email": "test@example.com"}'
```

**预期响应**：
```json
{"success": false, "errorCode": "PASSWORD_WEAK", "errorMsg": "密码强度不足..."}
```

---

### 2.3 测试 HYBRID 模式（订单创建）

```bash
curl -X POST http://localhost:8080/api/order/create \
  -H "Content-Type: application/json" \
  -d '{"orderNo": "ORD1234567890", "amount": 500.00, "userId": "U001", "items": [{"skuId": "SKU001", "qty": 2}]}'
```

**预期响应**：
```json
{"success": true, "orderId": "ORDxxx", "message": "订单创建成功"}
```

**失败示例（订单号格式错误）**：
```bash
curl -X POST http://localhost:8080/api/order/create \
  -H "Content-Type: application/json" \
  -d '{"orderNo": "INVALID", "amount": 500.00, "userId": "U001"}'
```

**预期响应**：
```json
{"success": false, "errorCode": "FORMAT_ERROR", "errorMsg": "订单号格式错误，格式应为 ORD+10位数字"}
```

---

### 2.4 手动调用校验

```bash
curl -X POST "http://localhost:8080/api/validate?flowId=SIMPLE_CHECK_FLOW" \
  -H "Content-Type: application/json" \
  -d '{"email": "test@example.com", "phone": "13812345678"}'
```

---

## 3. 注意事项

### 3.1 数据库依赖

以下功能需要 openGauss 数据库正常运行：

| 功能 | 依赖 |
|------|------|
| HYBRID 模式 DB 校验 | `sys_users` 表查询 |
| HANDLER 模式 | `sys_users` 表查询 |
| 日志记录 | `sys_validation_logs` 表 |

### 3.2 初始化数据库

首次启动前，执行数据库脚本：

```bash
psql -h <host> -U gaussdb -d gnosis_sample -f src/main/resources/db/paramcheck-opengauss.sql
```

或手动复制脚本内容到数据库工具执行。

### 3.3 配置说明

确保 `application.properties` 包含：

```properties
# 数据源配置
spring.datasource.url=jdbc:opengauss://<host>:5432/gnosis_sample
spring.datasource.username=<username>
spring.datasource.password=<password>

# SQL 初始化
spring.sql.init.mode=always
spring.sql.init.schema-locations=classpath:db/liteflow-opengauss.sql,classpath:db/paramcheck-opengauss.sql
```

---

## 4. 验证清单

| 序号 | 验证项 | 测试命令 | 预期结果 |
|------|--------|----------|----------|
| 1 | 健康检查 | `GET /api/health` | `{"status": "UP"}` |
| 2 | FLOW-成功 | POST /api/login 正确邮箱手机 | `success: true` |
| 3 | FLOW-失败 | POST /api/login 错误邮箱 | `errorCode: FORMAT_ERROR` |
| 4 | HANDLER-成功 | POST /api/user/register 强密码 | `success: true` |
| 5 | HANDLER-失败 | POST /api/user/register 弱密码 | `errorCode: PASSWORD_WEAK` |
| 6 | HYBRID-成功 | POST /api/order/create 正确格式 | `success: true` |
| 7 | HYBRID-失败 | POST /api/order/create 错误订单号 | `errorCode: FORMAT_ERROR` |

---

**启动类位置**: `paramcheck.ParamCheckApplication`  
**测试 Controller**: `paramcheck.controller.ValidationDemoController`
