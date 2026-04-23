-- 数据映射配置表
CREATE TABLE IF NOT EXISTS sys_data_mapping_config (
    id VARCHAR(128) PRIMARY KEY,
    config_name VARCHAR(256) NOT NULL,
    config_code VARCHAR(128) NOT NULL UNIQUE,
    system_id VARCHAR(128),
    system_name VARCHAR(256),
    config_version VARCHAR(64),
    api_version VARCHAR(64),
    target_endpoint VARCHAR(512),
    json_root_path VARCHAR(512),
    config_json TEXT,
    description VARCHAR(1024),
    status INT DEFAULT 1,
    create_user_id VARCHAR(128),
    update_user_id VARCHAR(128),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_data_mapping_config IS '数据映射配置表';
COMMENT ON COLUMN sys_data_mapping_config.id IS '主键';
COMMENT ON COLUMN sys_data_mapping_config.config_name IS '配置名称';
COMMENT ON COLUMN sys_data_mapping_config.config_code IS '配置编码';
COMMENT ON COLUMN sys_data_mapping_config.system_id IS '系统ID';
COMMENT ON COLUMN sys_data_mapping_config.system_name IS '系统名称';
COMMENT ON COLUMN sys_data_mapping_config.config_version IS '配置版本';
COMMENT ON COLUMN sys_data_mapping_config.api_version IS 'API版本';
COMMENT ON COLUMN sys_data_mapping_config.target_endpoint IS '目标接口地址';
COMMENT ON COLUMN sys_data_mapping_config.json_root_path IS 'JSON根路径';
COMMENT ON COLUMN sys_data_mapping_config.config_json IS '完整配置JSON';
COMMENT ON COLUMN sys_data_mapping_config.description IS '描述';
COMMENT ON COLUMN sys_data_mapping_config.status IS '状态（0=禁用，1=启用）';
COMMENT ON COLUMN sys_data_mapping_config.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_data_mapping_config.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_data_mapping_config.create_time IS '创建时间';
COMMENT ON COLUMN sys_data_mapping_config.update_time IS '更新时间';

-- 数据映射执行日志表
CREATE TABLE IF NOT EXISTS sys_data_mapping_execution_log (
    id VARCHAR(128) PRIMARY KEY,
    config_id VARCHAR(128),
    config_code VARCHAR(128),
    request_json TEXT,
    result_json TEXT,
    config_json TEXT,
    success BOOLEAN DEFAULT FALSE,
    error_info TEXT,
    execution_time INT,
    create_user_id VARCHAR(128),
    update_user_id VARCHAR(128),
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE sys_data_mapping_execution_log IS '数据映射执行日志表';
COMMENT ON COLUMN sys_data_mapping_execution_log.id IS '主键';
COMMENT ON COLUMN sys_data_mapping_execution_log.config_id IS '配置ID';
COMMENT ON COLUMN sys_data_mapping_execution_log.config_code IS '配置编码';
COMMENT ON COLUMN sys_data_mapping_execution_log.request_json IS '请求JSON';
COMMENT ON COLUMN sys_data_mapping_execution_log.result_json IS '结果JSON';
COMMENT ON COLUMN sys_data_mapping_execution_log.config_json IS '完整配置JSON';
COMMENT ON COLUMN sys_data_mapping_execution_log.success IS '是否成功';
COMMENT ON COLUMN sys_data_mapping_execution_log.error_info IS '错误信息';
COMMENT ON COLUMN sys_data_mapping_execution_log.execution_time IS '执行耗时(ms)';
COMMENT ON COLUMN sys_data_mapping_execution_log.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_data_mapping_execution_log.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_data_mapping_execution_log.create_time IS '创建时间';
COMMENT ON COLUMN sys_data_mapping_execution_log.update_time IS '更新时间';

-- 索引
CREATE INDEX IF NOT EXISTS idx_config_code ON sys_data_mapping_config(config_code);
CREATE INDEX IF NOT EXISTS idx_config_status ON sys_data_mapping_config(status);
CREATE INDEX IF NOT EXISTS idx_log_config_id ON sys_data_mapping_execution_log(config_id);
CREATE INDEX IF NOT EXISTS idx_log_success ON sys_data_mapping_execution_log(success);
CREATE INDEX IF NOT EXISTS idx_log_create_time ON sys_data_mapping_execution_log(create_time);
