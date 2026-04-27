INSERT INTO sys_data_mapping_config
(id, config_name, config_code, system_id, system_name, config_version, api_version, target_endpoint, json_root_path, config_json, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('test_config_001', 'ERP合同字段直接映射配置', 'ERP_DIRECT_001', 'ERP_SYSTEM', 'ERP系统', '1.0', 'v1.0', '/api/v1/contracts/create', '$.contractData', '{
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
}', 'ERP合同字段直接映射，支持类型转换、校验规则、查找映射', 1, 'admin', 'admin', '2026-04-27 14:26:36.745', '2026-04-27 14:26:36.745');
INSERT INTO sys_data_mapping_config
(id, config_name, config_code, system_id, system_name, config_version, api_version, target_endpoint, json_root_path, config_json, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('test_config_002', '根节点到指定节点映射配置', 'ERP_CROSS_002', 'ERP_SYSTEM', 'ERP系统', '1.0', 'v1.0', '/api/v1/contracts/cross', '$', '{
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
}', '根节点字段映射到指定节点，支持跨节点数据转换', 1, 'admin', 'admin', '2026-04-27 14:26:36.749', '2026-04-27 14:26:36.749');
INSERT INTO sys_data_mapping_config
(id, config_name, config_code, system_id, system_name, config_version, api_version, target_endpoint, json_root_path, config_json, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('test_config_003', '指定节点到根节点映射配置', 'ERP_CROSS_003', 'ERP_SYSTEM', 'ERP系统', '1.0', 'v1.0', '/api/v1/contracts/nodeToRoot', '$', '{
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
}', '从深层节点提取数据到根节点', 1, 'admin', 'admin', '2026-04-27 14:26:36.753', '2026-04-27 14:26:36.753');
INSERT INTO sys_data_mapping_config
(id, config_name, config_code, system_id, system_name, config_version, api_version, target_endpoint, json_root_path, config_json, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('test_config_004', 'List数据提取转换配置', 'ERP_LIST_004', 'ERP_SYSTEM', 'ERP系统', '1.0', 'v1.0', '/api/v1/contracts/list', '$.contractData', '{
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
}', '数组提取、按索引提取、值拼接等功能', 1, 'admin', 'admin', '2026-04-27 14:26:36.756', '2026-04-27 14:26:36.756');
INSERT INTO sys_data_mapping_config
(id, config_name, config_code, system_id, system_name, config_version, api_version, target_endpoint, json_root_path, config_json, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('test_config_005', 'String转List链式规则配置', 'ERP_STRING_005', 'ERP_SYSTEM', 'ERP系统', '1.0', 'v1.0', '/api/v1/contracts/string', '$.contractData', '{
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
}', '字符串拆分、链式规则、数据增强', 1, 'admin', 'admin', '2026-04-27 14:26:36.759', '2026-04-27 14:26:36.759');


INSERT INTO sys_data_mapping_test_case
(id, config_id, config_code, case_name, case_code, request_json, expected_result_json, actual_result_json, is_passed, diff_info, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('tc_002', 'test_config_001', 'ERP_DIRECT_001', '中文状态映射测试', 'TC_DIRECT_002', '{"contractData": {"contractNo": "  ht2024002  ", "contractAmount": 2000000, "contractStatus": "生效"}}', '{"contractNo": "HT2024002", "amount": 2000000.00, "status": "ACTIVE"}', '{"amount":2000000.00,"contractNo":"HT2024002","status":"ACTIVE"}', 1, NULL, '验证中文生效映射为ACTIVE，合同号trim+uppercase转换', 1, 'admin', 'admin', '2026-04-27 14:26:36.778', '2026-04-27 14:56:13.605');
INSERT INTO sys_data_mapping_test_case
(id, config_id, config_code, case_name, case_code, request_json, expected_result_json, actual_result_json, is_passed, diff_info, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('tc_001', 'test_config_001', 'ERP_DIRECT_001', '正常合同数据映射-英文状态', 'TC_DIRECT_001', '{"contractData": {"contractNo": "HT2024001", "contractAmount": 1500000.567, "contractStatus": "ACTIVE"}}', '{"contractNo": "HT2024001", "amount": 1500000.57, "status": "ACTIVE"}', '{"amount":1500000.57,"contractNo":"HT2024001","status":"ACTIVE"}', 1, NULL, '验证正常合同数据的字段映射和类型转换，英文状态直接保留', 1, 'admin', 'admin', '2026-04-27 14:26:36.775', '2026-04-27 14:56:13.755');
INSERT INTO sys_data_mapping_test_case
(id, config_id, config_code, case_name, case_code, request_json, expected_result_json, actual_result_json, is_passed, diff_info, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('tc_008', 'test_config_005', 'ERP_STRING_005', '字符串拆分和数据增强', 'TC_STRING_001', '{"contractData": {"partyNames": "XX科技,YY供应商"}}', '{"parties": [{"partyName": "XX科技", "partyType": "PRINCIPAL", "creditCode": "91110108MA1234567X"}, {"partyName": "YY供应商", "partyType": "VENDOR", "creditCode": "91110108MA7654321Y"}]}', '{"parties":[{"creditCode":"91110108MA1234567X","partyName":"XX科技","partyType":"PRINCIPAL"},{"creditCode":"91110108MA7654321Y","partyName":"YY供应商","partyType":"VENDOR"}]}', 1, NULL, '验证字符串按逗号拆分，并通过静态数据源增强', 1, 'admin', 'admin', '2026-04-27 14:26:36.792', '2026-04-27 14:56:12.641');
INSERT INTO sys_data_mapping_test_case
(id, config_id, config_code, case_name, case_code, request_json, expected_result_json, actual_result_json, is_passed, diff_info, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('tc_007', 'test_config_004', 'ERP_LIST_004', '数组提取和拼接', 'TC_LIST_001', '{"contractData": {"signatories": [{"partyName": "XX科技", "partyType": "甲方"}, {"partyName": "YY供应商", "partyType": "乙方"}]}}', '{"primaryParty": {"partyName": "XX科技", "partyType": "PRINCIPAL"}, "allPartyNames": "XX科技、YY供应商"}', '{"allPartyNames":"XX科技、YY供应商","primaryParty":{"partyName":"XX科技","partyType":"PRINCIPAL"}}', 1, NULL, '验证按索引提取第一个签署方，并拼接所有签署方名称', 1, 'admin', 'admin', '2026-04-27 14:26:36.790', '2026-04-27 14:56:12.784');
INSERT INTO sys_data_mapping_test_case
(id, config_id, config_code, case_name, case_code, request_json, expected_result_json, actual_result_json, is_passed, diff_info, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('tc_005', 'test_config_003', 'ERP_CROSS_003', '多层级节点提取-四舍五入', 'TC_CROSS_003', '{"basicInfo": {"contractName": "多层级测试"}, "paymentInfo": {"totalAmount": 9999.999}}', '{"contractName": "多层级测试", "totalAmount": 10000.00}', '{"totalAmount":10000.00,"contractName":"多层级测试"}', 1, NULL, '验证多层级节点的数据提取和四舍五入进位', 1, 'admin', 'admin', '2026-04-27 14:26:36.789', '2026-04-27 14:56:12.939');
INSERT INTO sys_data_mapping_test_case
(id, config_id, config_code, case_name, case_code, request_json, expected_result_json, actual_result_json, is_passed, diff_info, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('tc_004', 'test_config_003', 'ERP_CROSS_003', '子节点到根节点映射', 'TC_CROSS_002', '{"basicInfo": {"contractName": "测试合同"}, "paymentInfo": {"totalAmount": 5000.123}}', '{"contractName": "测试合同", "totalAmount": 5000.12}', '{"totalAmount":5000.12,"contractName":"测试合同"}', 1, NULL, '验证子节点数据提取到根节点', 1, 'admin', 'admin', '2026-04-27 14:26:36.788', '2026-04-27 14:56:13.076');
INSERT INTO sys_data_mapping_test_case
(id, config_id, config_code, case_name, case_code, request_json, expected_result_json, actual_result_json, is_passed, diff_info, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('tc_003', 'test_config_002', 'ERP_CROSS_002', '根节点到子节点映射', 'TC_CROSS_001', '{"contractNo": "  HT2024003  ", "contractAmount": 3000000.125}', '{"basicInfo": {"contractNo": "HT2024003"}, "paymentInfo": {"amount": 3000000.13}}', '{"paymentInfo":{"amount":3000000.13},"basicInfo":{"contractNo":"HT2024003"}}', 1, NULL, '验证根节点字段映射到子节点，支持trim和四舍五入', 1, 'admin', 'admin', '2026-04-27 14:26:36.786', '2026-04-27 14:56:13.221');
INSERT INTO sys_data_mapping_test_case
(id, config_id, config_code, case_name, case_code, request_json, expected_result_json, actual_result_json, is_passed, diff_info, description, status, create_user_id, update_user_id, create_time, update_time)
VALUES('tc_006', 'test_config_001', 'ERP_DIRECT_001', '完成状态映射测试', 'TC_DIRECT_003', '{"contractData": {"contractNo": "HT2024003", "contractAmount": 500000.125, "contractStatus": "完成"}}', '{"contractNo": "HT2024003", "amount": 500000.13, "status": "COMPLETED"}', '{"amount":500000.13,"contractNo":"HT2024003","status":"COMPLETED"}', 1, NULL, '验证中文完成映射为COMPLETED', 1, 'admin', 'admin', '2026-04-27 14:26:36.783', '2026-04-27 14:56:13.441');