-- public.sys_data_mapping_config definition

-- Drop table

-- DROP TABLE sys_data_mapping_config;

CREATE TABLE sys_data_mapping_config (
	id varchar(128) NOT NULL, -- 主键
	config_name varchar(256) NOT NULL, -- 配置名称
	config_code varchar(128) NOT NULL, -- 配置编码
	system_id varchar(128) NULL, -- 系统ID
	system_name varchar(256) NULL, -- 系统名称
	config_version varchar(64) NULL, -- 配置版本
	api_version varchar(64) NULL, -- API版本
	target_endpoint varchar(512) NULL, -- 目标接口地址
	json_root_path varchar(512) NULL, -- JSON根路径
	config_json text NULL, -- 完整配置JSON
	description varchar(1024) NULL, -- 描述
	status int4 DEFAULT 1 NULL, -- 状态（0=禁用，1=启用）
	create_user_id varchar(128) NULL, -- 创建人ID
	update_user_id varchar(128) NULL, -- 更新人ID
	create_time timestamp DEFAULT pg_systimestamp() NULL, -- 创建时间
	update_time timestamp DEFAULT pg_systimestamp() NULL, -- 更新时间
	CONSTRAINT sys_data_mapping_config_config_code_key UNIQUE (config_code),
	CONSTRAINT sys_data_mapping_config_pkey PRIMARY KEY (id)
)
WITH (
	orientation=row,
	compression=no
);
CREATE INDEX idx_config_code ON sys_data_mapping_config USING btree (config_code) TABLESPACE pg_default;
CREATE INDEX idx_config_status ON sys_data_mapping_config USING btree (status) TABLESPACE pg_default;
COMMENT ON TABLE public.sys_data_mapping_config IS '数据映射配置表';

-- Column comments

COMMENT ON COLUMN public.sys_data_mapping_config.id IS '主键';
COMMENT ON COLUMN public.sys_data_mapping_config.config_name IS '配置名称';
COMMENT ON COLUMN public.sys_data_mapping_config.config_code IS '配置编码';
COMMENT ON COLUMN public.sys_data_mapping_config.system_id IS '系统ID';
COMMENT ON COLUMN public.sys_data_mapping_config.system_name IS '系统名称';
COMMENT ON COLUMN public.sys_data_mapping_config.config_version IS '配置版本';
COMMENT ON COLUMN public.sys_data_mapping_config.api_version IS 'API版本';
COMMENT ON COLUMN public.sys_data_mapping_config.target_endpoint IS '目标接口地址';
COMMENT ON COLUMN public.sys_data_mapping_config.json_root_path IS 'JSON根路径';
COMMENT ON COLUMN public.sys_data_mapping_config.config_json IS '完整配置JSON';
COMMENT ON COLUMN public.sys_data_mapping_config.description IS '描述';
COMMENT ON COLUMN public.sys_data_mapping_config.status IS '状态（0=禁用，1=启用）';
COMMENT ON COLUMN public.sys_data_mapping_config.create_user_id IS '创建人ID';
COMMENT ON COLUMN public.sys_data_mapping_config.update_user_id IS '更新人ID';
COMMENT ON COLUMN public.sys_data_mapping_config.create_time IS '创建时间';
COMMENT ON COLUMN public.sys_data_mapping_config.update_time IS '更新时间';


-- public.sys_data_mapping_execution_log definition

-- Drop table

-- DROP TABLE sys_data_mapping_execution_log;

CREATE TABLE sys_data_mapping_execution_log (
	id varchar(128) NOT NULL, -- 主键
	config_id varchar(128) NULL, -- 配置ID
	config_code varchar(128) NULL, -- 配置编码
	request_json text NULL, -- 请求JSON
	result_json text NULL, -- 结果JSON
	config_json text NULL, -- 完整配置JSON
	success bool DEFAULT false NULL, -- 是否成功
	error_info text NULL, -- 错误信息
	execution_time int4 NULL, -- 执行耗时(ms)
	create_user_id varchar(128) NULL, -- 创建人ID
	update_user_id varchar(128) NULL, -- 更新人ID
	create_time timestamp DEFAULT pg_systimestamp() NULL, -- 创建时间
	update_time timestamp DEFAULT pg_systimestamp() NULL, -- 更新时间
	CONSTRAINT sys_data_mapping_execution_log_pkey PRIMARY KEY (id)
)
WITH (
	orientation=row,
	compression=no
);
CREATE INDEX idx_log_config_id ON sys_data_mapping_execution_log USING btree (config_id) TABLESPACE pg_default;
CREATE INDEX idx_log_create_time ON sys_data_mapping_execution_log USING btree (create_time) TABLESPACE pg_default;
CREATE INDEX idx_log_success ON sys_data_mapping_execution_log USING btree (success) TABLESPACE pg_default;
COMMENT ON TABLE public.sys_data_mapping_execution_log IS '数据映射执行日志表';

