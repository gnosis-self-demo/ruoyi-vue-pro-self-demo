CREATE TABLE IF NOT EXISTS sys_business_config (
    id VARCHAR(128) NOT NULL,
    name VARCHAR(128) NOT NULL,
    code VARCHAR(128) NOT NULL,
    description VARCHAR(512),
    business_type VARCHAR(64) NOT NULL,
    business_category VARCHAR(64),
    rule_engine_config_id VARCHAR(128),
    rule_engine_type VARCHAR(64),
    workflow_enabled BOOLEAN DEFAULT TRUE,
    status VARCHAR(32) NOT NULL DEFAULT 'active',
    tenant_id VARCHAR(128),
    create_user_id VARCHAR(128) NOT NULL,
    update_user_id VARCHAR(128) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE INDEX idx_business_config_code ON sys_business_config(code);
CREATE INDEX idx_business_config_type ON sys_business_config(business_type);
CREATE INDEX idx_business_config_tenant ON sys_business_config(tenant_id);

COMMENT ON TABLE sys_business_config IS '业务配置表';
COMMENT ON COLUMN sys_business_config.id IS '主键';
COMMENT ON COLUMN sys_business_config.name IS '业务名称';
COMMENT ON COLUMN sys_business_config.code IS '业务编码';
COMMENT ON COLUMN sys_business_config.description IS '业务描述';
COMMENT ON COLUMN sys_business_config.business_type IS '业务类型';
COMMENT ON COLUMN sys_business_config.business_category IS '业务分类';
COMMENT ON COLUMN sys_business_config.rule_engine_config_id IS '规则引擎配置ID';
COMMENT ON COLUMN sys_business_config.rule_engine_type IS '规则引擎类型';
COMMENT ON COLUMN sys_business_config.workflow_enabled IS '是否启用工作流';
COMMENT ON COLUMN sys_business_config.status IS '业务状态';
COMMENT ON COLUMN sys_business_config.tenant_id IS '租户ID';
COMMENT ON COLUMN sys_business_config.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_business_config.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_business_config.create_time IS '创建时间';
COMMENT ON COLUMN sys_business_config.update_time IS '更新时间';


CREATE TABLE IF NOT EXISTS sys_business_resource_binding (
    id VARCHAR(128) NOT NULL,
    business_config_id VARCHAR(128) NOT NULL,
    resource_type VARCHAR(64) NOT NULL,
    resource_id VARCHAR(128) NOT NULL,
    resource_name VARCHAR(256),
    binding_type VARCHAR(32) NOT NULL,
    binding_priority INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    metadata TEXT,
    create_user_id VARCHAR(128) NOT NULL,
    update_user_id VARCHAR(128) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE INDEX idx_resource_binding_business ON sys_business_resource_binding(business_config_id);
CREATE INDEX idx_resource_binding_resource ON sys_business_resource_binding(resource_type, resource_id);
CREATE INDEX idx_resource_binding_active ON sys_business_resource_binding(is_active);

COMMENT ON TABLE sys_business_resource_binding IS '业务资源关联表';
COMMENT ON COLUMN sys_business_resource_binding.id IS '主键';
COMMENT ON COLUMN sys_business_resource_binding.business_config_id IS '业务配置ID';
COMMENT ON COLUMN sys_business_resource_binding.resource_type IS '资源类型';
COMMENT ON COLUMN sys_business_resource_binding.resource_id IS '资源ID';
COMMENT ON COLUMN sys_business_resource_binding.resource_name IS '资源名称';
COMMENT ON COLUMN sys_business_resource_binding.binding_type IS '绑定类型';
COMMENT ON COLUMN sys_business_resource_binding.binding_priority IS '绑定优先级';
COMMENT ON COLUMN sys_business_resource_binding.is_active IS '是否激活';
COMMENT ON COLUMN sys_business_resource_binding.metadata IS '扩展元数据';
COMMENT ON COLUMN sys_business_resource_binding.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_business_resource_binding.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_business_resource_binding.create_time IS '创建时间';
COMMENT ON COLUMN sys_business_resource_binding.update_time IS '更新时间';


CREATE TABLE IF NOT EXISTS sys_process_orchestration_config (
    id VARCHAR(128) NOT NULL,
    business_config_id VARCHAR(128) NOT NULL,
    current_process_def_key VARCHAR(128),
    current_process_def_unique_key VARCHAR(128),
    current_process_system VARCHAR(128),
    next_process_def_key VARCHAR(128),
    next_process_def_unique_key VARCHAR(128),
    next_process_system VARCHAR(128),
    trigger_event VARCHAR(128),
    condition_expression TEXT,
    priority INT DEFAULT 0,
    is_active BOOLEAN DEFAULT TRUE,
    create_user_id VARCHAR(128) NOT NULL,
    update_user_id VARCHAR(128) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE INDEX idx_orchestration_business ON sys_process_orchestration_config(business_config_id);
CREATE INDEX idx_orchestration_current ON sys_process_orchestration_config(current_process_def_key);
CREATE INDEX idx_orchestration_next ON sys_process_orchestration_config(next_process_def_key);

COMMENT ON TABLE sys_process_orchestration_config IS '流程编排配置表';
COMMENT ON COLUMN sys_process_orchestration_config.id IS '主键';
COMMENT ON COLUMN sys_process_orchestration_config.business_config_id IS '业务配置ID';
COMMENT ON COLUMN sys_process_orchestration_config.current_process_def_key IS '当前流程定义';
COMMENT ON COLUMN sys_process_orchestration_config.current_process_def_unique_key IS '当前流程定义唯一键';
COMMENT ON COLUMN sys_process_orchestration_config.current_process_system IS '当前流程所属平台';
COMMENT ON COLUMN sys_process_orchestration_config.next_process_def_key IS '下一流程定义';
COMMENT ON COLUMN sys_process_orchestration_config.next_process_def_unique_key IS '下一流程定义唯一键';
COMMENT ON COLUMN sys_process_orchestration_config.next_process_system IS '下一流程所属平台';
COMMENT ON COLUMN sys_process_orchestration_config.trigger_event IS '触发事件';
COMMENT ON COLUMN sys_process_orchestration_config.condition_expression IS '条件表达式';
COMMENT ON COLUMN sys_process_orchestration_config.priority IS '优先级';
COMMENT ON COLUMN sys_process_orchestration_config.is_active IS '是否激活';
COMMENT ON COLUMN sys_process_orchestration_config.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_process_orchestration_config.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_process_orchestration_config.create_time IS '创建时间';
COMMENT ON COLUMN sys_process_orchestration_config.update_time IS '更新时间';


CREATE TABLE IF NOT EXISTS sys_process_event_config (
    id VARCHAR(128) NOT NULL,
    process_def_key VARCHAR(128) NOT NULL,
    process_def_unique_key VARCHAR(128),
    node_def_key VARCHAR(128),
    submit_action VARCHAR(64),
    match_business VARCHAR(128),
    change_business_status VARCHAR(64),
    event_type VARCHAR(64),
    event_handler VARCHAR(256),
    handler_config TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    create_user_id VARCHAR(128) NOT NULL,
    update_user_id VARCHAR(128) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE INDEX idx_event_config_process ON sys_process_event_config(process_def_key);
CREATE INDEX idx_event_config_node ON sys_process_event_config(node_def_key);
CREATE INDEX idx_event_config_business ON sys_process_event_config(match_business);

COMMENT ON TABLE sys_process_event_config IS '事件配置表';
COMMENT ON COLUMN sys_process_event_config.id IS '主键';
COMMENT ON COLUMN sys_process_event_config.process_def_key IS '流程定义键';
COMMENT ON COLUMN sys_process_event_config.process_def_unique_key IS '流程定义唯一键';
COMMENT ON COLUMN sys_process_event_config.node_def_key IS '节点定义键';
COMMENT ON COLUMN sys_process_event_config.submit_action IS '提交动作';
COMMENT ON COLUMN sys_process_event_config.match_business IS '匹配业务';
COMMENT ON COLUMN sys_process_event_config.change_business_status IS '修改业务的目标状态';
COMMENT ON COLUMN sys_process_event_config.event_type IS '事件类型';
COMMENT ON COLUMN sys_process_event_config.event_handler IS '事件处理器';
COMMENT ON COLUMN sys_process_event_config.handler_config IS '处理器配置';
COMMENT ON COLUMN sys_process_event_config.is_active IS '是否激活';
COMMENT ON COLUMN sys_process_event_config.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_process_event_config.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_process_event_config.create_time IS '创建时间';
COMMENT ON COLUMN sys_process_event_config.update_time IS '更新时间';


CREATE TABLE IF NOT EXISTS sys_process_transition_flow (
    id VARCHAR(128) NOT NULL,
    orchestration_config_id VARCHAR(128),
    event_config_id VARCHAR(128),
    process_def_key VARCHAR(128),
    process_def_unique_key VARCHAR(128),
    node_def_key VARCHAR(128),
    process_instance_id VARCHAR(128) NOT NULL,
    process_instance_sequence INT DEFAULT 1,
    business_description VARCHAR(512),
    business_id VARCHAR(128),
    from_node VARCHAR(128),
    to_node VARCHAR(128),
    transition_type VARCHAR(64),
    transition_result VARCHAR(32),
    remarks TEXT,
    create_user_id VARCHAR(128) NOT NULL,
    update_user_id VARCHAR(128) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);

CREATE INDEX idx_transition_instance ON sys_process_transition_flow(process_instance_id);
CREATE INDEX idx_transition_business ON sys_process_transition_flow(business_id);
CREATE INDEX idx_transition_time ON sys_process_transition_flow(create_time);

COMMENT ON TABLE sys_process_transition_flow IS '流转日志表';
COMMENT ON COLUMN sys_process_transition_flow.id IS '主键';
COMMENT ON COLUMN sys_process_transition_flow.orchestration_config_id IS '编排配置ID';
COMMENT ON COLUMN sys_process_transition_flow.event_config_id IS '事件配置ID';
COMMENT ON COLUMN sys_process_transition_flow.process_def_key IS '流程定义键';
COMMENT ON COLUMN sys_process_transition_flow.process_def_unique_key IS '流程定义唯一键';
COMMENT ON COLUMN sys_process_transition_flow.node_def_key IS '节点定义键';
COMMENT ON COLUMN sys_process_transition_flow.process_instance_id IS '流程实例ID';
COMMENT ON COLUMN sys_process_transition_flow.process_instance_sequence IS '流程实例序号';
COMMENT ON COLUMN sys_process_transition_flow.business_description IS '业务描述';
COMMENT ON COLUMN sys_process_transition_flow.business_id IS '业务ID';
COMMENT ON COLUMN sys_process_transition_flow.from_node IS '来源节点';
COMMENT ON COLUMN sys_process_transition_flow.to_node IS '目标节点';
COMMENT ON COLUMN sys_process_transition_flow.transition_type IS '流转类型';
COMMENT ON COLUMN sys_process_transition_flow.transition_result IS '流转结果';
COMMENT ON COLUMN sys_process_transition_flow.remarks IS '备注信息';
COMMENT ON COLUMN sys_process_transition_flow.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_process_transition_flow.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_process_transition_flow.create_time IS '创建时间';
COMMENT ON COLUMN sys_process_transition_flow.update_time IS '更新时间';
