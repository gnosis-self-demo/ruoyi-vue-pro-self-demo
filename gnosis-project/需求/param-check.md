# 🚀 项目开发指令：支持 LiteFlow 编排与自定义接口拓展的参数动态校验组件 (v2.0)

## 1. 项目背景与核心目标
构建一个**双模驱动、纯代码实现**的参数动态校验中间件。
1.  **模式一：LiteFlow 编排模式**。利用 **LiteFlow 2.11.3** 引擎，通过数据库配置的 EL 表达式动态编排校验流程（适合通用、标准化场景）。
2.  **模式二：自定义接口拓展模式**。允许开发人员通过实现标准 Java 接口 (`IValidationHandler`) 编写高度定制化的校验逻辑，并动态注册到引擎中（适合复杂、特定业务场景）。
3.  **核心约束**：**严禁使用存储过程/触发器**。所有逻辑（包括数据库校验）必须通过 **Java 代码 + 动态 SQL** 实现。**openGauss 3.0.0** 仅作为配置存储和数据查询源。

---

## 2. 运行环境与版本约束 (严格执行)

| 配置项 | 值/说明 | 备注 |
| :--- | :--- | :--- |
| **项目根路径** | `C:\works\project\ruoyi-vue-pro-self-demo\gnosis-project` | **绝对路径** |
| **开发包名** | `paramcheck` | 所有代码必须在此包下 |
| **LiteFlow 版本** | **2.11.3** | `liteflow-spring-boot-starter:2.11.3` |
| **openGauss 版本** | **3.0.0** | 兼容 PostgreSQL 协议，注意 JDBC 驱动 |
| **JDBC URL** | `jdbc:opengauss://localserver.gnosis:5432/gnosis_sample` | |
| **Username** | `gaussdb` | |
| **Password** | `Enmotech@123` | |
| **Schema** | `gnosis_sample` | **所有 SQL 需显式指定此 Schema** |
| **JSONPath 库** | `com.jayway.jsonpath:json-path` | 用于参数提取 |

### Maven 依赖参考 (`pom.xml`)
```xml
<dependencies>
    <!-- LiteFlow 2.11.3 -->
    <dependency>
        <groupId>com.yomahub</groupId>
        <artifactId>liteflow-spring-boot-starter</artifactId>
        <version>2.11.3</version>
    </dependency>
    
    <!-- openGauss JDBC Driver (适配 3.0.0) -->
    <dependency>
        <groupId>org.opengauss</groupId>
        <artifactId>opengauss-jdbc</artifactId>
        <version>5.0.0-og</version> <!-- 建议使用兼容 3.0 的稳定驱动版本 -->
    </dependency>
    
    <!-- JSONPath -->
    <dependency>
        <groupId>com.jayway.jsonpath</groupId>
        <artifactId>json-path</artifactId>
        <version>2.9.0</version>
    </dependency>
    
    <!-- Spring Boot Web & AOP (若主项目未包含) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-aop</artifactId>
    </dependency>
</dependencies>
```

---

## 3. 功能架构设计

### 3.1 双模校验引擎架构
*   **统一入口**：`DynamicValidationService`。
*   **路由策略**：
    *   `FLOW` 模式：加载 LiteFlow 流程，执行编排链。
    *   `HANDLER` 模式：查找注册的 `IValidationHandler` 实现类，直接执行。
    *   `HYBRID` 模式：先执行 LiteFlow 基础校验，再调用自定义 Handler 进行深度业务校验。
*   **自定义拓展机制**：
    *   接口：`paramcheck.handler.IValidationHandler`。
    *   注解：`@ValidationHandler("uniqueCode")`。
    *   注册：启动时自动扫描并注册到 `HandlerRegistry`。

### 3.2 数据库模型设计 (`gnosis_sample` Schema)

