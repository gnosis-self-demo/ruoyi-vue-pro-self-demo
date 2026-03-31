#!/bin/bash

echo "========================================"
echo "开放平台模块启动脚本"
echo "========================================"
echo ""

echo "注意：首次启动前请先手动初始化数据库"
echo "数据库初始化命令："
echo "  psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/openplat-opengauss.sql"
echo ""

echo "步骤 1: 启动后端..."
echo "后端服务将在 http://localhost:8080 启动"
echo "Swagger 文档：http://localhost:8080/swagger-ui.html"
echo ""
mvn spring-boot:run -Dspring-boot.run.profiles=openplat-simple -Dmaven.test.skip=true &
BACKEND_PID=$!
echo "后端启动中... (PID: $BACKEND_PID)"
echo ""

echo "等待后端启动..."
sleep 15

echo ""
echo "步骤 2: 启动前端..."
echo "前端服务将在 http://localhost:3000 启动"
echo ""
cd frontend-openplat
npm run dev &
FRONTEND_PID=$!
echo "前端启动中... (PID: $FRONTEND_PID)"

echo ""
echo "========================================"
echo "启动完成！"
echo "========================================"
echo ""
echo "访问地址:"
echo "  - 前端页面：http://localhost:3000"
echo "  - Swagger 文档：http://localhost:8080/swagger-ui.html"
echo "  - API 测试：http://localhost:8080/openplat/system/list"
echo ""
echo "按 Ctrl+C 停止所有服务"

# 等待进程
wait
