@echo off
chcp 65001 >nul
echo ========================================
echo   统一消息通知组件 - 后端服务启动
echo ========================================
echo.
echo 正在启动后端服务...
echo 服务地址: http://localhost:8091/notice
echo Swagger UI: http://localhost:8091/notice/swagger-ui.html
echo.
echo 按 Ctrl+C 停止服务
echo ========================================
echo.

cd /d %~dp0

echo [1/2] 正在安装依赖模块到本地仓库...
cd /d %~dp0\..
call mvn install -pl gnosis-common -am -q
if errorlevel 1 (
    echo 安装依赖失败！
    pause
    exit /b 1
)

echo [2/2] 正在启动应用...
cd /d %~dp0
call mvn spring-boot:run

pause