```sql
-- 1. 校验流程配置表 (支持 LiteFlow 和 Handler 混合模式)
CREATE TABLE IF NOT EXISTS gnosis_sample.sys_validation_flows (
    flow_id         VARCHAR(64) PRIMARY KEY,      -- 流程标识
    flow_name       VARCHAR(128) NOT NULL,
    mode_type       VARCHAR(20) DEFAULT 'FLOW',   -- 模式：FLOW, HANDLER, HYBRID
    el_expression   TEXT,                         -- LiteFlow EL (FLOW/HYBRID 必填)
    handler_code    VARCHAR(64),                  -- 自定义处理器编码 (HANDLER/HYBRID 必填)
    component_config JSONB,                       -- 组件配置 (JSONPath, 正则, SQL等)
    is_active       BOOLEAN DEFAULT TRUE,
    version         INT DEFAULT 1,
    updated_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 2. 校验日志表
CREATE TABLE IF NOT EXISTS gnosis_sample.sys_validation_logs (
    log_id          BIGSERIAL PRIMARY KEY,
    flow_id         VARCHAR(64),
    request_id      VARCHAR(64),
    mode_type       VARCHAR(20),
    input_snapshot  JSONB,
    failed_node     VARCHAR(64),
    error_msg       TEXT,
    created_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 初始化示例数据 (包含三种模式)
INSERT INTO gnosis_sample.sys_validation_flows (flow_id, flow_name, mode_type, el_expression, handler_code, component_config) VALUES 
('ORDER_CREATE_FLOW', '订单创建校验 (混合模式)', 'HYBRID', 
 'THEN(parse_param, check_format, check_db_user)', 
 'OrderBusinessHandler', 
 '{
    "parse_param": {"json_path": "$"},
    "check_format": {
        "rules": [
            {"path": "$.orderNo", "type": "REGEX", "pattern": "^ORD\\d{10}$", "msg": "订单号格式错误"},
            {"path": "$.amount", "type": "RANGE", "min": 0.01, "max": 1000000, "msg": "金额超出范围"}
        ]
    },
    "check_db_user": {
        "type": "DB_QUERY",
        "sql": "SELECT 1 FROM gnosis_sample.sys_users WHERE user_id = ? AND status = 1",
        "param_path": "$.userId",
        "msg": "用户不存在或被禁用"
    }
 }'::jsonb),

('USER_REGISTER_FLOW', '用户注册 (纯接口模式)', 'HANDLER', 
 NULL, 
 'UserRegisterHandler', 
 NULL),

('SIMPLE_CHECK_FLOW', '简单格式校验 (纯编排模式)', 'FLOW',
 'THEN(parse_param, check_format)',
 NULL,
 '{
    "parse_param": {"json_path": "$"},
    "check_format": {
        "rules": [
            {"path": "$.email", "type": "EMAIL", "msg": "邮箱格式错误"}
        ]
    }
 }'::jsonb);
```

---

## 4. 详细开发指引 (Agent Action Plan)

### 4.1 项目结构规划
在 `C:\works\project\ruoyi-vue-pro-self-demo\gnosis-project` 的 `paramcheck` 下创建：

```text
paramcheck
├── config
│   ├── LiteFlowConfig.java          // LiteFlow 2.11.3 配置 (规则来源)
│   ├── DataSourceConfig.java        // gnosis_sample 专属数据源
│   └── JsonPathConfig.java
├── domain
│   ├── ValidationFlow.java          // 实体类 (映射 sys_validation_flows)
│   ├── ValidationResult.java        // 校验结果 (success/fail)
│   └── ValidationContext.java       // 上下文 (含 rawRequest, parsedData)
├── repository
│   ├── FlowConfigRepository.java    // 读取 DB 配置
│   └── LogRepository.java           // 写入日志
├── handler                          // 【核心】自定义接口拓展
│   ├── IValidationHandler.java      // 接口定义
│   ├── ValidationHandler.java       // 注解定义
│   ├── HandlerRegistry.java         // 注册中心
│   └── impl
│       ├── OrderBusinessHandler.java// 示例：订单复杂逻辑
│       └── UserRegisterHandler.java // 示例：用户注册逻辑
├── component                        // 【核心】LiteFlow 组件
│   ├── ParseParamComponent.java     // 节点 ID: parse_param
│   ├── BasicRuleComponent.java      // 节点 ID: check_format
│   └── DbQueryComponent.java        // 节点 ID: check_db_user (动态 SQL)
├── service
│   ├── DynamicValidationService.java// 统一入口 (路由 FLOW/HANDLER/HYBRID)
│   └── FlowRefreshService.java      // 热加载 (轮询 DB)
├── aspect
│   └── ParamCheckAspect.java        // AOP 拦截 @ParamCheck
├── annotation
│   └── ParamCheck.java              // 方法注解
└── exception
    └── ValidationException.java     // 自定义异常
```

