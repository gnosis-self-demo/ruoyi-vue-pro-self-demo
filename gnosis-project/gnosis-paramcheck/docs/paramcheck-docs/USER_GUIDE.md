# 参数动态校验组件用户使用指南

## 1. 快速开始

### 1.1 环境准备

- JDK 8+
- Spring Boot 2.7.x
- openGauss 3.0.0
- LiteFlow 2.11.3

### 1.2 依赖配置

在 Maven 项目的 `pom.xml` 中添加以下依赖：

```xml
<dependencies>
    <!-- Spring Boot 核心依赖 -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    
    <!-- LiteFlow 依赖 -->
    <dependency>
        <groupId>com.yomahub</groupId>
        <artifactId>liteflow-core</artifactId>
        <version>2.11.3</version>
    </dependency>
    
    <!-- openGauss 驱动 -->
    <dependency>
        <groupId>org.opengauss</groupId>
        <artifactId>opengauss-jdbc</artifactId>
        <version>3.0.0</version>
    </dependency>
    
    <!-- JSONPath 依赖 -->
    <dependency>
        <groupId>com.jayway.jsonpath</groupId>
        <artifactId>json-path</artifactId>
        <version>2.7.0</version>
    </dependency>
</dependencies>
```

### 1.3 数据库初始化

执行以下 SQL 脚本初始化数据库：

```sql
-- 创建 schema
CREATE SCHEMA IF NOT EXISTS gnosis_sample;

-- 创建流程配置表
CREATE TABLE gnosis_sample.sys_validation_flows (
    flow_id VARCHAR(64) PRIMARY KEY,
    flow_name VARCHAR(255) NOT NULL,
    mode_type VARCHAR(10) NOT NULL, -- FLOW/HANDLER/HYBRID
    el_expression TEXT,
    handler_code VARCHAR(64),
    component_config JSONB,
    is_active BOOLEAN DEFAULT TRUE,
    version INT DEFAULT 1,
    updated_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 创建校验日志表
CREATE TABLE gnosis_sample.sys_validation_logs (
    log_id SERIAL PRIMARY KEY,
    flow_id VARCHAR(64) NOT NULL,
    request_id VARCHAR(64),
    input_snapshot JSONB,
    error_msg TEXT,
    created_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 插入示例数据
INSERT INTO gnosis_sample.sys_validation_flows (
    flow_id, flow_name, mode_type, el_expression, handler_code, component_config
) VALUES (
    'test_flow', '测试流程', 'FLOW', 'PARSE_PARAM -> BASIC_RULE -> DB_QUERY', NULL, 
    '{"parse_param": {"json_path": "$.user"}, "check_format": {"rules": [{"field": "userId", "pattern": "^\\d+$", "msg": "用户ID必须为数字"}], "msg": "参数格式错误"}, "check_db_user": {"sql": "SELECT COUNT(*) FROM users WHERE user_id = ?", "param_path": "$.userId", "msg": "用户不存在"}}'
);

INSERT INTO gnosis_sample.sys_validation_flows (
    flow_id, flow_name, mode_type, el_expression, handler_code, component_config
) VALUES (
    'test_handler', '测试处理器', 'HANDLER', NULL, 'userRegisterHandler', NULL
);

INSERT INTO gnosis_sample.sys_validation_flows (
    flow_id, flow_name, mode_type, el_expression, handler_code, component_config
) VALUES (
    'test_hybrid', '测试混合模式', 'HYBRID', 'PARSE_PARAM -> BASIC_RULE', 'orderBusinessHandler', 
    '{"parse_param": {"json_path": "$.order"}, "check_format": {"rules": [{"field": "orderId", "pattern": "^\\d+$", "msg": "订单ID必须为数字"}], "msg": "参数格式错误"}}'
);
```

## 2. 核心 API 使用

### 2.1 动态校验服务

**服务接口**：`DynamicValidationService.execute(String flowId, Object requestData)`

