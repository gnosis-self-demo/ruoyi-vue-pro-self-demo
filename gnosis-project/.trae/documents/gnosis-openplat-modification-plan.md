# gnosis-openplat 模块修改计划

本项目计划对 `gnosis-openplat` 模块进行前后端同步修改，以满足新的业务需求。主要涉及 API 配置调整、对接系统信息增强、限流逻辑实现以及代码清理。

## 1. 当前状态分析

### 1.1 后端结构
- **领域模型**: `OpenplatApiConfig` 包含 `needTimestamp` 和 `needNonce` 字段；`OpenplatSystem` 缺少 `appId` 和 `appSecret`。
- **拦截器**: `OpenplatAuthInterceptor` 仅验证 `timestamp`、`nonce` 和 `systemId` 的存在性及过期时间，未进行签名验证，且未实现限流。
- **数据库**: `openplat_api_config` 和 `openplat_system` 表结构与领域模型一致；存在冗余表 `openplat_app_auth`。
- **未使用类**: `OpenplatApiDoc`、`OpenplatRateLimit`、`OpenplatNonceCache`、`OpenplatAccessLog` 等类在业务逻辑中无实际调用。

### 1.2 前端结构
- **页面**: `ApiConfigManagement.jsx` 和 `SystemManagement.jsx` 提供了 CRUD 功能，但字段需要根据后端变更进行更新。

---

## 2. 变更内容

### 2.1 数据库变更 (`openplat-opengauss.sql`)
- **openplat_api_config**:
    - 删除字段 `need_timestamp`, `need_nonce`。
    - 增加字段 `need_anti_replay` (boolean, 默认 false)。
- **openplat_system**:
    - 增加字段 `app_id` (varchar(64))。
    - 增加字段 `app_secret` (varchar(128))。
- **清理**:
    - 删除 `openplat_app_auth` 表（信息已整合至 `openplat_system`）。
    - 删除 `openplat_api_doc` 表。
    - 删除 `openplat_rate_limit` 表。
    - 删除 `openplat_nonce_cache` 表（改用内存缓存或在 API 配置中体现）。
    - 删除 `openplat_access_log` 表。

### 2.2 后端变更 (Java)

#### 2.2.1 领域模型与 Mapper
- **OpenplatApiConfig.java**: 删除 `needTimestamp`, `needNonce`；添加 `needAntiReplay`。
- **OpenplatSystem.java**: 添加 `appId`, `appSecret`。
- **Mapper.xml**: 更新 `OpenplatApiConfigMapper.xml` 和 `OpenplatSystemMapper.xml` 中的 SQL 语句。

#### 2.2.2 鉴权与限流逻辑 (`OpenplatAuthInterceptor.java`)
- **签名验证**: 
    - 修改校验逻辑，从 `systemId` 改为 `appId`。
    - 根据 `appId` 查询系统信息，获取 `appSecret`。
    - 实现签名算法校验（参考 SDK 逻辑）。
- **防重放**:
    - 若 API 配置了 `needAntiReplay`，校验 `nonce`。使用 `Caffeine` 缓存已使用的 `nonce`，有效期同时间戳有效期（5分钟）。
- **限流实现**:
    - 使用 `Caffeine` 缓存，以 `apiPath + appId` 为 Key，记录请求次数。
    - 在 `preHandle` 中根据 `OpenplatApiConfig` 的 `rateLimit` 字段进行拦截。

#### 2.2.3 对外查询 API
- **OpenplatApiConfigController / Service**:
    - 增加根据 `apiCode` 查询 API 配置及关联系统信息的接口。
- **OpenplatSystemController / Service**:
    - 增加根据 `systemCode` 查询系统信息及关联 API 信息的接口。

#### 2.2.4 代码清理
- 删除以下类及对应的 Mapper、Service：
    - `OpenplatApiDoc`, `OpenplatApiDocMapper`, `OpenplatApiDocMapper.xml`
    - `OpenplatRateLimit`, `OpenplatRateLimitMapper`, `OpenplatRateLimitMapper.xml`
    - `OpenplatNonceCache`, `OpenplatNonceCacheMapper`, `OpenplatNonceCacheMapper.xml`
    - `OpenplatAccessLog`, `OpenplatAccessLogMapper`, `OpenplatAccessLogMapper.xml`, `OpenplatAccessLogService`, `OpenplatAccessLogServiceImpl`
    - `OpenplatAppAuth` 相关逻辑（若存在）。

### 2.3 前端变更 (React)

#### 2.3.1 API 配置管理 (`ApiConfigManagement.jsx`)
- 表格列: 删除“需要时间戳”、“需要 nonce”，增加“防重放”。
- 表单: 删除 `needTimestamp`, `needNonce` 切换开关，增加 `needAntiReplay` 切换开关。

#### 2.3.2 系统管理 (`SystemManagement.jsx`)
- 表格列: 增加 `App ID`。
- 表单: 增加 `appId` 和 `appSecret` 输入框。

---

## 3. 验证步骤

### 3.1 自动化测试
- 编写单元测试或集成测试，模拟 SDK 调用。
- 验证签名校验是否正确。
- 验证限流逻辑是否生效。
- 验证防重放逻辑是否生效。

### 3.2 手动验证
- 启动前后端应用。
- 在页面上修改 API 配置和系统信息，观察数据库变更。
- 使用更新后的 `OpenplatSdk` 或 `Postman` 调用开放接口，确保鉴权通过。

---

## 4. 假设与决策
- **限流精度**: 采用本地缓存 `Caffeine` 实现，不追求分布式环境下的绝对精确，满足基本需求。
- **防重放实现**: 同样采用 `Caffeine` 存储 `nonce`，避免依赖外部 Redis，保持模块轻量。
- **签名算法**: 统一使用 MD5 签名，算法逻辑与 `OpenplatSdk` 保持一致。
