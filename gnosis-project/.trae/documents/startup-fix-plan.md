# 项目启动问题解决计划

## 问题分析

### 1. 核心问题
- **启动类被忽略**: 即使指定了 `-Dspring-boot.run.mainClass=openplat.OpenplatApplication`,实际启动的仍是 `ParamCheckApplication`
- **数据库连接失败**: `Connection to localhost:5432 refused` - 数据库服务未启动或连接配置错误
- **包扫描冲突**: `ParamCheckApplication` 扫描 paramcheck 包，导致尝试创建数据源 Bean

### 2. 错误堆栈关键信息
```
Failed to execute goal org.springframework.boot:spring-boot-maven-plugin:run
Error creating bean with name 'paramCheckAspect'
Unsatisfied dependency expressed through field 'dynamicValidationService'
Failed to initialize pool: Connection to localhost:5432 refused
```

### 3. 根本原因
- pom.xml 中 Spring Boot Maven Plugin 的 `<mainClass>` 配置为 `paramcheck.ParamCheckApplication`
- Maven 命令行的 `-Dspring-boot.run.mainClass` 参数可能未正确传递
- 数据库服务可能未启动或配置的主机名不正确 (localserver.gnosis vs localhost)

## 解决方案

### 方案一：修复 Maven 配置（推荐）

#### 步骤 1: 修改 pom.xml 的 Spring Boot Maven Plugin 配置
- 将 `<mainClass>` 从 `paramcheck.ParamCheckApplication` 改为 `openplat.OpenplatApplication`
- 或者移除该配置，完全依赖命令行参数

#### 步骤 2: 确保数据库服务启动
- 验证数据库服务状态
- 测试数据库连接

#### 步骤 3: 使用正确的启动命令
```bash
mvn clean spring-boot:run \
  -Dspring-boot.run.mainClass=openplat.OpenplatApplication \
  -Dspring-boot.run.profiles=openplat-simple \
  -Dmaven.test.skip=true
```

### 方案二：创建独立的多模块项目结构

#### 步骤 1: 将 openplat 模块分离为独立项目
- 创建独立的 pom.xml
- 仅包含 openplat 相关的依赖

#### 步骤 2: 配置独立启动脚本
- 创建专用的启动脚本
- 配置正确的 classpath

### 方案三：使用可执行 JAR 方式启动

#### 步骤 1: 修改 pom.xml 打包配置
- 配置 Spring Boot Maven Plugin 的 classifier
- 创建独立的可执行 JAR

#### 步骤 2: 打包并运行
```bash
mvn clean package -Dmaven.test.skip=true
java -jar target/gnosis-project-1.0.0-SNAPSHOT.jar --spring.profiles.active=openplat-simple --spring.main.web-application-type=servlet
```

## 数据库连接问题修复

### 步骤 1: 验证数据库服务
```bash
# 测试数据库连接
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -c "SELECT 1"
```

### 步骤 2: 修正配置文件
- 检查 `application-openplat-simple.properties` 中的数据库 URL
- 根据实际情况调整主机名 (localserver.gnosis 或 localhost)

### 步骤 3: 初始化数据库表
```bash
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample \
  -f src/main/resources/db/openplat-opengauss.sql
```

## 验证步骤

### 1. 验证启动类正确
启动日志应显示:
```
Starting OpenplatApplication on ...
```
而不是:
```
Starting ParamCheckApplication on ...
```

### 2. 验证数据库连接成功
启动日志不应包含:
```
Failed to initialize pool: Connection refused
```

### 3. 验证应用启动成功
应看到:
```
========================================
开放平台启动成功！
访问地址:
  本地：http://localhost:8080
  Swagger: http://localhost:8080/swagger-ui.html
========================================
```

### 4. 验证 API 可访问
```bash
curl http://localhost:8080/api/openplat/system/list
```

## 实施计划

### 阶段 1: 立即修复（10 分钟）
1. 修改 pom.xml 中的 mainClass 配置
2. 验证数据库连接
3. 测试启动命令

### 阶段 2: 启动脚本优化（5 分钟）
1. 修复 start-openplat.sh 脚本
2. 创建 Windows 批处理脚本
3. 添加错误处理和日志输出

### 阶段 3: 文档更新（5 分钟）
1. 更新 STARTUP.md 文档
2. 添加故障排查章节
3. 创建快速启动指南

## 预期结果

执行以下命令后，项目能够正常启动:

```bash
# Windows
start-openplat.bat

# Linux/Mac
./start-openplat.sh

# 或手动命令
mvn spring-boot:run -Dspring-boot.run.mainClass=openplat.OpenplatApplication -Dspring-boot.run.profiles=openplat-simple
```

启动后:
- 后端服务运行在 http://localhost:8080
- Swagger 文档可访问
- 前端可独立启动在 http://localhost:3000
- 无数据库连接错误
- 无包扫描冲突错误

## 风险评估

### 低风险
- 修改 pom.xml 配置 - 可快速回滚
- 测试数据库连接 - 只读操作

### 中等风险
- 数据库表初始化 - 可能影响现有数据
- 启动脚本修改 - 需测试兼容性

### 缓解措施
- 所有修改前备份原文件
- 逐步验证每个修复步骤
- 保留原有启动方式作为备选
