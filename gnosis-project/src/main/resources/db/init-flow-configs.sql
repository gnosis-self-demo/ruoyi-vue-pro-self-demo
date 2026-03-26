-- 初始化流程配置数据
-- 场景1: HYBRID 混合模式 — 订单创建校验
INSERT INTO gnosis_sample.sys_validation_flows (flow_id, flow_name, business_type, mode_type, el_expression, handler_code, component_config, is_active, version)
VALUES (
    'ORDER_CREATE_FLOW',
    '订单创建校验 (混合模式)',
    'ORDER_CREATE',
    'HYBRID',
    'THEN(parse_param, check_format, check_db_user)',
    'OrderBusinessHandler',
    '{"parse_param": {"json_path": "$"}, "check_format": {"rules": [{"path": "$.orderNo", "type": "REGEX", "pattern": "^ORD[0-9]{10}$", "msg": "订单号格式错误，格式应为 ORD+10位数字"}, {"path": "$.amount", "type": "RANGE", "min": 0.01, "max": 1000000, "msg": "订单金额超出范围(0.01~1000000)"}, {"path": "$.userId", "type": "NOT_NULL", "msg": "用户ID不能为空"}]}, "check_db_user": {"type": "DB_QUERY", "sql": "SELECT COUNT(1) FROM gnosis_sample.sys_users WHERE user_id = ? AND status = 1", "param_path": "$.userId", "msg": "用户不存在或已被禁用"}}'::jsonb,
    TRUE,
    1
) ON CONFLICT (flow_id) DO UPDATE SET 
    flow_name = EXCLUDED.flow_name,
    business_type = EXCLUDED.business_type,
    mode_type = EXCLUDED.mode_type,
    el_expression = EXCLUDED.el_expression,
    handler_code = EXCLUDED.handler_code,
    component_config = EXCLUDED.component_config,
    is_active = EXCLUDED.is_active,
    version = sys_validation_flows.version + 1;

-- 场景2: HANDLER 纯接口模式 — 用户注册
INSERT INTO gnosis_sample.sys_validation_flows (flow_id, flow_name, business_type, mode_type, el_expression, handler_code, component_config, is_active, version)
VALUES (
    'USER_REGISTER_FLOW',
    '用户注册 (纯接口模式)',
    'USER_REGISTER',
    'HANDLER',
    NULL,
    'UserRegisterHandler',
    NULL,
    TRUE,
    1
) ON CONFLICT (flow_id) DO UPDATE SET 
    flow_name = EXCLUDED.flow_name,
    business_type = EXCLUDED.business_type,
    mode_type = EXCLUDED.mode_type,
    el_expression = EXCLUDED.el_expression,
    handler_code = EXCLUDED.handler_code,
    component_config = EXCLUDED.component_config,
    is_active = EXCLUDED.is_active,
    version = sys_validation_flows.version + 1;

-- 场景3: FLOW 纯编排模式 — 简单格式校验
INSERT INTO gnosis_sample.sys_validation_flows (flow_id, flow_name, business_type, mode_type, el_expression, handler_code, component_config, is_active, version)
VALUES (
    'SIMPLE_CHECK_FLOW',
    '简单格式校验 (纯编排模式)',
    'BASIC_DATA',
    'FLOW',
    'THEN(parse_param, check_format)',
    NULL,
    '{"parse_param": {"json_path": "$"}, "check_format": {"rules": [{"path": "$.email", "type": "EMAIL", "msg": "邮箱格式错误"}, {"path": "$.phone", "type": "PHONE", "msg": "手机号格式错误"}]}}'::jsonb,
    TRUE,
    1
) ON CONFLICT (flow_id) DO UPDATE SET 
    flow_name = EXCLUDED.flow_name,
    business_type = EXCLUDED.business_type,
    mode_type = EXCLUDED.mode_type,
    el_expression = EXCLUDED.el_expression,
    handler_code = EXCLUDED.handler_code,
    component_config = EXCLUDED.component_config,
    is_active = EXCLUDED.is_active,
    version = sys_validation_flows.version + 1;

-- 场景4: 混合模式 — 商品创建校验
INSERT INTO gnosis_sample.sys_validation_flows (flow_id, flow_name, business_type, mode_type, el_expression, handler_code, component_config, is_active, version)
VALUES (
    'PRODUCT_CREATE_FLOW',
    '商品创建校验 (混合模式)',
    'PRODUCT_CREATE',
    'HYBRID',
    'THEN(parse_param, check_format)',
    'ProductBusinessHandler',
    '{"parse_param": {"json_path": "$"}, "check_format": {"rules": [{"path": "$.productName", "type": "NOT_NULL", "msg": "商品名称不能为空"}, {"path": "$.price", "type": "RANGE", "min": 0.01, "max": 999999, "msg": "商品价格超出范围"}]}}'::jsonb,
    TRUE,
    1
) ON CONFLICT (flow_id) DO UPDATE SET 
    flow_name = EXCLUDED.flow_name,
    business_type = EXCLUDED.business_type,
    mode_type = EXCLUDED.mode_type,
    el_expression = EXCLUDED.el_expression,
    handler_code = EXCLUDED.handler_code,
    component_config = EXCLUDED.component_config,
    is_active = EXCLUDED.is_active,
    version = sys_validation_flows.version + 1;

-- 场景5: 纯接口模式 — 支付校验
INSERT INTO gnosis_sample.sys_validation_flows (flow_id, flow_name, business_type, mode_type, el_expression, handler_code, component_config, is_active, version)
VALUES (
    'PAYMENT_VALIDATION_FLOW',
    '支付校验 (纯接口模式)',
    'PAYMENT',
    'HANDLER',
    NULL,
    'PaymentValidationHandler',
    NULL,
    TRUE,
    1
) ON CONFLICT (flow_id) DO UPDATE SET 
    flow_name = EXCLUDED.flow_name,
    business_type = EXCLUDED.business_type,
    mode_type = EXCLUDED.mode_type,
    el_expression = EXCLUDED.el_expression,
    handler_code = EXCLUDED.handler_code,
    component_config = EXCLUDED.component_config,
    is_active = EXCLUDED.is_active,
    version = sys_validation_flows.version + 1;

COMMIT;

-- 验证数据
SELECT '=== 流程配置数据 ===' AS info;
SELECT flow_id, flow_name, business_type, mode_type, is_active, version
FROM gnosis_sample.sys_validation_flows
ORDER BY flow_id;