@echo off
chcp 65001 >nul
echo ========================================
echo 签章平台后端启动脚本
echo ========================================
echo.

REM 切换到脚本所在目录
cd /d %~dp0

echo 当前目录: %CD%
echo.

echo 编译项目...
call mvn clean compile "-Dmaven.test.skip=true"
if %ERRORLEVEL% neq 0 (
    echo.
    echo 编译失败，请检查错误信息
    pause
    exit /b %ERRORLEVEL%
)

echo.
echo 启动后端服务...
echo 后端服务将在 http://localhost:8095 启动
echo Swagger 文档: http://localhost:8095/swagger-ui.html
echo.

call mvn spring-boot:run "-Dmaven.test.skip=true"

pause
