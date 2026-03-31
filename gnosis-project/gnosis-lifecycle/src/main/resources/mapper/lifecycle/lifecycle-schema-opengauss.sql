-- ============================================================
-- 业务生命周期管理组件 - 数据库表结构
-- 数据库：openGauss 3.0.0
-- 创建日期：2026-03-27
-- ============================================================

-- ----------------------------
-- 1. 业务类型表 (lifecycle_business_type)
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_business_type;
CREATE TABLE lifecycle_business_type (
    id                   VARCHAR(64)         NOT NULL,
    name                 VARCHAR(128)        NOT NULL,
    code                 VARCHAR(64)         NOT NULL,
    description          VARCHAR(512),
    entry_permission_config TEXT,
    create_user_id       VARCHAR(128)        NOT NULL,
    update_user_id       VARCHAR(128)        NOT NULL,
    create_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_lifecycle_bt_code UNIQUE (code)
);

COMMENT ON TABLE lifecycle_business_type IS '业务类型表';
COMMENT ON COLUMN lifecycle_business_type.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_business_type.name IS '业务类型名称';
COMMENT ON COLUMN lifecycle_business_type.code IS '业务类型编码';
COMMENT ON COLUMN lifecycle_business_type.description IS '描述';
COMMENT ON COLUMN lifecycle_business_type.entry_permission_config IS '入口权限配置 (JSON)';
COMMENT ON COLUMN lifecycle_business_type.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_business_type.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_business_type.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_business_type.update_time IS '更新时间';

-- ----------------------------
-- 2. 状态节点表 (lifecycle_state)
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_state;
CREATE TABLE lifecycle_state (
    id                   VARCHAR(64)         NOT NULL,
    business_type_id     VARCHAR(64)         NOT NULL,
    state_name           VARCHAR(128)        NOT NULL,
    state_code           VARCHAR(64)         NOT NULL,
    state_type           VARCHAR(32)         NOT NULL,
    is_final_state       SMALLINT            NOT NULL DEFAULT 0,
    position_x           INTEGER,
    position_y           INTEGER,
    metadata             TEXT,
    create_user_id       VARCHAR(128)        NOT NULL,
    update_user_id       VARCHAR(128)        NOT NULL,
    create_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_lifecycle_state_code UNIQUE (business_type_id, state_code),
    CONSTRAINT fk_lifecycle_state_bt FOREIGN KEY (business_type_id) REFERENCES lifecycle_business_type(id) ON DELETE CASCADE
);

COMMENT ON TABLE lifecycle_state IS '状态节点表';
COMMENT ON COLUMN lifecycle_state.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_state.business_type_id IS '业务类型 ID';
COMMENT ON COLUMN lifecycle_state.state_name IS '状态名称';
COMMENT ON COLUMN lifecycle_state.state_code IS '状态编码';
COMMENT ON COLUMN lifecycle_state.state_type IS '状态类型 (initial/normal/final)';
COMMENT ON COLUMN lifecycle_state.is_final_state IS '是否终态 (0-否 1-是)';
COMMENT ON COLUMN lifecycle_state.position_x IS '位置 X 坐标';
COMMENT ON COLUMN lifecycle_state.position_y IS '位置 Y 坐标';
COMMENT ON COLUMN lifecycle_state.metadata IS '元数据 (JSON)';
COMMENT ON COLUMN lifecycle_state.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_state.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_state.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_state.update_time IS '更新时间';

-- ----------------------------
-- 3. 状态流转关系表 (lifecycle_state_transition)
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_state_transition;
CREATE TABLE lifecycle_state_transition (
    id                   VARCHAR(64)         NOT NULL,
    business_type_id     VARCHAR(64)         NOT NULL,
    from_state_id        VARCHAR(64)         NOT NULL,
    to_state_id          VARCHAR(64)         NOT NULL,
    transition_name      VARCHAR(128),
    transition_code      VARCHAR(64)         NOT NULL,
    condition_expression TEXT,
    priority             INTEGER             NOT NULL DEFAULT 0,
    create_user_id       VARCHAR(128)        NOT NULL,
    update_user_id       VARCHAR(128)        NOT NULL,
    create_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_lifecycle_transition_code UNIQUE (business_type_id, transition_code),
    CONSTRAINT fk_lifecycle_transition_bt FOREIGN KEY (business_type_id) REFERENCES lifecycle_business_type(id) ON DELETE CASCADE,
    CONSTRAINT fk_lifecycle_transition_from FOREIGN KEY (from_state_id) REFERENCES lifecycle_state(id) ON DELETE CASCADE,
    CONSTRAINT fk_lifecycle_transition_to FOREIGN KEY (to_state_id) REFERENCES lifecycle_state(id) ON DELETE CASCADE
);

