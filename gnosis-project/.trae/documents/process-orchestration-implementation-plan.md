# 流程编排组件 (gnosis-process-orchestration) 实施计划

## 一、模块概述

新建 `gnosis-process-orchestration` 模块，实现业务流程编排系统的全部功能。包含5张核心表的管理（CRUD + 导入导出 + 批量操作），以及提供给内部系统调用的便利化API。

## 二、技术约束（严格遵循）

- Java 1.8，Spring Boot 2.1.10.RELEASE
- MyBatis + mapper.xml 管理SQL（不使用MyBatis-Plus注解方式）
- Controller 全部使用 POST 请求，请求和响应封装成对象
- 使用 CommonResponse 统一响应
- 必须包含 create_user_id / update_user_id / create_time / update_time 字段
- 禁止存储过程，所有逻辑用 Java 代码实现
- 每个页面至少需要：分页查询、所有字段条件查询、导入、导出、详情、新建、编辑、修改、批量删除、批量启用、批量禁用
- openGauss 3.0.0 数据库，不使用新版本语言特性
- 前端 React + Ant Design + Vite

## 三、后端文件清单

### 3.1 模块骨架
| 文件 | 说明 |
|------|------|
| `gnosis-process-orchestration/pom.xml` | Maven 配置，参照 gnosis-openplat |
| `.../ProcessOrchestrationApplication.java` | 启动类，端口 8085 |
| `.../application.properties` | 配置文件，连接 openGauss |

### 3.2 Domain 实体层（5个实体）
| 文件 | 对应表 |
|------|--------|
| `domain/base/BaseEntity.java` | 基础实体（id, createUserId, updateUserId, createTime, updateTime） |
| `domain/BusinessConfig.java` | sys_business_config |
| `domain/BusinessResourceBinding.java` | sys_business_resource_binding |
| `domain/ProcessOrchestrationConfig.java` | sys_process_orchestration_config |
| `domain/ProcessEventConfig.java` | sys_process_event_config |
| `domain/ProcessTransitionFlow.java` | sys_process_transition_flow |

### 3.3 DTO 层（请求/响应对象）
| 文件 | 说明 |
|------|------|
| `dto/CommonResponse.java` | 统一响应（复用 openplat 模式） |
| `dto/IdRequest.java` | 单ID请求 |
| `dto/IdsRequest.java` | 批量ID请求 |
| `dto/PageRequest.java` | 分页请求基类 |
| `dto/BusinessConfigCreateRequest.java` | 业务配置新建请求 |
| `dto/BusinessConfigUpdateRequest.java` | 业务配置更新请求 |
| `dto/BusinessConfigQueryRequest.java` | 业务配置分页查询请求 |
| `dto/BusinessConfigVO.java` | 业务配置响应VO |
| `dto/BusinessResourceBindingCreateRequest.java` | 资源绑定新建请求 |
| `dto/BusinessResourceBindingUpdateRequest.java` | 资源绑定更新请求 |
| `dto/BusinessResourceBindingQueryRequest.java` | 资源绑定分页查询请求 |
| `dto/BusinessResourceBindingVO.java` | 资源绑定响应VO |
| `dto/ProcessOrchestrationConfigCreateRequest.java` | 编排配置新建请求 |
| `dto/ProcessOrchestrationConfigUpdateRequest.java` | 编排配置更新请求 |
| `dto/ProcessOrchestrationConfigQueryRequest.java` | 编排配置分页查询请求 |
| `dto/ProcessOrchestrationConfigVO.java` | 编排配置响应VO |
| `dto/ProcessEventConfigCreateRequest.java` | 事件配置新建请求 |
| `dto/ProcessEventConfigUpdateRequest.java` | 事件配置更新请求 |
| `dto/ProcessEventConfigQueryRequest.java` | 事件配置分页查询请求 |
| `dto/ProcessEventConfigVO.java` | 事件配置响应VO |
| `dto/ProcessTransitionFlowQueryRequest.java` | 流转日志分页查询请求 |
| `dto/ProcessTransitionFlowVO.java` | 流转日志响应VO |
| `dto/ProcessTriggerRequest.java` | 流程触发请求（内部API） |
| `dto/ProcessTriggerResult.java` | 流程触发结果（内部API） |
| `dto/ProcessEventSubmitRequest.java` | 事件提交请求（内部API） |
| `dto/ProcessEventSubmitResult.java` | 事件提交结果（内部API） |
| `dto/ProcessTransitionQueryRequest.java` | 流转查询请求（内部API） |
| `dto/PageResult.java` | 分页响应封装 |

