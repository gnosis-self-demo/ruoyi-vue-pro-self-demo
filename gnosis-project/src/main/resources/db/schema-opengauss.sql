-- 分布式队列表
CREATE TABLE IF NOT EXISTS sys_distributed_queue (
    id BIGSERIAL PRIMARY KEY,
    queue_name VARCHAR(100) NOT NULL,
    message_body TEXT NOT NULL,
    status VARCHAR(20) DEFAULT 'pending',
    consumer_id VARCHAR(100),
    attempt_count INTEGER DEFAULT 0,
    error_message TEXT,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- 队列结果表（用于同步等待）
CREATE TABLE IF NOT EXISTS sys_queue_result (
    request_id VARCHAR(100) PRIMARY KEY,
    success BOOLEAN NOT NULL,
    result_data TEXT,
    error_message TEXT,
    created_at TIMESTAMP
);

-- 分布式锁表（用于协调多节点定时任务）
CREATE TABLE IF NOT EXISTS sys_distributed_lock (
    lock_name VARCHAR(100) PRIMARY KEY,
    locked_by VARCHAR(100) NOT NULL,
    locked_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL
);

-- LiteFlow规则配置表
CREATE TABLE IF NOT EXISTS liteflow_sys_rule (
    id BIGSERIAL PRIMARY KEY,
    chain_name VARCHAR(100) NOT NULL,
    rule_content TEXT NOT NULL,
    rule_type VARCHAR(20) DEFAULT 'script',
    enabled BOOLEAN DEFAULT true,
    version INTEGER DEFAULT 1,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LiteFlow组件配置表
CREATE TABLE IF NOT EXISTS liteflow_sys_component (
    id BIGSERIAL PRIMARY KEY,
    component_id VARCHAR(100) NOT NULL,
    component_class VARCHAR(200) NOT NULL,
    component_name VARCHAR(100),
    enabled BOOLEAN DEFAULT true,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LiteFlow方法拦截配置表
CREATE TABLE IF NOT EXISTS liteflow_sys_method_intercept (
    id BIGSERIAL PRIMARY KEY,
    target_class VARCHAR(200) NOT NULL,
    target_method VARCHAR(200) NOT NULL,
    chain_name VARCHAR(100) NOT NULL,
    enabled BOOLEAN DEFAULT true,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- LiteFlow规则版本历史表
CREATE TABLE IF NOT EXISTS liteflow_sys_rule_history (
    id BIGSERIAL PRIMARY KEY,
    rule_id BIGINT NOT NULL,
    chain_name VARCHAR(100) NOT NULL,
    rule_content TEXT NOT NULL,
    version INTEGER NOT NULL,
    operator VARCHAR(100),
    operation_type VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 索引优化
CREATE INDEX IF NOT EXISTS idx_queue_status ON sys_distributed_queue(status);
CREATE INDEX IF NOT EXISTS idx_queue_name_status ON sys_distributed_queue(queue_name, status);
CREATE INDEX IF NOT EXISTS idx_queue_updated_at ON sys_distributed_queue(updated_at);
CREATE INDEX IF NOT EXISTS idx_result_created_at ON sys_queue_result(created_at);
CREATE INDEX IF NOT EXISTS idx_lock_expires_at ON sys_distributed_lock(expires_at);
CREATE INDEX IF NOT EXISTS idx_lf_rule_chain ON liteflow_sys_rule(chain_name, enabled);
CREATE INDEX IF NOT EXISTS idx_lf_rule_version ON liteflow_sys_rule(chain_name, version);
CREATE INDEX IF NOT EXISTS idx_lf_component_id ON liteflow_sys_component(component_id, enabled);
CREATE INDEX IF NOT EXISTS idx_lf_method_intercept_class_method ON liteflow_sys_method_intercept(target_class, target_method, enabled);
CREATE INDEX IF NOT EXISTS idx_lf_rule_history_rule_id ON liteflow_sys_rule_history(rule_id);

-- 添加注释
COMMENT ON TABLE sys_distributed_queue IS '分布式队列表';
COMMENT ON COLUMN sys_distributed_queue.id IS '主键ID';
COMMENT ON COLUMN sys_distributed_queue.queue_name IS '队列名称';
COMMENT ON COLUMN sys_distributed_queue.message_body IS '消息内容';
COMMENT ON COLUMN sys_distributed_queue.status IS '消息状态: pending, processing, done, failed, dead_letter';
COMMENT ON COLUMN sys_distributed_queue.consumer_id IS '消费者ID';
COMMENT ON COLUMN sys_distributed_queue.attempt_count IS '尝试次数';
COMMENT ON COLUMN sys_distributed_queue.error_message IS '错误信息';
COMMENT ON COLUMN sys_distributed_queue.created_at IS '创建时间';
COMMENT ON COLUMN sys_distributed_queue.updated_at IS '更新时间';

COMMENT ON TABLE sys_queue_result IS '队列结果表';
COMMENT ON COLUMN sys_queue_result.request_id IS '请求ID';
COMMENT ON COLUMN sys_queue_result.success IS '是否成功';
COMMENT ON COLUMN sys_queue_result.result_data IS '结果数据';
COMMENT ON COLUMN sys_queue_result.error_message IS '错误信息';
COMMENT ON COLUMN sys_queue_result.created_at IS '创建时间';

COMMENT ON TABLE sys_distributed_lock IS '分布式锁表';
COMMENT ON COLUMN sys_distributed_lock.lock_name IS '锁名称';
COMMENT ON COLUMN sys_distributed_lock.locked_by IS '锁定者标识';
COMMENT ON COLUMN sys_distributed_lock.locked_at IS '锁定时间';
COMMENT ON COLUMN sys_distributed_lock.expires_at IS '锁过期时间';

COMMENT ON TABLE liteflow_sys_rule IS 'LiteFlow规则配置表';
COMMENT ON COLUMN liteflow_sys_rule.id IS '主键ID';
COMMENT ON COLUMN liteflow_sys_rule.chain_name IS '流程链名称';
COMMENT ON COLUMN liteflow_sys_rule.rule_content IS '规则内容';
COMMENT ON COLUMN liteflow_sys_rule.rule_type IS '规则类型: script, xml, json, yml';
COMMENT ON COLUMN liteflow_sys_rule.enabled IS '是否启用';
COMMENT ON COLUMN liteflow_sys_rule.version IS '版本号';
COMMENT ON COLUMN liteflow_sys_rule.description IS '描述';
COMMENT ON COLUMN liteflow_sys_rule.created_at IS '创建时间';
COMMENT ON COLUMN liteflow_sys_rule.updated_at IS '更新时间';

COMMENT ON TABLE liteflow_sys_component IS 'LiteFlow组件配置表';
COMMENT ON COLUMN liteflow_sys_component.id IS '主键ID';
COMMENT ON COLUMN liteflow_sys_component.component_id IS '组件ID';
COMMENT ON COLUMN liteflow_sys_component.component_class IS '组件类全路径';
COMMENT ON COLUMN liteflow_sys_component.component_name IS '组件名称';
COMMENT ON COLUMN liteflow_sys_component.enabled IS '是否启用';
COMMENT ON COLUMN liteflow_sys_component.description IS '描述';
COMMENT ON COLUMN liteflow_sys_component.created_at IS '创建时间';
COMMENT ON COLUMN liteflow_sys_component.updated_at IS '更新时间';

COMMENT ON TABLE liteflow_sys_method_intercept IS 'LiteFlow方法拦截配置表';
COMMENT ON COLUMN liteflow_sys_method_intercept.id IS '主键ID';
COMMENT ON COLUMN liteflow_sys_method_intercept.target_class IS '目标类全路径';
COMMENT ON COLUMN liteflow_sys_method_intercept.target_method IS '目标方法名（可包含参数类型签名）';
COMMENT ON COLUMN liteflow_sys_method_intercept.chain_name IS '关联的流程链名称';
COMMENT ON COLUMN liteflow_sys_method_intercept.enabled IS '是否启用';
COMMENT ON COLUMN liteflow_sys_method_intercept.description IS '描述';
COMMENT ON COLUMN liteflow_sys_method_intercept.created_at IS '创建时间';
COMMENT ON COLUMN liteflow_sys_method_intercept.updated_at IS '更新时间';

COMMENT ON TABLE liteflow_sys_rule_history IS 'LiteFlow规则版本历史表';
COMMENT ON COLUMN liteflow_sys_rule_history.id IS '主键ID';
COMMENT ON COLUMN liteflow_sys_rule_history.rule_id IS '规则ID';
COMMENT ON COLUMN liteflow_sys_rule_history.chain_name IS '流程链名称';
COMMENT ON COLUMN liteflow_sys_rule_history.rule_content IS '规则内容';
COMMENT ON COLUMN liteflow_sys_rule_history.version IS '版本号';
COMMENT ON COLUMN liteflow_sys_rule_history.operator IS '操作人';
COMMENT ON COLUMN liteflow_sys_rule_history.operation_type IS '操作类型: create, update, delete';
COMMENT ON COLUMN liteflow_sys_rule_history.created_at IS '创建时间';
COMMENT ON COLUMN sys_distributed_lock.lock_name IS '锁名称';
COMMENT ON COLUMN sys_distributed_lock.locked_by IS '锁定者标识';
COMMENT ON COLUMN sys_distributed_lock.locked_at IS '锁定时间';
COMMENT ON COLUMN sys_distributed_lock.expires_at IS '锁过期时间';