# Maven 多模块完整分类管理计划

## 一、当前问题分析

### 1.1 现有项目结构问题

```
gnosis-project/
├── docs/                        # 文档散落在根目录
├── frontend/                   # 前端代码散落在根目录
├── frontend-lifecycle/         # 前端代码散落在根目录
├── frontend-openplat/          # 前端代码散落在根目录
├── gnosis-common/              # 只有Java代码
├── gnosis-distribute-queue/    # 只有Java代码和配置
├── gnosis-dynamic-liteflow/    # 只有Java代码和配置
├── gnosis-lifecycle/            # 只有Java代码和配置
├── gnosis-openplat/            # 只有Java代码和配置
└── gnosis-paramcheck/           # 只有Java代码
```

### 1.2 前端与后端模块对应关系

| 后端模块 | 前端模块 | 说明 |
|---------|---------|------|
| gnosis-distribute-queue | frontend | 分布式队列前端 |
| gnosis-lifecycle | frontend-lifecycle | 生命周期管理前端 |
| gnosis-openplat | frontend-openplat | 开放平台前端 |
| gnosis-paramcheck | (无独立前端) | 参数校验复用其他前端 |

### 1.3 文档与后端模块对应关系

| 后端模块 | 相关文档 | 说明 |
|---------|---------|------|
| gnosis-distribute-queue | dead-letter-*.md, queue-*.md, sdk-*.md | 队列相关文档 |
| gnosis-dynamic-liteflow | liteflow-*.md | LiteFlow相关文档 |
| gnosis-lifecycle | lifecycle-*.md, entry-*.md, business-*.md | 生命周期相关文档 |
| gnosis-openplat | api-examples.rest, business-scenarios.rest | API文档 |
| 通用 | STARTUP_*.md, TEST_*.md, PARAMCHECK_*.md | 通用文档 |

---

## 二、重构目标结构

```
gnosis-project/
├── pom.xml                          # 父POM，管理全局依赖
│
├── gnosis-common/                   # 公共模块
│   └── (仅Java代码，无前端和文档)
│
├── gnosis-distribute-queue/         # 分布式队列模块
│   ├── src/main/java/               # Java代码
│   ├── src/main/resources/          # 配置和SQL
│   ├── frontend/                    # 分布式队列前端
│   └── docs/                       # 分布式队列文档
│
├── gnosis-dynamic-liteflow/         # LiteFlow动态切面模块
│   ├── src/main/java/               # Java代码
│   ├── src/main/resources/          # 配置和SQL
│   ├── frontend/                    # LiteFlow前端（如有）
│   └── docs/                       # LiteFlow文档
│
├── gnosis-lifecycle/                # 生命周期管理模块
│   ├── src/main/java/               # Java代码
│   ├── src/main/resources/          # 配置和SQL
│   ├── frontend/                    # 生命周期前端
│   └── docs/                       # 生命周期文档
│
├── gnosis-openplat/                 # 开放平台模块
│   ├── src/main/java/               # Java代码
│   ├── src/main/resources/          # 配置和SQL
│   ├── frontend/                    # 开放平台前端
│   └── docs/                       # 开放平台文档
│
└── gnosis-paramcheck/               # 参数校验模块
    ├── src/main/java/               # Java代码
    ├── src/main/resources/          # 配置和SQL
    ├── frontend/                    # 参数校验前端
    └── docs/                       # 参数校验文档
```

---

## 三、实施步骤

### 阶段一：迁移前端代码到各模块

**任务 1.1**：迁移 frontend 到 gnosis-distribute-queue
- 将 `frontend/` 移动到 `gnosis-distribute-queue/frontend/`

**任务 1.2**：迁移 frontend-lifecycle 到 gnosis-lifecycle
- 将 `frontend-lifecycle/` 移动到 `gnosis-lifecycle/frontend/`

**任务 1.3**：迁移 frontend-openplat 到 gnosis-openplat
- 将 `frontend-openplat/` 移动到 `gnosis-openplat/frontend/`

**任务 1.4**：为其他模块创建空的 frontend 目录占位
- 为 gnosis-paramcheck、gnosis-dynamic-liteflow 创建 `frontend/` 目录
- 可选择为未来扩展使用

### 阶段二：迁移文档到各模块

**任务 2.1**：迁移分布式队列相关文档到 gnosis-distribute-queue/docs/
- dead-letter-*.md
- queue-rate-limit-examples.md
- sdk-*.md

**任务 2.2**：迁移 LiteFlow 相关文档到 gnosis-dynamic-liteflow/docs/
- liteflow-dynamic-aspect-guide.md

**任务 2.3**：迁移生命周期相关文档到 gnosis-lifecycle/docs/
- BUSINESS_LIFECYCLE_DELIVERY.md
- ENTRY_QUICK_REFERENCE.md
- lifecycle-entry-api-guide.md
- lifecycle-test-report.md
- entry-module-summary.md

**任务 2.4**：迁移开放平台相关文档到 gnosis-openplat/docs/
- api-examples.rest
- business-scenarios.rest

**任务 2.5**：迁移参数校验相关文档到 gnosis-paramcheck/docs/
- PARAMCHECK_USAGE.md
- paramcheck-docs/ 整个目录

**任务 2.6**：保留通用文档
- 将 STARTUP_GUIDE.md, TEST_REPORT.md, REFACTOR_VERIFICATION.md 等通用文档保留在根目录 docs/ 下
- 或创建 docs-common/ 目录存放

### 阶段三：更新前端配置

**任务 3.1**：更新 frontend 的 API 配置
- 修改 `frontend/src/utils/request.js` 中的 API 基础路径为 `http://localhost:8081`

**任务 3.2**：更新 frontend-lifecycle 的 API 配置
- 修改 `frontend/src/utils/request.js` 中的 API 基础路径为 `http://localhost:8080`

**任务 3.3**：更新 frontend-openplat 的 API 配置
- 修改 `frontend/src/utils/request.js` 中的 API 基础路径为 `http://localhost:8083`

### 阶段四：更新父 POM 和构建配置

**任务 4.1**：更新父 pom.xml 添加 frontend 和 docs 目录
- 可选：添加 frontend-maven-plugin 进行前端构建

**任务 4.2**：更新各模块 pom.xml
- 可选：添加前端构建依赖

### 阶段五：清理和验证

**任务 5.1**：删除根目录下的原始 frontend 和 docs 目录

**任务 5.2**：验证 Maven 编译通过

**任务 5.3**：验证前端构建通过

---

## 四、注意事项

1. **保持前端独立性**：前端使用独立的 package.json，可以独立运行
2. **API 路径更新**：确保前端配置的 API 路径与后端模块端口对应
3. **文档完整性**：迁移后确保文档完整可读
4. **备份原文件**：迁移前先备份

---

## 五、端口对应关系

| 后端模块 | 端口 | 前端模块 | 前端端口 |
|---------|------|---------|---------|
| gnosis-distribute-queue | 8081 | frontend | 3001 |
| gnosis-dynamic-liteflow | 8082 | (无独立前端) | - |
| gnosis-lifecycle | 8080 | frontend-lifecycle | 3000 |
| gnosis-openplat | 8083 | frontend-openplat | 3002 |
| gnosis-paramcheck | 8084 | (复用其他前端) | - |