### 4.2 核心代码实现要求

#### A. 自定义接口拓展机制 (`handler` 包)

**1. 接口定义**
```java
package paramcheck.handler;

import paramcheck.domain.ValidationContext;
import paramcheck.domain.ValidationResult;

public interface IValidationHandler {
    ValidationResult validate(ValidationContext context);
    default String getCode() { return this.getClass().getSimpleName(); }
}
```

**2. 注解定义**
```java
package paramcheck.handler;

import org.springframework.stereotype.Component;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ValidationHandler {
    String value(); // 唯一编码，对应 DB 配置中的 handler_code
}
```

**3. 注册中心 (`HandlerRegistry`)**
```java
package paramcheck.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class HandlerRegistry {
    private final Map<String, IValidationHandler> handlerMap = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ValidationHandler.class);
        for (Object bean : beans.values()) {
            if (bean instanceof IValidationHandler) {
                IValidationHandler handler = (IValidationHandler) bean;
                ValidationHandler annotation = bean.getClass().getAnnotation(ValidationHandler.class);
                handlerMap.put(annotation.value(), handler);
                System.out.println("[ParamCheck] Registered Handler: " + annotation.value());
            }
        }
    }

    public IValidationHandler getHandler(String code) {
        IValidationHandler handler = handlerMap.get(code);
        if (handler == null) {
            throw new RuntimeException("ValidationHandler not found: " + code);
        }
        return handler;
    }
}
```

**4. 示例实现 (`OrderBusinessHandler`)**
```java
package paramcheck.handler.impl;

import paramcheck.handler.IValidationHandler;
import paramcheck.handler.ValidationHandler;
import paramcheck.domain.ValidationContext;
import paramcheck.domain.ValidationResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.jayway.jsonpath.JsonPath;

@ValidationHandler("OrderBusinessHandler")
public class OrderBusinessHandler implements IValidationHandler {

    @Autowired
    private JdbcTemplate gnosisJdbcTemplate; // 指向 gnosis_sample

    @Override
    public ValidationResult validate(ValidationContext context) {
        Object rawData = context.getData("rawRequest");
        
        // 示例：复杂库存校验 (Java 代码实现，无存储过程)
        String skuId = JsonPath.read(rawData, "$.items[0].skuId");
        Integer count = gnosisJdbcTemplate.queryForObject(
            "SELECT COUNT(1) FROM gnosis_sample.product_stock WHERE sku_id = ? AND stock_qty > 0", 
            Integer.class, 
            skuId
        );
        
        if (count == null || count == 0) {
            return ValidationResult.fail("STOCK_NOT_ENOUGH", "商品库存不足 (SKU: " + skuId + ")");
        }
        
        // 示例：检查重复提交
        String orderNo = JsonPath.read(rawData, "$.orderNo");
        // ... 更多逻辑
        
        return ValidationResult.success();
    }
}
```

#### B. LiteFlow 组件实现 (`component` 包)
*注意：LiteFlow 2.11.3 使用 `@LiteflowComponent` 或默认命名策略。*

**1. `ParseParamComponent.java`**
```java
package paramcheck.component;

import com.yomahub.liteflow.core.NodeComponent;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.springframework.stereotype.Component;

@Component("parse_param")
public class ParseParamComponent extends NodeComponent {
    @Override
    public void process() {
        Object rawData = getContext().getData("rawRequest");
        String jsonPathExpr = this.getConfigValue("json_path", "$");
        
        DocumentContext context = JsonPath.parse(rawData);
        Object extracted = context.read(jsonPathExpr);
        
        getContext().setData("parsedData", extracted);
        getContext().setData("jsonContext", context);
    }
}
```

