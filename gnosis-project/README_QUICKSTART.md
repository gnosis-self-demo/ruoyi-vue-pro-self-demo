# 开放平台模块 - 快速启动参考

## 🚀 一键启动（推荐）

### Windows 用户
双击运行：
```
start-openplat.bat
```

或在 PowerShell 中执行：
```powershell
.\start-openplat.bat
```

### Linux/Mac 用户
```bash
chmod +x start-openplat.sh
./start-openplat.sh
```

---

## 📋 手动启动命令

### 1️⃣ 初始化数据库
```bash
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/openplat-opengauss.sql
```

### 2️⃣ 启动后端
```bash
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project
mvn spring-boot:run -Dspring-boot.run.mainClass=openplat.OpenplatApplication
```

**启动成功标志**：
```
========================================
开放平台启动成功！
Swagger 文档：http://localhost:8080/swagger-ui.html
前端地址：http://localhost:3000
========================================
```

### 3️⃣ 启动前端
```bash
cd c:\works\project\ruoyi-vue-pro-self-demo\gnosis-project\frontend-openplat
npm run dev
```

**启动成功标志**：
```
VITE v4.5.14  ready in 1301 ms

➜  Local:   http://localhost:3000/
```

---

## 🌐 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端页面 | http://localhost:3000 | React 管理界面 |
| Swagger 文档 | http://localhost:8080/swagger-ui.html | API 文档和测试 |
| 系统管理 API | http://localhost:8080/api/openplat/system/list | 系统列表接口 |

---

## 📁 页面导航

启动成功后，访问 http://localhost:3000，可以看到以下页面：

1. **对接系统管理** - `/systems`
   - 管理内外部对接系统
   - 功能：新增、编辑、删除、批量操作

2. **应用认证管理** - `/app-auth`
   - 管理应用 ID 和密钥
   - 功能：认证配置、验证测试

3. **API 配置管理** - `/api-config`
   - 配置 API 路由和规则
   - 功能：路由配置、示例链接、限流设置

4. **Webhook 配置管理** - `/webhook`
   - 配置事件回调
   - 功能：事件订阅、回调策略

---

## 🔧 常见问题

### 端口被占用
```bash
# 查看端口
netstat -ano | findstr :8080
netstat -ano | findstr :3000

# 杀死进程
taskkill /PID <进程 ID> /F
```

### 数据库连接失败
```bash
# 测试连接
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -c "SELECT 1"
```

### npm install 失败
```bash
# 清理缓存
npm cache clean --force

# 重新安装
rm -rf node_modules package-lock.json
npm install
```

---

## 📦 项目文件清单

### 后端文件
```
src/main/java/openplat/
├── OpenplatApplication.java          # ⭐ 独立启动类
├── domain/                           # 9 个实体类
├── mapper/                           # 8 个 Mapper 接口
├── service/                          # 5 个 Service 接口
├── service/impl/                     # 5 个 Service 实现
├── controller/                       # 4 个 Controller
└── dto/                              # 1 个响应对象

src/main/resources/
├── db/openplat-opengauss.sql         # ⭐ 数据库脚本
└── mapper/openplat/                  # 8 个 Mapper XML
```

### 前端文件
```
frontend-openplat/
├── src/
│   ├── pages/                        # 4 个管理页面
│   ├── services/                     # 4 个 API 服务
│   ├── utils/                        # 请求工具
│   └── App.jsx                       # 主应用
├── package.json
└── vite.config.js
```

### 文档文件
```
├── OPENPLAT_IMPLEMENTATION.md        # 实施文档
├── STARTUP_GUIDE.md                  # 详细启动指南
├── README_QUICKSTART.md              # 本文档
├── start-openplat.bat                # Windows 启动脚本
└── start-openplat.sh                 # Linux 启动脚本
```

---

## 🎯 技术栈

**后端**:
- Spring Boot 2.1.10.RELEASE
- MyBatis
- openGauss 3.0.0
- Java 1.8

**前端**:
- React 18.2.0
- Ant Design 5.8.4
- Vite 4.4.5

---

## 📞 支持

- **API 文档**: http://localhost:8080/swagger-ui.html
- **实施文档**: OPENPLAT_IMPLEMENTATION.md
- **详细指南**: STARTUP_GUIDE.md

---

**版本**: v1.0.0  
**更新日期**: 2026-03-27
