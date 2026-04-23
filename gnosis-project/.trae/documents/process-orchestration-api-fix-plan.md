# 流程编排组件接口修复计划

## 审计发现汇总

后端审计发现 **20个问题**（3严重/5高/5中/7低），前端审计发现 **14个问题**（3严重/5高/4中/2低）。

---

## 修复清单（按优先级排序）

### P0 - 严重问题（功能完全不可用）

| # | 问题 | 文件 | 修复方案 |
|---|------|------|----------|
| 1 | 导出只导出第一页数据 | 5个ServiceImpl + 5个Mapper XML | 新增 `selectAllForExport` 方法，不带 LIMIT/OFFSET |
| 2 | selectCount 条件与 selectList 不一致 | 4个Mapper XML | 补齐 selectCount 的 where 条件 |
| 3 | queryTransitions 按 businessCode 查询逻辑错误 | ProcessOrchestrationEngineImpl | 改为通过 processDefKey 关联查询 |
| 4 | 前端分页参数名错误（current/size → pageNum/pageSize） | 5个页面组件 | 修改分页参数名 |
| 5 | 前端导出 Blob 响应被拦截器破坏 | request.js | 导出请求跳过响应拦截器 |

### P1 - 高优先级问题（核心业务逻辑错误）

| # | 问题 | 文件 | 修复方案 |
|---|------|------|----------|
| 6 | trigger() 未校验 BusinessConfig 状态 | ProcessOrchestrationEngineImpl | 增加 status="active" 校验 |
| 7 | submitEvent() 未设置 processDefUniqueKey | ProcessOrchestrationEngineImpl | 从 eventConfig 获取并设置 |
| 8 | processInstanceId/Sequence 逻辑错误 | ProcessOrchestrationEngineImpl | ProcessEventSubmitRequest 增加 processInstanceId 字段，sequence 自增 |
| 9 | ProcessTriggerRequest 缺少 businessId | ProcessTriggerRequest + EngineImpl | 增加 businessId 字段 |
| 10 | @Transactional 与 try-catch 导致事务失效 | ProcessOrchestrationEngineImpl | 重构异常处理，让异常传播出方法边界 |
| 11 | 前端表格缺少创建人/更新人/时间列 | 5个页面组件 | 补充缺失的列定义 |
| 12 | 前端搜索表单缺少部分查询字段 | 5个页面组件 | 补充缺失的搜索字段 |

### P2 - 中优先级问题（配置/规范/健壮性）

| # | 问题 | 文件 | 修复方案 |
|---|------|------|----------|
| 13 | 数据源配置应使用 openGauss 驱动 | application.properties | 改为 opengauss 驱动 |
| 14 | MyBatis 配置前缀应使用 mybatis-plus | application.properties | 改为 mybatis-plus.* 前缀 |
| 15 | QueryRequest 时间字段类型不匹配 | 5个QueryRequest DTO | 增加 startTime/endTime Date 类型字段 |
| 16 | importData 缺少事务控制 | 5个ServiceImpl | 添加 @Transactional |
| 17 | 前端新建/编辑表单缺少 createUserId/updateUserId | 5个页面组件 | 表单中添加操作人ID字段 |

### P3 - 低优先级问题（代码质量）

| # | 问题 | 文件 | 修复方案 |
|---|------|------|----------|
| 18 | batchEnable/Disable 硬编码 "system" | 5个Controller | 从请求中获取操作人ID |
| 19 | 导出 workbook 资源管理 | 5个ServiceImpl | 使用 try-with-resources |
| 20 | getCellStringValue FORMULA/NUMERIC 处理 | 5个ServiceImpl | 改进单元格值读取逻辑 |
| 21 | update_time CURRENT_TIMESTAMP 与 Java 端不一致 | 5个Mapper XML | 统一使用数据库时间 |
| 22 | submitEvent 未设置 orchestrationConfigId | ProcessOrchestrationEngineImpl | 设为 null 即可（事件提交不一定关联编排配置） |

---

## 实施步骤

### 步骤1：修复后端 P0 问题
1. 5个 Mapper XML 新增 `selectAllForExport` 方法
2. 5个 Mapper 接口新增 `selectAllForExport` 方法声明
3. 5个 ServiceImpl 的 exportData 改用 selectAllForExport
4. 4个 Mapper XML 的 selectCount 补齐 where 条件
5. 修复 ProcessOrchestrationEngineImpl.queryTransitions 逻辑

### 步骤2：修复后端 P1 问题
6. ProcessOrchestrationEngineImpl.trigger() 增加状态校验
7. ProcessOrchestrationEngineImpl.submitEvent() 设置 processDefUniqueKey
8. ProcessEventSubmitRequest 增加 processInstanceId 字段
9. ProcessTriggerRequest 增加 businessId 字段
10. ProcessOrchestrationEngineImpl 重构事务和异常处理
11. trigger() 中设置 businessId 到 flow

### 步骤3：修复后端 P2 问题
12. application.properties 改用 openGauss 驱动和 mybatis-plus 前缀
13. 5个 QueryRequest 增加 startTime/endTime 字段
14. 5个 Mapper XML 的时间查询改为范围查询
15. 5个 ServiceImpl 的 importData 添加 @Transactional

### 步骤4：修复前端 P0 问题
16. 5个页面组件分页参数名修正
17. request.js 导出请求跳过拦截器

### 步骤5：修复前端 P1 问题
18. 5个页面组件补充缺失的表格列
19. 5个页面组件补充缺失的搜索字段
20. 5个页面组件表单添加操作人ID字段

### 步骤6：修复后端 P3 问题
21. 5个 Controller 的 batchEnable/Disable 从请求获取操作人ID
22. 5个 ServiceImpl 导出方法使用 try-with-resources
23. 5个 ServiceImpl 改进 getCellStringValue
24. 5个 Mapper XML update_time 统一使用 CURRENT_TIMESTAMP（删除 Java 端 setUpdateTime）

### 步骤7：编译验证
25. Maven 编译
26. 启动后端验证
27. curl 测试所有 API
28. 启动前端验证
