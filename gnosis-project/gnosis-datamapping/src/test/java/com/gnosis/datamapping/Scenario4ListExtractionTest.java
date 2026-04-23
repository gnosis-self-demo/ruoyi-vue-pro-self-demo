package com.gnosis.datamapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.datamapping.engine.ErrorDetail;
import com.gnosis.datamapping.engine.MappingEngine;

import java.util.List;
import java.util.Map;

/**
 * 场景4：List数据提取转换
 */
public class Scenario4ListExtractionTest {

    private static MappingEngine engine = new MappingEngine();

    public static void main(String[] args) {

        System.out.println("=== 场景4：List数据提取转换 ===");

        String configJson = buildConfigJson();
        String requestJson = "{\n" +
                "  \"contractData\": {\n" +
                "    \"contractNo\": \"HT2024001\",\n" +
                "    \"signatories\": [\n" +
                "      {\n" +
                "        \"partyName\": \"XX科技有限公司\",\n" +
                "        \"partyType\": \"甲方\",\n" +
                "        \"contactPerson\": \"张三\",\n" +
                "        \"contactPhone\": \"13800138000\",\n" +
                "        \"creditCode\": \"91110108MA1234567X\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"partyName\": \"YY供应商有限公司\",\n" +
                "        \"partyType\": \"乙方\",\n" +
                "        \"contactPerson\": \"李四\",\n" +
                "        \"contactPhone\": \"13900139000\",\n" +
                "        \"creditCode\": \"91110108MA7654321Y\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"partyName\": \"ZZ物流公司\",\n" +
                "        \"partyType\": \"丙方\",\n" +
                "        \"contactPerson\": \"王五\",\n" +
                "        \"contactPhone\": \"13700137000\",\n" +
                "        \"creditCode\": \"91110108MA8765432Z\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"contacts\": [\n" +
                "      {\"name\": \"张三\", \"phone\": \"13800138000\", \"department\": \"采购部\", \"isActive\": true},\n" +
                "      {\"name\": \"李四\", \"phone\": \"13900139000\", \"department\": \"财务部\", \"isActive\": false},\n" +
                "      {\"name\": \"王五\", \"phone\": \"13700137000\", \"department\": \"法务部\", \"isActive\": true},\n" +
                "      {\"name\": \"赵六\", \"phone\": \"13600136000\", \"department\": \"技术部\", \"isActive\": true}\n" +
                "    ]\n" +
                "  }\n" +
                "}";
        executeAndPrint("List提取转换", configJson, requestJson);

        System.out.println("\n--- 测试4.2：取第n条数据 ---");
        String config2 = buildExtractByIndexConfig(1);
        executeAndPrint("取第2条签约方", config2, requestJson);

        System.out.println("\n--- 测试4.3：按条件筛选 ---");
        String config3 = buildExtractByConditionConfig();
        executeAndPrint("筛选甲方签约方", config3, requestJson);
    }

