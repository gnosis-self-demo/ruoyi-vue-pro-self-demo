# 业务生命周期管理组件 - 启动说明

## 📋 目录

1. [快速启动](#快速启动)
2. [后端启动](#后端启动)
3. [前端启动](#前端启动)
4. [数据库初始化](#数据库初始化)
5. [常见问题](#常见问题)

---

## 🚀 快速启动

### 方式一：使用 IDEA 或 Eclipse

1. **后端**：直接运行 `LifecycleApplication.java` 的 main 方法
2. **前端**：在 `frontend-lifecycle` 目录下执行 `npm run dev`

### 方式二：使用 Maven 命令

```bash
# 后端启动
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project
mvn spring-boot:run

# 前端启动（新终端）
cd frontend-lifecycle
npm install
npm run dev
```

---

## 💻 后端启动

### 1. 环境要求

- JDK 1.8
- Maven 3.6+
- openGauss 3.0.0

### 2. 启动方式

#### 方式 A：运行启动类（推荐）

**文件位置**: `src/main/java/paramcheck/lifecycle/LifecycleApplication.java`

直接运行 `LifecycleApplication` 类的 `main` 方法即可。

#### 方式 B：Maven 命令

```bash
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project
mvn clean install
mvn spring-boot:run -Dspring-boot.run.mainClass=paramcheck.lifecycle.LifecycleApplication
```

#### 方式 C：打包后运行

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar target/gnosis-project-1.0.0-SNAPSHOT.jar
```

### 3. 启动参数（可选）

```bash
# 指定端口
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dserver.port=8081"

# 指定配置文件
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Dspring.profiles.active=dev"

# 调试模式
mvn spring-boot:run -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005"
```

### 4. 验证启动成功

启动成功后，控制台会显示：

```
=================================================
    业务生命周期管理组件启动成功！
=================================================
Swagger UI: http://localhost:8080/swagger-ui.html
前端地址：http://localhost:3000 (需单独启动)
=================================================
```

### 5. 访问地址

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **健康检查**: http://localhost:8080/actuator/health

---

## 🖥️ 前端启动

### 1. 环境要求

- Node.js 14.0+
- npm 6.0+

### 2. 项目位置

```
c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project\frontend-lifecycle
```

### 3. 启动步骤

```bash
# 进入前端目录
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project\frontend-lifecycle

# 安装依赖（首次启动）
npm install

# 启动开发服务器
npm run dev
```

### 4. 启动成功

```
  VITE v5.0.8  ready in 1234 ms

  ➜  Local:   http://localhost:3000/
  ➜  Network: use --host to expose
  ➜  press h to show help
```

### 5. 访问地址

- **前端首页**: http://localhost:3000

### 6. 构建生产版本

```bash
# 构建
npm run build

# 预览生产构建
npm run preview
```

---

## 🗄️ 数据库初始化

### 1. 连接信息

```yaml
数据库类型：openGauss 3.0.0
连接地址：jdbc:opengauss://localserver.gnosis:5432/gnosis_sample
用户名：gaussdb
密码：Enmotech@123
```

### 2. 初始化 SQL

```bash
# 使用 psql 执行
psql -U gaussdb -h localserver.gnosis -d gnosis_sample -f src/main/resources/db/lifecycle-schema-opengauss.sql
```

### 3. 手动执行

使用数据库管理工具（如 DBeaver、pgAdmin）执行以下 SQL 文件：

1. `src/main/resources/db/lifecycle-schema-opengauss.sql` - 创建表结构
2. `src/main/resources/db/lifecycle-init-data.sql` - 初始化数据（可选）

### 4. 验证数据库

```sql
-- 查看表
SELECT tablename FROM pg_tables WHERE schemaname = 'public' AND tablename LIKE 'lifecycle_%';

-- 查看数据
SELECT * FROM lifecycle_business_type LIMIT 10;
```

---

## 🔧 常见问题

### 1. 端口被占用

**问题**: 8080 或 3000 端口被占用

**解决方案**:

```bash
# 修改后端端口
# 在 application.properties 中添加
server.port=8081

# 修改前端端口
# 在 vite.config.js 中修改
server: {
  port: 3001
}
```

### 2. 数据库连接失败

**问题**: 无法连接到 openGauss 数据库

**解决方案**:

1. 检查数据库服务是否启动
2. 检查数据库连接信息是否正确
3. 检查防火墙设置
4. 确认数据库驱动版本

### 3. 前端依赖安装失败

**问题**: npm install 失败

**解决方案**:

```bash
# 清除缓存
npm cache clean --force

# 删除 node_modules
rm -rf node_modules package-lock.json

# 重新安装
npm install

# 使用淘宝镜像
npm install --registry=https://registry.npmmirror.com
```

### 4. 编译错误

**问题**: Maven 编译失败

**解决方案**:

```bash
# 清理并重新编译
mvn clean
mvn compile -U

# 跳过测试编译
mvn compile -DskipTests
```

### 5. Swagger UI 无法访问

**问题**: 访问 http://localhost:8080/swagger-ui.html 404

**解决方案**:

1. 确认后端已启动成功
2. 检查是否有 Spring Security 配置拦截
3. 查看 application.properties 中 Swagger 配置

---

## 📝 启动命令速查表

### 后端

```bash
# 方式 1：直接运行启动类
# 在 IDE 中运行 LifecycleApplication.java 的 main 方法

# 方式 2：Maven 启动
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project
mvn spring-boot:run

# 方式 3：打包运行
mvn clean package -DskipTests
java -jar target/gnosis-project-1.0.0-SNAPSHOT.jar

# 方式 4：指定主类
mvn spring-boot:run -Dspring-boot.run.mainClass=paramcheck.lifecycle.LifecycleApplication
```

### 前端

```bash
# 进入目录
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project\frontend-lifecycle

# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产构建
npm run preview
```

### 数据库

```bash
# 初始化数据库
psql -U gaussdb -h localserver.gnosis -d gnosis_sample -f src/main/resources/db/lifecycle-schema-opengauss.sql

# 或者使用图形化工具手动执行 SQL 文件
```

---

## 🎯 推荐的启动顺序

1. **第一步**: 初始化数据库（首次启动）
2. **第二步**: 启动后端服务
3. **第三步**: 启动前端服务
4. **第四步**: 访问前端页面进行测试

---

## 📞 技术支持

如遇到其他问题，请查看：

- 完整文档：`docs/BUSINESS_LIFECYCLE_DELIVERY.md`
- API 文档：`http://localhost:8080/swagger-ui.html`
- 测试报告：`docs/lifecycle-test-report.md`

---

**最后更新**: 2026-03-27  
**维护者**: gnosis team
