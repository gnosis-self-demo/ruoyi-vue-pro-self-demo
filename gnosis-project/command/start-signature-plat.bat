@echo off
chcp 65001 >nul
echo ========================================
echo 签章平台模块启动脚本
echo ========================================
echo.

REM 切换到命令脚本所在目录
cd /d %~dp0

echo 当前目录: %CD%
echo.

echo 注意: 首次启动前请先初始化数据库
echo 数据库将自动初始化 (spring.datasource.initialization-mode=always)
echo.

echo 步骤 1: 启动后端...
echo 后端服务将在 http://localhost:8095 启动
echo Swagger 文档: http://localhost:8095/swagger-ui.html
echo.

cd gnosis-signature-plat
start "gnosis-signature-plat-backend" cmd /c "cd /d %CD% && mvn spring-boot:run -Dmaven.test.skip=true"
cd ..

echo 后端启动中...
echo.

echo 等待后端启动 (15秒)...
timeout /t 15 /nobreak > nul

echo.
echo 步骤 2: 启动前端...
echo 前端服务将在 http://localhost:3008 启动
echo.

cd gnosis-signature-plat\frontend
start "gnosis-signature-plat-frontend" cmd /c "cd /d %CD% && npm run dev"
cd ..\..

echo ========================================
echo 启动完成!
echo ========================================
echo.
echo 访问地址:
echo   - 前端页面: http://localhost:3008
echo   - Swagger 文档: http://localhost:8095/swagger-ui.html
echo   - API 测试: http://localhost:8095/signature/file/page
echo.
echo 按任意键退出 (服务将继续在后台运行)...
pause > nul