### 3.4 Mapper 层（5个接口 + 5个XML）
| 文件 | 说明 |
|------|------|
| `mapper/BusinessConfigMapper.java` | 业务配置 Mapper 接口 |
| `mapper/BusinessResourceBindingMapper.java` | 资源绑定 Mapper 接口 |
| `mapper/ProcessOrchestrationConfigMapper.java` | 编排配置 Mapper 接口 |
| `mapper/ProcessEventConfigMapper.java` | 事件配置 Mapper 接口 |
| `mapper/ProcessTransitionFlowMapper.java` | 流转日志 Mapper 接口 |
| `resources/mapper/process/BusinessConfigMapper.xml` | 业务配置 SQL |
| `resources/mapper/process/BusinessResourceBindingMapper.xml` | 资源绑定 SQL |
| `resources/mapper/process/ProcessOrchestrationConfigMapper.xml` | 编排配置 SQL |
| `resources/mapper/process/ProcessEventConfigMapper.xml` | 事件配置 SQL |
| `resources/mapper/process/ProcessTransitionFlowMapper.xml` | 流转日志 SQL |

### 3.5 Service 层（5个接口 + 5个实现 + 1个引擎服务）
| 文件 | 说明 |
|------|------|
| `service/BusinessConfigService.java` | 业务配置服务接口 |
| `service/BusinessResourceBindingService.java` | 资源绑定服务接口 |
| `service/ProcessOrchestrationConfigService.java` | 编排配置服务接口 |
| `service/ProcessEventConfigService.java` | 事件配置服务接口 |
| `service/ProcessTransitionFlowService.java` | 流转日志服务接口 |
| `service/ProcessOrchestrationEngine.java` | 流程编排引擎（核心：条件匹配、流转触发） |
| `service/impl/BusinessConfigServiceImpl.java` | 业务配置服务实现 |
| `service/impl/BusinessResourceBindingServiceImpl.java` | 资源绑定服务实现 |
| `service/impl/ProcessOrchestrationConfigServiceImpl.java` | 编排配置服务实现 |
| `service/impl/ProcessEventConfigServiceImpl.java` | 事件配置服务实现 |
| `service/impl/ProcessTransitionFlowServiceImpl.java` | 流转日志服务实现 |
| `service/impl/ProcessOrchestrationEngineImpl.java` | 流程编排引擎实现 |

### 3.6 Controller 层（5个管理Controller + 1个内部API Controller）
| 文件 | 说明 |
|------|------|
| `controller/BusinessConfigController.java` | 业务配置管理（CRUD+批量+导入导出） |
| `controller/BusinessResourceBindingController.java` | 资源绑定管理 |
| `controller/ProcessOrchestrationConfigController.java` | 编排配置管理 |
| `controller/ProcessEventConfigController.java` | 事件配置管理 |
| `controller/ProcessTransitionFlowController.java` | 流转日志管理（只读+导出） |
| `controller/ProcessOrchestrationApiController.java` | 内部系统调用API |

### 3.7 SQL 建表脚本
| 文件 | 说明 |
|------|------|
| `resources/db/process/process-orchestration-opengauss.sql` | 5张表的DDL |

### 3.8 工具类
| 文件 | 说明 |
|------|------|
| `util/IdGenerator.java` | ID生成器 |
| `util/ConditionExpressionEvaluator.java` | 条件表达式求值器 |

## 四、前端文件清单

### 4.1 基础配置
| 文件 | 说明 |
|------|------|
| `frontend/package.json` | 依赖配置 |
| `frontend/vite.config.js` | Vite配置，端口3004，代理到8085 |
| `frontend/index.html` | 入口HTML |
| `frontend/src/main.jsx` | 入口JS |
| `frontend/src/index.css` | 全局样式 |
| `frontend/src/App.jsx` | 主布局（侧边栏+路由） |
| `frontend/src/utils/request.js` | Axios封装 |

### 4.2 API 层
| 文件 | 说明 |
|------|------|
| `frontend/src/api/businessConfigApi.js` | 业务配置API |
| `frontend/src/api/resourceBindingApi.js` | 资源绑定API |
| `frontend/src/api/orchestrationConfigApi.js` | 编排配置API |
| `frontend/src/api/eventConfigApi.js` | 事件配置API |
| `frontend/src/api/transitionFlowApi.js` | 流转日志API |

### 4.3 页面层（5个管理页面）
| 文件 | 说明 |
|------|------|
| `frontend/src/pages/BusinessConfigManagement.jsx` | 业务配置管理 |
| `frontend/src/pages/ResourceBindingManagement.jsx` | 资源绑定管理 |
| `frontend/src/pages/OrchestrationConfigManagement.jsx` | 编排配置管理 |
| `frontend/src/pages/EventConfigManagement.jsx` | 事件配置管理 |
| `frontend/src/pages/TransitionFlowManagement.jsx` | 流转日志管理 |

## 五、内部API设计（给内部系统调用）

