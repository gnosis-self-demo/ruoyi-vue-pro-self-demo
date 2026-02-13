# LiteFlow动态切面使用指南

## 1. 功能概述

本功能实现了基于数据库配置的动态方法拦截和逻辑替换，通过LiteFlow流程引擎实现业务逻辑的动态编排和替换。

### 核心特性
- **动态配置**：通过数据库表配置需要拦截的方法和对应的LiteFlow流程
- **零代码侵入**：无需修改原有业务代码，通过配置即可实现逻辑替换
- **灵活编排**：支持复杂的流程编排，可组合多个组件实现复杂业务逻辑
- **实时生效**：配置修改后立即生效，无需重启应用
- **安全回退**：LiteFlow执行失败时自动回退到原方法逻辑
- **重载方法支持**：正确处理方法重载场景
- **性能优化**：只拦截业务包下的方法，避免影响系统性能
- **缓存管理**：提供API接口管理配置缓存

## 2. 数据库表结构

### 2.1 LiteFlow规则表 (`liteflow_sys_rule`)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| chain_name | VARCHAR(100) | 流程链名称 |
| rule_content | TEXT | 规则内容 (Script/XML/JSON/YML格式) |
| rule_type | VARCHAR(20) | 规则类型，默认'script' |
| enabled | BOOLEAN | 是否启用 |
| version | INTEGER | 版本号 |
| description | VARCHAR(500) | 描述 |

### 2.2 方法拦截配置表 (`liteflow_sys_method_intercept`)
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| target_class | VARCHAR(200) | 目标类全路径 |
| target_method | VARCHAR(200) | 目标方法名（可包含参数类型签名） |
| chain_name | VARCHAR(100) | 关联的流程链名称 |
| enabled | BOOLEAN | 是否启用 |
| description | VARCHAR(500) | 描述 |

## 3. 使用示例

### 3.1 场景描述
假设我们有以下业务场景：
- **方法A** 调用 **方法B**
- 我们希望在配置命中方法A后，替换方法B的逻辑为LiteFlow组件

### 3.2 原始代码
```java
@Service
public class ExampleBusinessService {
    
    public String methodA(String input) {
        System.out.println("执行原方法A: " + input);
        // 调用方法B
        String result = methodB(input);
        return "方法A结果: " + result;
    }
    
    public String methodB(String input) {
        System.out.println("执行原方法B: " + input);
        return "原方法B处理结果: " + input;
    }
    
    // 重载方法示例
    public String methodB(String input, int count) {
        System.out.println("执行重载方法B: " + input + ", count=" + count);
        return "原重载方法B处理结果: " + input + " x" + count;
    }
}
```

### 3.3 配置步骤

#### 步骤1：创建LiteFlow规则
插入规则到 `liteflow_sys_rule` 表：
```sql
INSERT INTO liteflow_sys_rule (
    chain_name, 
    rule_content, 
    rule_type, 
    enabled, 
    description
) VALUES (
    'replaceMethodBChain',
    'return "LiteFlow组件处理结果: " + originalArgs[0].toUpperCase();',
    'script',
    true,
    '替换方法B逻辑的脚本规则'
);
```

#### 步骤2：配置方法拦截
插入拦截配置到 `liteflow_sys_method_intercept` 表：
```sql
INSERT INTO liteflow_sys_method_intercept (
    target_class,
    target_method,
    chain_name,
    enabled,
    description
) VALUES (
    'gnosis.sample.dynamic.liteflow.example.ExampleBusinessService',
    'methodB',
    'replaceMethodBChain',
    true,
    '拦截方法B并替换为LiteFlow组件'
);
```

#### 步骤3：处理重载方法
对于重载方法，可以使用完整方法签名：
```sql
INSERT INTO liteflow_sys_method_intercept (
    target_class,
    target_method,
    chain_name,
    enabled,
    description
) VALUES (
    'gnosis.sample.dynamic.liteflow.example.ExampleBusinessService',
    'methodB(String,int)',
    'replaceMethodBChain',
    true,
    '拦截重载方法B并替换为LiteFlow组件'
);
```

