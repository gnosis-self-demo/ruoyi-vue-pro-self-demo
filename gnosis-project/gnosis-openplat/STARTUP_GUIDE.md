# 前后端启动说明

## 问题总结

已修复以下问题：

### 后端问题
1. ✅ **MyBatis 包路径错误**
   - 文件：`application.properties`
   - 修复：`mybatis.type-aliases-package=com.gnosis.openplat.domain`

2. ✅ **Mapper XML 文件包路径错误**
   - 文件：所有 `mapper/openplat/*.xml` 文件
   - 修复：将 `openplat.domain.*` 改为 `com.gnosis.openplat.domain.*`
   - 修复：将 `openplat.mapper.*` 改为 `com.gnosis.openplat.mapper.*`

3. ✅ **启动类包扫描路径错误**
   - 文件：`OpenplatApplication.java`
   - 修复：`@ComponentScan(basePackages = {"com.gnosis.openplat"})`

4. ✅ **数据库驱动配置**
   - 文件：`application.properties`
   - 修复：使用 openGauss 驱动 `org.opengauss.Driver`
   - 修复：数据库 URL `jdbc:opengauss://localserver.gnosis:5432/gnosis_sample`

### 前端问题
1. ✅ **代理配置端口错误**
   - 文件：`frontend/vite.config.js`
   - 修复：将 `target: 'http://localhost:8080'` 改为 `target: 'http://localhost:8083'`

## 启动步骤

### 1. 初始化数据库（首次启动需要）

**重要：** 在第一次启动前，需要先执行数据库建表脚本。

方法一：使用数据库客户端工具
1. 使用 DBeaver、DataGrip 等工具连接数据库
   - 主机：localserver.gnosis
   - 端口：5432
   - 数据库：gnosis_sample
   - 用户：gaussdb
   - 密码：Enmotech@123

2. 执行建表脚本
   - 文件位置：`src/main/resources/db/openplat/openplat-opengauss.sql`

方法二：使用命令行工具
```bash
# 如果有 psql 或 gscli 工具
psql -h localserver.gnosis -U gaussdb -d gnosis_sample -f src/main/resources/db/openplat/openplat-opengauss.sql
```

### 2. 启动后端

```bash
cd gnosis-openplat
mvn spring-boot:run
```

启动成功后会显示：
```
========================================
开放平台启动成功！
访问地址:
  本地：http://localhost:8083
  外部：http://192.168.200.190:8083
  Swagger: http://localhost:8083/swagger-ui.html
  前端：http://localhost:3000
========================================
```

### 3. 启动前端

打开新的终端窗口：

```bash
cd gnosis-openplat/frontend
npm install  # 首次启动需要
npm run dev
```

启动成功后会显示：
```
VITE v4.5.14  ready in xxx ms

➜  Local:   http://localhost:3000/
```

## 访问地址

- 前端页面：http://localhost:3000
- 后端 API: http://localhost:8083
- Swagger 文档：http://localhost:8083/swagger-ui.html

## API 接口

后端提供以下 API 模块：
- `/openplat/api-config` - API 配置管理
- `/openplat/app-auth` - 应用认证管理
- `/openplat/system` - 系统管理
- `/openplat/webhook-config` - Webhook 配置管理

## 常见问题

### 1. 端口被占用
如果启动时提示端口被占用：
- 后端 8083 端口被占用：杀掉占用端口的进程
- 前端 3000 端口被占用：Vite 会自动使用 3001 端口

### 2. 接口返回 500 错误
- 检查数据库表是否已创建
- 检查数据库连接配置是否正确
- 查看后端日志确认具体错误信息

### 3. 前端请求 404
- 检查 vite.config.js 中的代理配置
- 确认后端服务已启动
- 检查 API 路径是否正确

## 技术栈

### 后端
- Spring Boot 2.1.10.RELEASE
- MyBatis-Plus 3.5.4
- openGauss 3.0.0
- Swagger 2.8.0

### 前端
- React 18.2.0
- Vite 4.4.5
- Ant Design 5.8.4
- Axios 1.4.0
