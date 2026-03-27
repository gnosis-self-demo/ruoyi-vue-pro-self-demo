-- 开放平台数据库脚本
-- 数据库：openGauss 3.0.0
-- 表名前缀：openplat_

-- 1. 对接系统表
CREATE TABLE IF NOT EXISTS openplat_system (
    id VARCHAR(128) PRIMARY KEY,
    system_code VARCHAR(64) NOT NULL UNIQUE COMMENT '系统编码',
    system_name VARCHAR(128) NOT NULL COMMENT '系统名称',
    system_type VARCHAR(32) NOT NULL COMMENT '系统类型',
   负责人 VARCHAR(64) COMMENT '负责人',
    status VARCHAR(16) DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
    description VARCHAR(512) COMMENT '描述',
    create_user_id VARCHAR(128) NOT NULL COMMENT '创建人 ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user_id VARCHAR(128) COMMENT '更新人 ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

COMMENT ON TABLE openplat_system IS '对接系统信息表';
COMMENT ON COLUMN openplat_system.system_code IS '系统编码';
COMMENT ON COLUMN openplat_system.system_name IS '系统名称';
COMMENT ON COLUMN openplat_system.system_type IS '系统类型';
COMMENT ON COLUMN openplat_system.负责人 IS '负责人';
COMMENT ON COLUMN openplat_system.status IS '状态：ENABLED/DISABLED';

-- 2. 应用认证表
CREATE TABLE IF NOT EXISTS openplat_app_auth (
    id VARCHAR(128) PRIMARY KEY,
    app_id VARCHAR(64) NOT NULL UNIQUE COMMENT '应用 ID',
    app_secret VARCHAR(128) NOT NULL COMMENT '应用密钥',
    system_id VARCHAR(128) NOT NULL COMMENT '关联系统 ID',
    status VARCHAR(16) DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
    description VARCHAR(512) COMMENT '描述',
    create_user_id VARCHAR(128) NOT NULL COMMENT '创建人 ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user_id VARCHAR(128) COMMENT '更新人 ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_app_system FOREIGN KEY (system_id) REFERENCES openplat_system(id)
);

COMMENT ON TABLE openplat_app_auth IS '应用认证表';
COMMENT ON COLUMN openplat_app_auth.app_id IS '应用 ID';
COMMENT ON COLUMN openplat_app_auth.app_secret IS '应用密钥';
COMMENT ON COLUMN openplat_app_auth.system_id IS '关联系统 ID';

CREATE INDEX idx_app_auth_system ON openplat_app_auth(system_id);

-- 3. API 配置表
CREATE TABLE IF NOT EXISTS openplat_api_config (
    id VARCHAR(128) PRIMARY KEY,
    api_code VARCHAR(64) NOT NULL UNIQUE COMMENT 'API 编码',
    api_name VARCHAR(128) NOT NULL COMMENT 'API 名称',
    api_path VARCHAR(256) NOT NULL COMMENT 'API 路径',
    api_method VARCHAR(16) NOT NULL DEFAULT 'POST' COMMENT '请求方法',
    example_url VARCHAR(512) COMMENT '示例文档链接',
    need_auth BOOLEAN DEFAULT TRUE COMMENT '是否需要认证',
    need_timestamp BOOLEAN DEFAULT TRUE COMMENT '是否需要时间戳',
    need_nonce BOOLEAN DEFAULT TRUE COMMENT '是否需要 nonce',
    rate_limit INTEGER DEFAULT 1000 COMMENT '限流阈值（次/分钟）',
    status VARCHAR(16) DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
    description VARCHAR(512) COMMENT '描述',
    create_user_id VARCHAR(128) NOT NULL COMMENT '创建人 ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user_id VARCHAR(128) COMMENT '更新人 ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

COMMENT ON TABLE openplat_api_config IS 'API 配置表';
COMMENT ON COLUMN openplat_api_config.example_url IS '示例文档链接';
COMMENT ON COLUMN openplat_api_config.need_auth IS '是否需要认证';
COMMENT ON COLUMN openplat_api_config.need_timestamp IS '是否需要时间戳';
COMMENT ON COLUMN openplat_api_config.need_nonce IS '是否需要 nonce';
COMMENT ON COLUMN openplat_api_config.rate_limit IS '限流阈值';

CREATE INDEX idx_api_config_path ON openplat_api_config(api_path);

-- 4. API 文档表
CREATE TABLE IF NOT EXISTS openplat_api_doc (
    id VARCHAR(128) PRIMARY KEY,
    api_id VARCHAR(128) NOT NULL COMMENT '关联 API ID',
    doc_title VARCHAR(256) NOT NULL COMMENT '文档标题',
    doc_content TEXT COMMENT '文档内容',
    request_params TEXT COMMENT '请求参数说明',
    response_params TEXT COMMENT '响应参数说明',
    error_codes TEXT COMMENT '错误码定义',
    version VARCHAR(32) DEFAULT '1.0.0' COMMENT '版本',
    create_user_id VARCHAR(128) NOT NULL COMMENT '创建人 ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user_id VARCHAR(128) COMMENT '更新人 ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    CONSTRAINT fk_doc_api FOREIGN KEY (api_id) REFERENCES openplat_api_config(id)
);

COMMENT ON TABLE openplat_api_doc IS 'API 文档表';

CREATE INDEX idx_api_doc_api ON openplat_api_doc(api_id);

-- 5. Webhook 配置表
CREATE TABLE IF NOT EXISTS openplat_webhook_config (
    id VARCHAR(128) PRIMARY KEY,
    webhook_name VARCHAR(128) NOT NULL COMMENT 'Webhook 名称',
    event_type VARCHAR(64) NOT NULL COMMENT '事件类型',
    callback_url VARCHAR(512) NOT NULL COMMENT '回调地址',
    secret VARCHAR(128) COMMENT '签名密钥',
    retry_times INTEGER DEFAULT 3 COMMENT '重试次数',
    retry_interval INTEGER DEFAULT 60 COMMENT '重试间隔（秒）',
    timeout INTEGER DEFAULT 30 COMMENT '超时时间（秒）',
    status VARCHAR(16) DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
    description VARCHAR(512) COMMENT '描述',
    create_user_id VARCHAR(128) NOT NULL COMMENT '创建人 ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user_id VARCHAR(128) COMMENT '更新人 ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

COMMENT ON TABLE openplat_webhook_config IS 'Webhook 配置表';
COMMENT ON COLUMN openplat_webhook_config.event_type IS '事件类型';
COMMENT ON COLUMN openplat_webhook_config.callback_url IS '回调地址';
COMMENT ON COLUMN openplat_webhook_config.retry_times IS '重试次数';

CREATE INDEX idx_webhook_event ON openplat_webhook_config(event_type);

-- 6. 调用日志表
CREATE TABLE IF NOT EXISTS openplat_access_log (
    id VARCHAR(128) PRIMARY KEY,
    app_id VARCHAR(64) NOT NULL COMMENT '应用 ID',
    api_code VARCHAR(64) NOT NULL COMMENT 'API 编码',
    request_method VARCHAR(16) COMMENT '请求方法',
    request_path VARCHAR(512) COMMENT '请求路径',
    request_params TEXT COMMENT '请求参数',
    request_body TEXT COMMENT '请求体',
    response_code INTEGER COMMENT '响应码',
    response_body TEXT COMMENT '响应体',
    cost_time INTEGER COMMENT '耗时（毫秒）',
    ip_address VARCHAR(64) COMMENT 'IP 地址',
    user_agent VARCHAR(512) COMMENT '用户代理',
    error_message TEXT COMMENT '错误信息',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
);

COMMENT ON TABLE openplat_access_log IS '调用日志表';

CREATE INDEX idx_access_log_app ON openplat_access_log(app_id);
CREATE INDEX idx_access_log_api ON openplat_access_log(api_code);
CREATE INDEX idx_access_log_time ON openplat_access_log(create_time);

-- 7. 限流配置表
CREATE TABLE IF NOT EXISTS openplat_rate_limit (
    id VARCHAR(128) PRIMARY KEY,
    limit_name VARCHAR(128) NOT NULL COMMENT '限流名称',
    limit_type VARCHAR(32) NOT NULL COMMENT '限流类型：APP/API/IP',
    limit_key VARCHAR(128) NOT NULL COMMENT '限流键',
    rate_limit INTEGER NOT NULL COMMENT '速率限制',
    burst_limit INTEGER DEFAULT 0 COMMENT '突发限制',
    window_size INTEGER DEFAULT 60 COMMENT '时间窗口（秒）',
    status VARCHAR(16) DEFAULT 'ENABLED' COMMENT '状态：ENABLED/DISABLED',
    description VARCHAR(512) COMMENT '描述',
    create_user_id VARCHAR(128) NOT NULL COMMENT '创建人 ID',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_user_id VARCHAR(128) COMMENT '更新人 ID',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间'
);

COMMENT ON TABLE openplat_rate_limit IS '限流配置表';
COMMENT ON COLUMN openplat_rate_limit.limit_type IS '限流类型：APP/API/IP';
COMMENT ON COLUMN openplat_rate_limit.limit_key IS '限流键';

CREATE INDEX idx_rate_limit_key ON openplat_rate_limit(limit_type, limit_key);

-- 8. 防重放缓存表
CREATE TABLE IF NOT EXISTS openplat_nonce_cache (
    id VARCHAR(128) PRIMARY KEY,
    app_id VARCHAR(64) NOT NULL COMMENT '应用 ID',
    nonce VARCHAR(128) NOT NULL COMMENT '随机数',
    expire_time TIMESTAMP NOT NULL COMMENT '过期时间',
    create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    CONSTRAINT uk_app_nonce UNIQUE (app_id, nonce)
);

COMMENT ON TABLE openplat_nonce_cache IS '防重放缓存表';

CREATE INDEX idx_nonce_expire ON openplat_nonce_cache(expire_time);

-- 初始化数据
-- 示例系统
INSERT INTO openplat_system (id, system_code, system_name, system_type, 负责人，status, description, create_user_id, update_user_id)
VALUES 
('sys_001', 'SRM', 'SRM 供应商平台', 'EXTERNAL', '张三', 'ENABLED', '供应商关系管理系统', 'admin', 'admin'),
('sys_002', 'ERP', 'ERP 采购模块', 'INTERNAL', '李四', 'ENABLED', '企业资源采购系统', 'admin', 'admin');

-- 示例应用
INSERT INTO openplat_app_auth (id, app_id, app_secret, system_id, status, description, create_user_id, update_user_id)
VALUES 
('app_001', 'app_srm_001', 'secret_srm_2024', 'sys_001', 'ENABLED', 'SRM 平台应用', 'admin', 'admin'),
('app_002', 'app_erp_001', 'secret_erp_2024', 'sys_002', 'ENABLED', 'ERP 采购应用', 'admin', 'admin');

-- 示例 API
INSERT INTO openplat_api_config (id, api_code, api_name, api_path, api_method, example_url, need_auth, need_timestamp, need_nonce, rate_limit, status, description, create_user_id, update_user_id)
VALUES 
('api_001', 'ORDER_CREATE', '订单创建接口', '/api/v1/order/create', 'POST', 'http://api-docs.example.com/order/create', TRUE, TRUE, TRUE, 1000, 'ENABLED', '创建采购订单', 'admin', 'admin'),
('api_002', 'CONTRACT_UPDATE', '合同状态变更', '/api/v1/contract/update', 'POST', 'http://api-docs.example.com/contract/update', TRUE, TRUE, TRUE, 500, 'ENABLED', '更新合同状态', 'admin', 'admin');

-- 示例 Webhook
INSERT INTO openplat_webhook_config (id, webhook_name, event_type, callback_url, secret, retry_times, retry_interval, timeout, status, description, create_user_id, update_user_id)
VALUES 
('webhook_001', '订单创建通知', 'ORDER_CREATED', 'http://srm.example.com/webhook/order', 'webhook_secret_001', 3, 60, 30, 'ENABLED', '订单创建后通知 SRM 系统', 'admin', 'admin');

-- 示例限流配置
INSERT INTO openplat_rate_limit (id, limit_name, limit_type, limit_key, rate_limit, burst_limit, window_size, status, description, create_user_id, update_user_id)
VALUES 
('limit_001', 'SRM 限流', 'APP', 'app_srm_001', 1000, 100, 60, 'ENABLED', 'SRM 平台限流配置', 'admin', 'admin');
