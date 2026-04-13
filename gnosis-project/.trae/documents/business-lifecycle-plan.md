# 业务生命周期管理组件开发计划

## 项目概述

基于 `business-lifecycle.md` 需求文档，开发一个业务生命周期管理组件，管理业务生命周期的入口控制、事件执行和出口控制。

## 技术栈要求

严格遵循以下技术规范：
- **后端**: Spring Boot 2.1.10.RELEASE + Java 1.8 + MyBatis + openGauss 3.0.0
- **前端**: React (注意：当前项目使用 Vue，需要调整或新建 React 前端)
- **包路径**: `paramcheck.lifecycle` (遵循通用约束.md 要求)
- **禁止**: 存储过程，所有逻辑用 Java 实现

## 模块划分

### 一、数据库设计模块

#### 1.1 核心表结构

1. **lifecycle_business_type** - 业务类型表
   - id, name, code, description, entry_permission_config
   - create_user_id, update_user_id, create_time, update_time

2. **lifecycle_state** - 状态节点表
   - id, business_type_id, state_name, state_code, state_type (initial/normal/final)
   - is_final_state, position_x, position_y, metadata
   - create_user_id, update_user_id, create_time, update_time

3. **lifecycle_state_transition** - 状态流转关系表
   - id, business_type_id, from_state_id, to_state_id, transition_name
   - transition_code, condition_expression, priority
   - create_user_id, update_user_id, create_time, update_time

4. **lifecycle_event_metadata** - 事件元数据字典表
   - id, event_type, metadata_key, metadata_type, description
   - allowed_values, is_required, sort_order
   - create_user_id, update_user_id, create_time, update_time

5. **lifecycle_event** - 事件记录表
   - id, business_type_id, business_id, event_type, event_data (JSON)
   - metadata_snapshot (JSON), source_system, status
   - create_user_id, update_user_id, create_time, update_time

6. **lifecycle_instance** - 生命周期实例表
   - id, business_type_id, business_id, current_state_id, status
   - started_at, finished_at, final_state_id
   - create_user_id, update_user_id, create_time, update_time

7. **lifecycle_instance_trace** - 实例流转追踪表
   - id, instance_id, from_state_id, to_state_id, event_id
   - transition_id, matched_condition, execution_result, error_message
   - create_user_id, update_user_id, create_time, update_time

8. **lifecycle_route_rule** - 路由规则表
   - id, business_type_id, event_type, rule_name, condition_expression
   - priority, is_active, version
   - create_user_id, update_user_id, create_time, update_time

9. **lifecycle_downstream_config** - 下游业务配置表
   - id, final_state_id, downstream_business_type, trigger_condition
   - trigger_mode, config_data, is_active
   - create_user_id, update_user_id, create_time, update_time

### 二、后端开发模块

#### 2.1 入口控制模块 (paramcheck.lifecycle.entry)

**功能点**:
- 业务入口注册管理
- 基础数据校验配置
- 发起权限控制

**需要开发的类**:
1. `EntryController` - 入口管理 REST API
2. `EntryService` - 入口业务逻辑
3. `BusinessTypeManager` - 业务类型管理
4. `EntryValidator` - 基础校验器
5. `PermissionChecker` - 权限检查器

**API 接口**:
- `POST /api/lifecycle/entry/register` - 注册业务入口
- `GET /api/lifecycle/entry/list` - 获取业务入口列表
- `PUT /api/lifecycle/entry/{id}` - 更新业务入口
- `DELETE /api/lifecycle/entry/{id}` - 删除业务入口
- `POST /api/lifecycle/entry/{id}/validate` - 校验入口数据

#### 2.2 事件执行引擎模块 (paramcheck.lifecycle.engine) - **核心**

**功能点**:
- 生命周期状态模型管理
- 事件 - 状态路由配置
- 工作流事件标准化 Adapter
- 条件路由执行引擎
- 路由规则测试沙箱

