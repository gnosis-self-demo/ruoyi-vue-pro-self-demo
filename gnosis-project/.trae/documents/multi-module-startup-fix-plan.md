# 项目启动问题解决计划

## 一、问题分析

### 1. LifecycleApplication 启动问题

**问题：**
- 排除了 `DataSourceAutoConfiguration`，导致 MyBatis 无法创建 SqlSessionFactory
- 缺少独立的配置文件
- 包扫描仅包含 "lifecycle"，但 lifecycle 模块可能依赖其他包的配置类

**解决方案：**
- 创建 `application-lifecycle.properties` 配置文件
- 修改启动类，移除不必要的 exclude 配置
- 添加必要的排除过滤器（排除其他模块）

### 2. OpenplatApplication 启动问题

**问题：**
- ComponentScan 同时扫描 "openplat" 和 "paramcheck"，导致冲突
- 之前修复的 pom.xml mainClass 已正确

**解决方案：**
- 修改 ComponentScan 仅扫描 "openplat"
- 添加排除其他模块的过滤器

### 3. ParamCheckApplication 启动问题

**问题：**
- 缺少独立的配置文件
- 包扫描仅包含 "paramcheck"，但可能依赖 lifecycle 模块的类

**解决方案：**
- 创建 `application-paramcheck.properties` 配置文件
- 修改启动类配置

---

## 二、SQL 脚本问题汇总

| 文件 | 问题 | 修复方式 |
|------|------|----------|
| lifecycle-engine-opengauss.sql | `BOOLEAN` 应为 `boolean` | 统一小写 |
| lifecycle-entry-opengauss.sql | 基本正确 | 微调 |
| lifecycle-exit-governance-opengauss.sql | `SMALLINT` 用于布尔字段 | 改为 `INTEGER` 或 `boolean` |
| paramcheck-opengauss.sql | JSONB、TIMESTAMPTZ、schema 前缀 `gnosis_sample.` | 适配 openGauss |
| liteflow-opengauss.sql | `BOOLEAN` 大小写 | 改为小写 |
| schema-opengauss.sql | 基本正确 | 无需修改 |
| openplat-opengauss.sql | 已修复 | 无需修改 |

---

## 三、修复步骤

### 阶段 1: 修复启动类

#### 1.1 修复 LifecycleApplication
```
文件: src/main/java/lifecycle/LifecycleApplication.java
修改:
- 移除 exclude = {DataSourceAutoConfiguration.class}
- 添加 @MapperScan("lifecycle.repository")
- 添加 @ComponentScan 排除其他模块
```

#### 1.2 修复 OpenplatApplication
```
文件: src/main/java/openplat/OpenplatApplication.java
修改:
- ComponentScan 仅扫描 "openplat"
- 添加排除其他模块的过滤器
```

#### 1.3 修复 ParamCheckApplication
```
文件: src/main/java/paramcheck/ParamCheckApplication.java
修改:
- 确认包扫描正确
- 可能需要添加 MyBatis MapperScan
```

### 阶段 2: 创建配置文件

#### 2.1 创建 application-lifecycle.properties
```
- 数据库连接: localserver.gnosis:5432/gnosis_sample
- MyBatis 配置: mapper/lifecycle/**/*.xml
- 排除其他模块的自动配置
```

#### 2.2 创建 application-paramcheck.properties
```
- 数据库连接: localserver.gnosis:5432/gnosis_sample
- MyBatis 配置: mapper/paramcheck/**/*.xml
- 排除其他模块的自动配置
```

### 阶段 3: 修复 SQL 脚本

#### 3.1 lifecycle-engine-opengauss.sql
```sql
-- BOOLEAN -> boolean (保持兼容性)
is_active INTEGER DEFAULT 1  -- 或保持 BOOLEAN 但用小写
```

#### 3.2 lifecycle-exit-governance-opengauss.sql
```sql
-- SMALLINT DEFAULT 0 -> INTEGER DEFAULT 0
allow_reactivate SMALLINT -> INTEGER
is_async SMALLINT -> INTEGER
is_active SMALLINT -> INTEGER
is_required SMALLINT -> INTEGER
is_searchable SMALLINT -> INTEGER
is_display SMALLINT -> INTEGER
is_current_version SMALLINT -> INTEGER
```

#### 3.3 paramcheck-opengauss.sql
```sql
-- JSONB -> TEXT (openGauss 可能不完全支持)
-- TIMESTAMPTZ -> TIMESTAMP
-- 移除 gnosis_sample. schema 前缀
-- 移除 ::jsonb 类型转换语法
```

#### 3.4 liteflow-opengauss.sql
```sql
-- BOOLEAN DEFAULT true -> boolean DEFAULT true
```

### 阶段 4: 生成启动脚本

#### 4.1 start-lifecycle.sh / start-lifecycle.bat
```bash
mvn spring-boot:run \
  -Dspring-boot.run.mainClass=lifecycle.LifecycleApplication \
  -Dspring-boot.run.profiles=lifecycle
```

#### 4.2 start-openplat.sh / start-openplat.bat (已修复)

#### 4.3 start-paramcheck.sh / start-paramcheck.bat
```bash
mvn spring-boot:run \
  -Dspring-boot.run.mainClass=paramcheck.ParamCheckApplication \
  -Dspring-boot.run.profiles=paramcheck
```

---

## 四、预期结果

### Lifecycle 模块
```bash
./start-lifecycle.sh
# 或
mvn spring-boot:run -Dlifecycle.LifecycleApplication -Dspring-boot.run.profiles=lifecycle
```
- 启动成功
- 访问 http://localhost:8080/swagger-ui.html

### Openplat 模块
```bash
./start-openplat.sh
# 或
mvn spring-boot:run -Dspring-boot.run.profiles=openplat-simple
```
- 启动成功
- 访问 http://localhost:8080/swagger-ui.html

### ParamCheck 模块
```bash
./start-paramcheck.sh
# 或
mvn spring-boot:run -Dparamcheck.ParamCheckApplication -Dspring-boot.run.profiles=paramcheck
```
- 启动成功
- 访问 http://localhost:8080/swagger-ui.html

---

## 五、风险评估

| 模块 | 风险等级 | 主要风险 |
|------|----------|----------|
| Lifecycle | 中 | 依赖 MyBatis 配置正确性 |
| Openplat | 低 | 已验证可启动 |
| ParamCheck | 中 | 可能有 Handler 类依赖问题 |

---

## 六、数据库初始化命令

```bash
# Lifecycle 模块
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/lifecycle-entry-opengauss.sql
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/lifecycle-engine-opengauss.sql
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/lifecycle-exit-governance-opengauss.sql

# Openplat 模块
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/openplat-opengauss.sql

# ParamCheck 模块
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/paramcheck-opengauss.sql

# LiteFlow 模块
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/liteflow-opengauss.sql
```
