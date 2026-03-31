# Maven 多模块重构计划

## 一、当前项目分析

### 1.1 现有模块识别

| 模块名称 | 包路径 | Application类 | 端口 | 配置文件 |
|---------|-------|-------------|------|---------|
| 分布式队列 | `gnosis.sample.distribute.queue` | DistributedQueueApplication | 8081 | application.properties |
| 生命周期 | `lifecycle` | LifecycleApplication | 8080 | application-lifecycle.properties |
| 开放平台 | `openplat` | OpenplatApplication | 8080 | application-openplat.properties |
| 参数校验 | `paramcheck` | ParamCheckApplication | 8080 | application-paramcheck.properties |
| LiteFlow动态切面 | `gnosis.sample.dynamic.liteflow` | (共用) | - | - |

### 1.2 现有资源分布

| 资源类型 | 路径 |
|---------|-----|
| Java代码 | `src/main/java/gnosis/`, `src/main/java/lifecycle/`, `src/main/java/openplat/`, `src/main/java/paramcheck/` |
| Mapper XML | `src/main/resources/mapper/lifecycle/`, `src/main/resources/mapper/openplat/` |
| SQL脚本 | `src/main/resources/db/` |
| 配置文件 | `src/main/resources/application*.properties` |
| LiteFlow规则 | `src/main/resources/liteflow-rule*.xml` |

---

## 二、重构后目标结构

```
gnosis-project/
├── pom.xml                          # 父POM，管理全局依赖和版本
├── gnosis-common/                   # 公共模块
│   ├── pom.xml
│   └── src/main/java/
│       └── com/gnosis/common/
│           ├── domain/              # 公共实体基类
│           ├── dto/                 # 公共DTO
│           ├── exception/           # 公共异常
│           └── util/                # 公共工具类
│
├── gnosis-distribute-queue/         # 分布式队列模块
│   ├── pom.xml
│   └── src/main/java/
│       └── com/gnosis/queue/
│           ├── controller/
│           ├── service/
│           ├── repository/
│           ├── model/
│           ├── processor/
│           ├── sdk/
│           ├── config/
│           ├── enums/
│           ├── exception/
│           └── DistributedQueueApplication.java
│   └── src/main/resources/
│       ├── mapper/queue/
│       ├── db/queue/
│       └── application.properties
│
├── gnosis-lifecycle/                # 生命周期管理模块
│   ├── pom.xml
│   └── src/main/java/
│       └── com/gnosis/lifecycle/
│           ├── controller/
│           ├── service/
│           ├── repository/
│           ├── domain/
│           ├── dto/
│           ├── engine/
│           ├── entry/
│           └── LifecycleApplication.java
│   └── src/main/resources/
│       ├── mapper/lifecycle/
│       ├── db/lifecycle/
│       └── application.properties
│
├── gnosis-openplat/                 # 开放平台模块
│   ├── pom.xml
│   └── src/main/java/
│       └── com/gnosis/openplat/
│           ├── controller/
│           ├── service/
│           ├── repository/
│           ├── domain/
│           ├── dto/
│           └── OpenplatApplication.java
│   └── src/main/resources/
│       ├── mapper/openplat/
│       ├── db/openplat/
│       └── application.properties
│
├── gnosis-paramcheck/               # 参数校验模块
│   ├── pom.xml
│   └── src/main/java/
│       └── com/gnosis/paramcheck/
│           ├── controller/
│           ├── service/
│           ├── repository/
│           ├── domain/
│           ├── aspect/
│           ├── handler/
│           ├── config/
│           ├── annotation/
│           ├── component/
│           ├── exception/
│           └── ParamCheckApplication.java
│   └── src/main/resources/
│       ├── mapper/paramcheck/
│       ├── db/paramcheck/
│       └── application.properties
│
└── gnosis-dynamic-liteflow/         # LiteFlow动态切面模块
    ├── pom.xml
    └── src/main/java/
        └── com/gnosis/dynamic/
            ├── aspect/
            ├── component/
            ├── config/
            ├── entity/
            ├── manager/
            ├── mapper/
            ├── service/
            └── web/
    └── src/main/resources/
        ├── mapper/dynamic/
        ├── db/dynamic/
        └── liteflow-rule.xml
```

---

## 三、模块依赖关系图

```
gnosis-common (公共基础)
    ↑
    ├──────────────┬──────────────┬──────────────┬──────────────
    ↓              ↓              ↓              ↓              ↓
gnosis-dynamic-   gnosis-        gnosis-        gnosis-        gnosis-
liteflow          distribute-    lifecycle      openplat       paramcheck
                  queue
```

