@echo off
chcp 65001 >nul
echo ============================================
echo  启动 Gnosis 微服务模块
echo ============================================
echo.

cd /d %~dp0

echo [1/5] 启动 gnosis-distribute-queue (端口 8081)...
start "gnosis-distribute-queue" cmd /c "cd /d %~dp0gnosis-distribute-queue && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [2/5] 启动 gnosis-dynamic-liteflow (端口 8082)...
start "gnosis-dynamic-liteflow" cmd /c "cd /d %~dp0gnosis-dynamic-liteflow && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [3/5] 启动 gnosis-lifecycle (端口 8080)...
start "gnosis-lifecycle" cmd /c "cd /d %~dp0gnosis-lifecycle && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [4/5] 启动 gnosis-openplat (端口 8083)...
start "gnosis-openplat" cmd /c "cd /d %~dp0gnosis-openplat && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo [5/5] 启动 gnosis-paramcheck (端口 8084)...
start "gnosis-paramcheck" cmd /c "cd /d %~dp0gnosis-paramcheck && mvn spring-boot:run"

echo.
echo ============================================
echo  所有模块已启动
echo  - gnosis-distribute-queue: http://localhost:8081
echo  - gnosis-dynamic-liteflow: http://localhost:8082
echo  - gnosis-lifecycle:       http://localhost:8080
echo  - gnosis-openplat:        http://localhost:8083
echo  - gnosis-paramcheck:      http://localhost:8084
echo ============================================
pause
