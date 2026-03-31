-- ============================================================
-- ParamCheck 模块: openGauss 3.0.0 数据库脚本
-- Schema: gnosis_sample
-- 严禁存储过程，所有逻辑均为 Java 代码 + 动态 SQL 实现
-- ============================================================

-- 1. 校验流程配置表
CREATE TABLE IF NOT EXISTS sys_validation_flows (
    flow_id         VARCHAR(64) PRIMARY KEY,
    flow_name       VARCHAR(128) NOT NULL,
    mode_type       VARCHAR(20) DEFAULT 'FLOW',
    el_expression   TEXT,
    handler_code    VARCHAR(64),
    component_config TEXT,
    business_type   VARCHAR(255),
    is_active       boolean DEFAULT true,
    version         INT DEFAULT 1,
    create_user_id  VARCHAR(128),
    update_user_id  VARCHAR(128),
    create_time     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_flow_handler UNIQUE (flow_id, handler_code)
);

COMMENT ON TABLE sys_validation_flows IS '参数校验流程配置表';
COMMENT ON COLUMN sys_validation_flows.flow_id IS '流程唯一标识';
COMMENT ON COLUMN sys_validation_flows.mode_type IS '校验模式: FLOW/HANDLER/HYBRID';
COMMENT ON COLUMN sys_validation_flows.el_expression IS 'LiteFlow EL 表达式';
COMMENT ON COLUMN sys_validation_flows.handler_code IS '自定义处理器编码';
COMMENT ON COLUMN sys_validation_flows.component_config IS '组件配置';
COMMENT ON COLUMN sys_validation_flows.business_type IS '业务类型';
COMMENT ON COLUMN sys_validation_flows.is_active IS '是否启用';
COMMENT ON COLUMN sys_validation_flows.version IS '版本号';
COMMENT ON COLUMN sys_validation_flows.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_validation_flows.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_validation_flows.create_time IS '创建时间';
COMMENT ON COLUMN sys_validation_flows.updated_time IS '更新时间';

-- 2. 校验日志表
CREATE TABLE IF NOT EXISTS sys_validation_logs (
    log_id          BIGSERIAL PRIMARY KEY,
    flow_id         VARCHAR(64),
    request_id      VARCHAR(64),
    mode_type       VARCHAR(20),
    input_snapshot  TEXT,
    failed_node     VARCHAR(64),
    error_msg       TEXT,
    created_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_validation_logs IS '参数校验执行日志表';
COMMENT ON COLUMN sys_validation_logs.input_snapshot IS '请求入参快照';
COMMENT ON COLUMN sys_validation_logs.failed_node IS '失败节点ID';

-- 3. 辅助表: 示例产品库存
CREATE TABLE IF NOT EXISTS product_stock (
    sku_id          VARCHAR(64) PRIMARY KEY,
    sku_name        VARCHAR(128),
    stock_qty       INT DEFAULT 0,
    updated_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 4. 辅助表: 示例订单记录
CREATE TABLE IF NOT EXISTS order_record (
    order_id        BIGSERIAL PRIMARY KEY,
    order_no        VARCHAR(64) NOT NULL,
    user_id         VARCHAR(64),
    amount          DECIMAL(15,2),
    status          VARCHAR(20),
    created_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 5. 辅助表: 示例用户表
CREATE TABLE IF NOT EXISTS sys_users (
    user_id         VARCHAR(64) PRIMARY KEY,
    username        VARCHAR(64) NOT NULL UNIQUE,
    password        VARCHAR(128),
    email           VARCHAR(128),
    status          INT DEFAULT 1,
    created_time    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- 初始化测试数据 (三种模式全覆盖)
-- ============================================================

-- 场景1: HYBRID 混合模式 - 订单创建校验
INSERT INTO sys_validation_flows (flow_id, flow_name, business_type, mode_type, el_expression, handler_code, component_config, create_user_id, update_user_id)
VALUES (
    'ORDER_CREATE_FLOW',
    'Order creation validation (Hybrid mode)',
    'ORDER_CREATE',
    'HYBRID',
    'THEN(parse_param, check_format, check_db_user)',
    'OrderBusinessHandler',
    '{"parse_param": {"json_path": "$"}, "check_format": {"rules": [{"path": "$.orderNo", "type": "REGEX", "pattern": "^ORD[0-9]{10}$", "msg": "Order number format error"}, {"path": "$.amount", "type": "RANGE", "min": 0.01, "max": 1000000, "msg": "Order amount out of range"}, {"path": "$.userId", "type": "NOT_NULL", "msg": "User ID cannot be empty"}]}, "check_db_user": {"type": "DB_QUERY", "sql": "SELECT COUNT(1) FROM sys_users WHERE user_id = ? AND status = 1", "param_path": "$.userId", "msg": "User does not exist or is disabled"}}',
    'admin',
    'admin'
) ON CONFLICT (flow_id) DO NOTHING;

-- 场景2: HANDLER 纯接口模式 - 用户注册
INSERT INTO sys_validation_flows (flow_id, flow_name, business_type, mode_type, el_expression, handler_code, component_config, create_user_id, update_user_id)
VALUES (
    'USER_REGISTER_FLOW',
    'User registration (Handler mode)',
    'USER_REGISTER',
    'HANDLER',
    NULL,
    'UserRegisterHandler',
    NULL,
    'admin',
    'admin'
) ON CONFLICT (flow_id) DO NOTHING;

-- 场景3: FLOW 纯编排模式 - 简单格式校验
INSERT INTO sys_validation_flows (flow_id, flow_name, business_type, mode_type, el_expression, handler_code, component_config, create_user_id, update_user_id)
VALUES (
    'SIMPLE_CHECK_FLOW',
    'Simple format validation (Flow mode)',
    'BASIC_DATA',
    'FLOW',
    'THEN(parse_param, check_format)',
    NULL,
    '{"parse_param": {"json_path": "$"}, "check_format": {"rules": [{"path": "$.email", "type": "EMAIL", "msg": "Email format error"}, {"path": "$.phone", "type": "PHONE", "msg": "Phone format error"}]}}',
    'admin',
    'admin'
) ON CONFLICT (flow_id) DO NOTHING;

-- 初始化示例产品库存数据
INSERT INTO product_stock (sku_id, sku_name, stock_qty) VALUES
    ('SKU001', 'iPhone 15 Pro', 100),
    ('SKU002', 'MacBook Pro 14', 50),
    ('SKU003', 'AirPods Pro', 200)
ON CONFLICT (sku_id) DO NOTHING;

-- 初始化示例用户数据
INSERT INTO sys_users (user_id, username, email, status) VALUES
    ('U001', 'zhangsan', 'zhangsan@example.com', 1),
    ('U002', 'lisi', 'lisi@example.com', 1),
    ('U003', 'wangwu', 'wangwu@example.com', 0)
ON CONFLICT (user_id) DO NOTHING;

COMMIT;