### 3.4 执行效果

**调用前**：
```java
ExampleBusinessService service = new ExampleBusinessService();
String result = service.methodA("hello");
// 输出: "方法A结果: 原方法B处理结果: hello"
```

**配置后**：
```java
ExampleBusinessService service = new ExampleBusinessService();
String result = service.methodA("hello");
// 输出: "方法A结果: LiteFlow组件处理结果: HELLO"
```

## 4. REST API接口

### 4.1 规则管理接口
- **GET /liteflow/business-aspect/rules** - 获取所有启用的规则
- **POST /liteflow/business-aspect/rules** - 保存或更新规则
- **DELETE /liteflow/business-aspect/rules/{id}** - 删除规则
- **PUT /liteflow/business-aspect/rules/{id}/status** - 启用/禁用规则

### 4.2 拦截配置管理接口
- **GET /liteflow/business-aspect/intercepts** - 获取所有启用的拦截配置
- **POST /liteflow/business-aspect/intercepts** - 保存或更新拦截配置
- **DELETE /liteflow/business-aspect/intercepts/{id}** - 删除拦截配置
- **PUT /liteflow/business-aspect/intercepts/{id}/status** - 启用/禁用拦截配置
- **POST /liteflow/business-aspect/cache/refresh** - 刷新配置缓存

## 5. 配置示例

### 5.1 Script规则示例（默认）
```javascript
// 简单脚本规则
return "处理结果: " + originalArgs[0];

// 复杂脚本规则
String input = (String) originalArgs[0];
if (input.length() > 10) {
    return "长字符串处理: " + input.toUpperCase();
} else {
    return "短字符串处理: " + input.toLowerCase();
}
```

### 5.2 XML格式规则示例
```xml
<flow>
    <chain name="complexProcess">
        <when value="conditionCheck">
            <then value="normalProcess"/>
        </when>
        <otherwise>
            <then value="exceptionProcess"/>
        </otherwise>
    </chain>
</flow>
```

## 6. 注意事项

1. **性能影响**：切面只拦截`gnosis.sample`包下的业务方法，避免影响框架和系统方法
2. **重载方法**：支持方法重载，可通过完整方法签名进行精确匹配
3. **参数传递**：原始方法参数通过 `originalArgs` 传递给LiteFlow上下文
4. **返回值**：LiteFlow组件必须通过 `context.setData("result", returnValue)` 设置返回值
5. **异常处理**：LiteFlow执行异常时会自动回退到原方法逻辑
6. **缓存机制**：拦截配置有内存缓存，可通过API刷新缓存
7. **默认规则类型**：默认使用Script规则类型，支持Java代码直接编写

## 7. 最佳实践

1. **命名规范**：流程链名称建议使用业务相关的有意义名称
2. **版本管理**：利用version字段进行规则版本管理
3. **测试验证**：在生产环境使用前，先在测试环境验证配置正确性
4. **监控日志**：关注切面执行日志，及时发现性能问题
5. **备份策略**：定期备份配置表，防止配置丢失
6. **缓存管理**：配置修改后记得刷新缓存，确保配置生效

## 8. 故障排查

### 8.1 常见问题
- **配置不生效**：检查enabled字段是否为true，检查target_class和target_method是否正确
- **LiteFlow执行失败**：检查规则语法是否正确，检查组件是否存在
- **性能下降**：减少不必要的方法拦截配置
- **缓存未更新**：配置修改后调用缓存刷新API

### 8.2 调试方法
- 开启DEBUG日志：`logging.level.gnosis.sample.dynamic.liteflow.aspect=DEBUG`
- 查看拦截日志：切面会记录所有拦截和执行情况
- 使用API接口验证配置：通过REST API查询当前生效的配置
- 调用缓存刷新API：确保配置变更生效