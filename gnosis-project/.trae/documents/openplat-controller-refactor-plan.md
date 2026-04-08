# Openplat Controller 重构计划

## 重构目标

将 `gnosis-openplat` 模块下所有 Controller 统一重构为：
1. **全部使用 POST 请求**（不遵循 REST 规范）
2. **参数统一用对象包装**（即使只有一个参数，也必须用对象包装）
3. **响应保持 CommonResponse 包装**

## 现状分析

### 当前问题
| 问题类型 | 具体表现 |
|---------|---------|
| 使用了 GET 请求 | `list()`, `getById(@PathVariable)`, `getSystemApis(@PathVariable)`, `checkApiConfig(@RequestParam)` |
| 使用了 PUT 请求 | `update()`, `batchEnable()`, `batchDisable()` |
| 使用了 DELETE 请求 | `delete(@PathVariable)`, `batchDelete()` |
| 参数未包装 | `@PathVariable String id`, `@RequestParam String apiCode`, `String[] ids`, `Map<String, Object>` |

### 涉及的 Controller（共4个）
1. `OpenplatApiConfigController` - API配置管理
2. `OpenplatApiSystemRelationController` - API与系统关系管理
3. `OpenplatSystemController` - 对接系统管理
4. `OpenplatWebhookConfigController` - Webhook配置管理

---

## 实施步骤

### 第一步：创建通用请求 DTO 类

在 `com.gnosis.openplat.dto` 包下创建以下请求对象：

#### 1.1 `IdRequest.java` - 单ID请求包装
```java
@Data
public class IdRequest {
    private String id;
}
```

#### 1.2 `IdsRequest.java` - 批量ID请求包装
```java
@Data
public class IdsRequest {
    private List<String> ids;
}
```

#### 1.3 `PageQueryRequest<T>.java` - 分页查询请求基类（可选，如果需要分页参数）

### 第二步：重构 `OpenplatApiConfigController`

| 原方法 | HTTP | 参数方式 | → 新方法 | HTTP | 参数方式 |
|--------|------|---------|----------|------|---------|
| `list()` | GET | 无参 | `list()` | POST | `@RequestBody OpenplatApiConfig` |
| `list(apiConfig)` | POST | Domain对象 | 合并到上面 | - | - |
| `getById(id)` | GET | @PathVariable | `detail()` | POST | `@RequestBody IdRequest` |
| `save(apiConfig)` | POST | Domain对象 | 保持不变 | POST | `@RequestBody OpenplatApiConfig` |
| `update(apiConfig)` | PUT | Domain对象 | `update()` | POST | `@RequestBody OpenplatApiConfig` |
| `delete(id)` | DELETE | @PathVariable | `delete()` | POST | `@RequestBody IdRequest` |
| `batchDelete(ids)` | DELETE | String[] | `batchDelete()` | POST | `@RequestBody IdsRequest` |
| `batchEnable(ids)` | PUT | String[] | `batchEnable()` | POST | `@RequestBody IdsRequest` |
| `batchDisable(ids)` | PUT | String[] | `batchDisable()` | POST | `@RequestBody IdsRequest` |
| `checkApiConfig(...)` | GET | @RequestParam | `check()` | POST | 专用DTO |
| `checkApiConfigPost(...)` | POST | Map | 删除（合并到上面的check） | - | - |
| `relateSystems(...)` | POST | Map | `relateSystems()` | POST | 专用DTO |
| `unrelateSystems(...)` | POST | Map | `unrelateSystems()` | POST | 专用DTO |

**需要额外创建的专用DTO：**
- `ApiConfigCheckRequest` - 包含 `apiCode`, `apiPath`
- `RelateSystemsRequest` - 包含 `apiId`, `systemIds`(List<String>)

### 第三步：重构 `OpenplatApiSystemRelationController`

| 原方法 | HTTP | 参数方式 | → 新方法 | HTTP | 参数方式 |
|--------|------|---------|----------|------|---------|
| `list(relation)` | GET+POST | Domain对象 | `list()` | POST | `@RequestBody OpenplatApiSystemRelation` |
| `getById(id)` | GET | @PathVariable | `detail()` | POST | `@RequestBody IdRequest` |
| `save(relation)` | POST | Domain对象 | 保持不变 | POST | `@RequestBody OpenplatApiSystemRelation` |
| `update(relation)` | PUT | Domain对象 | `update()` | POST | `@RequestBody OpenplatApiSystemRelation` |
| `delete(id)` | DELETE | @PathVariable | `delete()` | POST | `@RequestBody IdRequest` |
| `batchDelete(ids)` | DELETE | String[] | `batchDelete()` | POST | `@RequestBody IdsRequest` |
| `batchEnable(ids)` | PUT | String[] | `batchEnable()` | POST | `@RequestBody IdsRequest` |
| `batchDisable(ids)` | PUT | String[] | `batchDisable()` | POST | `@RequestBody IdsRequest` |