COMMENT ON TABLE lifecycle_state_transition IS '状态流转关系表';
COMMENT ON COLUMN lifecycle_state_transition.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_state_transition.business_type_id IS '业务类型 ID';
COMMENT ON COLUMN lifecycle_state_transition.from_state_id IS '源状态 ID';
COMMENT ON COLUMN lifecycle_state_transition.to_state_id IS '目标状态 ID';
COMMENT ON COLUMN lifecycle_state_transition.transition_name IS '流转名称';
COMMENT ON COLUMN lifecycle_state_transition.transition_code IS '流转编码';
COMMENT ON COLUMN lifecycle_state_transition.condition_expression IS '条件表达式';
COMMENT ON COLUMN lifecycle_state_transition.priority IS '优先级 (数字越大优先级越高)';
COMMENT ON COLUMN lifecycle_state_transition.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_state_transition.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_state_transition.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_state_transition.update_time IS '更新时间';

-- ----------------------------
-- 4. 事件元数据字典表 (lifecycle_event_metadata)
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_event_metadata;
CREATE TABLE lifecycle_event_metadata (
    id                   VARCHAR(64)         NOT NULL,
    event_type           VARCHAR(64)         NOT NULL,
    metadata_key         VARCHAR(64)         NOT NULL,
    metadata_type        VARCHAR(32)         NOT NULL,
    description          VARCHAR(256),
    allowed_values       TEXT,
    is_required          SMALLINT            NOT NULL DEFAULT 0,
    sort_order           INTEGER             NOT NULL DEFAULT 0,
    create_user_id       VARCHAR(128)        NOT NULL,
    update_user_id       VARCHAR(128)        NOT NULL,
    create_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_lifecycle_event_meta UNIQUE (event_type, metadata_key)
);

COMMENT ON TABLE lifecycle_event_metadata IS '事件元数据字典表';
COMMENT ON COLUMN lifecycle_event_metadata.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_event_metadata.event_type IS '事件类型';
COMMENT ON COLUMN lifecycle_event_metadata.metadata_key IS '元数据键';
COMMENT ON COLUMN lifecycle_event_metadata.metadata_type IS '元数据类型 (string/number/boolean/array/object)';
COMMENT ON COLUMN lifecycle_event_metadata.description IS '描述';
COMMENT ON COLUMN lifecycle_event_metadata.allowed_values IS '允许的值 (JSON 数组)';
COMMENT ON COLUMN lifecycle_event_metadata.is_required IS '是否必填 (0-否 1-是)';
COMMENT ON COLUMN lifecycle_event_metadata.sort_order IS '排序';
COMMENT ON COLUMN lifecycle_event_metadata.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_event_metadata.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_event_metadata.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_event_metadata.update_time IS '更新时间';

-- ----------------------------
-- 5. 事件记录表 (lifecycle_event)
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_event;
CREATE TABLE lifecycle_event (
    id                   VARCHAR(64)         NOT NULL,
    business_type_id     VARCHAR(64)         NOT NULL,
    business_id          VARCHAR(128)        NOT NULL,
    event_type           VARCHAR(64)         NOT NULL,
    event_data           TEXT,
    metadata_snapshot    TEXT,
    source_system        VARCHAR(64),
    status               VARCHAR(32)         NOT NULL DEFAULT 'PENDING',
    create_user_id       VARCHAR(128)        NOT NULL,
    update_user_id       VARCHAR(128)        NOT NULL,
    create_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_lifecycle_event_bt FOREIGN KEY (business_type_id) REFERENCES lifecycle_business_type(id) ON DELETE CASCADE
);