**参数说明**：
- `flowId`：流程ID
- `requestData`：待校验的请求数据（支持 Map、JSON String 或任意对象）

**返回值**：`ValidationResult` 对象，包含校验结果和错误信息（如有）

**使用示例**：

```java
@Autowired
private DynamicValidationService dynamicValidationService;

// 准备请求数据
Map<String, Object> requestData = new HashMap<>();
requestData.put("userId", "123");
requestData.put("amount", 1000);

// 执行校验
ValidationResult result = dynamicValidationService.execute("test_flow", requestData);

// 处理结果
if (result.isSuccess()) {
    System.out.println("校验通过");
} else {
    System.out.println("校验失败：" + result.getErrorMsg());
    System.out.println("错误码：" + result.getErrorCode());
    System.out.println("失败节点：" + result.getFailedNode());
}
```

### 2.2 配置管理 API

**基础路径**：`/api/paramcheck/config`

**接口列表**：

| 方法 | 路径 | 功能 |
|------|------|------|
| GET | /flows | 获取所有激活的流程配置 |
| GET | /flows/{flowId} | 获取单个流程配置详情 |
| POST | /flows | 保存流程配置（创建或更新） |
| POST | /flows/{flowId}/refresh | 手动刷新指定流程 |
| POST | /flows/refresh-all | 全量刷新所有流程 |
| POST | /flows/{flowId}/deactivate | 禁用流程 |

**使用示例**：

```bash
# 获取所有激活的流程配置
curl -X GET "http://localhost:8080/api/paramcheck/config/flows"

# 获取单个流程配置详情
curl -X GET "http://localhost:8080/api/paramcheck/config/flows/test_flow"

# 保存流程配置
curl -X POST "http://localhost:8080/api/paramcheck/config/flows" \
  -H "Content-Type: application/json" \
  -d '{
    "flowId": "new_flow",
    "flowName": "新流程",
    "modeType": "FLOW",
    "elExpression": "PARSE_PARAM -> BASIC_RULE",
    "componentConfig": {
      "parse_param": {
        "json_path": "$.data"
      },
      "check_format": {
        "rules": [
          {
            "field": "name",
            "pattern": "^[a-zA-Z]+$",
            "msg": "名称必须为字母"
          }
        ]
      }
    },
    "isActive": true,
    "version": 1
  }'

# 手动刷新流程
curl -X POST "http://localhost:8080/api/paramcheck/config/flows/test_flow/refresh"

# 全量刷新所有流程
curl -X POST "http://localhost:8080/api/paramcheck/config/flows/refresh-all"

# 禁用流程
curl -X POST "http://localhost:8080/api/paramcheck/config/flows/test_flow/deactivate"
```

## 3. 三种校验模式

### 3.1 FLOW 模式

**特点**：纯编排模式，通过 LiteFlow 引擎执行 EL 表达式

**适用场景**：
- 规则相对固定，可通过配置调整
- 多个校验步骤需要按顺序执行
- 适合简单到中等复杂度的校验逻辑

**配置示例**：

```json
{
  "flowId": "flow_mode",
  "flowName": "FLOW模式示例",
  "modeType": "FLOW",
  "elExpression": "PARSE_PARAM -> BASIC_RULE -> DB_QUERY",
  "componentConfig": {
    "parse_param": {
      "json_path": "$.user"
    },
    "check_format": {
      "rules": [
        {
          "field": "userId",
          "pattern": "^\\d+$",
          "msg": "用户ID必须为数字"
        }
      ],
      "msg": "参数格式错误"
    },
    "check_db_user": {
      "sql": "SELECT COUNT(*) FROM users WHERE user_id = ?",
      "param_path": "$.userId",
      "msg": "用户不存在"
    }
  }
}
```

### 3.2 HANDLER 模式

**特点**：纯代码模式，直接调用自定义处理器

