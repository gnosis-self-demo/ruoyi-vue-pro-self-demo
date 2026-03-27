-- 业务生命周期管理组件 - 核心事件执行引擎模块
-- 数据库表结构 SQL (openGauss)
-- 创建时间：2026-03-27

-- ===========================
-- 1. 路由规则表
-- ===========================
CREATE TABLE IF NOT EXISTS lifecycle_route_rule (
    id VARCHAR(64) PRIMARY KEY,
    business_type_id VARCHAR(64) NOT NULL COMMENT '业务类型 ID',
    event_type VARCHAR(64) NOT NULL COMMENT '事件类型',
    rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
    rule_code VARCHAR(64) COMMENT '规则编码',
    description VARCHAR(512) COMMENT '描述信息',
    condition_expression TEXT COMMENT '条件表达式',
    priority INTEGER DEFAULT 0 COMMENT '优先级 (数字越大优先级越高)',
    is_active INTEGER DEFAULT 1 COMMENT '是否启用 (0-否 1-是)',
    version INTEGER DEFAULT 1 COMMENT '版本号',
    create_user_id VARCHAR(64) NOT NULL COMMENT '创建人 ID',
    update_user_id VARCHAR(64) NOT NULL COMMENT '更新人 ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

COMMENT ON TABLE lifecycle_route_rule IS '路由规则表';
COMMENT ON COLUMN lifecycle_route_rule.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_route_rule.business_type_id IS '业务类型 ID';
COMMENT ON COLUMN lifecycle_route_rule.event_type IS '事件类型';
COMMENT ON COLUMN lifecycle_route_rule.rule_name IS '规则名称';
COMMENT ON COLUMN lifecycle_route_rule.rule_code IS '规则编码';
COMMENT ON COLUMN lifecycle_route_rule.description IS '描述信息';
COMMENT ON COLUMN lifecycle_route_rule.condition_expression IS '条件表达式';
COMMENT ON COLUMN lifecycle_route_rule.priority IS '优先级';
COMMENT ON COLUMN lifecycle_route_rule.is_active IS '是否启用';
COMMENT ON COLUMN lifecycle_route_rule.version IS '版本号';
COMMENT ON COLUMN lifecycle_route_rule.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_route_rule.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_route_rule.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_route_rule.update_time IS '更新时间';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_route_rule_business_type ON lifecycle_route_rule(business_type_id);
CREATE INDEX IF NOT EXISTS idx_route_rule_event_type ON lifecycle_route_rule(event_type);
CREATE INDEX IF NOT EXISTS idx_route_rule_is_active ON lifecycle_route_rule(is_active);
CREATE INDEX IF NOT EXISTS idx_route_rule_priority ON lifecycle_route_rule(priority DESC);

-- ===========================
-- 2. 事件表
-- ===========================
CREATE TABLE IF NOT EXISTS lifecycle_event (
    id VARCHAR(64) PRIMARY KEY,
    business_type_id VARCHAR(64) NOT NULL COMMENT '业务类型 ID',
    event_type VARCHAR(64) NOT NULL COMMENT '事件类型',
    business_instance_id VARCHAR(128) NOT NULL COMMENT '业务实例 ID',
    event_data TEXT COMMENT '事件数据 (JSON)',
    metadata TEXT COMMENT '扩展元数据 (JSON)',
    operator_id VARCHAR(64) COMMENT '操作人 ID',
    remark VARCHAR(512) COMMENT '备注信息',
    process_status VARCHAR(32) DEFAULT 'PENDING' COMMENT '处理状态',
    process_result TEXT COMMENT '处理结果',
    create_user_id VARCHAR(64) NOT NULL COMMENT '创建人 ID',
    update_user_id VARCHAR(64) NOT NULL COMMENT '更新人 ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

COMMENT ON TABLE lifecycle_event IS '事件表';
COMMENT ON COLUMN lifecycle_event.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_event.business_type_id IS '业务类型 ID';
COMMENT ON COLUMN lifecycle_event.event_type IS '事件类型';
COMMENT ON COLUMN lifecycle_event.business_instance_id IS '业务实例 ID';
COMMENT ON COLUMN lifecycle_event.event_data IS '事件数据';
COMMENT ON COLUMN lifecycle_event.metadata IS '扩展元数据';
COMMENT ON COLUMN lifecycle_event.operator_id IS '操作人 ID';
COMMENT ON COLUMN lifecycle_event.remark IS '备注信息';
COMMENT ON COLUMN lifecycle_event.process_status IS '处理状态';
COMMENT ON COLUMN lifecycle_event.process_result IS '处理结果';
COMMENT ON COLUMN lifecycle_event.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_event.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_event.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_event.update_time IS '更新时间';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_event_business_type ON lifecycle_event(business_type_id);
CREATE INDEX IF NOT EXISTS idx_event_event_type ON lifecycle_event(event_type);
CREATE INDEX IF NOT EXISTS idx_event_instance_id ON lifecycle_event(business_instance_id);
CREATE INDEX IF NOT EXISTS idx_event_process_status ON lifecycle_event(process_status);
CREATE INDEX IF NOT EXISTS idx_event_create_time ON lifecycle_event(create_time DESC);

-- ===========================
-- 3. 业务实例表
-- ===========================
CREATE TABLE IF NOT EXISTS lifecycle_instance (
    id VARCHAR(64) PRIMARY KEY,
    business_type_id VARCHAR(64) NOT NULL COMMENT '业务类型 ID',
    business_instance_id VARCHAR(128) NOT NULL COMMENT '业务实例 ID',
    current_state_id VARCHAR(64) NOT NULL COMMENT '当前状态 ID',
    current_state_code VARCHAR(64) NOT NULL COMMENT '当前状态编码',
    instance_data TEXT COMMENT '实例数据 (JSON)',
    start_time TIMESTAMP COMMENT '开始时间',
    end_time TIMESTAMP COMMENT '结束时间',
    status VARCHAR(32) DEFAULT 'RUNNING' COMMENT '实例状态',
    create_user_id VARCHAR(64) NOT NULL COMMENT '创建人 ID',
    update_user_id VARCHAR(64) NOT NULL COMMENT '更新人 ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

COMMENT ON TABLE lifecycle_instance IS '业务实例表';
COMMENT ON COLUMN lifecycle_instance.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_instance.business_type_id IS '业务类型 ID';
COMMENT ON COLUMN lifecycle_instance.business_instance_id IS '业务实例 ID';
COMMENT ON COLUMN lifecycle_instance.current_state_id IS '当前状态 ID';
COMMENT ON COLUMN lifecycle_instance.current_state_code IS '当前状态编码';
COMMENT ON COLUMN lifecycle_instance.instance_data IS '实例数据';
COMMENT ON COLUMN lifecycle_instance.start_time IS '开始时间';
COMMENT ON COLUMN lifecycle_instance.end_time IS '结束时间';
COMMENT ON COLUMN lifecycle_instance.status IS '实例状态';
COMMENT ON COLUMN lifecycle_instance.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_instance.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_instance.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_instance.update_time IS '更新时间';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_instance_business_type ON lifecycle_instance(business_type_id);
CREATE INDEX IF NOT EXISTS idx_instance_instance_id ON lifecycle_instance(business_instance_id);
CREATE INDEX IF NOT EXISTS idx_instance_current_state ON lifecycle_instance(current_state_id);
CREATE INDEX IF NOT EXISTS idx_instance_status ON lifecycle_instance(status);

-- ===========================
-- 4. 实例流转轨迹表
-- ===========================
CREATE TABLE IF NOT EXISTS lifecycle_instance_trace (
    id VARCHAR(64) PRIMARY KEY,
    instance_id VARCHAR(64) NOT NULL COMMENT '实例 ID',
    business_type_id VARCHAR(64) NOT NULL COMMENT '业务类型 ID',
    business_instance_id VARCHAR(128) NOT NULL COMMENT '业务实例 ID',
    transition_id VARCHAR(64) COMMENT '流转 ID',
    transition_code VARCHAR(64) COMMENT '流转编码',
    from_state_id VARCHAR(64) NOT NULL COMMENT '源状态 ID',
    from_state_code VARCHAR(64) NOT NULL COMMENT '源状态编码',
    to_state_id VARCHAR(64) NOT NULL COMMENT '目标状态 ID',
    to_state_code VARCHAR(64) NOT NULL COMMENT '目标状态编码',
    transition_data TEXT COMMENT '流转数据 (JSON)',
    operator_id VARCHAR(64) COMMENT '操作人 ID',
    remark VARCHAR(512) COMMENT '备注信息',
    create_user_id VARCHAR(64) NOT NULL COMMENT '创建人 ID',
    update_user_id VARCHAR(64) NOT NULL COMMENT '更新人 ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

COMMENT ON TABLE lifecycle_instance_trace IS '实例流转轨迹表';
COMMENT ON COLUMN lifecycle_instance_trace.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_instance_trace.instance_id IS '实例 ID';
COMMENT ON COLUMN lifecycle_instance_trace.business_type_id IS '业务类型 ID';
COMMENT ON COLUMN lifecycle_instance_trace.business_instance_id IS '业务实例 ID';
COMMENT ON COLUMN lifecycle_instance_trace.transition_id IS '流转 ID';
COMMENT ON COLUMN lifecycle_instance_trace.transition_code IS '流转编码';
COMMENT ON COLUMN lifecycle_instance_trace.from_state_id IS '源状态 ID';
COMMENT ON COLUMN lifecycle_instance_trace.from_state_code IS '源状态编码';
COMMENT ON COLUMN lifecycle_instance_trace.to_state_id IS '目标状态 ID';
COMMENT ON COLUMN lifecycle_instance_trace.to_state_code IS '目标状态编码';
COMMENT ON COLUMN lifecycle_instance_trace.transition_data IS '流转数据';
COMMENT ON COLUMN lifecycle_instance_trace.operator_id IS '操作人 ID';
COMMENT ON COLUMN lifecycle_instance_trace.remark IS '备注信息';
COMMENT ON COLUMN lifecycle_instance_trace.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_instance_trace.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_instance_trace.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_instance_trace.update_time IS '更新时间';

-- 创建索引
CREATE INDEX IF NOT EXISTS idx_trace_instance_id ON lifecycle_instance_trace(instance_id);
CREATE INDEX IF NOT EXISTS idx_trace_business_instance ON lifecycle_instance_trace(business_instance_id);
CREATE INDEX IF NOT EXISTS idx_trace_transition ON lifecycle_instance_trace(transition_id);
CREATE INDEX IF NOT EXISTS idx_trace_create_time ON lifecycle_instance_trace(create_time DESC);

-- ===========================
-- 初始化数据
-- ===========================

-- 插入示例路由规则
INSERT INTO lifecycle_route_rule (id, business_type_id, event_type, rule_name, rule_code, description, condition_expression, priority, is_active, version, create_user_id, update_user_id, create_time, update_time)
VALUES ('route_rule_001', 'business_type_001', 'ORDER_CREATED', '订单创建路由规则', 'ORDER_CREATED_ROUTE', '订单创建时的默认路由规则', '(action == ''CREATE'') AND (node_id == ''start'')', 10, 1, 1, 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;

-- 插入示例事件元数据
INSERT INTO lifecycle_event (id, business_type_id, event_type, business_instance_id, event_data, metadata, operator_id, remark, process_status, create_user_id, update_user_id, create_time, update_time)
VALUES ('event_001', 'business_type_001', 'ORDER_CREATED', 'ORDER_20260327001', '{"orderId":"123","amount":100}', '{"action":"CREATE","node_id":"start"}', 'user123', '订单创建', 'PENDING', 'system', 'system', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (id) DO NOTHING;
