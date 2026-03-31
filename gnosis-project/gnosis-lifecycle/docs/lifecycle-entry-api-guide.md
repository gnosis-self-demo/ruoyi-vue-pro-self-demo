# 业务生命周期入口管理 API 使用指南

## 概述

本模块提供业务生命周期入口的注册、管理和校验功能。

## 基础路径

```
/api/lifecycle/entry
```

## API 接口列表

### 1. 注册业务入口

**接口**: `POST /api/lifecycle/entry/register`

**描述**: 创建新的业务入口

**请求体**:
```json
{
  "name": "订单管理",
  "code": "ORDER_MANAGE",
  "description": "订单管理业务类型",
  "entryPermissionConfig": "{\"roles\":[\"ADMIN\",\"USER\"],\"disabled\":false}"
}
```

**响应示例**:
```json
{
  "id": "123456",
  "name": "订单管理",
  "code": "ORDER_MANAGE",
  "description": "订单管理业务类型",
  "entryPermissionConfig": "{\"roles\":[\"ADMIN\",\"USER\"],\"disabled\":false}",
  "createUserId": "system",
  "updateUserId": "system",
  "createTime": "2026-03-27 15:00:00",
  "updateTime": "2026-03-27 15:00:00"
}
```

---

### 2. 获取业务入口列表（分页）

**接口**: `GET /api/lifecycle/entry/list`

**描述**: 分页查询业务入口列表

**请求参数**:
- `name` (可选): 业务类型名称模糊查询
- `code` (可选): 业务类型编码精确查询
- `pageNum` (可选，默认 1): 页码
- `pageSize` (可选，默认 10): 每页大小

**请求示例**:
```
GET /api/lifecycle/entry/list?name=订单&pageNum=1&pageSize=10
```

**响应示例**:
```json
[
  {
    "id": "123456",
    "name": "订单管理",
    "code": "ORDER_MANAGE",
    "description": "订单管理业务类型",
    "entryPermissionConfig": "{\"roles\":[\"ADMIN\",\"USER\"],\"disabled\":false}",
    "createUserId": "system",
    "updateUserId": "system",
    "createTime": "2026-03-27 15:00:00",
    "updateTime": "2026-03-27 15:00:00"
  }
]
```

---

### 3. 获取业务入口详情

**接口**: `GET /api/lifecycle/entry/{id}`

**描述**: 根据 ID 获取业务入口详细信息

**路径参数**:
- `id`: 业务类型 ID

**请求示例**:
```
GET /api/lifecycle/entry/123456
```

**响应示例**:
```json
{
  "id": "123456",
  "name": "订单管理",
  "code": "ORDER_MANAGE",
  "description": "订单管理业务类型",
  "entryPermissionConfig": "{\"roles\":[\"ADMIN\",\"USER\"],\"disabled\":false}",
  "createUserId": "system",
  "updateUserId": "system",
  "createTime": "2026-03-27 15:00:00",
  "updateTime": "2026-03-27 15:00:00"
}
```

---

### 4. 更新业务入口

**接口**: `PUT /api/lifecycle/entry/{id}`

**描述**: 更新指定 ID 的业务入口信息

**路径参数**:
- `id`: 业务类型 ID

**请求体**:
```json
{
  "name": "订单管理 (更新)",
  "description": "更新后的订单管理业务类型",
  "entryPermissionConfig": "{\"roles\":[\"ADMIN\",\"USER\",\"MANAGER\"],\"disabled\":false}"
}
```

**响应示例**:
```json
{
  "id": "123456",
  "name": "订单管理 (更新)",
  "code": "ORDER_MANAGE",
  "description": "更新后的订单管理业务类型",
  "entryPermissionConfig": "{\"roles\":[\"ADMIN\",\"USER\",\"MANAGER\"],\"disabled\":false}",
  "createUserId": "system",
  "updateUserId": "system",
  "createTime": "2026-03-27 15:00:00",
  "updateTime": "2026-03-27 15:30:00"
}
```

---

### 5. 删除业务入口

**接口**: `DELETE /api/lifecycle/entry/{id}`

**描述**: 删除指定 ID 的业务入口

**路径参数**:
- `id`: 业务类型 ID

**请求示例**:
```
DELETE /api/lifecycle/entry/123456
```

**响应示例**:
```
200 OK
```

---

### 6. 批量删除业务入口

**接口**: `DELETE /api/lifecycle/entry/batch`

**描述**: 批量删除多个业务入口

**请求体**:
```json
["123456", "123457", "123458"]
```