**适用场景**：
- 校验逻辑复杂，需要自定义实现
- 涉及外部系统调用或复杂业务逻辑
- 需要更灵活的校验控制

**自定义处理器示例**：

```java
@ValidationHandler("customHandler")
@Component
public class CustomHandler implements IValidationHandler {
    @Override
    public ValidationResult validate(ValidationContext context) {
        // 获取请求数据
        Object requestData = context.getData("rawRequest");
        
        // 自定义校验逻辑
        if (requestData == null) {
            return ValidationResult.fail("PARAM_ERROR", "请求数据不能为空");
        }
        
        // 更复杂的校验逻辑...
        
        return ValidationResult.success();
    }
}
```

**配置示例**：

```json
{
  "flowId": "handler_mode",
  "flowName": "HANDLER模式示例",
  "modeType": "HANDLER",
  "handlerCode": "customHandler"
}
```

### 3.3 HYBRID 模式

**特点**：混合模式，先执行 FLOW 流程，再执行 HANDLER

**适用场景**：
- 既有规则校验，又有复杂业务逻辑
- 需要先进行基础校验，再进行业务校验
- 适合复杂的校验场景

**配置示例**：

```json
{
  "flowId": "hybrid_mode",
  "flowName": "HYBRID模式示例",
  "modeType": "HYBRID",
  "elExpression": "PARSE_PARAM -> BASIC_RULE",
  "handlerCode": "orderBusinessHandler",
  "componentConfig": {
    "parse_param": {
      "json_path": "$.order"
    },
    "check_format": {
      "rules": [
        {
          "field": "orderId",
          "pattern": "^\\d+$",
          "msg": "订单ID必须为数字"
        }
      ],
      "msg": "参数格式错误"
    }
  }
}
```

## 4. 扩展开发

### 4.1 自定义处理器

**步骤**：
1. 实现 `IValidationHandler` 接口
2. 添加 `@ValidationHandler` 注解，指定处理器编码
3. 注册为 Spring Bean

**示例**：

```java
@ValidationHandler("userRegisterHandler")
@Component
public class UserRegisterHandler implements IValidationHandler {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Override
    public ValidationResult validate(ValidationContext context) {
        Map<String, Object> requestData = (Map<String, Object>) context.getData("rawRequest");
        String userId = (String) requestData.get("userId");
        
        // 检查用户是否已存在
        Integer count = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM users WHERE user_id = ?",
            Integer.class, userId
        );
        
        if (count > 0) {
            return ValidationResult.fail("USER_EXISTS", "用户已存在");
        }
        
        return ValidationResult.success();
    }
}
```

### 4.2 自定义 LiteFlow 组件

**步骤**：
1. 继承 `NodeComponent` 类
2. 实现 `process()` 方法
3. 在 EL 表达式中引用

**示例**：

```java
@Component("CUSTOM_COMPONENT")
public class CustomComponent extends NodeComponent {
    @Override
    public void process() {
        // 获取请求数据
        Map<String, Object> requestData = this.getRequestData();
        
        // 自定义处理逻辑
        String value = (String) requestData.get("customField");
        if (value == null || value.isEmpty()) {
            this.setIsEnd(true);
            this.setException(new ValidationException("CUSTOM_ERROR", "自定义字段不能为空"));
        }
        
        // 处理成功
        requestData.put("customResult", "处理成功");
    }
}
```

**EL 表达式示例**：

```
PARSE_PARAM -> CUSTOM_COMPONENT -> BASIC_RULE
```

## 5. 配置管理

### 5.1 流程配置结构

**核心字段**：
- `flowId`：流程唯一标识
- `flowName`：流程名称
- `modeType`：校验模式 (FLOW/HANDLER/HYBRID)
- `elExpression`：LiteFlow EL 表达式（FLOW/HYBRID 模式必填）
- `handlerCode`：自定义处理器编码（HANDLER/HYBRID 模式必填）
- `componentConfig`：组件配置（JSON 格式）
- `isActive`：是否激活
- `version`：版本号

