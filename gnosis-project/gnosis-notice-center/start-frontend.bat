@echo off
chcp 65001 >nul
echo ========================================
echo   统一消息通知组件 - 前端服务启动
echo ========================================
echo.
echo 正在启动前端服务...
echo 访问地址: http://localhost:3006
echo.
echo 按 Ctrl+C 停止服务
echo ========================================
echo.

cd /d %~dp0frontend
npm run dev  --host

pause
