-- ============================================================
-- ParamCheck 模块: openGauss 3.0.0 数据库脚本
-- Schema: gnosis_sample
-- 严禁存储过程，所有逻辑均为 Java 代码 + 动态 SQL 实现
-- ============================================================

-- 1. 校验流程配置表
CREATE TABLE IF NOT EXISTS gnosis_sample.sys_validation_flows (
    flow_id         VARCHAR(64) PRIMARY KEY,
    flow_name       VARCHAR(128) NOT NULL,
    mode_type       VARCHAR(20) DEFAULT 'FLOW' CHECK (mode_type IN ('FLOW', 'HANDLER', 'HYBRID')),
    el_expression   TEXT,
    handler_code    VARCHAR(64),
    component_config JSONB,
    is_active       BOOLEAN DEFAULT TRUE,
    version         INT DEFAULT 1,
    updated_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_flow_handler UNIQUE (flow_id, handler_code)
);

COMMENT ON TABLE gnosis_sample.sys_validation_flows IS '参数校验流程配置表 — 支持 LiteFlow 编排 + 自定义 Handler 混合模式';
COMMENT ON COLUMN gnosis_sample.sys_validation_flows.flow_id IS '流程唯一标识';
COMMENT ON COLUMN gnosis_sample.sys_validation_flows.mode_type IS '校验模式: FLOW=纯编排, HANDLER=纯代码, HYBRID=混合';
COMMENT ON COLUMN gnosis_sample.sys_validation_flows.el_expression IS 'LiteFlow EL 表达式 (FLOW/HYBRID 必填)';
COMMENT ON COLUMN gnosis_sample.sys_validation_flows.handler_code IS '自定义处理器编码 (HANDLER/HYBRID 必填)';
COMMENT ON COLUMN gnosis_sample.sys_validation_flows.component_config IS '组件配置 (JSONB: JSONPath规则/SQL/正则等)';

-- 2. 校验日志表
CREATE TABLE IF NOT EXISTS gnosis_sample.sys_validation_logs (
    log_id          BIGSERIAL PRIMARY KEY,
    flow_id         VARCHAR(64),
    request_id      VARCHAR(64),
    mode_type       VARCHAR(20),
    input_snapshot  JSONB,
    failed_node     VARCHAR(64),
    error_msg       TEXT,
    created_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE gnosis_sample.sys_validation_logs IS '参数校验执行日志表';
COMMENT ON COLUMN gnosis_sample.sys_validation_logs.input_snapshot IS '请求入参快照 (JSONB)';
COMMENT ON COLUMN gnosis_sample.sys_validation_logs.failed_node IS '失败节点ID';

-- 3. 辅助表: 示例产品库存 (用于 Handler 中 Java 代码 DB 校验演示)
CREATE TABLE IF NOT EXISTS gnosis_sample.product_stock (
    sku_id          VARCHAR(64) PRIMARY KEY,
    sku_name        VARCHAR(128),
    stock_qty       INT DEFAULT 0,
    updated_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 4. 辅助表: 示例订单记录 (用于 Handler 中防重提交校验演示)
CREATE TABLE IF NOT EXISTS gnosis_sample.order_record (
    order_id        BIGSERIAL PRIMARY KEY,
    order_no        VARCHAR(64) NOT NULL,
    user_id         VARCHAR(64),
    amount          DECIMAL(15,2),
    status          VARCHAR(20),
    created_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- 5. 辅助表: 示例用户表 (用于 DB 查询校验)
CREATE TABLE IF NOT EXISTS gnosis_sample.sys_users (
    user_id         VARCHAR(64) PRIMARY KEY,
    username        VARCHAR(64) NOT NULL UNIQUE,
    password        VARCHAR(128),
    email           VARCHAR(128),
    status          INT DEFAULT 1,
    created_time    TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 初始化测试数据 (三种模式全覆盖)
-- ============================================================

-- 场景1: HYBRID 混合模式 — 订单创建校验
INSERT INTO gnosis_sample.sys_validation_flows (flow_id, flow_name, mode_type, el_expression, handler_code, component_config)
VALUES (
    'ORDER_CREATE_FLOW',
    '订单创建校验 (混合模式)',
    'HYBRID',
    'THEN(parse_param, check_format, check_db_user)',
    'OrderBusinessHandler',
    '{
        "parse_param": {"json_path": "$"},
        "check_format": {
            "rules": [
                {"path": "$.orderNo", "type": "REGEX", "pattern": "^ORD[0-9]{10}$", "msg": "订单号格式错误，格式应为 ORD+10位数字"},
                {"path": "$.amount", "type": "RANGE", "min": 0.01, "max": 1000000, "msg": "订单金额超出范围(0.01~1000000)"},
                {"path": "$.userId", "type": "NOT_NULL", "msg": "用户ID不能为空"}
            ]
        },
        "check_db_user": {
            "type": "DB_QUERY",
            "sql": "SELECT COUNT(1) FROM gnosis_sample.sys_users WHERE user_id = ? AND status = 1",
            "param_path": "$.userId",
            "msg": "用户不存在或已被禁用"
        }
    }'::jsonb
) ON CONFLICT (flow_id) DO NOTHING;

-- 场景2: HANDLER 纯接口模式 — 用户注册
INSERT INTO gnosis_sample.sys_validation_flows (flow_id, flow_name, mode_type, el_expression, handler_code, component_config)
VALUES (
    'USER_REGISTER_FLOW',
    '用户注册 (纯接口模式)',
    'HANDLER',
    NULL,
    'UserRegisterHandler',
    NULL
) ON CONFLICT (flow_id) DO NOTHING;

-- 场景3: FLOW 纯编排模式 — 简单格式校验
INSERT INTO gnosis_sample.sys_validation_flows (flow_id, flow_name, mode_type, el_expression, handler_code, component_config)
VALUES (
    'SIMPLE_CHECK_FLOW',
    '简单格式校验 (纯编排模式)',
    'FLOW',
    'THEN(parse_param, check_format)',
    NULL,
    '{
        "parse_param": {"json_path": "$"},
        "check_format": {
            "rules": [
                {"path": "$.email", "type": "EMAIL", "msg": "邮箱格式错误"},
                {"path": "$.phone", "type": "PHONE", "msg": "手机号格式错误"}
            ]
        }
    }'::jsonb
) ON CONFLICT (flow_id) DO NOTHING;

-- 初始化示例产品库存数据
INSERT INTO gnosis_sample.product_stock (sku_id, sku_name, stock_qty) VALUES
    ('SKU001', 'iPhone 15 Pro', 100),
    ('SKU002', 'MacBook Pro 14', 50),
    ('SKU003', 'AirPods Pro', 200)
ON CONFLICT (sku_id) DO NOTHING;

-- 初始化示例用户数据
INSERT INTO gnosis_sample.sys_users (user_id, username, email, status) VALUES
    ('U001', 'zhangsan', 'zhangsan@example.com', 1),
    ('U002', 'lisi', 'lisi@example.com', 1),
    ('U003', 'wangwu', 'wangwu@example.com', 0)
ON CONFLICT (user_id) DO NOTHING;

COMMIT;

-- ============================================================
-- 验证查询
-- ============================================================
SELECT '=== sys_validation_flows ===' AS info;
SELECT flow_id, flow_name, mode_type, el_expression, handler_code, is_active, version
FROM gnosis_sample.sys_validation_flows
ORDER BY mode_type;

SELECT '=== product_stock ===' AS info;
SELECT * FROM gnosis_sample.product_stock;

SELECT '=== sys_users ===' AS info;
SELECT user_id, username, status FROM gnosis_sample.sys_users;