**2. `DbQueryComponent.java` (动态 SQL 校验)**
```java
package paramcheck.component;

import com.yomahub.liteflow.core.NodeComponent;
import paramcheck.exception.ValidationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.jayway.jsonpath.JsonPath;
import java.util.List;

@Component("check_db_user") // 节点 ID 需与配置匹配，或通过通用组件动态处理
public class DbQueryComponent extends NodeComponent {
    
    @Autowired
    private JdbcTemplate gnosisJdbcTemplate;

    @Override
    public void process() {
        // 从 LiteFlow 配置获取参数 (需在 Service 层将 DB 配置注入到 LiteFlow 上下文或配置中心)
        // 此处简化：假设配置已通过某种方式传入，实际项目中建议通过 Context 传递配置详情
        String sql = this.getConfigValue("sql"); 
        String paramPath = this.getConfigValue("param_path");
        String errorMsg = this.getConfigValue("msg", "DB Validation Failed");
        
        if (sql == null) return; // 若无 SQL 配置则跳过

        Object rawData = getContext().getData("rawRequest");
        Object paramValue = JsonPath.read(rawData, paramPath);

        boolean exists = false;
        try {
            Integer result = gnosisJdbcTemplate.queryForObject(sql, Integer.class, paramValue);
            exists = (result != null && result > 0);
        } catch (Exception e) {
            exists = false;
        }

        if (!exists) {
            throw new ValidationException(errorMsg);
        }
    }
}
```
*注：为了灵活性，实际开发中可创建一个通用的 `DynamicDbCheckComponent`，在 `component_config` 中定义多个 SQL 规则，由该组件循环执行。*

#### C. 统一服务入口 (`DynamicValidationService`)

```java
package paramcheck.service;

import paramcheck.domain.*;
import paramcheck.handler.HandlerRegistry;
import paramcheck.repository.FlowConfigRepository;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.flow.LiteflowResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DynamicValidationService {

    @Autowired
    private FlowConfigRepository flowConfigRepository;
    
    @Autowired
    private HandlerRegistry handlerRegistry;
    
    @Autowired
    private FlowExecutor flowExecutor;

    @Transactional(rollbackFor = Exception.class)
    public ValidationResult execute(String flowId, Object requestData) {
        ValidationFlow flow = flowConfigRepository.findById(flowId)
            .orElseThrow(() -> new RuntimeException("Flow not found: " + flowId));

        if (!flow.getIsActive()) {
            return ValidationResult.success();
        }

        ValidationContext context = new ValidationContext();
        context.setData("rawRequest", requestData);
        context.setData("flowId", flowId);

        String mode = flow.getModeType();
        
        try {
            if ("HANDLER".equals(mode)) {
                return executeHandler(flow.getHandlerCode(), context);
            } else if ("FLOW".equals(mode)) {
                return executeLiteFlow(flow.getFlowId(), context);
            } else if ("HYBRID".equals(mode)) {
                // 混合模式：先 Flow 后 Handler
                ValidationResult flowResult = executeLiteFlow(flow.getFlowId(), context);
                if (!flowResult.isSuccess()) return flowResult;
                return executeHandler(flow.getHandlerCode(), context);
            } else {
                throw new IllegalArgumentException("Unknown mode: " + mode);
            }
        } catch (Exception e) {
            logFailure(flowId, context, e.getMessage());
            return ValidationResult.fail("SYSTEM_ERROR", e.getMessage());
        }
    }

    private ValidationResult executeHandler(String code, ValidationContext context) {
        return handlerRegistry.getHandler(code).validate(context);
    }

    private ValidationResult executeLiteFlow(String flowId, ValidationContext context) {
        // LiteFlow 2.11.3 执行方式
        LiteflowResponse response = flowExecutor.execute2Resp(flowId, context);
        if (!response.isSuccess()) {
            throw new RuntimeException(response.getErrMsg());
        }
        return ValidationResult.success();
    }
    
    private void logFailure(String flowId, ValidationContext context, String msg) {
        // 实现日志入库逻辑
    }
}
```

---

## 5. 多场景示例说明

### 场景 1：电商订单创建 (混合模式 HYBRID)
*   **需求**：校验订单号格式 (正则)、用户状态 (DB 查询)、库存锁定 (复杂逻辑)。
*   **配置**：
    *   `mode_type`: `HYBRID`
    *   `el_expression`: `THEN(parse_param, check_format, check_db_user)`
    *   `handler_code`: `OrderBusinessHandler`
*   **执行流**：
    1.  `parse_param`: 提取 JSON。
    2.  `check_format`: 校验订单号 `^ORD\d{10}$`。
    3.  `check_db_user`: SQL 查询用户状态。
    4.  `OrderBusinessHandler`: Java 代码执行库存预占检查、防重提交检查。