**需要开发的类**:
1. `EventController` - 事件管理 REST API
2. `EventService` - 事件处理服务
3. `StateModelManager` - 状态模型管理
4. `RouteRuleManager` - 路由规则管理
5. `WorkflowEventAdapter` - 工作流事件适配器
6. `ConditionRouterEngine` - 条件路由引擎
7. `RuleTestSandbox` - 规则测试沙箱
8. `StateTransitionExecutor` - 状态流转执行器

**API 接口**:
- `POST /api/lifecycle/event` - 接收事件（核心接口）
- `GET /api/lifecycle/state/model/{businessTypeId}` - 获取状态模型
- `POST /api/lifecycle/state/model` - 创建状态模型
- `PUT /api/lifecycle/state/model/{id}` - 更新状态模型
- `POST /api/lifecycle/route/rule` - 创建路由规则
- `PUT /api/lifecycle/route/rule/{id}` - 更新路由规则
- `GET /api/lifecycle/route/rule/list` - 获取路由规则列表
- `POST /api/lifecycle/route/test` - 测试路由规则
- `GET /api/lifecycle/event/metadata/dictionary` - 获取事件元数据字典

#### 2.3 出口控制模块 (paramcheck.lifecycle.exit)

**功能点**:
- 终态标识配置
- 下游触发配置
- 标准事件发布

**需要开发的类**:
1. `ExitController` - 出口管理 REST API
2. `ExitService` - 出口业务逻辑
3. `FinalStateManager` - 终态管理器
4. `DownstreamTrigger` - 下游触发器
5. `LifecycleEventPublisher` - 生命周期事件发布器

**API 接口**:
- `POST /api/lifecycle/exit/final-state` - 配置终态
- `POST /api/lifecycle/exit/downstream` - 配置下游触发
- `GET /api/lifecycle/exit/downstream/{finalStateId}` - 获取下游配置
- `PUT /api/lifecycle/exit/downstream/{id}` - 更新下游配置

#### 2.4 配置治理模块 (paramcheck.lifecycle.governance)

**功能点**:
- 事件元数据字典管理
- 路由规则版本管理
- 全链路追踪看板

**需要开发的类**:
1. `GovernanceController` - 配置治理 REST API
2. `MetadataDictionaryService` - 元数据字典服务
3. `RouteRuleVersionService` - 路由规则版本服务
4. `TraceService` - 追踪服务
5. `TraceViewService` - 追踪看板服务

**API 接口**:
- `POST /api/lifecycle/governance/metadata` - 创建元数据
- `GET /api/lifecycle/governance/metadata/list` - 获取元数据列表
- `GET /api/lifecycle/governance/rule-version/{ruleId}` - 获取规则版本历史
- `POST /api/lifecycle/governance/rule-version/{id}/rollback` - 回滚规则版本
- `GET /api/lifecycle/governance/trace/{instanceId}` - 获取实例追踪
- `GET /api/lifecycle/governance/trace/dashboard` - 获取追踪看板数据

#### 2.5 领域模型 (paramcheck.lifecycle.domain)

**需要开发的实体类**:
1. `LifecycleBusinessType` - 业务类型实体
2. `LifecycleState` - 状态节点实体
3. `LifecycleStateTransition` - 状态流转实体
4. `LifecycleEventMetadata` - 事件元数据实体
5. `LifecycleEvent` - 事件实体
6. `LifecycleInstance` - 生命周期实例实体
7. `LifecycleInstanceTrace` - 实例追踪实体
8. `LifecycleRouteRule` - 路由规则实体
9. `LifecycleDownstreamConfig` - 下游配置实体

#### 2.6 数据访问层 (paramcheck.lifecycle.repository)

**需要开发的 Mapper 接口和 XML**:
1. `LifecycleBusinessTypeMapper` + XML
2. `LifecycleStateMapper` + XML
3. `LifecycleStateTransitionMapper` + XML
4. `LifecycleEventMetadataMapper` + XML
5. `LifecycleEventMapper` + XML
6. `LifecycleInstanceMapper` + XML
7. `LifecycleInstanceTraceMapper` + XML
8. `LifecycleRouteRuleMapper` + XML
9. `LifecycleDownstreamConfigMapper` + XML

#### 2.7 DTO 和 VO (paramcheck.lifecycle.dto)