COMMENT ON TABLE lifecycle_event IS '事件记录表';
COMMENT ON COLUMN lifecycle_event.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_event.business_type_id IS '业务类型 ID';
COMMENT ON COLUMN lifecycle_event.business_id IS '业务 ID';
COMMENT ON COLUMN lifecycle_event.event_type IS '事件类型';
COMMENT ON COLUMN lifecycle_event.event_data IS '事件数据 (JSON)';
COMMENT ON COLUMN lifecycle_event.metadata_snapshot IS '元数据快照 (JSON)';
COMMENT ON COLUMN lifecycle_event.source_system IS '来源系统';
COMMENT ON COLUMN lifecycle_event.status IS '状态 (PENDING/PROCESSING/SUCCESS/FAILED)';
COMMENT ON COLUMN lifecycle_event.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_event.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_event.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_event.update_time IS '更新时间';

-- ----------------------------
-- 6. 生命周期实例表 (lifecycle_instance)
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_instance;
CREATE TABLE lifecycle_instance (
    id                   VARCHAR(64)         NOT NULL,
    business_type_id     VARCHAR(64)         NOT NULL,
    business_id          VARCHAR(128)        NOT NULL,
    current_state_id     VARCHAR(64),
    status               VARCHAR(32)         NOT NULL DEFAULT 'ACTIVE',
    started_at           TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    finished_at          TIMESTAMP,
    final_state_id       VARCHAR(64),
    create_user_id       VARCHAR(128)        NOT NULL,
    update_user_id       VARCHAR(128)        NOT NULL,
    create_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT uk_lifecycle_instance_business UNIQUE (business_type_id, business_id),
    CONSTRAINT fk_lifecycle_instance_bt FOREIGN KEY (business_type_id) REFERENCES lifecycle_business_type(id) ON DELETE CASCADE,
    CONSTRAINT fk_lifecycle_instance_current_state FOREIGN KEY (current_state_id) REFERENCES lifecycle_state(id),
    CONSTRAINT fk_lifecycle_instance_final_state FOREIGN KEY (final_state_id) REFERENCES lifecycle_state(id)
);

COMMENT ON TABLE lifecycle_instance IS '生命周期实例表';
COMMENT ON COLUMN lifecycle_instance.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_instance.business_type_id IS '业务类型 ID';
COMMENT ON COLUMN lifecycle_instance.business_id IS '业务 ID';
COMMENT ON COLUMN lifecycle_instance.current_state_id IS '当前状态 ID';
COMMENT ON COLUMN lifecycle_instance.status IS '状态 (ACTIVE/COMPLETED/CANCELLED)';
COMMENT ON COLUMN lifecycle_instance.started_at IS '开始时间';
COMMENT ON COLUMN lifecycle_instance.finished_at IS '结束时间';
COMMENT ON COLUMN lifecycle_instance.final_state_id IS '终态 ID';
COMMENT ON COLUMN lifecycle_instance.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_instance.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_instance.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_instance.update_time IS '更新时间';

-- ----------------------------
-- 7. 实例流转追踪表 (lifecycle_instance_trace)
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_instance_trace;
CREATE TABLE lifecycle_instance_trace (
    id                   VARCHAR(64)         NOT NULL,
    instance_id          VARCHAR(64)         NOT NULL,
    from_state_id        VARCHAR(64),
    to_state_id          VARCHAR(64)         NOT NULL,
    event_id             VARCHAR(64),
    transition_id        VARCHAR(64),
    matched_condition    TEXT,
    execution_result     VARCHAR(32)         NOT NULL DEFAULT 'SUCCESS',
    error_message        TEXT,
    create_user_id       VARCHAR(128)        NOT NULL,
    update_user_id       VARCHAR(128)        NOT NULL,
    create_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_lifecycle_trace_instance FOREIGN KEY (instance_id) REFERENCES lifecycle_instance(id) ON DELETE CASCADE,
    CONSTRAINT fk_lifecycle_trace_from_state FOREIGN KEY (from_state_id) REFERENCES lifecycle_state(id),
    CONSTRAINT fk_lifecycle_trace_to_state FOREIGN KEY (to_state_id) REFERENCES lifecycle_state(id),
    CONSTRAINT fk_lifecycle_trace_event FOREIGN KEY (event_id) REFERENCES lifecycle_event(id),
    CONSTRAINT fk_lifecycle_trace_transition FOREIGN KEY (transition_id) REFERENCES lifecycle_state_transition(id)
);

