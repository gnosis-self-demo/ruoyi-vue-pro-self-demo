#!/bin/bash
cd "$(dirname "$0")"

echo "========================================"
echo "签章平台后端启动脚本"
echo "========================================"
echo ""

echo "编译项目..."
mvn clean compile -Dmaven.test.skip=true

echo ""
echo "启动后端服务..."
echo "后端服务将在 http://localhost:8095 启动"
echo "Swagger 文档：http://localhost:8095/swagger-ui.html"
echo ""

mvn spring-boot:run -Dmaven.test.skip=true
