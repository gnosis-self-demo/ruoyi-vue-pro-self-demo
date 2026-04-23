package com.gnosis.datamapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.datamapping.engine.ErrorDetail;
import com.gnosis.datamapping.engine.MappingEngine;

import java.util.List;
import java.util.Map;

/**
 * 场景5：String转List + 链式规则处理
 */
public class Scenario5StringToListTest {

    private static MappingEngine engine = new MappingEngine();

    public static void main(String[] args) {

        System.out.println("=== 场景5：String转List + 链式规则 ===");

        String configJson = buildConfigJson();
        String requestJson = "{\n" +
                "  \"contractData\": {\n" +
                "    \"contractNo\": \"HT2024001\",\n" +
                "    \"partyNames\": \"XX科技, YY供应商, ZZ物流, 未知公司\",\n" +
                "    \"contactPhones\": \"13800138000; 13900139000; 13700137000\",\n" +
                "    \"paymentTerms\": \"30%@验收;70%@交付\"\n" +
                "  }\n" +
                "}";
        executeAndPrint("String转List+链式规则", configJson, requestJson);

        System.out.println("\n--- 测试5.2：String按分隔符拆分后还能再匹配规则 ---");
        String config2 = buildChainedRuleConfig();
        String request2 = "{\n" +
                "  \"contractData\": {\n" +
                "    \"partyNames\": \"XX科技, YY供应商\",\n" +
                "    \"contactPhones\": \"13800138000; 13900139000\"\n" +
                "  }\n" +
                "}";
        executeAndPrint("String拆分后链式处理", config2, request2);
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

        JSONArray stringToListMappings = new JSONArray();

        // partyNames 拆分 + 链式增强
        JSONObject mapping1 = new JSONObject();
        mapping1.put("type", "stringSplitToList");
        mapping1.put("sourcePath", "$.partyNames");
        mapping1.put("targetPath", "$.parties");
        mapping1.put("separator", ",");
        mapping1.put("trimItems", true);
        mapping1.put("removeEmpty", true);
        mapping1.put("maxItems", 10);
        mapping1.put("description", "签约方名称字符串转数组并补充完整信息");

        JSONArray chainedMappings1 = new JSONArray();
        JSONObject chained1 = new JSONObject();
        chained1.put("type", "enrichListItems");
        chained1.put("enrichmentSource", "partyInfoMapping");
        chained1.put("keyField", "itemValue");
        chained1.put("targetFields", new JSONArray().fluentAdd("partyType").fluentAdd("creditCode").fluentAdd("contactPerson"));
        chainedMappings1.add(chained1);

        mapping1.put("chainedMappings", chainedMappings1);
        stringToListMappings.add(mapping1);

        // contactPhones 拆分 + 格式化 + 转换为对象
        JSONObject mapping2 = new JSONObject();
        mapping2.put("type", "stringSplitToList");
        mapping2.put("sourcePath", "$.contactPhones");
        mapping2.put("targetPath", "$.contacts");
        mapping2.put("separator", ";");
        mapping2.put("trimItems", true);
        mapping2.put("removeEmpty", true);

        JSONArray chainedMappings2 = new JSONArray();
        JSONObject chained2a = new JSONObject();
        chained2a.put("type", "transformListItems");
        chained2a.put("itemTransforms", new JSONArray().fluentAdd("phoneFormat"));
        chainedMappings2.add(chained2a);

        JSONObject chained2b = new JSONObject();
        chained2b.put("type", "convertListToObjects");
        JSONObject objectTemplate = new JSONObject();
        objectTemplate.put("phone", "{itemValue}");
        objectTemplate.put("type", "MOBILE");
        objectTemplate.put("isPrimary", false);
        chained2b.put("objectTemplate", objectTemplate);
        chainedMappings2.add(chained2b);

        mapping2.put("chainedMappings", chainedMappings2);
        stringToListMappings.add(mapping2);

        // paymentTerms 正则拆分
        JSONObject mapping3 = new JSONObject();
        mapping3.put("type", "stringSplitWithPattern");
        mapping3.put("sourcePath", "$.paymentTerms");
        mapping3.put("targetPath", "$.paymentSchedule");
        mapping3.put("pattern", "(\\d+)%@([^;]+)");

        JSONArray captureGroups = new JSONArray();
        JSONObject group1 = new JSONObject();
        group1.put("groupIndex", 1);
        group1.put("targetField", "percentage");
        group1.put("dataType", "decimal");
        group1.put("transforms", new JSONArray().fluentAdd("divide(100)"));
        captureGroups.add(group1);

        JSONObject group2 = new JSONObject();
        group2.put("groupIndex", 2);
        group2.put("targetField", "milestone");
        group2.put("dataType", "string");
        group2.put("transforms", new JSONArray().fluentAdd("trim"));
        captureGroups.add(group2);

        mapping3.put("captureGroups", captureGroups);
        mapping3.put("description", "付款条款字符串转付款计划数组");
        stringToListMappings.add(mapping3);

        config.put("stringToListMappings", stringToListMappings);

        JSONObject enrichmentMappings = new JSONObject();
        JSONObject partyInfoMapping = new JSONObject();
        partyInfoMapping.put("type", "lookup");
        partyInfoMapping.put("dataSource", "partyInfo");
        partyInfoMapping.put("keyField", "partyName");
        partyInfoMapping.put("mappingFields", new JSONObject().fluentPut("partyType", "partyType").fluentPut("creditCode", "creditCode").fluentPut("contactPerson", "contactPerson"));
        partyInfoMapping.put("defaultValues", new JSONObject().fluentPut("partyType", "OTHER").fluentPut("creditCode", "").fluentPut("contactPerson", ""));
        enrichmentMappings.put("partyInfoMapping", partyInfoMapping);
        config.put("enrichmentMappings", enrichmentMappings);

        JSONObject dataSources = new JSONObject();
        JSONObject partyInfoSource = new JSONObject();
        partyInfoSource.put("type", "static");
        JSONObject staticData = new JSONObject();
        staticData.put("XX科技", new JSONObject().fluentPut("partyType", "PRINCIPAL").fluentPut("creditCode", "91110108MA1234567X").fluentPut("contactPerson", "张三"));
        staticData.put("YY供应商", new JSONObject().fluentPut("partyType", "VENDOR").fluentPut("creditCode", "91110108MA7654321Y").fluentPut("contactPerson", "李四"));
        staticData.put("ZZ物流", new JSONObject().fluentPut("partyType", "THIRD_PARTY").fluentPut("creditCode", "91110108MA8765432Z").fluentPut("contactPerson", "王五"));
        partyInfoSource.put("staticData", staticData);
        dataSources.put("partyInfo", partyInfoSource);
        config.put("dataSources", dataSources);

        JSONObject transformRules = new JSONObject();

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

    private static String buildChainedRuleConfig() {
        JSONObject config = new JSONObject();
        JSONObject apiConfig = new JSONObject();
        apiConfig.put("jsonRootPath", "$.contractData");
        config.put("apiConfig", apiConfig);

        JSONArray stringToListMappings = new JSONArray();

        JSONObject mapping1 = new JSONObject();
        mapping1.put("type", "stringSplitToList");
        mapping1.put("sourcePath", "$.partyNames");
        mapping1.put("targetPath", "$.parties");
        mapping1.put("separator", ",");
        mapping1.put("trimItems", true);
        mapping1.put("removeEmpty", true);
        stringToListMappings.add(mapping1);

        JSONObject mapping2 = new JSONObject();
        mapping2.put("type", "stringSplitToList");
        mapping2.put("sourcePath", "$.contactPhones");
        mapping2.put("targetPath", "$.phones");
        mapping2.put("separator", ";");
        mapping2.put("trimItems", true);
        mapping2.put("removeEmpty", true);

        JSONArray chainedMappings = new JSONArray();
        JSONObject chained1 = new JSONObject();
        chained1.put("type", "transformListItems");
        chained1.put("itemTransforms", new JSONArray().fluentAdd("phoneFormat"));
        chainedMappings.add(chained1);
        mapping2.put("chainedMappings", chainedMappings);
        stringToListMappings.add(mapping2);

        config.put("stringToListMappings", stringToListMappings);

        JSONObject transformRules = new JSONObject();
        JSONObject phoneFormat = new JSONObject();
        phoneFormat.put("type", "regex");
        phoneFormat.put("pattern", "(\\d{3})\\d{4}(\\d{4})");
        phoneFormat.put("replacement", "$1****$2");
        transformRules.put("phoneFormat", phoneFormat);

        config.put("transformRules", transformRules);

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