COMMENT ON TABLE lifecycle_instance_trace IS '实例流转追踪表';
COMMENT ON COLUMN lifecycle_instance_trace.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_instance_trace.instance_id IS '实例 ID';
COMMENT ON COLUMN lifecycle_instance_trace.from_state_id IS '源状态 ID';
COMMENT ON COLUMN lifecycle_instance_trace.to_state_id IS '目标状态 ID';
COMMENT ON COLUMN lifecycle_instance_trace.event_id IS '事件 ID';
COMMENT ON COLUMN lifecycle_instance_trace.transition_id IS '流转 ID';
COMMENT ON COLUMN lifecycle_instance_trace.matched_condition IS '匹配的条件 (JSON)';
COMMENT ON COLUMN lifecycle_instance_trace.execution_result IS '执行结果 (SUCCESS/FAILED)';
COMMENT ON COLUMN lifecycle_instance_trace.error_message IS '错误信息';
COMMENT ON COLUMN lifecycle_instance_trace.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_instance_trace.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_instance_trace.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_instance_trace.update_time IS '更新时间';

-- ----------------------------
-- 8. 路由规则表 (lifecycle_route_rule)
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_route_rule;
CREATE TABLE lifecycle_route_rule (
    id                   VARCHAR(64)         NOT NULL,
    business_type_id     VARCHAR(64)         NOT NULL,
    event_type           VARCHAR(64)         NOT NULL,
    rule_name            VARCHAR(128)        NOT NULL,
    condition_expression TEXT                NOT NULL,
    priority             INTEGER             NOT NULL DEFAULT 0,
    is_active            SMALLINT            NOT NULL DEFAULT 1,
    version              INTEGER             NOT NULL DEFAULT 1,
    create_user_id       VARCHAR(128)        NOT NULL,
    update_user_id       VARCHAR(128)        NOT NULL,
    create_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_lifecycle_route_bt FOREIGN KEY (business_type_id) REFERENCES lifecycle_business_type(id) ON DELETE CASCADE
);

COMMENT ON TABLE lifecycle_route_rule IS '路由规则表';
COMMENT ON COLUMN lifecycle_route_rule.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_route_rule.business_type_id IS '业务类型 ID';
COMMENT ON COLUMN lifecycle_route_rule.event_type IS '事件类型';
COMMENT ON COLUMN lifecycle_route_rule.rule_name IS '规则名称';
COMMENT ON COLUMN lifecycle_route_rule.condition_expression IS '条件表达式';
COMMENT ON COLUMN lifecycle_route_rule.priority IS '优先级 (数字越大优先级越高)';
COMMENT ON COLUMN lifecycle_route_rule.is_active IS '是否启用 (0-否 1-是)';
COMMENT ON COLUMN lifecycle_route_rule.version IS '版本号';
COMMENT ON COLUMN lifecycle_route_rule.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_route_rule.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_route_rule.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_route_rule.update_time IS '更新时间';

-- ----------------------------
-- 9. 下游业务配置表 (lifecycle_downstream_config)
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_downstream_config;
CREATE TABLE lifecycle_downstream_config (
    id                   VARCHAR(64)         NOT NULL,
    final_state_id       VARCHAR(64)         NOT NULL,
    downstream_business_type VARCHAR(64)     NOT NULL,
    trigger_condition    VARCHAR(256),
    trigger_mode         VARCHAR(32)         NOT NULL DEFAULT 'AUTO',
    config_data          TEXT,
    is_active            SMALLINT            NOT NULL DEFAULT 1,
    create_user_id       VARCHAR(128)        NOT NULL,
    update_user_id       VARCHAR(128)        NOT NULL,
    create_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time          TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_lifecycle_downstream_state FOREIGN KEY (final_state_id) REFERENCES lifecycle_state(id) ON DELETE CASCADE
);

