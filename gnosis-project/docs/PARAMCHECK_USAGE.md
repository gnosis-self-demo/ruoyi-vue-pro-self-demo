# 参数动态校验组件使用手册

> 基于 LiteFlow 2.11.3 + openGauss 3.0.0，支持 FLOW / HANDLER / HYBRID 三种校验模式

---

## 1. 快速开始

### 1.1 引入依赖

确保 `pom.xml` 已包含以下依赖：

```xml
<!-- LiteFlow 2.11.3 -->
<dependency>
    <groupId>com.yomahub</groupId>
    <artifactId>liteflow-spring-boot-starter</artifactId>
    <version>2.11.3</version>
</dependency>

<!-- JSONPath -->
<dependency>
    <groupId>com.jayway.jsonpath</groupId>
    <artifactId>json-path</artifactId>
    <version>2.9.0</version>
</dependency>

<!-- openGauss JDBC -->
<dependency>
    <groupId>org.opengauss</groupId>
    <artifactId>opengauss-jdbc</artifactId>
    <version>3.1.0</version>
</dependency>
```

### 1.2 执行数据库脚本

在 openGauss gnosis_sample schema 执行：

```bash
psql -h <host> -U gaussdb -d gnosis_sample -f paramcheck-opengauss.sql
```

或手动执行 `src/main/resources/db/paramcheck-opengauss.sql` 中的 SQL。

---

## 2. 三种校验模式

### 模式 1: HANDLER（纯代码模式）

**适用场景**：复杂业务校验（库存检查、外部 API 调用、防重提交）

**使用步骤**：

1. **实现 IValidationHandler 接口**

```java
package com.example.module.handler;

import com.cmbc.oa.module.paramcheck.domain.ValidationContext;
import com.cmbc.oa.module.paramcheck.domain.ValidationResult;
import com.cmbc.oa.module.paramcheck.handler.IValidationHandler;
import com.cmbc.oa.module.paramcheck.handler.ValidationHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单业务处理器
 */
@Component
@ValidationHandler("OrderBusinessHandler")  // 必须与 DB 中的 handler_code 一致
public class OrderBusinessHandler implements IValidationHandler {

    @Autowired
    private JdbcTemplate gnosisJdbcTemplate;

    @Override
    public ValidationResult validate(ValidationContext context) {
        // 获取原始请求数据
        Object rawData = context.getData("rawRequest");
        
        // 1. 库存校验（Java 代码 + JDBC，无存储过程）
        String skuId = extractJsonPath(rawData, "$.items[0].skuId");
        Integer qty = Integer.parseInt(String.valueOf(extractJsonPath(rawData, "$.items[0].qty")));
        
        Integer stock = gnosisJdbcTemplate.queryForObject(
            "SELECT stock_qty FROM gnosis_sample.product_stock WHERE sku_id = ?",
            Integer.class, skuId);
            
        if (stock == null || stock < qty) {
            return ValidationResult.fail("STOCK_NOT_ENOUGH", 
                "商品库存不足: SKU=" + skuId + ", 请求=" + qty + ", 可用=" + stock);
        }
        
        // 2. 防重复提交
        String orderNo = extractJsonPath(rawData, "$.orderNo");
        Integer exists = gnosisJdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM gnosis_sample.order_record " +
            "WHERE order_no = ? AND created_time > CURRENT_TIMESTAMP - INTERVAL '5 minutes'",
            Integer.class, orderNo);
            
        if (exists != null && exists > 0) {
            return ValidationResult.fail("DUPLICATE_ORDER", "订单重复提交: " + orderNo);
        }
        
        return ValidationResult.success();
    }
    
    private Object extractJsonPath(Object data, String path) {
        try {
            return com.jayway.jsonpath.JsonPath.read(data, path);
        } catch (Exception e) {
            return null;
        }
    }
}
```

2. **配置 DB 流程记录**

```sql
INSERT INTO gnosis_sample.sys_validation_flows 
(flow_id, flow_name, mode_type, handler_code)
VALUES 
('ORDER_CREATE_FLOW', '订单创建校验', 'HANDLER', 'OrderBusinessHandler');
```

3. **在 Controller 中使用**

```java
package com.example.controller;

import com.cmbc.oa.module.paramcheck.annotation.ParamCheck;
import com.cmbc.oa.module.paramcheck.domain.ValidationResult;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    /**
     * 创建订单 - 自动校验
     */
    @PostMapping("/create")
    @ParamCheck(flowId = "ORDER_CREATE_FLOW")  // 指定流程 ID
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> orderData) {
        // 校验通过后执行业务逻辑
        return Map.of("success", true, "orderId", "ORD" + System.currentTimeMillis());
    }
    
    /**
     * 手动调用校验
     */
    @PostMapping("/validate")
    public ValidationResult validateOrder(@RequestBody Map<String, Object> orderData) {
        // 注入 DynamicValidationService 并调用
        // validationService.execute("ORDER_CREATE_FLOW", orderData);
        return ValidationResult.success();
    }
}
```