**请求 DTO**:
1. `BusinessTypeCreateRequest`
2. `StateModelCreateRequest`
3. `RouteRuleCreateRequest`
4. `EventSubmitRequest`
5. `RouteTestRequest`
6. `DownstreamConfigRequest`

**响应 VO**:
1. `BusinessTypeVO`
2. `StateModelVO`
3. `RouteRuleVO`
4. `EventVO`
5. `InstanceTraceVO`
6. `TraceDashboardVO`

### 三、前端开发模块 (React)

**注意**: 当前项目使用 Vue，需要：
- 方案 A: 调整前端技术栈为 React
- 方案 B: 新建独立的 React 前端项目

#### 3.1 页面组件

1. **业务类型管理页面** (`BusinessTypeManagement.jsx`)
   - 业务类型列表（分页）
   - 新建/编辑/删除业务类型
   - 业务类型详情

2. **状态模型配置页面** (`StateModelConfig.jsx`)
   - 可视化状态节点配置
   - 状态流转关系配置
   - 状态模型导入/导出

3. **路由规则配置页面** (`RouteRuleConfig.jsx`)
   - 路由规则列表（分页）
   - 新建/编辑/删除路由规则
   - 规则条件表达式配置
   - 规则版本管理
   - 规则测试沙箱

4. **事件监控页面** (`EventMonitor.jsx`)
   - 事件记录列表（分页）
   - 事件详情查看
   - 事件元数据快照

5. **实例追踪页面** (`InstanceTrace.jsx`)
   - 生命周期实例列表（分页）
   - 实例流转路径可视化
   - 全链路追踪看板

6. **出口配置页面** (`ExitConfig.jsx`)
   - 终态配置
   - 下游触发配置
   - 事件发布配置

7. **元数据字典页面** (`MetadataDictionary.jsx`)
   - 事件元数据字段管理
   - 字段类型和约束配置

#### 3.2 通用组件

1. `StateDiagramCanvas` - 状态图绘制画布
2. `ConditionExpressionEditor` - 条件表达式编辑器
3. `RouteRuleTester` - 规则测试器
4. `TraceTimeline` - 追踪时间轴

#### 3.3 API 服务

1. `lifecycleApi.js` - 生命周期管理 API
2. `eventApi.js` - 事件管理 API
3. `ruleApi.js` - 规则管理 API
4. `traceApi.js` - 追踪 API

### 四、数据库脚本模块

**需要创建的 SQL 文件**:
1. `lifecycle-schema-opengauss.sql` - 表结构定义
2. `lifecycle-init-data.sql` - 初始化数据
3. `lifecycle-indexes.sql` - 索引定义
4. `lifecycle-constraints.sql` - 约束定义

### 五、测试模块

#### 5.1 单元测试

1. `EntryServiceTest` - 入口服务测试
2. `ConditionRouterEngineTest` - 路由引擎测试
3. `WorkflowEventAdapterTest` - 事件适配器测试
4. `StateTransitionExecutorTest` - 状态流转测试
5. `TraceServiceTest` - 追踪服务测试

#### 5.2 集成测试

1. `LifecycleIntegrationTest` - 生命周期集成测试
2. `EventProcessingIntegrationTest` - 事件处理集成测试
3. `RouteRuleIntegrationTest` - 路由规则集成测试

#### 5.3 API 测试

1. `EntryApiTest` - 入口 API 测试
2. `EventApiTest` - 事件 API 测试
3. `GovernanceApiTest` - 治理 API 测试

### 六、文档模块

1. `LIFECYCLE_USER_GUIDE.md` - 用户指南
2. `LIFECYCLE_API_DOC.md` - API 文档
3. `LIFECYCLE_DEPLOYMENT.md` - 部署指南
4. `LIFECYCLE_TEST_REPORT.md` - 测试报告

## 开发步骤规划

### 阶段一：数据库设计和基础架构 (Day 1)

1. 创建数据库表结构 SQL 脚本
2. 执行 SQL 脚本创建表
3. 创建领域模型实体类
4. 创建 Mapper 接口和 XML

