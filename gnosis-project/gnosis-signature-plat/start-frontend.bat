@echo off
chcp 65001 >nul
echo ========================================
echo 签章平台前端启动脚本
echo ========================================
echo.

REM 切换到前端目录
cd /d %~dp0frontend

echo 当前目录: %CD%
echo.

if not exist node_modules (
    echo 安装依赖...
    call npm install
    if %ERRORLEVEL% neq 0 (
        echo.
        echo npm install 失败，请检查错误信息
        pause
        exit /b %ERRORLEVEL%
    )
) else (
    echo 依赖已存在，跳过安装
)

echo.
echo 启动前端开发服务...
echo 前端服务将在 http://localhost:3008 启动
echo.

call npm run dev

pause
