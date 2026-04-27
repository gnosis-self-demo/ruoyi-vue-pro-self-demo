-- Update ERP_DIRECT_001 config to include ACTIVE -> ACTIVE mapping
-- This ensures English status "ACTIVE" is preserved instead of falling back to "DRAFT"

UPDATE sys_data_mapping_config 
SET config_json = REPLACE(
    REPLACE(
        config_json, 
        '"mapping":{"稿":"DRAFT","生效":"ACTIVE","完成":"COMPLETED"}',
        '"mapping":{"草稿":"DRAFT","生效":"ACTIVE","完成":"COMPLETED","ACTIVE":"ACTIVE"}'
    ),
    '"mapping":{"绋":"DRAFT","鐢熸晥":"ACTIVE","瀹屾垚":"COMPLETED"}',
    '"mapping":{"草稿":"DRAFT","生效":"ACTIVE","完成":"COMPLETED","ACTIVE":"ACTIVE"}'
)
WHERE id = 'test_config_001';
