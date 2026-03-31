@echo off
echo ========================================
echo 开放平台模块启动脚本
echo ========================================
echo.

echo 注意：首次启动前请先手动初始化数据库
echo 数据库初始化命令：
echo   psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/openplat-opengauss.sql
echo.

echo 步骤 1: 启动后端...
echo 后端服务将在 http://localhost:8080 启动
echo Swagger 文档：http://localhost:8080/swagger-ui.html
echo.
start /B mvn spring-boot:run -Dspring-boot.run.profiles=openplat-simple -Dmaven.test.skip=true > backend.log 2>&1
echo 后端启动中...
echo.

echo 等待后端启动（15秒）...
timeout /t 15 /nobreak > nul

echo.
echo 步骤 2: 启动前端...
echo 前端服务将在 http://localhost:3000 启动
echo.
cd frontend-openplat
start /B npm run dev > frontend.log 2>&1
cd ..

echo ========================================
echo 启动完成！
echo ========================================
echo.
echo 访问地址:
echo   - 前端页面：http://localhost:3000
echo   - Swagger 文档：http://localhost:8080/swagger-ui.html
echo   - API 测试：http://localhost:8080/openplat/system/list
echo.
echo 日志文件：
echo   - 后端日志：backend.log
echo   - 前端日志：frontend-openplat/frontend.log
echo.
echo 按任意键退出（服务将继续在后台运行）...
pause > nul
