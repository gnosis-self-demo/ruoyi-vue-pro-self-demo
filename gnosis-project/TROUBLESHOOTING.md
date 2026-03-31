# 开放平台模块启动问题解决记录

## ✅ 已解决的问题

### 1. LiteFlow 初始化错误
**错误信息**:
```
Error creating bean with name 'liteflowExecutorInit': 
Invocation of init method failed; nested exception is 
com.yomahub.liteflow.exception.FlowExecutorNotInitException: 
init flow executor cause error for path [classpath:liteflow-rule.xml],
reason: [check_db_user] is not exist
```

**解决方案**:
1. 创建了独立的 LiteFlow 配置文件 `liteflow-rule-openplat.xml`（空配置）
2. 在 `application-openplat.properties` 中添加：
   ```properties
   liteflow.enable=false
   ```
3. 修改启动类排除不需要的包扫描

### 2. 数据库连接错误
**错误信息**:
```
Failed to initialize pool: Connection to localserver.gnosis:5432 refused
```

**解决方案**:
1. 使用 PostgreSQL 驱动代替 openGauss 驱动（更好的兼容性）
2. 修改数据库连接 URL：
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/gnosis_sample?currentSchema=gnosis_sample
   ```

### 3. Spring Boot 自动配置冲突
**问题**: paramcheck 和 lifecycle 模块的 Bean 创建失败

**解决方案**:
1. 在启动类中使用 `@ComponentScan` 的 `excludeFilters` 排除不需要的包
2. 在配置文件中禁用不需要的自动配置：
   ```properties
   spring.autoconfigure.exclude=\
     org.springframework.boot.actuate.autoconfigure.jdbc.DataSourceHealthIndicatorAutoConfiguration,\
     com.yomahub.liteflow.springboot.config.LiteflowMainAutoConfiguration
   ```

---

## 📋 最终解决方案

### 方式一：使用简化配置（推荐）

**启动命令**:
```bash
mvn spring-boot:run \
  -Dspring-boot.run.mainClass=openplat.OpenplatApplication \
  -Dspring-boot.run.profiles=openplat-simple \
  -Dmaven.test.skip=true
```

**配置文件**: `application-openplat-simple.properties`

**特点**:
- 最小化依赖
- 禁用了所有不需要的自动配置
- 启动速度快

---

### 方式二：使用完整配置

**启动命令**:
```bash
mvn spring-boot:run \
  -Dspring-boot.run.mainClass=openplat.OpenplatApplication \
  -Dspring-boot.run.profiles=openplat \
  -Dmaven.test.skip=true
```

**配置文件**: `application-openplat.properties`

**特点**:
- 包含完整的监控和日志配置
- 适合生产环境

---

## 🔧 启动步骤

### 1. 初始化数据库
```bash
psql -h localhost -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/openplat-opengauss.sql
```

### 2. 启动后端
```bash
# Windows
start-openplat.bat

# Linux/Mac
./start-openplat.sh

# 或手动启动
mvn spring-boot:run -Dspring-boot.run.mainClass=openplat.OpenplatApplication -Dspring-boot.run.profiles=openplat-simple -Dmaven.test.skip=true
```

### 3. 启动前端
```bash
cd frontend-openplat
npm run dev
```

### 4. 访问系统
- 前端：http://localhost:3000
- Swagger: http://localhost:8080/swagger-ui.html

---

## 📝 关键文件清单

### 启动类
- `OpenplatApplication.java` - openplat 独立启动类

### 配置文件
- `application-openplat.properties` - 完整配置
- `application-openplat-simple.properties` - 简化配置（推荐）
- `liteflow-rule-openplat.xml` - LiteFlow 空配置

### 启动脚本
- `start-openplat.bat` - Windows 启动脚本
- `start-openplat.sh` - Linux/Mac启动脚本

### 文档
- `STARTUP_GUIDE.md` - 详细启动指南
- `README_QUICKSTART.md` - 快速启动参考
- `TROUBLESHOOTING.md` - 本文档

---

## ⚠️ 注意事项

1. **数据库连接**: 确保数据库服务正在运行
   ```bash
   # 测试数据库连接
   psql -h localhost -p 5432 -U gaussdb -d gnosis_sample -c "SELECT 1"
   ```

2. **端口占用**: 确保 8080 和 3000 端口未被占用
   ```bash
   # Windows
   netstat -ano | findstr :8080
   netstat -ano | findstr :3000
   
   # Linux/Mac
   lsof -i :8080
   lsof -i :3000
   ```

3. **Maven 依赖**: 首次启动需要下载依赖，可能需要较长时间

---

## 🎯 验证启动成功

### 后端验证
启动成功后会看到：
```
========================================
开放平台启动成功！
访问地址:
  本地：http://localhost:8080
  外部：http://192.168.x.x:8080
  Swagger: http://localhost:8080/swagger-ui.html
  前端：http://localhost:3000
========================================
```

### 前端验证
启动成功后会看到：
```
VITE v4.5.14  ready in 1301 ms

➜  Local:   http://localhost:3000/
```

### API 测试
```bash
# 测试系统列表 API
curl http://localhost:8080/api/openplat/system/list

# 应该返回 JSON 数据
{"code":200,"message":"success","data":[...]}
```

---

## 📞 技术支持

如果遇到问题：
1. 查看日志文件：`logs/openplat.log`
2. 检查数据库连接
3. 确认端口未被占用
4. 参考文档：
   - STARTUP_GUIDE.md
   - README_QUICKSTART.md
   - DELIVERY_CHECKLIST.md

---

**更新时间**: 2026-03-27  
**版本**: v1.0.0