---

## 四、实施步骤

### 阶段一：创建父POM和公共模块

**任务 1.1**：创建父 pom.xml
- 提取所有版本属性
- 定义 dependencyManagement
- 定义 pluginManagement
- 配置编译插件

**任务 1.2**：创建 gnosis-common 公共模块
- 创建基础 domain 基类（如 BaseEntity）
- 创建公共 DTO
- 创建公共异常类
- 创建公共工具类

### 阶段二：拆分分布式队列模块

**任务 2.1**：创建 gnosis-distribute-queue 模块结构
- 创建 pom.xml（依赖 common）
- 移动 `sample/distribute/queue` 下所有代码到 `com/gnosis/queue`
- 创建 config 目录，移入相关配置类
- 创建 repository 目录，移入 mapper 接口

**任务 2.2**：拆分分布式队列资源文件
- 创建 `src/main/resources/mapper/queue/`
- 移动或新建 queue 相关的 Mapper XML
- 创建 `src/main/resources/db/queue/`
- 移动 queue 相关的 SQL 脚本
- 提取 application.properties 中的 queue 相关配置

### 阶段三：拆分生命周期模块

**任务 3.1**：创建 gnosis-lifecycle 模块结构
- 创建 pom.xml（依赖 common）
- 移动 `lifecycle` 包下所有代码到 `com/gnosis/lifecycle`
- 整理 package 结构

**任务 3.2**：拆分生命周期资源文件
- 创建 `src/main/resources/mapper/lifecycle/`
- 移动 lifecycle 相关的 Mapper XML
- 创建 `src/main/resources/db/lifecycle/`
- 移动 lifecycle 相关的 SQL 脚本
- 提取 application-lifecycle.properties

### 阶段四：拆分开放平台模块

**任务 4.1**：创建 gnosis-openplat 模块结构
- 创建 pom.xml（依赖 common）
- 移动 `openplat` 包下所有代码到 `com/gnosis/openplat`
- 整理 package 结构

**任务 4.2**：拆分开放平台资源文件
- 创建 `src/main/resources/mapper/openplat/`
- 移动 openplat 相关的 Mapper XML
- 创建 `src/main/resources/db/openplat/`
- 移动 openplat 相关的 SQL 脚本
- 提取 application-openplat.properties

### 阶段五：拆分参数校验模块

**任务 5.1**：创建 gnosis-paramcheck 模块结构
- 创建 pom.xml（依赖 common）
- 移动 `paramcheck` 包下所有代码到 `com/gnosis/paramcheck`
- 整理 package 结构

**任务 5.2**：拆分参数校验资源文件
- 创建 `src/main/resources/mapper/paramcheck/`
- 移动 paramcheck 相关的 Mapper XML
- 创建 `src/main/resources/db/paramcheck/`
- 移动 paramcheck 相关的 SQL 脚本
- 提取 application-paramcheck.properties

### 阶段六：拆分LiteFlow动态切面模块

**任务 6.1**：创建 gnosis-dynamic-liteflow 模块结构
- 创建 pom.xml（依赖 common）
- 移动 `sample/dynamic/liteflow` 下所有代码到 `com/gnosis/dynamic/liteflow`
- 创建 config 目录，移入配置类

**任务 6.2**：拆分LiteFlow资源文件
- 创建 `src/main/resources/mapper/dynamic/`
- 移动 dynamic 相关的 Mapper XML
- 创建 `src/main/resources/db/dynamic/`
- 移动 dynamic 相关的 SQL 脚本
- 移动 liteflow-rule.xml

### 阶段七：清理和验证

**任务 7.1**：清理原 src 目录
- 删除 `src/main/java/gnosis/` 目录
- 删除原有的 Application 类

**任务 7.2**：验证编译
- 执行 `mvn clean compile` 验证所有模块编译通过
- 检查依赖完整性

---

## 五、注意事项

1. **依赖管理**：所有模块依赖统一在父 POM 的 dependencyManagement 中管理
2. **公共代码提取**：BaseEntity、CommonResponse 等公共类移到 common 模块
3. **配置分离**：每个模块的 application.properties 只包含该模块需要的配置
4. **SQL脚本分离**：每个模块只保留自己需要的 SQL 脚本
5. **包名统一**：所有代码移动到 `com.gnosis.{module}` 包下
6. **编译验证**：每个阶段完成后验证编译通过

---

## 六、风险控制

1. 先备份原项目
2. 按阶段执行，每阶段完成后验证
3. 保留原有目录结构直到验证通过
4. 记录每个阶段遇到的问题和解决方案