### 阶段二：入口控制模块开发 (Day 2)

1. 开发入口控制 Service 层
2. 开发入口控制 Controller 层
3. 开发入口校验器
4. 编写单元测试

### 阶段三：事件执行引擎开发 (Day 3-4) - **核心**

1. 开发状态模型管理
2. 开发路由规则管理
3. 开发工作流事件适配器
4. 开发条件路由引擎
5. 开发规则测试沙箱
6. 编写单元测试和集成测试

### 阶段四：出口控制模块开发 (Day 5)

1. 开发终态管理器
2. 开发下游触发器
3. 开发事件发布器
4. 编写单元测试

### 阶段五：配置治理模块开发 (Day 6)

1. 开发元数据字典服务
2. 开发规则版本管理
3. 开发追踪服务
4. 开发追踪看板
5. 编写单元测试

### 阶段六：前端开发 (Day 7-9)

1. 搭建 React 前端项目结构
2. 开发业务类型管理页面
3. 开发状态模型配置页面
4. 开发路由规则配置页面
5. 开发事件监控页面
6. 开发实例追踪页面
7. 开发出口配置页面
8. 开发元数据字典页面

### 阶段七：集成测试和验证 (Day 10)

1. 执行集成测试
2. 执行 API 测试
3. 性能测试
4. 修复 Bug

### 阶段八：文档和交付 (Day 11)

1. 编写用户指南
2. 编写 API 文档
3. 编写部署指南
4. 编写测试报告
5. 最终交付

## 智能体任务分配

### 1. backend-architect
**任务**: 设计后端架构、数据库设计、核心引擎实现
- 数据库表结构设计
- 领域模型设计
- 事件执行引擎架构
- 条件路由引擎实现

### 2. backend-developer
**任务**: 开发具体后端代码
- 入口控制模块开发
- 出口控制模块开发
- 配置治理模块开发
- Mapper XML 开发
- 单元测试编写

### 3. react-frontend-developer
**任务**: 前端 React 开发
- 搭建 React 项目结构
- 开发所有前端页面
- 开发通用组件
- API 集成

### 4. api-test-pro
**任务**: API 测试
- 接口测试
- 性能测试
- 负载测试

### 5. java-testing-expert
**任务**: 单元测试和集成测试
- 编写单元测试
- 编写集成测试
- 代码覆盖率分析

## 关键技术点

### 1. 条件路由引擎实现

```java
// 条件表达式解析和匹配
public class ConditionRouterEngine {
    // 支持 AND/OR 组合
    // 支持优先级排序
    // 首条匹配生效
}
```

### 2. 工作流事件标准化

```java
// 将不同工作流平台的事件转换为标准格式
public class WorkflowEventAdapter {
    // 提取标准化元数据
    // action, node_id, operator 等
}
```

### 3. 状态流转执行

```java
// 状态机实现
public class StateTransitionExecutor {
    // 状态验证
    // 流转执行
    // 追踪记录
}
```

### 4. 全链路追踪

```java
// 记录完整的事件流转路径
public class TraceService {
    // 事件接收记录
    // 条件匹配路径
    // 状态变更流水
}
```

## 交付物清单

1. ✅ 完整的后端代码（paramcheck.lifecycle 包）
2. ✅ 完整的 React 前端代码
3. ✅ 数据库 SQL 脚本
4. ✅ 单元测试和集成测试
5. ✅ API 测试报告
6. ✅ 用户指南
7. ✅ API 文档
8. ✅ 部署指南

## 注意事项

1. **技术栈版本**: 严格遵循 Java 1.8、Spring Boot 2.1.10.RELEASE
2. **数据库**: 使用 openGauss 3.0.0，不使用新版本特性
3. **包路径**: 所有代码参考项目规范建包
4. **禁止存储过程**: 所有逻辑用 Java 实现
5. **审计字段**: 所有表必须包含 create_user_id, update_user_id, create_time, update_time
6. **功能完整性**: 每个页面至少包含分页查询、条件查询、导入、导出、详情、新建、编辑、修改、批量删除、批量启用、批量禁用
