-- =====================================================
-- 业务生命周期管理 - 出口控制模块和配置治理模块
-- 数据库表结构 (openGauss 3.0.0)
-- =====================================================

-- ----------------------------
-- 1. 出口控制模块 - 终态配置表
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_final_state_config;
CREATE TABLE lifecycle_final_state_config (
    id                      VARCHAR(64)     NOT NULL,
    business_type_id        VARCHAR(64)     NOT NULL,
    state_id                VARCHAR(64)     NOT NULL,
    state_code              VARCHAR(64)     NOT NULL,
    state_name              VARCHAR(128)    NOT NULL,
    final_action_type       VARCHAR(32)     DEFAULT NULL,
    final_action_config     TEXT            DEFAULT NULL,
    allow_reactivate        INTEGER        DEFAULT 0,
    remark                  VARCHAR(500)    DEFAULT NULL,
    create_user_id          VARCHAR(128)    NOT NULL,
    update_user_id          VARCHAR(128)    NOT NULL,
    create_time             TIMESTAMP       NOT NULL,
    update_time             TIMESTAMP       NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE lifecycle_final_state_config IS '终态配置表';
COMMENT ON COLUMN lifecycle_final_state_config.id IS '主键ID';
COMMENT ON COLUMN lifecycle_final_state_config.business_type_id IS '业务类型ID';
COMMENT ON COLUMN lifecycle_final_state_config.state_id IS '状态ID';
COMMENT ON COLUMN lifecycle_final_state_config.state_code IS '状态编码';
COMMENT ON COLUMN lifecycle_final_state_config.state_name IS '状态名称';
COMMENT ON COLUMN lifecycle_final_state_config.final_action_type IS '终态动作类型';
COMMENT ON COLUMN lifecycle_final_state_config.final_action_config IS '终态动作配置';
COMMENT ON COLUMN lifecycle_final_state_config.allow_reactivate IS '是否允许重新激活';
COMMENT ON COLUMN lifecycle_final_state_config.remark IS '备注';
COMMENT ON COLUMN lifecycle_final_state_config.create_user_id IS '创建人ID';
COMMENT ON COLUMN lifecycle_final_state_config.update_user_id IS '更新人ID';
COMMENT ON COLUMN lifecycle_final_state_config.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_final_state_config.update_time IS '更新时间';

-- ----------------------------
-- 2. 出口控制模块 - 下游触发配置表
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_downstream_trigger_config;
CREATE TABLE lifecycle_downstream_trigger_config (
    id                      VARCHAR(64)     NOT NULL,
    final_state_id          VARCHAR(64)     NOT NULL,
    trigger_type            VARCHAR(32)     NOT NULL,
    trigger_url             VARCHAR(500)    DEFAULT NULL,
    trigger_method          VARCHAR(16)     DEFAULT 'POST',
    trigger_headers         TEXT            DEFAULT NULL,
    trigger_payload         TEXT            DEFAULT NULL,
    mq_topic                VARCHAR(128)    DEFAULT NULL,
    mq_tag                  VARCHAR(128)    DEFAULT NULL,
    mq_message_template     TEXT            DEFAULT NULL,
    email_recipients        VARCHAR(500)    DEFAULT NULL,
    email_template          VARCHAR(128)    DEFAULT NULL,
    sms_recipients          VARCHAR(500)    DEFAULT NULL,
    sms_template            VARCHAR(128)    DEFAULT NULL,
    retry_times             INTEGER         DEFAULT 3,
    retry_interval          INTEGER         DEFAULT 60,
    timeout_seconds         INTEGER         DEFAULT 30,
    is_async                INTEGER        DEFAULT 1,
    is_active               INTEGER        DEFAULT 1,
    trigger_condition       VARCHAR(500)    DEFAULT NULL,
    remark                  VARCHAR(500)    DEFAULT NULL,
    create_user_id          VARCHAR(128)    NOT NULL,
    update_user_id          VARCHAR(128)    NOT NULL,
    create_time             TIMESTAMP       NOT NULL,
    update_time             TIMESTAMP       NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE lifecycle_downstream_trigger_config IS '下游触发配置表';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.id IS '主键ID';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.final_state_id IS '终态配置ID';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.trigger_type IS '触发类型';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.trigger_url IS '触发URL';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.trigger_method IS '触发方法';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.trigger_headers IS '请求头配置';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.trigger_payload IS '请求体模板';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.mq_topic IS 'MQ主题';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.mq_tag IS 'MQ标签';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.mq_message_template IS 'MQ消息模板';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.email_recipients IS '邮件接收人列表';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.email_template IS '邮件模板编码';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.sms_recipients IS '短信接收人列表';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.sms_template IS '短信模板编码';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.retry_times IS '重试次数';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.retry_interval IS '重试间隔';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.timeout_seconds IS '超时时间';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.is_async IS '是否异步';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.is_active IS '是否启用';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.trigger_condition IS '触发条件';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.remark IS '备注';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.create_user_id IS '创建人ID';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.update_user_id IS '更新人ID';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_downstream_trigger_config.update_time IS '更新时间';

-- ----------------------------
-- 3. 出口控制模块 - 事件发布记录表
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_event_publish_record;
CREATE TABLE lifecycle_event_publish_record (
    id                      VARCHAR(64)     NOT NULL,
    event_id                VARCHAR(64)     NOT NULL,
    business_type_id        VARCHAR(64)     NOT NULL,
    business_instance_id    VARCHAR(128)    NOT NULL,
    publish_type            VARCHAR(32)     NOT NULL,
    target_type             VARCHAR(32)     NOT NULL,
    target_config           TEXT            DEFAULT NULL,
    payload                 TEXT            NOT NULL,
    publish_status          VARCHAR(32)     DEFAULT 'PENDING',
    retry_count             INTEGER         DEFAULT 0,
    error_message           TEXT            DEFAULT NULL,
    response_data           TEXT            DEFAULT NULL,
    publish_time            TIMESTAMP       DEFAULT NULL,
    remark                  VARCHAR(500)    DEFAULT NULL,
    create_user_id          VARCHAR(128)    NOT NULL,
    update_user_id          VARCHAR(128)    NOT NULL,
    create_time             TIMESTAMP       NOT NULL,
    update_time             TIMESTAMP       NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE lifecycle_event_publish_record IS '事件发布记录表';
COMMENT ON COLUMN lifecycle_event_publish_record.id IS '主键ID';
COMMENT ON COLUMN lifecycle_event_publish_record.event_id IS '事件ID';
COMMENT ON COLUMN lifecycle_event_publish_record.business_type_id IS '业务类型ID';
COMMENT ON COLUMN lifecycle_event_publish_record.business_instance_id IS '业务实例ID';
COMMENT ON COLUMN lifecycle_event_publish_record.publish_type IS '发布类型';
COMMENT ON COLUMN lifecycle_event_publish_record.target_type IS '目标类型';
COMMENT ON COLUMN lifecycle_event_publish_record.target_config IS '目标配置';
COMMENT ON COLUMN lifecycle_event_publish_record.payload IS '消息内容';
COMMENT ON COLUMN lifecycle_event_publish_record.publish_status IS '发布状态';
COMMENT ON COLUMN lifecycle_event_publish_record.retry_count IS '重试次数';
COMMENT ON COLUMN lifecycle_event_publish_record.error_message IS '错误信息';
COMMENT ON COLUMN lifecycle_event_publish_record.response_data IS '响应数据';
COMMENT ON COLUMN lifecycle_event_publish_record.publish_time IS '发布时间';
COMMENT ON COLUMN lifecycle_event_publish_record.remark IS '备注';
COMMENT ON COLUMN lifecycle_event_publish_record.create_user_id IS '创建人ID';
COMMENT ON COLUMN lifecycle_event_publish_record.update_user_id IS '更新人ID';
COMMENT ON COLUMN lifecycle_event_publish_record.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_event_publish_record.update_time IS '更新时间';

-- ----------------------------
-- 4. 配置治理模块 - 元数据字典表
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_metadata_dictionary;
CREATE TABLE lifecycle_metadata_dictionary (
    id                      VARCHAR(64)     NOT NULL,
    business_type_id        VARCHAR(64)     NOT NULL,
    field_name              VARCHAR(64)     NOT NULL,
    field_label             VARCHAR(128)    NOT NULL,
    field_type              VARCHAR(32)     NOT NULL,
    field_length            INTEGER         DEFAULT 255,
    is_required             INTEGER        DEFAULT 0,
    is_searchable           INTEGER        DEFAULT 0,
    is_display              INTEGER        DEFAULT 1,
    display_order           INTEGER         DEFAULT 0,
    enum_values             TEXT            DEFAULT NULL,
    validation_rule         VARCHAR(500)    DEFAULT NULL,
    default_value           VARCHAR(500)    DEFAULT NULL,
    remark                  VARCHAR(500)    DEFAULT NULL,
    create_user_id          VARCHAR(128)    NOT NULL,
    update_user_id          VARCHAR(128)    NOT NULL,
    create_time             TIMESTAMP       NOT NULL,
    update_time             TIMESTAMP       NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE lifecycle_metadata_dictionary IS '元数据字典表';
COMMENT ON COLUMN lifecycle_metadata_dictionary.id IS '主键ID';
COMMENT ON COLUMN lifecycle_metadata_dictionary.business_type_id IS '业务类型ID';
COMMENT ON COLUMN lifecycle_metadata_dictionary.field_name IS '字段名称';
COMMENT ON COLUMN lifecycle_metadata_dictionary.field_label IS '字段标签';
COMMENT ON COLUMN lifecycle_metadata_dictionary.field_type IS '字段类型';
COMMENT ON COLUMN lifecycle_metadata_dictionary.field_length IS '字段长度';
COMMENT ON COLUMN lifecycle_metadata_dictionary.is_required IS '是否必填';
COMMENT ON COLUMN lifecycle_metadata_dictionary.is_searchable IS '是否可搜索';
COMMENT ON COLUMN lifecycle_metadata_dictionary.is_display IS '是否展示';
COMMENT ON COLUMN lifecycle_metadata_dictionary.display_order IS '展示顺序';
COMMENT ON COLUMN lifecycle_metadata_dictionary.enum_values IS '枚举值列表';
COMMENT ON COLUMN lifecycle_metadata_dictionary.validation_rule IS '校验规则';
COMMENT ON COLUMN lifecycle_metadata_dictionary.default_value IS '默认值';
COMMENT ON COLUMN lifecycle_metadata_dictionary.remark IS '备注';
COMMENT ON COLUMN lifecycle_metadata_dictionary.create_user_id IS '创建人ID';
COMMENT ON COLUMN lifecycle_metadata_dictionary.update_user_id IS '更新人ID';
COMMENT ON COLUMN lifecycle_metadata_dictionary.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_metadata_dictionary.update_time IS '更新时间';

-- ----------------------------
-- 5. 配置治理模块 - 规则版本历史表
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_route_rule_version;
CREATE TABLE lifecycle_route_rule_version (
    id                      VARCHAR(64)     NOT NULL,
    rule_id                 VARCHAR(64)     NOT NULL,
    version_number          INTEGER         NOT NULL,
    rule_content            TEXT            NOT NULL,
    change_type             VARCHAR(32)     NOT NULL,
    change_reason           VARCHAR(500)    DEFAULT NULL,
    is_current_version      INTEGER        DEFAULT 0,
    rollback_from_version   INTEGER         DEFAULT NULL,
    remark                  VARCHAR(500)    DEFAULT NULL,
    create_user_id          VARCHAR(128)    NOT NULL,
    update_user_id          VARCHAR(128)    NOT NULL,
    create_time             TIMESTAMP       NOT NULL,
    update_time             TIMESTAMP       NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE lifecycle_route_rule_version IS '规则版本历史表';
COMMENT ON COLUMN lifecycle_route_rule_version.id IS '主键ID';
COMMENT ON COLUMN lifecycle_route_rule_version.rule_id IS '规则ID';
COMMENT ON COLUMN lifecycle_route_rule_version.version_number IS '版本号';
COMMENT ON COLUMN lifecycle_route_rule_version.rule_content IS '规则内容';
COMMENT ON COLUMN lifecycle_route_rule_version.change_type IS '变更类型';
COMMENT ON COLUMN lifecycle_route_rule_version.change_reason IS '变更原因';
COMMENT ON COLUMN lifecycle_route_rule_version.is_current_version IS '是否当前版本';
COMMENT ON COLUMN lifecycle_route_rule_version.rollback_from_version IS '回滚来源版本';
COMMENT ON COLUMN lifecycle_route_rule_version.remark IS '备注';
COMMENT ON COLUMN lifecycle_route_rule_version.create_user_id IS '创建人ID';
COMMENT ON COLUMN lifecycle_route_rule_version.update_user_id IS '更新人ID';
COMMENT ON COLUMN lifecycle_route_rule_version.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_route_rule_version.update_time IS '更新时间';

-- ----------------------------
-- 6. 配置治理模块 - 实例追踪表
-- ----------------------------
DROP TABLE IF EXISTS lifecycle_instance_trace;
CREATE TABLE lifecycle_instance_trace (
    id                      VARCHAR(64)     NOT NULL,
    business_type_id        VARCHAR(64)     NOT NULL,
    business_instance_id    VARCHAR(128)    NOT NULL,
    trace_type              VARCHAR(32)     NOT NULL,
    trace_data              TEXT            NOT NULL,
    source_state_id         VARCHAR(64)     DEFAULT NULL,
    source_state_code       VARCHAR(64)     DEFAULT NULL,
    target_state_id         VARCHAR(64)     DEFAULT NULL,
    target_state_code       VARCHAR(64)     DEFAULT NULL,
    event_id                VARCHAR(64)     DEFAULT NULL,
    event_type              VARCHAR(64)     DEFAULT NULL,
    action_type             VARCHAR(64)     DEFAULT NULL,
    action_result           TEXT            DEFAULT NULL,
    trace_status            VARCHAR(32)     DEFAULT 'SUCCESS',
    error_message           TEXT            DEFAULT NULL,
    execution_time_ms       BIGINT          DEFAULT NULL,
    remark                  VARCHAR(500)    DEFAULT NULL,
    create_user_id          VARCHAR(128)    NOT NULL,
    update_user_id          VARCHAR(128)    NOT NULL,
    create_time             TIMESTAMP       NOT NULL,
    update_time             TIMESTAMP       NOT NULL,
    PRIMARY KEY (id)
);

COMMENT ON TABLE lifecycle_instance_trace IS '实例追踪表';
COMMENT ON COLUMN lifecycle_instance_trace.id IS '主键ID';
COMMENT ON COLUMN lifecycle_instance_trace.business_type_id IS '业务类型ID';
COMMENT ON COLUMN lifecycle_instance_trace.business_instance_id IS '业务实例ID';
COMMENT ON COLUMN lifecycle_instance_trace.trace_type IS '追踪类型';
COMMENT ON COLUMN lifecycle_instance_trace.trace_data IS '追踪数据';
COMMENT ON COLUMN lifecycle_instance_trace.source_state_id IS '源状态ID';
COMMENT ON COLUMN lifecycle_instance_trace.source_state_code IS '源状态编码';
COMMENT ON COLUMN lifecycle_instance_trace.target_state_id IS '目标状态ID';
COMMENT ON COLUMN lifecycle_instance_trace.target_state_code IS '目标状态编码';
COMMENT ON COLUMN lifecycle_instance_trace.event_id IS '事件ID';
COMMENT ON COLUMN lifecycle_instance_trace.event_type IS '事件类型';
COMMENT ON COLUMN lifecycle_instance_trace.action_type IS '动作类型';
COMMENT ON COLUMN lifecycle_instance_trace.action_result IS '执行结果';
COMMENT ON COLUMN lifecycle_instance_trace.trace_status IS '追踪状态';
COMMENT ON COLUMN lifecycle_instance_trace.error_message IS '错误信息';
COMMENT ON COLUMN lifecycle_instance_trace.execution_time_ms IS '执行耗时';
COMMENT ON COLUMN lifecycle_instance_trace.remark IS '备注';
COMMENT ON COLUMN lifecycle_instance_trace.create_user_id IS '创建人ID';
COMMENT ON COLUMN lifecycle_instance_trace.update_user_id IS '更新人ID';
COMMENT ON COLUMN lifecycle_instance_trace.create_time IS '创建时间';
COMMENT ON COLUMN lifecycle_instance_trace.update_time IS '更新时间';

-- ----------------------------
-- 索引创建
-- ----------------------------
CREATE INDEX idx_lifecycle_final_state_business_type ON lifecycle_final_state_config(business_type_id);
CREATE INDEX idx_lifecycle_final_state_state ON lifecycle_final_state_config(state_id);
CREATE INDEX idx_lifecycle_downstream_final_state ON lifecycle_downstream_trigger_config(final_state_id);
CREATE INDEX idx_lifecycle_downstream_active ON lifecycle_downstream_trigger_config(is_active);
CREATE INDEX idx_lifecycle_event_publish_event ON lifecycle_event_publish_record(event_id);
CREATE INDEX idx_lifecycle_event_publish_business ON lifecycle_event_publish_record(business_instance_id);
CREATE INDEX idx_lifecycle_event_publish_status ON lifecycle_event_publish_record(publish_status);
CREATE INDEX idx_lifecycle_metadata_business_type ON lifecycle_metadata_dictionary(business_type_id);
CREATE INDEX idx_lifecycle_metadata_field ON lifecycle_metadata_dictionary(field_name);
CREATE INDEX idx_lifecycle_rule_version_rule ON lifecycle_route_rule_version(rule_id);
CREATE INDEX idx_lifecycle_rule_version_current ON lifecycle_route_rule_version(is_current_version);
CREATE INDEX idx_lifecycle_trace_business ON lifecycle_instance_trace(business_instance_id);
CREATE INDEX idx_lifecycle_trace_type ON lifecycle_instance_trace(trace_type);
CREATE INDEX idx_lifecycle_trace_time ON lifecycle_instance_trace(create_time);
