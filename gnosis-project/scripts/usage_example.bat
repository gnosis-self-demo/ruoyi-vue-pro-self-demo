@echo off
:: 分布式队列系统使用示例脚本 (Windows版本)

echo 🚀 分布式队列系统使用示例
echo ========================

set BASE_URL=http://localhost:8080

:: 1. 健康检查
echo 1. 健康检查...
curl -s %BASE_URL%/health
echo.

:: 2. 查看当前配置
echo 2. 查看当前配置...
curl -s %BASE_URL%/admin/queue-config
echo.

:: 3. 配置SMS队列参数
echo 3. 配置SMS队列参数...
curl -s -X POST "%BASE_URL%/admin/queue-config/sms_queue/max-length?maxLength=1000"
curl -s -X POST "%BASE_URL%/admin/queue-config/sms_queue/max-qps?maxQps=50"
echo.

:: 4. 配置Email队列参数
echo 4. 配置Email队列参数...
curl -s -X POST "%BASE_URL%/admin/queue-config/email_queue/max-length?maxLength=500"
curl -s -X POST "%BASE_URL%/admin/queue-config/email_queue/max-qps?maxQps=30"
echo.

:: 5. 异步提交SMS任务
echo 5. 异步提交SMS任务...
curl -s -X POST %BASE_URL%/tasks/sms_queue ^
  -H "Content-Type: application/json" ^
  -d "{\"phone\":\"13800138000\",\"content\":\"您的验证码是：123456\"}"
echo.

:: 6. 异步提交Email任务
echo 6. 异步提交Email任务...
curl -s -X POST %BASE_URL%/tasks/email_queue ^
  -H "Content-Type: application/json" ^
  -d "{\"email\":\"user@example.com\",\"subject\":\"系统通知\",\"content\":\"这是一封测试邮件\"}"
echo.

:: 7. 同步提交任务
echo 7. 同步提交SMS任务...
curl -s -X POST "%BASE_URL%/tasks-sync/sms_queue?timeoutMs=3000" ^
  -H "Content-Type: application/json" ^
  -d "{\"phone\":\"13800138001\",\"content\":\"同步处理的短信\"}"
echo.

:: 8. 查看队列状态
echo 8. 查看SMS队列状态...
curl -s %BASE_URL%/queues/sms_queue/status
echo.

echo ✅ 示例执行完成！
echo 💡 提示：请确保服务已在8080端口启动
pause