### 场景 2：新用户注册 (纯接口模式 HANDLER)
*   **需求**：极度复杂的密码强度算法、第三方黑名单 API 调用。
*   **配置**：
    *   `mode_type`: `HANDLER`
    *   `handler_code`: `UserRegisterHandler`
    *   `el_expression`: `NULL`
*   **执行流**：
    1.  直接调用 `UserRegisterHandler.validate()`。
    2.  Handler 内部执行密码复杂度计算、调用外部 HTTP 接口。

### 场景 3：基础数据录入 (纯编排模式 FLOW)
*   **需求**：仅需校验邮箱格式、手机号格式。
*   **配置**：
    *   `mode_type`: `FLOW`
    *   `el_expression`: `THEN(parse_param, check_format)`
    *   `handler_code`: `NULL`
*   **执行流**：
    1.  `parse_param`: 提取数据。
    2.  `check_format`: 校验邮箱、手机号正则。
    3.  结束。

---

## 6. 验收标准

1.  **代码合规**：
    *   **零存储过程**：确认无 `CallableStatement` 或 `CREATE FUNCTION` 调用。
    *   **Schema 隔离**：SQL 显式带 `gnosis_sample.` 前缀。
    *   **版本匹配**：LiteFlow 使用 2.11.3，API 调用符合该版本规范。
2.  **功能验证**：
    *   **FLOW 模式**：修改 DB 中的 EL 表达式，重启或不重启（视热加载实现）后生效。
    *   **HANDLER 模式**：新增一个 `@ValidationHandler` 实现类，配置 DB 后可成功调用。
    *   **HYBRID 模式**：流程前半段失败则不执行 Handler；前半段成功则继续执行 Handler。
3.  **环境连接**：
    *   成功连接 `localserver.gnosis:5432` (openGauss 3.0.0)。
    *   数据正确写入 `gnosis_sample` schema。

---

## 7. 给 Agent 的最终执行 Prompt

> "请基于以下严格约束，在 `C:\works\project\ruoyi-vue-pro-self-demo\gnosis-project` 项目中开发参数动态校验模块。
>
> **核心约束**：
> 2.  **版本要求**：
>     *   **LiteFlow**: **2.11.3** (注意 API 差异，如 `FlowExecutor.execute2Resp`)。
>     *   **openGauss**: **3.0.0** (JDBC 驱动需兼容)。
> 3.  **数据库**：连接 `jdbc:opengauss://localserver.gnosis:5432/gnosis_sample` (user: gaussdb, pass: Enmotech@123)。所有 SQL 必须显式指定 `gnosis_sample.` schema。
> 4.  **禁止存储过程**：所有逻辑（包括复杂校验）必须用 **Java 代码** 实现，通过 `JdbcTemplate` 执行动态 SQL，**严禁**调用存储过程、函数或触发器。
> 5.  **双模引擎**：
>     *   支持 **LiteFlow 编排模式** (解析 DB 中的 EL 表达式)。
>     *   支持 **自定义接口拓展模式**：定义 `IValidationHandler` 接口和 `@ValidationHandler` 注解，支持动态注册和调用。
>     *   支持 **混合模式** (先 Flow 后 Handler)。
>
> **交付内容**：
> 1.  **SQL 脚本**：创建 `sys_validation_flows` (含 `mode_type`, `handler_code` 字段) 和 `sys_validation_logs`，插入涵盖 FLOW, HANDLER, HYBRID 三种模式的测试数据。
> 2.  **Java 代码**：
>     *   `config`: 数据源配置, LiteFlow 2.11.3 配置。
>     *   `handler`: `IValidationHandler`, `@ValidationHandler`, `HandlerRegistry`, 以及两个示例实现 (`OrderBusinessHandler`, `UserRegisterHandler`)。
>     *   `component`: `ParseParamComponent` (JSONPath), `BasicRuleComponent`, `DbQueryComponent` (动态 SQL)。
>     *   `service`: `DynamicValidationService` (实现三种模式路由), `FlowRefreshService`。
>     *   `aspect`: `ParamCheckAspect` 和 `@ParamCheck` 注解。
>     *   `domain`: 实体类与上下文对象。
> 3.  **测试用例**：JUnit 测试类，分别模拟三种场景（订单创建、用户注册、基础录入），验证路由逻辑、JSONPath 解析、DB 查询及自定义 Handler 的执行情况。
>
> 请开始生成完整的 SQL 和 Java 代码。"