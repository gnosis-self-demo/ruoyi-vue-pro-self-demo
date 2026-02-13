-- 分布式队列表
CREATE TABLE IF NOT EXISTS sys_distributed_queue (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
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

-- 插入测试规则
INSERT INTO liteflow_sys_rule (chain_name, rule_content, rule_type, enabled, description) 
VALUES ('testChain', 'return "Hello from LiteFlow SQL Rule";', 'script', true, 'Test rule for startup verification');