### 第四步：重构 `OpenplatSystemController`

| 原方法 | HTTP | 参数方式 | → 新方法 | HTTP | 参数方式 |
|--------|------|---------|----------|------|---------|
| `list()` | GET | 无参 | `list()` | POST | `@RequestBody OpenplatSystem` |
| `list(system)` | POST | Domain对象 | 合并到上面 | - | - |
| `getById(id)` | GET | @PathVariable | `detail()` | POST | `@RequestBody IdRequest` |
| `save(system)` | POST | Domain对象 | 保持不变 | POST | `@RequestBody OpenplatSystem` |
| `update(system)` | PUT | Domain对象 | `update()` | POST | `@RequestBody OpenplatSystem` |
| `delete(id)` | DELETE | @PathVariable | `delete()` | POST | `@RequestBody IdRequest` |
| `batchDelete(ids)` | DELETE | String[] | `batchDelete()` | POST | `@RequestBody IdsRequest` |
| `batchEnable(ids)` | PUT | String[] | `batchEnable()` | POST | `@RequestBody IdsRequest` |
| `batchDisable(ids)` | PUT | String[] | `batchDisable()` | POST | `@RequestBody IdsRequest` |
| `getSystemApis(systemId)` | GET | @PathVariable | `getSystemApis()` | POST | `@RequestBody IdRequest` |

### 第五步：重构 `OpenplatWebhookConfigController`

| 原方法 | HTTP | 参数方式 | → 新方法 | HTTP | 参数方式 |
|--------|------|---------|----------|------|---------|
| `list(webhookConfig)` | GET | Domain对象 | `list()` | POST | `@RequestBody OpenplatWebhookConfig` |
| `getById(id)` | GET | @PathVariable | `detail()` | POST | `@RequestBody IdRequest` |
| `save(webhookConfig)` | POST | Domain对象 | 保持不变 | POST | `@RequestBody OpenplatWebhookConfig` |
| `update(webhookConfig)` | PUT | Domain对象 | `update()` | POST | `@RequestBody OpenplatWebhookConfig` |
| `delete(id)` | DELETE | @PathVariable | `delete()` | POST | `@RequestBody IdRequest` |
| `batchDelete(ids)` | DELETE | String[] | `batchDelete()` | POST | `@RequestBody IdsRequest` |
| `batchEnable(ids)` | PUT | String[] | `batchEnable()` | POST | `@RequestBody IdsRequest` |
| `batchDisable(ids)` | PUT | String[] | `batchDisable()` | POST | `@RequestBody IdsRequest` |

---

## 需要新建的文件清单

| 文件路径 | 说明 |
|---------|------|
| `dto/IdRequest.java` | 通用单ID请求包装类 |
| `dto/IdsRequest.java` | 通用批量ID请求包装类 |
| `dto/ApiConfigCheckRequest.java` | API配置检查请求DTO |
| `dto/RelateSystemsRequest.java` | 关联系统请求DTO |

## 需要修改的文件清单

| 文件路径 | 修改内容 |
|---------|---------|
| `controller/OpenplatApiConfigController.java` | 全部方法改为POST + 参数对象化 |
| `controller/OpenplatApiSystemRelationController.java` | 全部方法改为POST + 参数对象化 |
| `controller/OpenplatSystemController.java` | 全部方法改为POST + 参数对象化 |
| `controller/OpenplatWebhookConfigController.java` | 全部方法改为POST + 参数对象化 |

## 重构后的接口规范示例

```java
// 详情查询（原 GET /{id}）
@PostMapping("/detail")
public CommonResponse<OpenplatApiConfig> detail(@RequestBody IdRequest request) {
    return CommonResponse.success(apiConfigService.getById(request.getId()));
}

// 删除（原 DELETE /{id}）
@PostMapping("/delete")
public CommonResponse<String> delete(@RequestBody IdRequest request) {
    apiConfigService.delete(request.getId());
    return CommonResponse.success("删除成功");
}

// 批量删除（原 DELETE /batch，参数为 String[]）
@PostMapping("/batchDelete")
public CommonResponse<String> batchDelete(@RequestBody IdsRequest request) {
    apiConfigService.batchDelete(request.getIds().toArray(new String[0]));
    return CommonResponse.success("批量删除成功");
}
```

## 执行顺序

1. 创建通用请求 DTO（IdRequest, IdsRequest）
2. 创建专用请求 DTO（ApiConfigCheckRequest, RelateSystemsRequest）
3. 重构 OpenplatApiConfigController
4. 重构 OpenplatApiSystemRelationController
5. 重构 OpenplatSystemController
6. 重构 OpenplatWebhookConfigController
