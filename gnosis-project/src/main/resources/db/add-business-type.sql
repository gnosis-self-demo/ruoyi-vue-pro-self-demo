-- 添加 business_type 字段到 sys_validation_flows 表
ALTER TABLE gnosis_sample.sys_validation_flows ADD COLUMN business_type VARCHAR(255);

-- 添加字段注释
COMMENT ON COLUMN gnosis_sample.sys_validation_flows.business_type IS '业务类型（多个，逗号分隔）';

-- 更新现有数据的 business_type 字段
UPDATE gnosis_sample.sys_validation_flows SET business_type = 'ORDER_CREATE' WHERE flow_id = 'ORDER_CREATE_FLOW';
UPDATE gnosis_sample.sys_validation_flows SET business_type = 'USER_REGISTER' WHERE flow_id = 'USER_REGISTER_FLOW';
UPDATE gnosis_sample.sys_validation_flows SET business_type = 'BASIC_DATA' WHERE flow_id = 'SIMPLE_CHECK_FLOW';

COMMIT;