**componentConfig 结构**：

```json
{
  "parse_param": {
    "json_path": "$.data" // JSONPath 表达式
  },
  "check_format": {
    "rules": [
      {
        "field": "fieldName", // 字段名
        "pattern": "^\\d+$", // 正则表达式
        "msg": "错误消息" // 错误提示
      }
    ],
    "msg": "参数格式错误" // 通用错误消息
  },
  "check_db_user": {
    "sql": "SELECT COUNT(*) FROM users WHERE user_id = ?", // SQL 查询
    "param_path": "$.userId", // 参数路径
    "msg": "用户不存在" // 错误消息
  }
}
```

### 5.2 版本控制

- 每次更新流程配置时，版本号自动递增
- 支持通过版本号追踪配置变更
- 可根据版本号回滚配置

### 5.3 热加载

- 系统启动时自动加载所有激活的流程配置
- 定时轮询检测配置变更（每 30 秒）
- 支持通过 API 手动刷新流程配置

## 6. 监控与日志

### 6.1 日志记录

- 校验失败时自动记录详细日志
- 日志包含：流程 ID、请求数据、错误信息、时间戳
- 日志存储在 `gnosis_sample.sys_validation_logs` 表中

### 6.2 监控指标

- 校验成功率
- 校验响应时间
- 错误率分布
- 流程执行次数

## 7. 常见问题

### 7.1 流程未找到

**问题**：执行校验时提示 "Flow not found"
**解决**：
- 检查流程 ID 是否正确
- 确认流程是否已激活
- 检查数据库连接是否正常

### 7.2 处理器未找到

**问题**：执行 HANDLER 模式时提示 "Handler not found"
**解决**：
- 检查处理器编码是否正确
- 确认处理器是否已注册（添加了 @ValidationHandler 注解）
- 检查处理器是否被 Spring 扫描到

### 7.3 LiteFlow 链未找到

**问题**：执行 FLOW 模式时提示 "Chain not found"
**解决**：
- 检查 EL 表达式是否正确
- 确认 LiteFlow 组件是否已注册
- 检查 liteflow-rule.xml 配置文件

### 7.4 数据库连接失败

**问题**：执行 DB_QUERY 组件时提示数据库连接失败
**解决**：
- 检查数据库连接配置
- 确认数据库服务是否正常运行
- 检查数据库用户权限

## 8. 最佳实践

### 8.1 流程设计

- 合理划分校验流程，避免过于复杂的 EL 表达式
- 对于复杂业务逻辑，优先使用 HANDLER 模式
- 对于简单规则校验，使用 FLOW 模式
- 对于混合场景，使用 HYBRID 模式

### 8.2 性能优化

- 合理使用缓存，减少数据库访问
- 优化 SQL 查询，避免慢查询
- 避免在处理器中执行耗时操作
- 考虑使用异步处理复杂校验

### 8.3 安全建议

- 对输入参数进行严格验证
- 使用参数化查询，防止 SQL 注入
- 对敏感信息进行脱敏处理
- 限制配置管理 API 的访问权限

### 8.4 维护建议

- 定期备份流程配置
- 建立配置变更审批流程
- 监控校验失败率，及时发现问题
- 定期清理过期日志

## 9. 示例场景

### 9.1 用户注册校验

**流程**：
1. 解析参数
2. 验证参数格式
3. 检查用户是否已存在
4. 检查手机号是否已注册

**配置**：

