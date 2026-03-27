# 开放平台模块 - 前后端启动命令

## 快速启动步骤

### 步骤 1：初始化数据库
```bash
# Windows PowerShell
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/openplat-opengauss.sql

# 或者使用 Git Bash
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample < src/main/resources/db/openplat-opengauss.sql
```

### 步骤 2：启动后端（openplat 独立模式）

#### 方式 A：使用 Maven 命令（推荐）
```bash
# Windows PowerShell
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project
mvn spring-boot:run -Dspring-boot.run.mainClass=openplat.OpenplatApplication

# 或者使用 Git Bash
cd /c/works/project/ruoyi-vue-pro-self-demo/gnosis-project
mvn spring-boot:run -Dspring-boot.run.mainClass=openplat.OpenplatApplication
```

#### 方式 B：先编译再运行
```bash
# 编译
mvn clean compile

# 运行
mvn exec:java -Dexec.mainClass="openplat.OpenplatApplication"
```

#### 方式 C：打包后运行
```bash
# 打包
mvn clean package -DskipTests

# 运行 jar
java -jar target/gnosis-project-1.0.0-SNAPSHOT.jar --spring-boot.run.mainClass=openplat.OpenplatApplication
```

### 步骤 3：启动前端

#### 首次启动（需要安装依赖）
```bash
# Windows PowerShell
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project\frontend-openplat
npm install
npm run dev

# 或者使用 Git Bash
cd /c/works/project/ruoyi-vue-pro-self-demo/gnosis-project/frontend-openplat
npm install
npm run dev
```

#### 后续启动（已安装依赖）
```bash
# Windows PowerShell
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project\frontend-openplat
npm run dev

# 或者使用 Git Bash
cd /c/works/project/ruoyi-vue-pro-self-demo/gnosis-project/frontend-openplat
npm run dev
```

### 步骤 4：访问系统

启动成功后，访问以下地址：

- **前端页面**: http://localhost:3000
- **Swagger 文档**: http://localhost:8080/swagger-ui.html
- **API 测试**: http://localhost:8080/api/openplat/system/list

---

## 完整启动命令（一键复制）

### Windows PowerShell 版本
```powershell
# 1. 初始化数据库
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/openplat-opengauss.sql

# 2. 启动后端（新窗口）
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project; mvn spring-boot:run -Dspring-boot.run.mainClass=openplat.OpenplatApplication"

# 3. 启动前端（新窗口）
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project\frontend-openplat; npm run dev"
```

### Git Bash 版本
```bash
# 1. 初始化数据库
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample < src/main/resources/db/openplat-opengauss.sql

# 2. 启动后端（后台运行）
cd /c/works/project/ruoyi-vue-pro-self-demo/gnosis-project
mvn spring-boot:run -Dspring-boot.run.mainClass=openplat.OpenplatApplication &

# 3. 启动前端（新终端）
cd /c/works/project/ruoyi-vue-pro-self-demo/gnosis-project/frontend-openplat
npm run dev
```

---

## 启动配置说明

### 后端配置
- **应用端口**: 8080
- **数据库**: openGauss 3.0.0
- **数据库地址**: localserver.gnosis:5432
- **数据库名**: gnosis_sample
- **用户名**: gaussdb
- **密码**: Enmotech@123

### 前端配置
- **开发服务器端口**: 3000
- **代理配置**: /api -> http://localhost:8080
- **构建工具**: Vite 4.4.5
- **UI 框架**: Ant Design 5.8.4

---

## 验证启动成功

### 后端验证
```bash
# 检查后端是否启动
curl http://localhost:8080/actuator/health

# 测试 API
curl http://localhost:8080/api/openplat/system/list
```

### 前端验证
```bash
# 检查前端是否启动
curl http://localhost:3000
```

### 浏览器访问
1. 打开浏览器访问 http://localhost:3000
2. 查看页面是否正常显示
3. 尝试访问各个管理页面：
   - 对接系统管理：http://localhost:3000/systems
   - 应用认证管理：http://localhost:3000/app-auth
   - API 配置管理：http://localhost:3000/api-config
   - Webhook 配置管理：http://localhost:3000/webhook

---

## 常见问题

### 1. 端口被占用
```bash
# 查看端口占用
netstat -ano | findstr :8080
netstat -ano | findstr :3000

# 杀死占用端口的进程
taskkill /PID <进程 ID> /F
```

### 2. 数据库连接失败
```bash
# 测试数据库连接
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -c "SELECT 1"
```

### 3. npm install 失败
```bash
# 清除 npm 缓存
npm cache clean --force

# 删除 node_modules 重新安装
rm -rf node_modules package-lock.json
npm install
```

### 4. Maven 编译错误
```bash
# 清理并重新编译
mvn clean compile -U -DskipTests

# 查看详细错误信息
mvn spring-boot:run -Dspring-boot.run.mainClass=openplat.OpenplatApplication -X
```

---

## 开发环境要求

- **JDK**: 1.8
- **Maven**: 3.6+
- **Node.js**: 14.0+
- **npm**: 6.0+
- **数据库**: openGauss 3.0.0
- **IDE**: IntelliJ IDEA / VS Code

---

## 项目结构

```
gnosis-project/
├── src/main/java/openplat/          # openplat 后端代码
│   ├── OpenplatApplication.java     # 独立启动类
│   ├── domain/                      # 实体类
│   ├── mapper/                      # Mapper 接口
│   ├── service/                     # Service 层
│   ├── controller/                  # Controller 层
│   └── dto/                         # 数据传输对象
├── src/main/resources/
│   ├── db/openplat-opengauss.sql    # 数据库脚本
│   └── mapper/openplat/             # Mapper XML
├── frontend-openplat/               # React 前端工程
│   ├── src/
│   │   ├── pages/                   # 页面组件
│   │   ├── services/                # API 服务
│   │   └── utils/                   # 工具类
│   └── package.json
└── OPENPLAT_IMPLEMENTATION.md       # 实施文档
```

---

## 技术支持

- **API 文档**: http://localhost:8080/swagger-ui.html
- **实施文档**: OPENPLAT_IMPLEMENTATION.md
- **前端说明**: frontend-openplat/README.md

---

**最后更新时间**: 2026-03-27  
**模块版本**: v1.0.0
