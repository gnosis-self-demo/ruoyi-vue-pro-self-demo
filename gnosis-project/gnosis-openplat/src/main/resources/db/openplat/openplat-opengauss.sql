-- 开放平台数据库脚本
-- 数据库：openGauss 3.0.0
-- 表名前缀：openplat_

-- 1. 对接系统表
CREATE TABLE IF NOT EXISTS openplat_system (
    id VARCHAR(128) PRIMARY KEY,
    system_code VARCHAR(64) NOT NULL UNIQUE,
    system_name VARCHAR(128) NOT NULL,
    system_type VARCHAR(32) NOT NULL,
    app_id VARCHAR(64),
    app_secret VARCHAR(128),
    principal VARCHAR(64),
    status VARCHAR(16) DEFAULT 'ENABLED',
    description VARCHAR(512),
    create_user_id VARCHAR(128) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_user_id VARCHAR(128),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE openplat_system IS '对接系统信息表';
COMMENT ON COLUMN openplat_system.system_code IS '系统编码';
COMMENT ON COLUMN openplat_system.system_name IS '系统名称';
COMMENT ON COLUMN openplat_system.system_type IS '系统类型';
COMMENT ON COLUMN openplat_system.app_id IS '应用ID';
COMMENT ON COLUMN openplat_system.app_secret IS '应用密钥';
COMMENT ON COLUMN openplat_system.principal IS '负责人';
COMMENT ON COLUMN openplat_system.status IS '状态：ENABLED/DISABLED';

-- 2. API 配置表
CREATE TABLE IF NOT EXISTS openplat_api_config (
    id VARCHAR(128) PRIMARY KEY,
    api_code VARCHAR(64) NOT NULL UNIQUE,
    api_name VARCHAR(128) NOT NULL,
    api_path VARCHAR(256) NOT NULL,
    api_method VARCHAR(16) NOT NULL DEFAULT 'POST',
    example_url VARCHAR(512),
    need_auth boolean DEFAULT true,
    need_anti_replay boolean DEFAULT false,
    rate_limit INTEGER DEFAULT 1000,
    system_id VARCHAR(128),
    status VARCHAR(16) DEFAULT 'ENABLED',
    description VARCHAR(512),
    create_user_id VARCHAR(128) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_user_id VARCHAR(128),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE openplat_api_config IS 'API配置表';
COMMENT ON COLUMN openplat_api_config.example_url IS '示例文档链接';
COMMENT ON COLUMN openplat_api_config.need_auth IS '是否需要认证';
COMMENT ON COLUMN openplat_api_config.need_anti_replay IS '是否需要防重放';
COMMENT ON COLUMN openplat_api_config.rate_limit IS '限流阈值';
COMMENT ON COLUMN openplat_api_config.system_id IS '关联系统ID';

CREATE INDEX idx_api_config_path ON openplat_api_config(api_path);
CREATE INDEX idx_api_config_system ON openplat_api_config(system_id);

-- 3. Webhook 配置表
CREATE TABLE IF NOT EXISTS openplat_webhook_config (
    id VARCHAR(128) PRIMARY KEY,
    webhook_name VARCHAR(128) NOT NULL,
    event_type VARCHAR(64) NOT NULL,
    callback_url VARCHAR(512) NOT NULL,
    secret VARCHAR(128),
    retry_times INTEGER DEFAULT 3,
    retry_interval INTEGER DEFAULT 60,
    timeout INTEGER DEFAULT 30,
    status VARCHAR(16) DEFAULT 'ENABLED',
    description VARCHAR(512),
    create_user_id VARCHAR(128) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_user_id VARCHAR(128),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE openplat_webhook_config IS 'Webhook配置表';
COMMENT ON COLUMN openplat_webhook_config.event_type IS '事件类型';
COMMENT ON COLUMN openplat_webhook_config.callback_url IS '回调地址';
COMMENT ON COLUMN openplat_webhook_config.retry_times IS '重试次数';

CREATE INDEX idx_webhook_event ON openplat_webhook_config(event_type);

-- 4. API与系统关系配置表（多对多关联）
CREATE TABLE IF NOT EXISTS openplat_api_system_relation (
    relation_id VARCHAR(128) PRIMARY KEY,
    api_id VARCHAR(128) NOT NULL,
    system_id VARCHAR(128) NOT NULL,
    status VARCHAR(16) DEFAULT 'ENABLED',
    description VARCHAR(512),
    create_user_id VARCHAR(128) NOT NULL,
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_user_id VARCHAR(128),
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_rel_api FOREIGN KEY (api_id) REFERENCES openplat_api_config(id),
    CONSTRAINT fk_rel_system FOREIGN KEY (system_id) REFERENCES openplat_system(id)
);

COMMENT ON TABLE openplat_api_system_relation IS 'API与系统关系配置表';
COMMENT ON COLUMN openplat_api_system_relation.relation_id IS '关系ID';
COMMENT ON COLUMN openplat_api_system_relation.api_id IS 'API配置ID';
COMMENT ON COLUMN openplat_api_system_relation.system_id IS '系统ID';
COMMENT ON COLUMN openplat_api_system_relation.status IS '状态：ENABLED/DISABLED';

CREATE INDEX idx_api_sys_rel_api ON openplat_api_system_relation(api_id);
CREATE INDEX idx_api_sys_rel_system ON openplat_api_system_relation(system_id);

-- 初始化数据
-- 示例系统
INSERT INTO openplat_system (id, system_code, system_name, system_type, app_id, app_secret, principal, status, description, create_user_id, update_user_id)
VALUES
('sys_001', 'SRM', 'SRM supplier platform', 'EXTERNAL', 'app_srm_001', 'secret_srm_2024', 'Zhang San', 'ENABLED', 'Supplier relationship management system', 'admin', 'admin'),
('sys_002', 'ERP', 'ERP purchasing module', 'INTERNAL', 'app_erp_001', 'secret_erp_2024', 'Li Si', 'ENABLED', 'Enterprise resource procurement system', 'admin', 'admin');

-- 示例 API
INSERT INTO openplat_api_config (id, api_code, api_name, api_path, api_method, example_url, need_auth, need_anti_replay, rate_limit, system_id, status, description, create_user_id, update_user_id)
VALUES
('api_001', 'ORDER_CREATE', 'Order creation interface', '/api/v1/order/create', 'POST', 'http://api-docs.example.com/order/create', true, true, 1000, 'sys_001', 'ENABLED', 'Create purchase order', 'admin', 'admin'),
('api_002', 'CONTRACT_UPDATE', 'Contract status update', '/api/v1/contract/update', 'POST', 'http://api-docs.example.com/contract/update', true, true, 500, 'sys_002', 'ENABLED', 'Update contract status', 'admin', 'admin');

-- 示例 Webhook
INSERT INTO openplat_webhook_config (id, webhook_name, event_type, callback_url, secret, retry_times, retry_interval, timeout, status, description, create_user_id, update_user_id)
VALUES
('webhook_001', 'Order creation notification', 'ORDER_CREATED', 'http://srm.example.com/webhook/order', 'webhook_secret_001', 3, 60, 30, 'ENABLED', 'Notify SRM system after order creation', 'admin', 'admin');