```json
{
  "flowId": "user_register",
  "flowName": "用户注册校验",
  "modeType": "HYBRID",
  "elExpression": "PARSE_PARAM -> BASIC_RULE",
  "handlerCode": "userRegisterHandler",
  "componentConfig": {
    "parse_param": {
      "json_path": "$.user"
    },
    "check_format": {
      "rules": [
        {
          "field": "userId",
          "pattern": "^\\d+$",
          "msg": "用户ID必须为数字"
        },
        {
          "field": "phone",
          "pattern": "^1[3-9]\\d{9}$",
          "msg": "手机号格式错误"
        }
      ],
      "msg": "参数格式错误"
    }
  }
}
```

### 9.2 订单创建校验

**流程**：
1. 解析参数
2. 验证订单信息
3. 检查用户余额
4. 检查商品库存

**配置**：

```json
{
  "flowId": "order_create",
  "flowName": "订单创建校验",
  "modeType": "HYBRID",
  "elExpression": "PARSE_PARAM -> BASIC_RULE",
  "handlerCode": "orderBusinessHandler",
  "componentConfig": {
    "parse_param": {
      "json_path": "$.order"
    },
    "check_format": {
      "rules": [
        {
          "field": "orderId",
          "pattern": "^\\d+$",
          "msg": "订单ID必须为数字"
        },
        {
          "field": "amount",
          "pattern": "^\\d+(\\.\\d{1,2})?$",
          "msg": "金额格式错误"
        }
      ],
      "msg": "参数格式错误"
    }
  }
}
```

### 9.3 商品上架校验

**流程**：
1. 解析参数
2. 验证商品信息
3. 检查分类是否存在
4. 检查品牌是否存在

**配置**：

```json
{
  "flowId": "product_上架",
  "flowName": "商品上架校验",
  "modeType": "HYBRID",
  "elExpression": "PARSE_PARAM -> BASIC_RULE -> DB_QUERY",
  "handlerCode": "productHandler",
  "componentConfig": {
    "parse_param": {
      "json_path": "$.product"
    },
    "check_format": {
      "rules": [
        {
          "field": "productId",
          "pattern": "^P\\d+$",
          "msg": "商品ID格式错误"
        },
        {
          "field": "price",
          "pattern": "^\\d+(\\.\\d{1,2})?$",
          "msg": "价格格式错误"
        },
        {
          "field": "stock",
          "pattern": "^\\d+$",
          "msg": "库存必须为数字"
        }
      ],
      "msg": "商品信息格式错误"
    },
    "check_db_category": {
      "sql": "SELECT COUNT(*) FROM categories WHERE category_id = ?",
      "param_path": "$.categoryId",
      "msg": "分类不存在"
    },
    "check_db_brand": {
      "sql": "SELECT COUNT(*) FROM brands WHERE brand_id = ?",
      "param_path": "$.brandId",
      "msg": "品牌不存在"
    }
  }
}
```

### 9.4 支付交易校验

**流程**：
1. 解析参数
2. 验证交易信息
3. 检查支付渠道
4. 检查交易限额

**配置**：

```json
{
  "flowId": "payment_transaction",
  "flowName": "支付交易校验",
  "modeType": "HYBRID",
  "elExpression": "PARSE_PARAM -> BASIC_RULE",
  "handlerCode": "paymentHandler",
  "componentConfig": {
    "parse_param": {
      "json_path": "$.transaction"
    },
    "check_format": {
      "rules": [
        {
          "field": "transactionId",
          "pattern": "^TX\\d+$",
          "msg": "交易ID格式错误"
        },
        {
          "field": "amount",
          "pattern": "^\\d+(\\.\\d{1,2})?$",
          "msg": "金额格式错误"
        },
        {
          "field": "paymentChannel",
          "pattern": "^(alipay|wechat|bank)$",
          "msg": "支付渠道错误"
        }
      ],
      "msg": "交易信息格式错误"
    }
  }
}
```

### 9.5 用户登录校验

**流程**：
1. 解析参数
2. 验证登录信息
3. 检查用户是否存在
4. 检查密码是否正确
5. 检查账户状态

**配置**：

