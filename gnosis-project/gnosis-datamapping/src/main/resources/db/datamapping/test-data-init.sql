-- 测试数据初始化脚本

-- 清理现有数据
DELETE FROM sys_data_mapping_execution_log;
DELETE FROM sys_data_mapping_config;

-- 插入测试配置数据 - 场景1：字段直接匹配
INSERT INTO sys_data_mapping_config (
    id, config_name, config_code, system_id, system_name, config_version, 
    api_version, target_endpoint, json_root_path, config_json, description, 
    status, create_user_id, update_user_id, create_time, update_time
) VALUES (
    'test_config_001',
    'ERP合同字段直接映射配置',
    'ERP_DIRECT_001',
    'ERP_SYSTEM',
    'ERP系统',
    '1.0',
    'v1.0',
    '/api/v1/contracts/create',
    '$.contractData',
    '{
  "configMetadata": {
    "systemId": "ERP_CONTRACT",
    "systemName": "ERP合同系统",
    "configVersion": "1.0"
  },
  "apiConfig": {
    "apiVersion": "v1.0",
    "targetEndpoint": "/api/v1/contracts/create",
    "jsonRootPath": "$.contractData"
  },
  "directMappings": [
    {
      "sourceField": "contractNo",
      "targetField": "contractNo",
      "dataType": "string",
      "required": true,
      "validationRules": [
        {"type": "required", "message": "合同编号不能为空"},
        {"type": "maxLength", "value": 50, "message": "合同编号长度不能超过50"},
        {"type": "pattern", "value": "^[A-Z0-9_-]+$", "message": "合同编号只能包含大写字母、数字、下划线和横线"}
      ],
      "transforms": ["trim", "uppercase"]
    },
    {
      "sourceField": "contractAmount",
      "targetField": "amount",
      "dataType": "decimal",
      "required": true,
      "validationRules": [
        {"type": "required", "message": "合同金额不能为空"},
        {"type": "min", "value": 0.01, "message": "合同金额必须大于0"}
      ],
      "transforms": ["round(2)"]
    },
    {
      "sourceField": "contractStatus",
      "targetField": "status",
      "dataType": "string",
      "required": true,
      "validationRules": [
        {"type": "enum", "values": ["DRAFT", "ACTIVE", "COMPLETED", "CANCELLED"]}
      ],
      "transforms": ["statusMapping"]
    }
  ],
  "transformRules": {
    "statusMapping": {
      "type": "lookup",
      "mapping": {"草稿": "DRAFT", "生效": "ACTIVE", "完成": "COMPLETED", "ACTIVE": "ACTIVE"},
      "defaultValue": "DRAFT"
    },
    "trim": {"type": "function", "function": "String.trim()"},
    "uppercase": {"type": "function", "function": "String.toUpperCase()"},
    "round": {"type": "function", "function": "Math.round(value, precision)", "parameters": {"precision": 2}}
  }
}',
    'ERP合同字段直接映射，支持类型转换、校验规则、查找映射',
    1,
    'admin',
    'admin',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 插入测试配置数据 - 场景2：根节点→指定节点
INSERT INTO sys_data_mapping_config (
    id, config_name, config_code, system_id, system_name, config_version, 
    api_version, target_endpoint, json_root_path, config_json, description, 
    status, create_user_id, update_user_id, create_time, update_time
) VALUES (
    'test_config_002',
    '根节点到指定节点映射配置',
    'ERP_CROSS_002',
    'ERP_SYSTEM',
    'ERP系统',
    '1.0',
    'v1.0',
    '/api/v1/contracts/cross',
    '$',
    '{
  "apiConfig": {"jsonRootPath": "$"},
  "crossNodeMappings": [
    {
      "direction": "rootToNode",
      "sourcePath": "$.contractNo",
      "targetPath": "$.basicInfo.contractNo",
      "dataType": "string",
      "transforms": ["trim"]
    },
    {
      "direction": "rootToNode",
      "sourcePath": "$.contractAmount",
      "targetPath": "$.paymentInfo.amount",
      "dataType": "decimal",
      "transforms": ["round(2)"]
    }
  ],
  "transformRules": {
    "trim": {"type": "function", "function": "String.trim()"},
    "round": {"type": "function", "function": "Math.round(value, precision)", "parameters": {"precision": 2}}
  }
}',
    '根节点字段映射到指定节点，支持跨节点数据转换',
    1,
    'admin',
    'admin',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 插入测试配置数据 - 场景3：指定节点→根节点
INSERT INTO sys_data_mapping_config (
    id, config_name, config_code, system_id, system_name, config_version, 
    api_version, target_endpoint, json_root_path, config_json, description, 
    status, create_user_id, update_user_id, create_time, update_time
) VALUES (
    'test_config_003',
    '指定节点到根节点映射配置',
    'ERP_CROSS_003',
    'ERP_SYSTEM',
    'ERP系统',
    '1.0',
    'v1.0',
    '/api/v1/contracts/nodeToRoot',
    '$',
    '{
  "apiConfig": {"jsonRootPath": "$"},
  "crossNodeMappings": [
    {
      "direction": "nodeToRoot",
      "sourcePath": "$.basicInfo.contractName",
      "targetPath": "$.contractName",
      "dataType": "string"
    },
    {
      "direction": "nodeToRoot",
      "sourcePath": "$.paymentInfo.totalAmount",
      "targetPath": "$.totalAmount",
      "dataType": "decimal",
      "transforms": ["round(2)"]
    }
  ],
  "transformRules": {
    "round": {"type": "function", "function": "Math.round(value, precision)", "parameters": {"precision": 2}}
  }
}',
    '从深层节点提取数据到根节点',
    1,
    'admin',
    'admin',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 插入测试配置数据 - 场景4：List数据提取
