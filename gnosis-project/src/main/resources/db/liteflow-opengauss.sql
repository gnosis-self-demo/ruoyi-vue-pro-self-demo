-- LiteFlow规则配置表
CREATE TABLE IF NOT EXISTS liteflow_sys_rule (
    id BIGSERIAL PRIMARY KEY,
    chain_name VARCHAR(100) NOT NULL,
    rule_content TEXT NOT NULL,
    rule_type VARCHAR(20) DEFAULT 'xml',
    enabled BOOLEAN DEFAULT true,
    version INTEGER DEFAULT 1,
    description VARCHAR(500),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 添加表注释
COMMENT ON TABLE liteflow_sys_rule IS 'LiteFlow规则配置表';
COMMENT ON COLUMN liteflow_sys_rule.id IS '主键ID';
COMMENT ON COLUMN liteflow_sys_rule.chain_name IS '规则链名称';
COMMENT ON COLUMN liteflow_sys_rule.rule_content IS '规则内容';
COMMENT ON COLUMN liteflow_sys_rule.rule_type IS '规则类型（xml/script）';
COMMENT ON COLUMN liteflow_sys_rule.enabled IS '是否启用';
COMMENT ON COLUMN liteflow_sys_rule.version IS '版本号';
COMMENT ON COLUMN liteflow_sys_rule.description IS '描述';
COMMENT ON COLUMN liteflow_sys_rule.created_at IS '创建时间';
COMMENT ON COLUMN liteflow_sys_rule.updated_at IS '更新时间';

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

-- 添加表注释
COMMENT ON TABLE liteflow_sys_component IS 'LiteFlow组件配置表';
COMMENT ON COLUMN liteflow_sys_component.id IS '主键ID';
COMMENT ON COLUMN liteflow_sys_component.component_id IS '组件ID';
COMMENT ON COLUMN liteflow_sys_component.component_class IS '组件类名';
COMMENT ON COLUMN liteflow_sys_component.component_name IS '组件名称';
COMMENT ON COLUMN liteflow_sys_component.enabled IS '是否启用';
COMMENT ON COLUMN liteflow_sys_component.description IS '描述';
COMMENT ON COLUMN liteflow_sys_component.created_at IS '创建时间';
COMMENT ON COLUMN liteflow_sys_component.updated_at IS '更新时间';

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

-- 添加表注释
COMMENT ON TABLE liteflow_sys_method_intercept IS 'LiteFlow方法拦截配置表';
COMMENT ON COLUMN liteflow_sys_method_intercept.id IS '主键ID';
COMMENT ON COLUMN liteflow_sys_method_intercept.target_class IS '目标类名';
COMMENT ON COLUMN liteflow_sys_method_intercept.target_method IS '目标方法名';
COMMENT ON COLUMN liteflow_sys_method_intercept.chain_name IS '关联的规则链名称';
COMMENT ON COLUMN liteflow_sys_method_intercept.enabled IS '是否启用';
COMMENT ON COLUMN liteflow_sys_method_intercept.description IS '描述';
COMMENT ON COLUMN liteflow_sys_method_intercept.created_at IS '创建时间';
COMMENT ON COLUMN liteflow_sys_method_intercept.updated_at IS '更新时间';

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

-- 添加表注释
COMMENT ON TABLE liteflow_sys_rule_history IS 'LiteFlow规则版本历史表';
COMMENT ON COLUMN liteflow_sys_rule_history.id IS '主键ID';
COMMENT ON COLUMN liteflow_sys_rule_history.rule_id IS '规则ID';
COMMENT ON COLUMN liteflow_sys_rule_history.chain_name IS '规则链名称';
COMMENT ON COLUMN liteflow_sys_rule_history.rule_content IS '规则内容';
COMMENT ON COLUMN liteflow_sys_rule_history.version IS '版本号';
COMMENT ON COLUMN liteflow_sys_rule_history.operator IS '操作人';
COMMENT ON COLUMN liteflow_sys_rule_history.operation_type IS '操作类型（INSERT/UPDATE/DELETE）';
COMMENT ON COLUMN liteflow_sys_rule_history.created_at IS '创建时间';

-- 插入测试规则
INSERT INTO liteflow_sys_rule (chain_name, rule_content, rule_type, enabled, description) 
VALUES ('testChain', 'THEN(testComponent);', 'xml', true, 'Test rule for startup verification');