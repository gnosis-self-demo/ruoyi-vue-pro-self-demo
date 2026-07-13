#!/bin/bash
cd "$(dirname "$0")"

echo "========================================"
echo "签章平台前端启动脚本"
echo "========================================"
echo ""

echo "安装依赖..."
npm install

echo ""
echo "启动前端开发服务..."
echo "前端服务将在 http://localhost:3008 启动"
echo ""

npm run dev