INSERT INTO sys_data_mapping_config (
    id, config_name, config_code, system_id, system_name, config_version, 
    api_version, target_endpoint, json_root_path, config_json, description, 
    status, create_user_id, update_user_id, create_time, update_time
) VALUES (
    'test_config_004',
    'List数据提取转换配置',
    'ERP_LIST_004',
    'ERP_SYSTEM',
    'ERP系统',
    '1.0',
    'v1.0',
    '/api/v1/contracts/list',
    '$.contractData',
    '{
  "apiConfig": {"jsonRootPath": "$.contractData"},
  "listExtractionMappings": [
    {
      "type": "extractByIndex",
      "sourcePath": "$.signatories[*]",
      "targetPath": "$.primaryParty",
      "index": 0,
      "itemMappings": [
        {"source": "$.partyName", "target": "$.partyName", "dataType": "string"},
        {"source": "$.partyType", "target": "$.partyType", "dataType": "string", "transforms": ["partyTypeMapping"]}
      ]
    },
    {
      "type": "concatenateValues",
      "sourcePath": "$.signatories[*].partyName",
      "targetPath": "$.allPartyNames",
      "separator": "、"
    }
  ],
  "transformRules": {
    "partyTypeMapping": {
      "type": "lookup",
      "mapping": {"甲方": "PRINCIPAL", "乙方": "VENDOR"},
      "defaultValue": "OTHER"
    }
  }
}',
    '数组提取、按索引提取、值拼接等功能',
    1,
    'admin',
    'admin',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 插入测试配置数据 - 场景5：String转List
INSERT INTO sys_data_mapping_config (
    id, config_name, config_code, system_id, system_name, config_version, 
    api_version, target_endpoint, json_root_path, config_json, description, 
    status, create_user_id, update_user_id, create_time, update_time
) VALUES (
    'test_config_005',
    'String转List链式规则配置',
    'ERP_STRING_005',
    'ERP_SYSTEM',
    'ERP系统',
    '1.0',
    'v1.0',
    '/api/v1/contracts/string',
    '$.contractData',
    '{
  "apiConfig": {"jsonRootPath": "$.contractData"},
  "stringToListMappings": [
    {
      "type": "stringSplitToList",
      "sourcePath": "$.partyNames",
      "targetPath": "$.parties",
      "separator": ",",
      "trimItems": true,
      "removeEmpty": true,
      "chainedMappings": [
        {
          "type": "enrichListItems",
          "enrichmentSource": "partyInfoMapping",
          "keyField": "itemValue",
          "targetFields": ["partyType", "creditCode"]
        }
      ]
    }
  ],
  "enrichmentMappings": {
    "partyInfoMapping": {
      "type": "lookup",
      "dataSource": "partyInfo",
      "keyField": "partyName",
      "mappingFields": {"partyType": "partyType", "creditCode": "creditCode"},
      "defaultValues": {"partyType": "OTHER", "creditCode": ""}
    }
  },
  "dataSources": {
    "partyInfo": {
      "type": "static",
      "staticData": {
        "XX科技": {"partyType": "PRINCIPAL", "creditCode": "91110108MA1234567X"},
        "YY供应商": {"partyType": "VENDOR", "creditCode": "91110108MA7654321Y"}
      }
    }
  }
}',
    '字符串拆分、链式规则、数据增强',
    1,
    'admin',
    'admin',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- 插入测试执行日志
INSERT INTO sys_data_mapping_execution_log (
    id, config_id, config_code, request_json, result_json, config_json,
    success, error_info, execution_time, create_user_id, update_user_id, 
    create_time, update_time
) VALUES (
    'test_log_001',
    'test_config_001',
    'ERP_DIRECT_001',
    '{"contractData": {"contractNo": "HT2024001", "contractAmount": 1500000.567, "contractStatus": "生效"}}',
    '{"success": true, "resultData": {"contractNo": "HT2024001", "amount": 1500000.57, "status": "ACTIVE"}}',
    (SELECT config_json FROM sys_data_mapping_config WHERE id = 'test_config_001'),
    true,
    NULL,
    25,
    'system',
    'system',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

INSERT INTO sys_data_mapping_execution_log (
    id, config_id, config_code, request_json, result_json, config_json,
    success, error_info, execution_time, create_user_id, update_user_id, 
    create_time, update_time
) VALUES (
    'test_log_002',
    'test_config_001',
    'ERP_DIRECT_001',
    '{"contractData": {"contractNo": "invalid-no", "contractAmount": -100}}',
    '{"success": false, "errors": [], "validationErrors": [{"field": "contractNo", "rule": "pattern", "message": "合同编号只能包含大写字母、数字、下划线和横线"}]}',
    (SELECT config_json FROM sys_data_mapping_config WHERE id = 'test_config_001'),
    false,
    '[{"field": "contractNo", "rule": "pattern", "message": "合同编号格式错误"}]',
    18,
    'system',
    'system',
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);
