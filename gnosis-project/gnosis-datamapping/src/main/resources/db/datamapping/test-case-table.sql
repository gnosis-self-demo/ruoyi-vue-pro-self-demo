DROP TABLE IF EXISTS sys_data_mapping_test_case;

CREATE TABLE sys_data_mapping_test_case (
    id VARCHAR(64) NOT NULL PRIMARY KEY,
    config_id VARCHAR(64) NOT NULL,
    config_code VARCHAR(100),
    case_name VARCHAR(200) NOT NULL,
    case_code VARCHAR(100) NOT NULL,
    request_json TEXT NOT NULL,
    expected_result_json TEXT NOT NULL,
    actual_result_json TEXT,
    is_passed INTEGER DEFAULT 0,
    diff_info TEXT,
    description TEXT,
    status INTEGER DEFAULT 1,
    create_user_id VARCHAR(128),
    update_user_id VARCHAR(128),
    create_time TIMESTAMP,
    update_time TIMESTAMP
);

CREATE INDEX idx_tc_config_id ON sys_data_mapping_test_case(config_id);
CREATE INDEX idx_tc_config_code ON sys_data_mapping_test_case(config_code);
CREATE INDEX idx_tc_case_code ON sys_data_mapping_test_case(case_code);
CREATE INDEX idx_tc_status ON sys_data_mapping_test_case(status);
CREATE INDEX idx_tc_is_passed ON sys_data_mapping_test_case(is_passed);

COMMENT ON TABLE sys_data_mapping_test_case IS '数据映射测试用例表';
COMMENT ON COLUMN sys_data_mapping_test_case.id IS '主键ID';
COMMENT ON COLUMN sys_data_mapping_test_case.config_id IS '关联配置ID';
COMMENT ON COLUMN sys_data_mapping_test_case.config_code IS '关联配置编码';
COMMENT ON COLUMN sys_data_mapping_test_case.case_name IS '用例名称';
COMMENT ON COLUMN sys_data_mapping_test_case.case_code IS '用例编码';
COMMENT ON COLUMN sys_data_mapping_test_case.request_json IS '测试请求JSON';
COMMENT ON COLUMN sys_data_mapping_test_case.expected_result_json IS '期望结果JSON';
COMMENT ON COLUMN sys_data_mapping_test_case.actual_result_json IS '实际结果JSON';
COMMENT ON COLUMN sys_data_mapping_test_case.is_passed IS '是否通过(0失败 1通过)';
COMMENT ON COLUMN sys_data_mapping_test_case.diff_info IS '差异信息';
COMMENT ON COLUMN sys_data_mapping_test_case.description IS '用例描述';
COMMENT ON COLUMN sys_data_mapping_test_case.status IS '状态(0禁用 1启用)';
COMMENT ON COLUMN sys_data_mapping_test_case.create_user_id IS '创建人ID';
COMMENT ON COLUMN sys_data_mapping_test_case.update_user_id IS '更新人ID';
COMMENT ON COLUMN sys_data_mapping_test_case.create_time IS '创建时间';
COMMENT ON COLUMN sys_data_mapping_test_case.update_time IS '更新时间';