---

### 模式 2: FLOW（纯编排模式）

**适用场景**：简单格式校验（正则、邮箱、手机号、范围校验）

**使用步骤**：

1. **配置校验规则（DB）**

```sql
INSERT INTO gnosis_sample.sys_validation_flows 
(flow_id, flow_name, mode_type, el_expression, component_config)
VALUES 
('USER_LOGIN_FLOW', '用户登录校验', 'FLOW', 'THEN(check_format)',
'{
    "check_format": {
        "rules": [
            {"path": "$.username", "type": "NOT_NULL", "msg": "用户名不能为空"},
            {"path": "$.password", "type": "LENGTH", "min": 6, "max": 32, "msg": "密码长度需6~32位"},
            {"path": "$.email", "type": "EMAIL", "msg": "邮箱格式错误"},
            {"path": "$.phone", "type": "PHONE", "msg": "手机号格式错误"}
        ]
    }
}'::jsonb);
```

2. **配置 liteflow-rule.xml**

确保 `src/main/resources/liteflow-rule.xml` 中有对应 chain：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<flow>
    <chain name="USER_LOGIN_FLOW">THEN(check_format);</chain>
</flow>
```

3. **使用**

```java
@PostMapping("/login")
@ParamCheck(flowId = "USER_LOGIN_FLOW")
public Map<String, Object> login(@RequestBody Map<String, Object> data) {
    return Map.of("success", true);
}
```

---

### 模式 3: HYBRID（混合模式）

**适用场景**：先做格式校验，校验通过后再执行业务逻辑

**示例：订单创建**

1. **DB 配置**

```sql
INSERT INTO gnosis_sample.sys_validation_flows 
(flow_id, flow_name, mode_type, el_expression, handler_code, component_config)
VALUES 
('ORDER_CREATE_FLOW', '订单创建校验', 'HYBRID', 'THEN(parse_param, check_format, check_db_user)',
 'OrderBusinessHandler',
'{
    "parse_param": {"json_path": "$"},
    "check_format": {
        "rules": [
            {"path": "$.orderNo", "type": "REGEX", "pattern": "^ORD[0-9]{10}$", "msg": "订单号格式错误"},
            {"path": "$.amount", "type": "RANGE", "min": 0.01, "max": 1000000, "msg": "金额超出范围"}
        ]
    },
    "check_db_user": {
        "sql": "SELECT COUNT(1) FROM gnosis_sample.sys_users WHERE user_id = ? AND status = 1",
        "param_path": "$.userId",
        "msg": "用户不存在或已禁用"
    }
}'::jsonb);
```

2. **liteflow-rule.xml**

```xml
<chain name="ORDER_CREATE_FLOW">THEN(parse_param, check_format, check_db_user);</chain>
```

---

## 3. 支持的规则类型

| 类型 | 说明 | 配置示例 |
|------|------|----------|
| `NOT_NULL` | 非空校验 | `{"path": "$.name", "type": "NOT_NULL", "msg": "名称必填"}` |
| `REGEX` | 正则校验 | `{"path": "$.orderNo", "type": "REGEX", "pattern": "^ORD[0-9]{10}$", "msg": "格式错误"}` |
| `EMAIL` | 邮箱格式 | `{"path": "$.email", "type": "EMAIL", "msg": "邮箱格式错误"}` |
| `PHONE` | 手机号格式 | `{"path": "$.phone", "type": "PHONE", "msg": "手机号格式错误"}` |
| `RANGE` | 数值范围 | `{"path": "$.amount", "type": "RANGE", "min": 0.01, "max": 1000000}` |
| `LENGTH` | 字符串长度 | `{"path": "$.password", "type": "LENGTH", "min": 6, "max": 32}` |

---

## 4. API 参考

### 4.1 @ParamCheck 注解

```java
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ParamCheck {
    String flowId();           // 必填：流程 ID
    boolean enabled() default true;           // 是否启用
    boolean throwOnFail() default true;       // 失败时是否抛异常
}
```

### 4.2 ValidationResult

```java
// 成功结果
ValidationResult.success()

// 失败结果
ValidationResult.fail("ERROR_CODE", "错误消息");
ValidationResult.fail("ERROR_CODE", "错误消息", "failed_node");
```

### 4.3 ValidationContext

```java
ValidationContext ctx = new ValidationContext();

// 存取数据
ctx.setData("key", value);
Object val = ctx.getData("key");
Object valWithDefault = ctx.getData("key", defaultValue);

// 获取原始请求
Object rawData = ctx.getData("rawRequest");