COMMENT ON TABLE lifecycle_downstream_config IS '下游业务配置表';
COMMENT ON COLUMN lifecycle_downstream_config.id IS '主键 ID';
COMMENT ON COLUMN lifecycle_downstream_config.final_state_id IS '终态 ID';
COMMENT ON COLUMN lifecycle_downstream_config.downstream_business_type IS '下游业务类型';
COMMENT ON COLUMN lifecycle_downstream_config.trigger_condition IS '触发条件';
COMMENT ON COLUMN lifecycle_downstream_config.trigger_mode IS '触发模式 (AUTO/MANUAL)';
COMMENT ON COLUMN lifecycle_downstream_config.config_data IS '配置数据 (JSON)';
COMMENT ON COLUMN lifecycle_downstream_config.is_active IS '是否启用 (0-否 1-是)';
COMMENT ON COLUMN lifecycle_downstream_config.create_user_id IS '创建人 ID';
COMMENT ON COLUMN lifecycle_downstream_config.update_user_id IS '更新人 ID';
COMMENT ON COLUMN lifecycle_downstream_config.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_downstream_config.update_time IS '更新时间';

-- ----------------------------
-- 索引创建
-- ----------------------------

-- 业务类型表索引
CREATE INDEX idx_lifecycle_bt_code ON lifecycle_business_type(code);
CREATE INDEX idx_lifecycle_bt_name ON lifecycle_business_type(name);

-- 状态节点表索引
CREATE INDEX idx_lifecycle_state_bt_id ON lifecycle_state(business_type_id);
CREATE INDEX idx_lifecycle_state_code ON lifecycle_state(state_code);
CREATE INDEX idx_lifecycle_state_type ON lifecycle_state(state_type);
CREATE INDEX idx_lifecycle_state_final ON lifecycle_state(is_final_state);

-- 状态流转关系表索引
CREATE INDEX idx_lifecycle_transition_bt_id ON lifecycle_state_transition(business_type_id);
CREATE INDEX idx_lifecycle_transition_from ON lifecycle_state_transition(from_state_id);
CREATE INDEX idx_lifecycle_transition_to ON lifecycle_state_transition(to_state_id);

-- 事件元数据字典表索引
CREATE INDEX idx_lifecycle_event_meta_type ON lifecycle_event_metadata(event_type);

-- 事件记录表索引
CREATE INDEX idx_lifecycle_event_bt_id ON lifecycle_event(business_type_id);
CREATE INDEX idx_lifecycle_event_business_id ON lifecycle_event(business_id);
CREATE INDEX idx_lifecycle_event_type ON lifecycle_event(event_type);
CREATE INDEX idx_lifecycle_event_status ON lifecycle_event(status);
CREATE INDEX idx_lifecycle_event_create_time ON lifecycle_event(create_time);

-- 生命周期实例表索引
CREATE INDEX idx_lifecycle_instance_bt_id ON lifecycle_instance(business_type_id);
CREATE INDEX idx_lifecycle_instance_business_id ON lifecycle_instance(business_id);
CREATE INDEX idx_lifecycle_instance_current_state ON lifecycle_instance(current_state_id);
CREATE INDEX idx_lifecycle_instance_status ON lifecycle_instance(status);
CREATE INDEX idx_lifecycle_instance_started_at ON lifecycle_instance(started_at);

-- 实例流转追踪表索引
CREATE INDEX idx_lifecycle_trace_instance_id ON lifecycle_instance_trace(instance_id);
CREATE INDEX idx_lifecycle_trace_from_state ON lifecycle_instance_trace(from_state_id);
CREATE INDEX idx_lifecycle_trace_to_state ON lifecycle_instance_trace(to_state_id);
CREATE INDEX idx_lifecycle_trace_event_id ON lifecycle_instance_trace(event_id);
CREATE INDEX idx_lifecycle_trace_create_time ON lifecycle_instance_trace(create_time);

-- 路由规则表索引
CREATE INDEX idx_lifecycle_route_bt_id ON lifecycle_route_rule(business_type_id);
CREATE INDEX idx_lifecycle_route_event_type ON lifecycle_route_rule(event_type);
CREATE INDEX idx_lifecycle_route_active ON lifecycle_route_rule(is_active);
CREATE INDEX idx_lifecycle_route_priority ON lifecycle_route_rule(priority);

-- 下游业务配置表索引
CREATE INDEX idx_lifecycle_downstream_state_id ON lifecycle_downstream_config(final_state_id);
CREATE INDEX idx_lifecycle_downstream_active ON lifecycle_downstream_config(is_active);