-- Column comments

COMMENT ON COLUMN public.sys_data_mapping_execution_log.id IS '主键';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.config_id IS '配置ID';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.config_code IS '配置编码';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.request_json IS '请求JSON';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.result_json IS '结果JSON';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.config_json IS '完整配置JSON';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.success IS '是否成功';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.error_info IS '错误信息';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.execution_time IS '执行耗时(ms)';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.create_user_id IS '创建人ID';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.update_user_id IS '更新人ID';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.create_time IS '创建时间';
COMMENT ON COLUMN public.sys_data_mapping_execution_log.update_time IS '更新时间';


-- public.sys_data_mapping_test_case definition

-- Drop table

-- DROP TABLE sys_data_mapping_test_case;

CREATE TABLE sys_data_mapping_test_case (
	id varchar(64) NOT NULL, -- 主键ID
	config_id varchar(64) NOT NULL, -- 关联配置ID
	config_code varchar(100) NULL, -- 关联配置编码
	case_name varchar(200) NOT NULL, -- 用例名称
	case_code varchar(100) NOT NULL, -- 用例编码
	request_json text NOT NULL, -- 测试请求JSON
	expected_result_json text NOT NULL, -- 期望结果JSON
	actual_result_json text NULL, -- 实际结果JSON
	is_passed int4 DEFAULT 0 NULL, -- 是否通过(0失败 1通过)
	diff_info text NULL, -- 差异信息
	description text NULL, -- 用例描述
	status int4 DEFAULT 1 NULL, -- 状态(0禁用 1启用)
	create_user_id varchar(128) NULL, -- 创建人ID
	update_user_id varchar(128) NULL, -- 更新人ID
	create_time timestamp NULL, -- 创建时间
	update_time timestamp NULL, -- 更新时间
	CONSTRAINT sys_data_mapping_test_case_pkey PRIMARY KEY (id)
)
WITH (
	orientation=row,
	compression=no
);
CREATE INDEX idx_tc_case_code ON sys_data_mapping_test_case USING btree (case_code) TABLESPACE pg_default;
CREATE INDEX idx_tc_config_code ON sys_data_mapping_test_case USING btree (config_code) TABLESPACE pg_default;
CREATE INDEX idx_tc_config_id ON sys_data_mapping_test_case USING btree (config_id) TABLESPACE pg_default;
CREATE INDEX idx_tc_is_passed ON sys_data_mapping_test_case USING btree (is_passed) TABLESPACE pg_default;
CREATE INDEX idx_tc_status ON sys_data_mapping_test_case USING btree (status) TABLESPACE pg_default;
COMMENT ON TABLE public.sys_data_mapping_test_case IS '数据映射测试用例表';

-- Column comments

COMMENT ON COLUMN public.sys_data_mapping_test_case.id IS '主键ID';
COMMENT ON COLUMN public.sys_data_mapping_test_case.config_id IS '关联配置ID';
COMMENT ON COLUMN public.sys_data_mapping_test_case.config_code IS '关联配置编码';
COMMENT ON COLUMN public.sys_data_mapping_test_case.case_name IS '用例名称';
COMMENT ON COLUMN public.sys_data_mapping_test_case.case_code IS '用例编码';
COMMENT ON COLUMN public.sys_data_mapping_test_case.request_json IS '测试请求JSON';
COMMENT ON COLUMN public.sys_data_mapping_test_case.expected_result_json IS '期望结果JSON';
COMMENT ON COLUMN public.sys_data_mapping_test_case.actual_result_json IS '实际结果JSON';
COMMENT ON COLUMN public.sys_data_mapping_test_case.is_passed IS '是否通过(0失败 1通过)';
COMMENT ON COLUMN public.sys_data_mapping_test_case.diff_info IS '差异信息';
COMMENT ON COLUMN public.sys_data_mapping_test_case.description IS '用例描述';
COMMENT ON COLUMN public.sys_data_mapping_test_case.status IS '状态(0禁用 1启用)';
COMMENT ON COLUMN public.sys_data_mapping_test_case.create_user_id IS '创建人ID';
COMMENT ON COLUMN public.sys_data_mapping_test_case.update_user_id IS '更新人ID';
COMMENT ON COLUMN public.sys_data_mapping_test_case.create_time IS '创建时间';
COMMENT ON COLUMN public.sys_data_mapping_test_case.update_time IS '更新时间';

