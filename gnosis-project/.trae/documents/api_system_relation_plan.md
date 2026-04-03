# API与系统关系管理实现计划

## [x] 任务1：修改API配置管理接口，支持关联多个系统
- **优先级**：P0
- **依赖**：无
- **描述**：
  - 修改 OpenplatApiConfigController，添加关联系统的接口
  - 修改 OpenplatApiConfigService，添加关联系统的业务逻辑
  - 修改 OpenplatApiSystemRelationService，添加批量关联系统的方法
- **成功标准**：
  - API配置可以关联多个系统
  - 能够批量添加和删除API与系统的关联
- **测试要求**：
  - `programmatic` TR-1.1：调用关联系统接口返回200状态码
  - `programmatic` TR-1.2：API配置成功关联多个系统
- **备注**：需要确保API与系统的关联关系正确存储和管理

## [x] 任务2：在系统管理中添加查询关联API的接口
- **优先级**：P0
- **依赖**：任务1
- **描述**：
  - 修改 OpenplatSystemController，添加查询关联API的接口
  - 修改 OpenplatSystemService，添加查询关联API的业务逻辑
  - 修改 OpenplatApiSystemRelationService，添加根据系统ID查询关联API的方法
- **成功标准**：
  - 系统管理接口能够查询到关联的API列表
  - 能够根据系统ID获取关联的API配置
- **测试要求**：
  - `programmatic` TR-2.1：调用查询关联API接口返回200状态码
  - `programmatic` TR-2.2：能够正确返回系统关联的API列表
- **备注**：需要确保查询结果包含完整的API配置信息

## [x] 任务3：修改前端API配置管理页面，支持关联多个系统
- **优先级**：P1
- **依赖**：任务1
- **描述**：
  - 修改 ApiConfigManagement.jsx，添加关联系统的功能
  - 实现选择多个系统进行关联的界面
  - 实现批量添加和删除关联关系的功能
- **成功标准**：
  - 前端页面能够选择多个系统进行关联
  - 能够批量添加和删除关联关系
- **测试要求**：
  - `human-judgement` TR-3.1：界面操作流畅，用户体验良好
  - `programmatic` TR-3.2：关联操作能够成功提交到后端
- **备注**：需要确保前端与后端接口的正确对接

## [x] 任务4：在前端系统管理页面添加查询关联API的功能
- **优先级**：P1
- **依赖**：任务2
- **描述**：
  - 修改 SystemManagement.jsx，添加查询关联API的功能
  - 实现点击系统查看关联API的界面
  - 实现关联API的列表展示
- **成功标准**：
  - 前端页面能够展示系统关联的API列表
  - 能够查看关联API的详细信息
- **测试要求**：
  - `human-judgement` TR-4.1：界面操作流畅，用户体验良好
  - `programmatic` TR-4.2：查询操作能够成功获取后端数据
- **备注**：需要确保前端与后端接口的正确对接