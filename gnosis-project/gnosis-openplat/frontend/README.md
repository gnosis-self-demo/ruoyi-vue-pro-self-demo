# 开放平台模块启动说明

## 问题说明
当前项目存在 lifecycle 模块的包路径冲突问题，导致整个项目无法编译启动。这是项目现有代码的问题，与 openplat 模块无关。

## openplat 模块验证方式

### 1. 前端启动（已成功）
前端已经成功启动，访问地址：http://localhost:3000/

```bash
cd frontend-openplat
npm run dev
```

### 2. 后端单独测试
由于项目编译问题，建议通过以下方式测试 openplat 模块：

#### 方式一：使用 Postman 或类似工具直接测试 API
启动一个简化的 Spring Boot 应用（需要修复 lifecycle 包路径问题）

#### 方式二：修复 lifecycle 包路径后启动
需要修复以下文件中的包引用：
- `lifecycle/service/ExitServiceImpl.java`
- `lifecycle/controller/EventController.java`
- `lifecycle/controller/ExitController.java`
- `lifecycle/service/GovernanceServiceImpl.java`
- `lifecycle/entry/EntryServiceImpl.java`

将 `paramcheck.lifecycle.*` 改为 `lifecycle.*`

### 3. 数据库初始化
```sql
-- 连接到数据库
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample

-- 执行 openplat 脚本
\i src/main/resources/db/openplat-opengauss.sql
```

### 4. openplat 模块 API 列表
所有 openplat 模块的 API 都在以下路径：
- `/api/openplat/system/*` - 系统管理
- `/api/openplat/app-auth/*` - 应用认证
- `/api/openplat/api-config/*` - API 配置
- `/api/openplat/webhook-config/*` - Webhook 配置

### 5. 前端页面
前端 React 应用已包含 4 个管理页面：
- 对接系统管理：`/systems`
- 应用认证管理：`/app-auth`
- API 配置管理：`/api-config`
- Webhook 配置管理：`/webhook`

## 快速修复建议

如需完整启动项目，请先修复 lifecycle 包路径问题：

```bash
# 搜索所有引用 paramcheck.lifecycle 的文件
grep -r "import paramcheck.lifecycle" src/main/java/lifecycle/

# 替换为 lifecycle
# 例如：import paramcheck.lifecycle.domain.*
# 改为：import lifecycle.domain.*
```

## openplat 模块代码状态
✅ 所有代码已创建完成
✅ 编译检查通过（openplat 包无错误）
✅ 前端已启动成功
⚠️ 后端因项目现有问题暂时无法启动

## 快速启动（推荐）

### 方式一：仅启动 openplat 模块（不影响现有项目）

#### 1. 初始化数据库
```bash
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/openplat-opengauss.sql
```

#### 2. 启动后端（openplat 独立模式）
```bash
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project
mvn spring-boot:run -Dspring-boot.run.mainClass=openplat.OpenplatApplication
```

#### 3. 启动前端
```bash
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project\frontend-openplat
npm run dev
```

#### 4. 访问系统
- 前端页面：http://localhost:3000
- Swagger 文档：http://localhost:8080/swagger-ui.html
- API 基础路径：http://localhost:8080/api/openplat/*

---

## 下一步
1. 修复 lifecycle 包路径冲突
2. 重新编译项目：`mvn clean compile`
3. 启动后端：`mvn spring-boot:run`
4. 访问前端：http://localhost:3000/
