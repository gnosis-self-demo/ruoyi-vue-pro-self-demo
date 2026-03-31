#!/bin/bash

echo "========================================"
echo "参数动态校验模块启动脚本"
echo "========================================"
echo ""

echo "注意：首次启动前请先手动初始化数据库"
echo "数据库初始化命令："
echo "  psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/paramcheck-opengauss.sql"
echo "  psql -h localserver.gnosis -p 5432 -U gaussdb -d gnosis_sample -f src/main/resources/db/liteflow-opengauss.sql"
echo ""

echo "启动后端..."
echo "后端服务将在 http://localhost:8080 启动"
echo "Swagger 文档：http://localhost:8080/swagger-ui.html"
echo ""
mvn spring-boot:run -Dspring-boot.run.mainClass=paramcheck.ParamCheckApplication -Dspring-boot.run.profiles=paramcheck -Dmaven.test.skip=true

echo ""
echo "========================================"
echo "启动完成！"
echo "========================================"
echo ""
echo "访问地址:"
echo "  - Swagger 文档：http://localhost:8080/swagger-ui.html"
echo "  - API 测试：http://localhost:8080/paramcheck/businessType/list"
