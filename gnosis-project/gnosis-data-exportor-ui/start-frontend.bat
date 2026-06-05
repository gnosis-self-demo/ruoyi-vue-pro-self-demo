@echo off
chcp 65001 >nul
echo ============================================
echo   gnosis-data-exportor-ui 前端启动脚本
echo ============================================

set "UI_DIR=%~dp0"
cd /d "%UI_DIR%"

echo.
echo [INFO] 当前目录: %CD%
echo [INFO] 正在启动前端开发服务 (port 3000)...
echo [INFO] API 代理目标: http://localhost:8090
echo.

call npm run dev

echo.
echo [INFO] 前端服务已停止.
pause