// 获取请求 ID（用于日志追踪）
String requestId = ctx.getRequestId();
```

---

## 5. 数据库表

### 5.1 sys_validation_flows（校验流程配置）

```sql
CREATE TABLE gnosis_sample.sys_validation_flows (
    flow_id         VARCHAR(64) PRIMARY KEY,    -- 流程唯一标识
    flow_name       VARCHAR(128) NOT NULL,       -- 流程名称
    mode_type       VARCHAR(20) CHECK (mode_type IN ('FLOW', 'HANDLER', 'HYBRID')),
    el_expression   TEXT,                        -- LiteFlow EL 表达式 (FLOW/HYBRID 必填)
    handler_code    VARCHAR(64),                 -- 自定义处理器编码 (HANDLER/HYBRID 必填)
    component_config JSONB,                      -- 组件配置 (JSONPath规则/SQL/正则等)
    is_active       BOOLEAN DEFAULT TRUE,        -- 是否激活
    version         INT DEFAULT 1,               -- 版本号 (用于热更新检测)
    updated_time    TIMESTAMPTZ                  -- 更新时间
);
```

### 5.2 sys_validation_logs（校验日志）

```sql
CREATE TABLE gnosis_sample.sys_validation_logs (
    log_id          BIGSERIAL PRIMARY KEY,
    flow_id         VARCHAR(64),
    request_id      VARCHAR(64),
    mode_type       VARCHAR(20),
    input_snapshot  JSONB,           -- 请求数据快照
    failed_node     VARCHAR(64),      -- 失败的节点
    error_msg       TEXT,
    created_time    TIMESTAMPTZ
);
```

---

## 6. 单元测试

运行测试类：

```bash
# 编译测试
mvn test -Dtest=ValidationModeTest

# 运行所有测试
mvn test -Dtest="com.cmbc.oa.module.paramcheck.*Test"
```

### 测试用例说明

| 测试类 | 测试内容 |
|--------|----------|
| `ValidationModeTest` | 规则类型校验（REGEX/EMAIL/PHONE/RANGE/LENGTH/NOT_NULL）、模式路由 |
| `ScenarioTest` | 场景集成测试：订单创建、用户注册、简单录入 |
| `HandlerTest` | Handler 注册、密码强度校验、用户名重复检查 |

---

## 7. 常见问题

### Q1: 如何添加新的校验规则类型？

在 `BasicRuleComponent.validateValue()` 方法中添加新的 `case`。

### Q2: Handler 中如何获取请求数据？

```java
Object rawData = context.getData("rawRequest");
// 或使用 JSONPath 提取
String orderNo = com.jayway.jsonpath.JsonPath.read(rawData, "$.orderNo");
```

### Q3: 如何禁用某个流程的校验？

```sql
UPDATE gnosis_sample.sys_validation_flows 
SET is_active = FALSE 
WHERE flow_id = 'ORDER_CREATE_FLOW';
```

### Q4: 如何实现热更新？

流程配置变更后，`FlowRefreshService` 每 30 秒自动检测版本号变化并重载。

---

## 8. 完整示例

### 完整示例：用户注册流程

**1. 创建 Handler**

```java
@Component
@ValidationHandler("UserRegisterHandler")
public class UserRegisterHandler implements IValidationHandler {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    private static final Pattern PASSWORD_PATTERN = 
        Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d]{8,}$");
    
    @Override
    public ValidationResult validate(ValidationContext context) {
        Object data = context.getData("rawRequest");
        
        // 用户名非空
        String username = extract(data, "$.username");
        if (username == null || username.trim().isEmpty()) {
            return ValidationResult.fail("USERNAME_EMPTY", "用户名不能为空");
        }
        
        // 用户名重复检查
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM gnosis_sample.sys_users WHERE username = ?",
            Integer.class, username);
        if (count != null && count > 0) {
            return ValidationResult.fail("USERNAME_EXISTS", "用户名已存在");
        }
        
        // 密码强度
        String password = extract(data, "$.password");
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return ValidationResult.fail("PASSWORD_WEAK", 
                "密码需至少8位，包含大小写字母和数字");
        }
        
        return ValidationResult.success();
    }
    
    private String extract(Object data, String path) {
        try {
            return String.valueOf(com.jayway.jsonpath.JsonPath.read(data, path));
        } catch (Exception e) {
            return null;
        }
    }
}
```

**2. 配置 DB**

```sql
INSERT INTO gnosis_sample.sys_validation_flows 
(flow_id, flow_name, mode_type, handler_code)
VALUES 
('USER_REGISTER_FLOW', '用户注册', 'HANDLER', 'UserRegisterHandler');
```

**3. Controller 使用**

```java
@PostMapping("/register")
@ParamCheck(flowId = "USER_REGISTER_FLOW")
public Map<String, Object> register(@RequestBody Map<String, Object> userData) {
    // 校验通过后注册用户
    return Map.of("success", true, "userId", "U" + System.currentTimeMillis());
}
```

---

**手册版本**: 1.0  
**最后更新**: 2026-03-20
