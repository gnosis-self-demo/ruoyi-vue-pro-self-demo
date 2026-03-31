# 业务生命周期入口管理 - 快速参考

## 项目结构

```
src/main/java/paramcheck/lifecycle/
├── entry/                      # 入口控制模块
│   ├── EntryService.java
│   ├── EntryServiceImpl.java
│   ├── EntryController.java
│   ├── BusinessTypeManager.java
│   ├── EntryValidator.java
│   └── PermissionChecker.java
└── dto/                        # 数据传输对象
    ├── BusinessTypeCreateRequest.java
    ├── BusinessTypeUpdateRequest.java
    ├── BusinessTypeVO.java
    ├── EntryValidationRequest.java
    └── EntryValidationResult.java
```

## API 接口速查

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/lifecycle/entry/register` | 注册业务入口 |
| GET | `/api/lifecycle/entry/list` | 获取列表（分页） |
| GET | `/api/lifecycle/entry/{id}` | 获取详情 |
| PUT | `/api/lifecycle/entry/{id}` | 更新业务入口 |
| DELETE | `/api/lifecycle/entry/{id}` | 删除业务入口 |
| DELETE | `/api/lifecycle/entry/batch` | 批量删除 |
| POST | `/api/lifecycle/entry/batch/enable` | 批量启用 |
| POST | `/api/lifecycle/entry/batch/disable` | 批量禁用 |
| POST | `/api/lifecycle/entry/{id}/validate` | 校验入口数据 |

## 请求示例

### 注册业务入口
```json
POST /api/lifecycle/entry/register
{
  "name": "订单管理",
  "code": "ORDER_MANAGE",
  "description": "订单管理业务类型",
  "entryPermissionConfig": "{\"roles\":[\"ADMIN\",\"USER\"],\"disabled\":false}"
}
```

### 校验入口
```json
POST /api/lifecycle/entry/{id}/validate
{
  "userId": "user123",
  "userRoles": ["ADMIN"],
  "validationData": {
    "field1": "value1"
  }
}
```

### 批量操作
```json
POST /api/lifecycle/entry/batch/enable
["id1", "id2", "id3"]
```

## 权限配置格式

```json
{
  "roles": ["ADMIN", "USER"],      // 允许的角色
  "users": ["user123"],            // 允许的用户（可选）
  "disabled": false                // 是否禁用
}
```

## 响应格式

### 成功响应
```json
{
  "passed": true,
  "message": "校验通过",
  "errorCode": "SUCCESS"
}
```

### 失败响应
```json
{
  "passed": false,
  "message": "权限不足",
  "errorCode": "PERMISSION_DENIED"
}
```

## 错误码

| 错误码 | 说明 |
|--------|------|
| SUCCESS | 成功 |
| PERMISSION_DENIED | 权限不足 |
| ENTRY_DISABLED | 入口已禁用 |
| VALIDATION_DATA_EMPTY | 数据为空 |
| FIELD_EMPTY | 字段为空 |
| BUSINESS_TYPE_NOT_FOUND | 业务类型不存在 |

## 数据库表

**表名**: `lifecycle_business_type`

**字段**:
- `id` - 主键
- `name` - 业务类型名称
- `code` - 业务类型编码（唯一）
- `description` - 描述
- `entry_permission_config` - 权限配置（JSON）
- `create_user_id` - 创建人 ID
- `update_user_id` - 更新人 ID
- `create_time` - 创建时间
- `update_time` - 更新时间

## 快速启动

1. **初始化数据库**
```bash
psql -U gaussdb -d gnosis_sample -f src/main/resources/db/lifecycle-entry-opengauss.sql
```

2. **启动应用**
```bash
mvn spring-boot:run
```

3. **访问 Swagger**
```
http://localhost:8080/swagger-ui.html
```

## 编译状态

✅ 编译成功 - 136 个源文件

## 详细文档

- API 使用指南：`docs/lifecycle-entry-api-guide.md`
- 开发总结：`docs/entry-module-summary.md`
