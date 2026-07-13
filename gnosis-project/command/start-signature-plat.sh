#!/bin/bash
cd "$(dirname "$0")"

echo "========================================"
echo "签章平台模块启动脚本"
echo "========================================"
echo ""

echo "注意: 首次启动前请先初始化数据库"
echo "数据库将自动初始化 (spring.datasource.initialization-mode=always)"
echo ""

echo "步骤 1: 启动后端..."
echo "后端服务将在 http://localhost:8095 启动"
echo "Swagger 文档: http://localhost:8095/swagger-ui.html"
echo ""

cd gnosis-signature-plat
mvn spring-boot:run -Dmaven.test.skip=true &
BACKEND_PID=$!
echo "后端启动中... (PID: $BACKEND_PID)"

echo ""
echo "等待后端启动..."
sleep 15

echo ""
echo "步骤 2: 启动前端..."
echo "前端服务将在 http://localhost:3008 启动"
echo ""

cd frontend
npm run dev &
FRONTEND_PID=$!
echo "前端启动中... (PID: $FRONTEND_PID)"

cd ../..

echo ""
echo "========================================"
echo "启动完成!"
echo "========================================"
echo ""
echo "访问地址:"
echo "  - 前端页面: http://localhost:3008"
echo "  - Swagger 文档: http://localhost:8095/swagger-ui.html"
echo "  - API 测试: http://localhost:8095/signature/file/page"
echo ""
echo "按 Ctrl+C 停止所有服务"

# 等待进程
wait