**响应示例**:
```json
{
  "success": true,
  "message": "批量删除成功",
  "count": 3
}
```

---

### 7. 批量启用业务入口

**接口**: `POST /api/lifecycle/entry/batch/enable`

**描述**: 批量启用多个业务入口

**请求体**:
```json
["123456", "123457", "123458"]
```

**响应示例**:
```json
{
  "success": true,
  "message": "批量启用成功",
  "count": 3
}
```

---

### 8. 批量禁用业务入口

**接口**: `POST /api/lifecycle/entry/batch/disable`

**描述**: 批量禁用多个业务入口

**请求体**:
```json
["123456", "123457", "123458"]
```

**响应示例**:
```json
{
  "success": true,
  "message": "批量禁用成功",
  "count": 3
}
```

---

### 9. 校验入口数据

**接口**: `POST /api/lifecycle/entry/{id}/validate`

**描述**: 校验用户是否有权限访问该业务入口

**路径参数**:
- `id`: 业务类型 ID

**请求体**:
```json
{
  "userId": "user123",
  "userRoles": ["ADMIN", "USER"],
  "validationData": {
    "field1": "value1",
    "field2": "value2"
  }
}
```

**响应示例 (通过)**:
```json
{
  "passed": true,
  "message": "入口校验通过",
  "errorCode": "SUCCESS",
  "businessTypeName": "订单管理",
  "businessTypeCode": "ORDER_MANAGE"
}
```

**响应示例 (失败)**:
```json
{
  "passed": false,
  "message": "用户角色不满足要求，需要角色：[\"ADMIN\"]",
  "errorCode": "PERMISSION_DENIED",
  "businessTypeName": "订单管理",
  "businessTypeCode": "ORDER_MANAGE"
}
```

---

## 权限配置说明

`entryPermissionConfig` 字段支持以下配置项：

### 角色限制

```json
{
  "roles": ["ADMIN", "USER"]
}
```

表示只有拥有 ADMIN 或 USER 角色的用户才能访问。

### 用户白名单

```json
{
  "users": ["user123", "user456"]
}
```

表示只有指定用户才能访问。

### 禁用状态

```json
{
  "disabled": true
}
```

表示禁用该业务入口。

### 组合配置

```json
{
  "roles": ["ADMIN", "USER"],
  "users": ["user123"],
  "disabled": false
}
```

表示：未禁用，需要 ADMIN 或 USER 角色，或者是 user123 用户。

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| SUCCESS | 成功 |
| VALIDATION_FAILED | 校验失败 |
| VALIDATION_DATA_EMPTY | 校验数据为空 |
| FIELD_EMPTY | 字段为空 |
| PERMISSION_DENIED | 权限不足 |
| ENTRY_DISABLED | 入口已禁用 |
| PERMISSION_CHECK_ERROR | 权限检查异常 |
| BUSINESS_TYPE_NOT_FOUND | 业务类型不存在 |
| VALIDATION_ERROR | 校验异常 |

---

## 使用示例 (cURL)

### 注册业务入口

```bash
curl -X POST http://localhost:8080/api/lifecycle/entry/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "订单管理",
    "code": "ORDER_MANAGE",
    "description": "订单管理业务类型",
    "entryPermissionConfig": "{\"roles\":[\"ADMIN\",\"USER\"],\"disabled\":false}"
  }'
```

### 获取列表

```bash
curl -X GET "http://localhost:8080/api/lifecycle/entry/list?pageNum=1&pageSize=10"
```

### 校验入口

```bash
curl -X POST http://localhost:8080/api/lifecycle/entry/123456/validate \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "user123",
    "userRoles": ["ADMIN"],
    "validationData": {
      "orderNo": "ORD123456",
      "amount": 100.00
    }
  }'
```

### 批量禁用

```bash
curl -X POST http://localhost:8080/api/lifecycle/entry/batch/disable \
  -H "Content-Type: application/json" \
  -d '["123456", "123457"]'
```

---

## 数据库初始化

执行以下 SQL 文件初始化数据库表：

```bash
psql -U gaussdb -d gnosis_sample -f src/main/resources/db/lifecycle-entry-opengauss.sql
```

或者手动执行 SQL 文件中的内容。

---

## 注意事项

1. 所有 API 都需要先初始化数据库表
2. `entryPermissionConfig` 字段必须是有效的 JSON 格式
3. 业务类型编码（code）必须唯一
4. 删除操作会级联删除相关数据，请谨慎操作
5. 当前版本用户 ID 硬编码为 "system"，实际项目需要从安全上下文获取