### 5.1 流程触发 API
- **POST** `/api/process-orchestration/internal/trigger`
- 输入：businessCode, processDefKey, triggerEvent, contextData(Map), operatorId
- 逻辑：根据businessCode找到BusinessConfig → 根据processDefKey+triggerEvent找到OrchestrationConfig → 评估conditionExpression → 匹配到nextProcessDefKey → 触发事件配置 → 记录流转日志
- 输出：ProcessTriggerResult (success, nextProcessDefKey, transitionFlowId, message)

### 5.2 事件提交 API
- **POST** `/api/process-orchestration/internal/event/submit`
- 输入：processDefKey, nodeDefKey, submitAction, businessCode, businessId, contextData(Map), operatorId
- 逻辑：根据processDefKey+nodeDefKey+submitAction+businessCode找到ProcessEventConfig → 执行changeBusinessStatus → 记录流转日志
- 输出：ProcessEventSubmitResult (success, changedStatus, eventConfigId, message)

### 5.3 流转查询 API
- **POST** `/api/process-orchestration/internal/transition/query`
- 输入：processInstanceId / businessId / businessCode
- 输出：List<ProcessTransitionFlowVO> 完整流转链路

### 5.4 获取业务资源 API
- **POST** `/api/process-orchestration/internal/resource/query`
- 输入：businessCode, resourceType
- 输出：List<BusinessResourceBindingVO> 该业务绑定的资源列表

### 5.5 获取下一流程节点 API
- **POST** `/api/process-orchestration/internal/next-node/query`
- 输入：businessCode, currentProcessDefKey, triggerEvent, contextData(Map)
- 逻辑：评估条件表达式，返回匹配的下一节点列表
- 输出：List<ProcessOrchestrationConfigVO> 匹配的编排配置列表

## 六、每个管理Controller的标准API列表

每个管理Controller（除流转日志外）均提供以下API（全部POST）：

| API路径 | 说明 |
|---------|------|
| `/list` | 分页查询（支持所有字段条件查询） |
| `/detail` | 详情查询 |
| `/save` | 新建 |
| `/update` | 编辑修改 |
| `/delete` | 单条删除 |
| `/batchDelete` | 批量删除 |
| `/batchEnable` | 批量启用 |
| `/batchDisable` | 批量禁用 |
| `/import` | 导入Excel |
| `/export` | 导出Excel |

流转日志Controller（只读）：
| API路径 | 说明 |
|---------|------|
| `/list` | 分页查询 |
| `/detail` | 详情查询 |
| `/export` | 导出Excel |

## 七、实施步骤

### 阶段1：后端骨架搭建
1. 创建模块目录结构 `gnosis-process-orchestration`
2. 编写 `pom.xml`
3. 编写 `ProcessOrchestrationApplication.java` 启动类
4. 编写 `application.properties`
5. 编写 SQL 建表脚本
6. 更新父 pom.xml 添加 module

### 阶段2：后端基础层
7. 编写 BaseEntity
8. 编写 5个 Domain 实体类
9. 编写 DTO 层（CommonResponse, IdRequest, IdsRequest, PageRequest, PageResult, 各表请求/响应DTO）
10. 编写 IdGenerator 工具类
11. 编写 ConditionExpressionEvaluator 条件表达式求值器

### 阶段3：后端Mapper层
12. 编写 5个 Mapper 接口
13. 编写 5个 Mapper XML（含分页、条件查询、批量操作）

### 阶段4：后端Service层
14. 编写 5个 Service 接口
15. 编写 5个 Service 实现（含分页、条件查询、批量操作、导入导出）
16. 编写 ProcessOrchestrationEngine 引擎接口和实现

### 阶段5：后端Controller层
17. 编写 5个管理 Controller
18. 编写 1个内部API Controller

### 阶段6：前端搭建
19. 创建 frontend 目录结构
20. 编写 package.json, vite.config.js, index.html
21. 编写 main.jsx, App.jsx, index.css, request.js
22. 编写 5个 API 文件
23. 编写 5个管理页面组件

### 阶段7：验证
24. 编译后端项目
25. 启动后端服务验证
26. 启动前端服务验证

## 八、关键设计决策

1. **ID策略**：使用雪花算法生成字符串ID（与openplat模块一致）
2. **分页实现**：在Mapper XML中使用LIMIT/OFFSET，Service层计算offset
3. **条件表达式求值**：使用Aviator表达式引擎（轻量级，已列入技术栈），如不可用则使用简单的反射+OGNL
4. **导入导出**：使用Apache POI 4.1.2
5. **启用/禁用**：BusinessConfig使用status字段(active/locked/deleted)，其他表使用is_active字段
6. **流转日志**：只增不改，不提供删除和修改功能
7. **端口分配**：后端8085，前端3004