```json
{
  "flowId": "user_login",
  "flowName": "用户登录校验",
  "modeType": "HYBRID",
  "elExpression": "PARSE_PARAM -> BASIC_RULE",
  "handlerCode": "loginHandler",
  "componentConfig": {
    "parse_param": {
      "json_path": "$.login"
    },
    "check_format": {
      "rules": [
        {
          "field": "username",
          "pattern": "^[a-zA-Z0-9_]{3,20}$",
          "msg": "用户名格式错误"
        },
        {
          "field": "password",
          "pattern": ".{6,20}",
          "msg": "密码长度错误"
        }
      ],
      "msg": "登录信息格式错误"
    }
  }
}
```

### 9.6 优惠券使用校验

**流程**：
1. 解析参数
2. 验证优惠券信息
3. 检查优惠券是否存在
4. 检查优惠券是否过期
5. 检查优惠券使用条件

**配置**：

```json
{
  "flowId": "coupon_usage",
  "flowName": "优惠券使用校验",
  "modeType": "HYBRID",
  "elExpression": "PARSE_PARAM -> BASIC_RULE -> DB_QUERY",
  "handlerCode": "couponHandler",
  "componentConfig": {
    "parse_param": {
      "json_path": "$.coupon"
    },
    "check_format": {
      "rules": [
        {
          "field": "couponCode",
          "pattern": "^C\\d+$",
          "msg": "优惠券码格式错误"
        },
        {
          "field": "orderAmount",
          "pattern": "^\\d+(\\.\\d{1,2})?$",
          "msg": "订单金额格式错误"
        }
      ],
      "msg": "优惠券信息格式错误"
    },
    "check_db_coupon": {
      "sql": "SELECT COUNT(*) FROM coupons WHERE coupon_code = ? AND end_time > CURRENT_TIMESTAMP",
      "param_path": "$.couponCode",
      "msg": "优惠券不存在或已过期"
    }
  }
}
```

### 9.7 物流发货校验

**流程**：
1. 解析参数
2. 验证物流信息
3. 检查订单是否存在
4. 检查订单状态
5. 检查物流地址

**配置**：

```json
{
  "flowId": "logistics_shipment",
  "flowName": "物流发货校验",
  "modeType": "HYBRID",
  "elExpression": "PARSE_PARAM -> BASIC_RULE -> DB_QUERY",
  "handlerCode": "logisticsHandler",
  "componentConfig": {
    "parse_param": {
      "json_path": "$.logistics"
    },
    "check_format": {
      "rules": [
        {
          "field": "orderId",
          "pattern": "^\\d+$",
          "msg": "订单ID格式错误"
        },
        {
          "field": "trackingNumber",
          "pattern": "^[A-Z0-9]{6,20}$",
          "msg": "物流单号格式错误"
        },
        {
          "field": "carrier",
          "pattern": "^[a-zA-Z0-9\\s]{2,50}$",
          "msg": "物流公司名称错误"
        }
      ],
      "msg": "物流信息格式错误"
    },
    "check_db_order": {
      "sql": "SELECT COUNT(*) FROM orders WHERE order_id = ? AND status = 'PAYED'",
      "param_path": "$.orderId",
      "msg": "订单不存在或状态错误"
    }
  }
}
```

## 10. 总结

参数动态校验组件提供了一种灵活、可扩展的参数校验解决方案，支持三种校验模式，可满足不同场景的需求。通过配置管理 API，用户可以方便地管理和版本控制流程配置，实现动态调整校验规则，而无需修改代码。

该组件具有以下特点：
- **灵活性**：支持三种校验模式，适应不同场景
- **可扩展性**：支持自定义处理器和 LiteFlow 组件
- **性能优化**：内置缓存机制，提高系统性能
- **易于管理**：提供配置管理 API，方便版本控制
- **可靠性**：完善的错误处理和日志记录

通过本指南，用户可以快速上手使用参数动态校验组件，实现高效、可靠的参数校验功能。