    private static String buildConfigJson() {
        JSONObject config = new JSONObject();
        JSONObject metadata = new JSONObject();
        metadata.put("systemId", "ERP_CONTRACT");
        metadata.put("systemName", "ERP合同系统");
        metadata.put("configVersion", "1.0");
        config.put("configMetadata", metadata);

        JSONObject apiConfig = new JSONObject();
        apiConfig.put("apiVersion", "v1.0");
        apiConfig.put("targetEndpoint", "/api/v1/contracts/create");
        apiConfig.put("jsonRootPath", "$.contractData");
        config.put("apiConfig", apiConfig);

        JSONArray listExtractionMappings = new JSONArray();

        JSONObject mapping1 = new JSONObject();
        mapping1.put("type", "extractByIndex");
        mapping1.put("sourcePath", "$.signatories[*]");
        mapping1.put("targetPath", "$.primaryParty");
        mapping1.put("index", 0);

        JSONArray itemMappings1 = new JSONArray();
        JSONObject item1 = new JSONObject();
        item1.put("source", "$.partyName");
        item1.put("target", "$.partyName");
        item1.put("dataType", "string");
        item1.put("transforms", new JSONArray().fluentAdd("trim"));
        itemMappings1.add(item1);

        JSONObject item2 = new JSONObject();
        item2.put("source", "$.partyType");
        item2.put("target", "$.partyType");
        item2.put("dataType", "string");
        item2.put("transforms", new JSONArray().fluentAdd("partyTypeMapping"));
        itemMappings1.add(item2);

        JSONObject item3 = new JSONObject();
        item3.put("source", "$.contactPerson");
        item3.put("target", "$.contactPerson");
        item3.put("dataType", "string");
        item3.put("transforms", new JSONArray().fluentAdd("trim"));
        itemMappings1.add(item3);

        JSONObject item4 = new JSONObject();
        item4.put("source", "$.contactPhone");
        item4.put("target", "$.contactPhone");
        item4.put("dataType", "string");
        item4.put("transforms", new JSONArray().fluentAdd("phoneFormat"));
        itemMappings1.add(item4);

        mapping1.put("itemMappings", itemMappings1);
        mapping1.put("description", "取签约方列表第一条作为主签约方");
        listExtractionMappings.add(mapping1);

        JSONObject mapping2 = new JSONObject();
        mapping2.put("type", "extractByCondition");
        mapping2.put("sourcePath", "$.signatories[*]");
        mapping2.put("targetPath", "$.principalParty");
        mapping2.put("condition", "$.partyType == '甲方'");
        mapping2.put("takeFirst", true);

        JSONArray itemMappings2 = new JSONArray();
        JSONObject item5 = new JSONObject();
        item5.put("source", "$.partyName");
        item5.put("target", "$.partyName");
        item5.put("dataType", "string");
        itemMappings2.add(item5);

        JSONObject item6 = new JSONObject();
        item6.put("source", "$.creditCode");
        item6.put("target", "$.creditCode");
        item6.put("dataType", "string");
        itemMappings2.add(item6);

        mapping2.put("itemMappings", itemMappings2);
        mapping2.put("description", "筛选出甲方作为主签约方");
        listExtractionMappings.add(mapping2);

        JSONObject mapping3 = new JSONObject();
        mapping3.put("type", "concatenateValues");
        mapping3.put("sourcePath", "$.signatories[*].partyName");
        mapping3.put("targetPath", "$.allPartyNames");
        mapping3.put("separator", "、");
        mapping3.put("description", "所有签约方名称（顿号分隔）");
        listExtractionMappings.add(mapping3);

        JSONObject mapping4 = new JSONObject();
        mapping4.put("type", "concatenateWithFormat");
        mapping4.put("sourcePath", "$.signatories[*]");
        mapping4.put("targetPath", "$.partyInfoSummary");
        mapping4.put("separator", "; ");
        mapping4.put("formatTemplate", "{partyName}({partyType})");
        mapping4.put("description", "签约方信息摘要");
        listExtractionMappings.add(mapping4);

        JSONObject mapping5 = new JSONObject();
        mapping5.put("type", "filterAndTransform");
        mapping5.put("sourcePath", "$.contacts[*]");
        mapping5.put("targetPath", "$.activeContacts");
        mapping5.put("filterCondition", "$.isActive == true");
        mapping5.put("maxItems", 5);

        JSONArray itemMappings5 = new JSONArray();
        JSONObject item7 = new JSONObject();
        item7.put("source", "$.name");
        item7.put("target", "$.name");
        item7.put("dataType", "string");
        itemMappings5.add(item7);

        JSONObject item8 = new JSONObject();
        item8.put("source", "$.phone");
        item8.put("target", "$.phone");
        item8.put("dataType", "string");
        item8.put("transforms", new JSONArray().fluentAdd("phoneFormat"));
        itemMappings5.add(item8);

        JSONObject item9 = new JSONObject();
        item9.put("source", "$.department");
        item9.put("target", "$.department");
        item9.put("dataType", "string");
        itemMappings5.add(item9);

        mapping5.put("itemMappings", itemMappings5);
        mapping5.put("description", "筛选出激活的联系人（最多5个）");
        listExtractionMappings.add(mapping5);

        config.put("listExtractionMappings", listExtractionMappings);

        JSONObject transformRules = new JSONObject();

        JSONObject partyTypeMapping = new JSONObject();
        partyTypeMapping.put("type", "lookup");
        partyTypeMapping.put("mapping", new JSONObject().fluentPut("甲方", "PRINCIPAL").fluentPut("乙方", "VENDOR").fluentPut("丙方", "THIRD_PARTY"));
        partyTypeMapping.put("defaultValue", "OTHER");
        partyTypeMapping.put("ignoreCase", true);
        transformRules.put("partyTypeMapping", partyTypeMapping);

        JSONObject phoneFormat = new JSONObject();
        phoneFormat.put("type", "regex");
        phoneFormat.put("pattern", "(\\d{3})\\d{4}(\\d{4})");
        phoneFormat.put("replacement", "$1****$2");
        transformRules.put("phoneFormat", phoneFormat);

        JSONObject trim = new JSONObject();
        trim.put("type", "function");
        trim.put("function", "String.trim()");
        transformRules.put("trim", trim);

        config.put("transformRules", transformRules);

        return config.toJSONString();
    }

