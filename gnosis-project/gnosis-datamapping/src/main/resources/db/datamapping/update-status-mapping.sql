-- Update statusMapping to include ACTIVE -> ACTIVE mapping
UPDATE sys_data_mapping_config 
SET config_json = REPLACE(config_json, '"mapping":{"草稿":"DRAFT","生效":"ACTIVE","完成":"COMPLETED"}', '"mapping":{"草稿":"DRAFT","生效":"ACTIVE","完成":"COMPLETED","ACTIVE":"ACTIVE"}')
WHERE id = 'test_config_001';
