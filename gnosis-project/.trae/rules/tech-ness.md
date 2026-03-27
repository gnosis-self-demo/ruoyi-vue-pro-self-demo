- 后端技术栈要求
Spring Boot: 2.1.10.RELEASE 版本
Spring Cloud: 2.1.4.RELEASE 版本
Java: 1.8 版本
Maven: 项目构建工具
Hutool: 5.8.12 版本 - Java工具包，简化开发
MyBatis-Plus: 3.5.4 版本 - 持久层框架
Lombok: 1.16.16 版本 - 简化Java代码
FastJSON: 1.2.83 版本 - JSON处理库
Guava: 20.0 版本 - Google开源Java工具库
Apache POI: 4.1.2 版本 - 处理Office文档
LiteFlow: 2.11.3 版本 - 轻量级流程引擎
Spring Web: Web应用框架
Swagger: 2.8.0 版本 - API文档生成工具
OpenFeign: 微服务调用框架
Activiti: 6.0.0 版本 - 工作流引擎
MySQL驱动: OpenGauss 3.0.0 和 GoldenDB 5.1.46.30
Redis: Spring Boot Starter Data Redis
Druid: 阿里数据库连接池
Caffeine: 本地缓存实现
RBAC权限控制: 基于Spring Security的权限系统
JWT: 0.7.0 版本 - JSON Web Token
XXL-JOB: 2.3.1 版本 - 分布式任务调度平台
Logback: 日志框架
Jackson: 2.10.3 版本 - JSON处理库
Jasypt: 加密配置工具
Swagger UI: API文档展示
Apache HttpClient: 4.5.2 版本 - HTTP客户端
- 前端技术栈要求：React
- 数据库要求
`jdbc:opengauss://localserver.gnosis:5432/gnosis_sample` (user: gaussdb, pass: Enmotech@123)
openGauss版本 3.0.0  不要用新版本的语言特性
- 库表和功能设计
必须存在以下字段
create_user_id / 创建人ID / varchar(128)
update_user_id / 更新人ID / varchar(128)
create_time / 创建时间 / timestamp
update_time / 更新时间 / timestamp
- 使用Mybatis，使用mapper.xml管理SQL
- 完全遵循 技术要求.md 文件内容的技术约束 
- 禁止存储过程：所有逻辑（包括复杂校验）必须用 **Java 代码** 实现 
- 所有页面列表字段必须包含 创建人ID、创建时间、更新人ID、更新时间 
- 每个页面至少需要分页查询、所有字段的条件查询、导入、导出、详情、新建、编辑、修改、批量删除、批量启用、批量禁用 功能