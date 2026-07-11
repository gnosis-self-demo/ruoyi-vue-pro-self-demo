@echo off
chcp 65001 >nul
echo ========================================
echo   统一消息通知组件 - 启动全部服务
echo ========================================
echo.
echo 将同时启动后端和前端服务
echo 后端: http://localhost:8091/notice
echo 前端: http://localhost:3006
echo ========================================
echo.

echo [1/3] 正在安装依赖模块到本地仓库...
cd /d %~dp0\..
call mvn install -pl gnosis-common -am -q
if errorlevel 1 (
    echo 安装依赖失败！
    pause
    exit /b 1
)

echo [2/3] 正在启动后端服务...
start "Notice Backend" cmd /c "cd /d %~dp0 && mvn spring-boot:run"

echo 等待后端服务初始化...
timeout /t 15 /nobreak >nul

echo [3/3] 正在启动前端服务...
start "Notice Frontend" cmd /c "cd /d %~dp0frontend && npm run dev"

echo.
echo 服务启动中，请稍候...
echo 后端 Swagger UI: http://localhost:8091/notice/swagger-ui.html
echo 前端访问地址: http://localhost:3006