    private static String buildExtractByIndexConfig(int index) {
        JSONObject config = new JSONObject();
        JSONObject apiConfig = new JSONObject();
        apiConfig.put("jsonRootPath", "$.contractData");
        config.put("apiConfig", apiConfig);

        JSONArray listExtractionMappings = new JSONArray();
        JSONObject mapping = new JSONObject();
        mapping.put("type", "extractByIndex");
        mapping.put("sourcePath", "$.signatories[*]");
        mapping.put("targetPath", "$.selectedParty");
        mapping.put("index", index);
        listExtractionMappings.add(mapping);

        config.put("listExtractionMappings", listExtractionMappings);
        config.put("transformRules", new JSONObject());

        return config.toJSONString();
    }

    private static String buildExtractByConditionConfig() {
        JSONObject config = new JSONObject();
        JSONObject apiConfig = new JSONObject();
        apiConfig.put("jsonRootPath", "$.contractData");
        config.put("apiConfig", apiConfig);

        JSONArray listExtractionMappings = new JSONArray();
        JSONObject mapping = new JSONObject();
        mapping.put("type", "extractByCondition");
        mapping.put("sourcePath", "$.signatories[*]");
        mapping.put("targetPath", "$.filteredParties");
        mapping.put("condition", "$.partyType == '甲方'");
        mapping.put("takeFirst", false);
        listExtractionMappings.add(mapping);

        config.put("listExtractionMappings", listExtractionMappings);
        config.put("transformRules", new JSONObject());

        return config.toJSONString();
    }

    private static void executeAndPrint(String testName, String configJson, String requestJson) {
        System.out.println("【测试名称】" + testName);
        System.out.println("【请求JSON】");
        System.out.println(formatJson(requestJson));

        try {
            Map<String, Object> result = engine.execute(configJson, requestJson);
            Boolean success = (Boolean) result.get("success");
            Object resultData = result.get("resultData");
            List<ErrorDetail> errors = (List<ErrorDetail>) result.get("errors");
            List<ErrorDetail> validationErrors = (List<ErrorDetail>) result.get("validationErrors");

            System.out.println("\n【执行结果】");
            System.out.println("  成功: " + success);

            if (resultData != null) {
                System.out.println("  转换后数据:");
                System.out.println("    " + formatJson(JSON.toJSONString(resultData)));
            }

            if (errors != null && !errors.isEmpty()) {
                System.out.println("  转换错误:");
                for (ErrorDetail error : errors) {
                    System.out.println("    - " + error.getMessage());
                }
            }

            if (validationErrors != null && !validationErrors.isEmpty()) {
                System.out.println("  校验失败:");
                for (ErrorDetail error : validationErrors) {
                    System.out.println("    - " + error.getMessage());
                }
            }
        } catch (Exception e) {
            System.out.println("  执行异常: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println();
    }

    private static String formatJson(String json) {
        try {
            Object obj = JSON.parse(json);
            return JSON.toJSONString(obj, true);
        } catch (Exception e) {
            return json;
        }
    }
}
