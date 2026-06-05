@echo off
chcp 65001 >nul
echo ============================================
echo   gnosis-data-exportor 后端启动脚本
echo ============================================

set "PROJECT_DIR=%~dp0..\.."
cd /d "%PROJECT_DIR%"

echo.
echo [INFO] 当前目录: %CD%
echo [INFO] 正在编译并启动后端服务 (port 8090)...
echo.

call mvn spring-boot:run -pl gnosis-data-exportor -DskipTests

echo.
echo [INFO] 后端服务已停止.
pause
