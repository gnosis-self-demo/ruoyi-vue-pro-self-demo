# 数据库初始化说明

## 连接信息
- 数据库：openGauss 3.0.0
- 地址：localserver.gnosis:5432
- 数据库：gnosis_sample
- 用户：gaussdb
- 密码：Enmotech@123

## 初始化步骤

1. 使用数据库客户端工具连接数据库
   - DBeaver
   - DataGrip
   - 或其他支持 openGauss/PostgreSQL 的工具

2. 执行建表脚本
   - 脚本位置：`src/main/resources/db/openplat/openplat-opengauss.sql`
   - 或者手动执行以下 SQL 语句创建表

3. 验证表是否创建成功
   ```sql
   SELECT tablename FROM pg_tables WHERE schemaname = 'gnosis_sample';
   ```

## 需要创建的表
- openplat_system - 对接系统信息表
- openplat_app_auth - 应用认证表
- openplat_api_config - API 配置表
- openplat_api_doc - API 文档表
- openplat_webhook_config - Webhook 配置表
- openplat_access_log - 调用日志表
- openplat_rate_limit - 限流配置表
- openplat_nonce_cache - 防重放缓存表

## 注意事项
- 如果表已存在，脚本会跳过创建
- 脚本中包含初始化数据，可以直接使用
- 确保数据库用户有创建表的权限
