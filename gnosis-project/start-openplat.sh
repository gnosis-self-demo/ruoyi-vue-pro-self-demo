#!/bin/bash

echo "========================================"
echo "开放平台模块启动脚本"
echo "========================================"
echo ""

echo "步骤 1: 初始化数据库..."
psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample < src/main/resources/db/openplat-opengauss.sql
if [ $? -ne 0 ]; then
    echo "数据库初始化失败，请检查数据库连接"
    exit 1
fi
echo "数据库初始化成功!"
echo ""

echo "步骤 2: 启动后端..."
echo "后端服务将在 http://localhost:8080 启动"
echo "Swagger 文档：http://localhost:8080/swagger-ui.html"
echo ""
mvn spring-boot:run -Dspring-boot.run.mainClass=openplat.OpenplatApplication &
BACKEND_PID=$!
echo "后端启动中... (PID: $BACKEND_PID)"
echo ""

echo "等待后端启动..."
sleep 10

echo ""
echo "步骤 3: 启动前端..."
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
echo "  - API 测试：http://localhost:8080/api/openplat/system/list"
echo ""
echo "按 Ctrl+C 停止所有服务"

# 等待进程
wait
