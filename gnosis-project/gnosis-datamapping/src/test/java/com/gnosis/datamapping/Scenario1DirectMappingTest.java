package com.gnosis.datamapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.datamapping.engine.ErrorDetail;
import com.gnosis.datamapping.engine.MappingEngine;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * 场景1：字段直接匹配（含类型转换和校验）
 * 测试要点：类型映射、输入合法性、必输校验
 */
public class Scenario1DirectMappingTest {

    private static MappingEngine engine = new MappingEngine();

    @Test
    public void test11NormalConversion() {
        String configJson = buildConfigJson();
        String request1 = "{\n" +
                "  \"contractData\": {\n" +
                "    \"contractNo\": \" ht2024001 \",\n" +
                "    \"contractAmount\": 1500000.567,\n" +
                "    \"contractStatus\": \"生效\",\n" +
                "    \"priorityLevel\": \"高\"\n" +
                "  }\n" +
                "}";

        Map<String, Object> result = engine.execute(configJson, request1);
        assertTrue((Boolean) result.get("success"));

        Object resultData = result.get("resultData");
        assertNotNull(resultData);

        JSONObject output = (JSONObject) resultData;
        assertEquals("HT2024001", output.getString("contractNo"));
        assertEquals("ACTIVE", output.getString("status"));
        assertEquals("HIGH", output.getString("priority"));

        @SuppressWarnings("unchecked")
        List<ErrorDetail> errors = (List<ErrorDetail>) result.get("errors");
        @SuppressWarnings("unchecked")
        List<ErrorDetail> validationErrors = (List<ErrorDetail>) result.get("validationErrors");
        assertTrue(errors.isEmpty());
        assertTrue(validationErrors.isEmpty());

        printResult("正常转换", result, request1);
    }

    @Test
    public void test12ValidationFailure() {
        String configJson = buildConfigJson();
        String request2 = "{\n" +
                "  \"contractData\": {\n" +
                "    \"contractNo\": \"ht-2024-001\",\n" +
                "    \"contractAmount\": -100,\n" +
                "    \"contractStatus\": \"审批中\",\n" +
                "    \"priorityLevel\": \"极高\"\n" +
                "  }\n" +
                "}";

        Map<String, Object> result = engine.execute(configJson, request2);
        assertFalse((Boolean) result.get("success"));

        @SuppressWarnings("unchecked")
        List<ErrorDetail> validationErrors = (List<ErrorDetail>) result.get("validationErrors");
        assertTrue("应该有校验失败", !validationErrors.isEmpty() || !((List<ErrorDetail>) result.get("errors")).isEmpty());

        printResult("校验失败", result, request2);
    }

    @Test
    public void test13TypeConversionSuccess() {
        String config = buildTypeConversionConfig();
        String request = "{\"sourceData\": {\"name\": \"test\", \"age\": \"25\", \"email\": \"123\"}}";

        Map<String, Object> result = engine.execute(config, request);
        assertTrue((Boolean) result.get("success"));

        JSONObject output = (JSONObject) result.get("resultData");
        assertEquals(Integer.valueOf(25), output.getInteger("age"));

        printResult("String转Integer成功", result, request);
    }

    @Test
    public void test14TypeConversionFailureWithFallback() {
        String config = buildTypeConversionConfig();
        String request = "{\"sourceData\": {\"name\": \"test\", \"age\": \"abc\", \"email\": \"xyz\"}}";

        Map<String, Object> result = engine.execute(config, request);
        assertFalse((Boolean) result.get("success"));

        @SuppressWarnings("unchecked")
        List<ErrorDetail> errors = (List<ErrorDetail>) result.get("errors");
        assertFalse(errors.isEmpty());

        JSONObject output = (JSONObject) result.get("resultData");
        assertEquals("abc", output.getString("age"));

        printResult("String转Integer失败（可降级）", result, request);
    }

    @Test
    public void test15RequiredFieldValidation() {
        String config = buildTypeConversionConfig();
        String request = "{\"sourceData\": {\"name\": \"\", \"age\": \"\", \"email\": \"\"}}";

        Map<String, Object> result = engine.execute(config, request);
        assertFalse((Boolean) result.get("success"));

        @SuppressWarnings("unchecked")
        List<ErrorDetail> validationErrors = (List<ErrorDetail>) result.get("validationErrors");
        assertFalse(validationErrors.isEmpty());

        printResult("必填字段为空", result, request);
    }

    private static String buildConfigJson() {
        JSONObject config = new JSONObject();

        JSONObject metadata = new JSONObject();
        metadata.put("systemId", "ERP_CONTRACT");
        metadata.put("systemName", "ERP合同系统");
        metadata.put("configVersion", "1.0");
        metadata.put("lastModified", "2024-04-23T16:40:00");
        config.put("configMetadata", metadata);

        JSONObject apiConfig = new JSONObject();
        apiConfig.put("apiVersion", "v1.0");
        apiConfig.put("targetEndpoint", "/api/v1/contracts/create");
        apiConfig.put("jsonRootPath", "$.contractData");
        config.put("apiConfig", apiConfig);

        com.alibaba.fastjson.JSONArray directMappings = new com.alibaba.fastjson.JSONArray();

        JSONObject field1 = new JSONObject();
        field1.put("sourceField", "contractNo");
        field1.put("targetField", "contractNo");
        field1.put("dataType", "string");
        field1.put("required", true);
        com.alibaba.fastjson.JSONArray rules1 = new com.alibaba.fastjson.JSONArray();
        JSONObject rule1 = new JSONObject();
        rule1.put("type", "required");
        rule1.put("message", "合同编号不能为空");
        rules1.add(rule1);
        JSONObject rule2 = new JSONObject();
        rule2.put("type", "maxLength");
        rule2.put("value", 50);
        rule2.put("message", "合同编号长度不能超过50");
        rules1.add(rule2);
        JSONObject rule3 = new JSONObject();
        rule3.put("type", "pattern");
        rule3.put("value", "^[A-Z0-9_-]+$");
        rule3.put("message", "合同编号只能包含大写字母、数字、下划线和横线");
        rules1.add(rule3);
        field1.put("validationRules", rules1);
        com.alibaba.fastjson.JSONArray transforms1 = new com.alibaba.fastjson.JSONArray();
        transforms1.add("trim");
        transforms1.add("uppercase");
        field1.put("transforms", transforms1);
        directMappings.add(field1);

        JSONObject field2 = new JSONObject();
        field2.put("sourceField", "contractAmount");
        field2.put("targetField", "amount");
        field2.put("dataType", "decimal");
        field2.put("required", true);
        com.alibaba.fastjson.JSONArray rules2 = new com.alibaba.fastjson.JSONArray();
        JSONObject rule4 = new JSONObject();
        rule4.put("type", "required");
        rule4.put("message", "合同金额不能为空");
        rules2.add(rule4);
        JSONObject rule5 = new JSONObject();
        rule5.put("type", "min");
        rule5.put("value", 0.01);
        rule5.put("message", "合同金额必须大于0");
        rules2.add(rule5);
        field2.put("validationRules", rules2);
        com.alibaba.fastjson.JSONArray transforms2 = new com.alibaba.fastjson.JSONArray();
        transforms2.add("round(2)");
        field2.put("transforms", transforms2);
        directMappings.add(field2);

        JSONObject field3 = new JSONObject();
        field3.put("sourceField", "contractStatus");
        field3.put("targetField", "status");
        field3.put("dataType", "string");
        field3.put("required", true);
        com.alibaba.fastjson.JSONArray rules3 = new com.alibaba.fastjson.JSONArray();
        JSONObject rule6 = new JSONObject();
        rule6.put("type", "required");
        rules3.add(rule6);
        JSONObject rule7 = new JSONObject();
        rule7.put("type", "enum");
        rule7.put("values", new com.alibaba.fastjson.JSONArray().fluentAdd("DRAFT").fluentAdd("ACTIVE").fluentAdd("COMPLETED").fluentAdd("CANCELLED"));
        rule7.put("message", "合同状态只能是DRAFT、ACTIVE、COMPLETED或CANCELLED");
        rules3.add(rule7);
        field3.put("validationRules", rules3);
        com.alibaba.fastjson.JSONArray transforms3 = new com.alibaba.fastjson.JSONArray();
        transforms3.add("statusMapping");
        field3.put("transforms", transforms3);
        directMappings.add(field3);

        JSONObject field4 = new JSONObject();
        field4.put("sourceField", "priorityLevel");
        field4.put("targetField", "priority");
        field4.put("dataType", "string");
        field4.put("required", false);
        field4.put("defaultValue", "MEDIUM");
        com.alibaba.fastjson.JSONArray rules4 = new com.alibaba.fastjson.JSONArray();
        JSONObject rule8 = new JSONObject();
        rule8.put("type", "enum");
        rule8.put("values", new com.alibaba.fastjson.JSONArray().fluentAdd("LOW").fluentAdd("MEDIUM").fluentAdd("HIGH").fluentAdd("URGENT"));
        rules4.add(rule8);
        field4.put("validationRules", rules4);
        com.alibaba.fastjson.JSONArray transforms4 = new com.alibaba.fastjson.JSONArray();
        transforms4.add("priorityMapping");
        field4.put("transforms", transforms4);
        directMappings.add(field4);

        config.put("directMappings", directMappings);

        JSONObject transformRules = new JSONObject();

        JSONObject statusMapping = new JSONObject();
        statusMapping.put("type", "lookup");
        JSONObject statusMap = new JSONObject();
        statusMap.put("草稿", "DRAFT");
        statusMap.put("生效", "ACTIVE");
        statusMap.put("完成", "COMPLETED");
        statusMap.put("作废", "CANCELLED");
        statusMapping.put("mapping", statusMap);
        statusMapping.put("defaultValue", "DRAFT");
        statusMapping.put("ignoreCase", true);
        transformRules.put("statusMapping", statusMapping);

        JSONObject priorityMapping = new JSONObject();
        priorityMapping.put("type", "lookup");
        JSONObject priorityMap = new JSONObject();
        priorityMap.put("低", "LOW");
        priorityMap.put("中", "MEDIUM");
        priorityMap.put("高", "HIGH");
        priorityMap.put("紧急", "URGENT");
        priorityMapping.put("mapping", priorityMap);
        priorityMapping.put("defaultValue", "MEDIUM");
        priorityMapping.put("ignoreCase", true);
        transformRules.put("priorityMapping", priorityMapping);

        JSONObject trimRule = new JSONObject();
        trimRule.put("type", "function");
        trimRule.put("function", "String.trim()");
        transformRules.put("trim", trimRule);

        JSONObject uppercaseRule = new JSONObject();
        uppercaseRule.put("type", "function");
        uppercaseRule.put("function", "String.toUpperCase()");
        transformRules.put("uppercase", uppercaseRule);

        JSONObject roundRule = new JSONObject();
        roundRule.put("type", "function");
        roundRule.put("function", "Math.round(value, precision)");
        JSONObject roundParams = new JSONObject();
        roundParams.put("precision", 2);
        roundRule.put("parameters", roundParams);
        transformRules.put("round", roundRule);

        config.put("transformRules", transformRules);

        JSONObject validationEngine = new JSONObject();
        validationEngine.put("stopOnFirstError", false);
        validationEngine.put("collectAllErrors", true);
        config.put("validationEngine", validationEngine);

        return config.toJSONString();
    }

    private static String buildTypeConversionConfig() {
        JSONObject config = new JSONObject();
        JSONObject apiConfig = new JSONObject();
        apiConfig.put("jsonRootPath", "$.sourceData");
        config.put("apiConfig", apiConfig);

        com.alibaba.fastjson.JSONArray directMappings = new com.alibaba.fastjson.JSONArray();

        JSONObject field1 = new JSONObject();
        field1.put("sourceField", "name");
        field1.put("targetField", "name");
        field1.put("dataType", "string");
        field1.put("required", true);
        com.alibaba.fastjson.JSONArray rules1 = new com.alibaba.fastjson.JSONArray();
        JSONObject reqRule = new JSONObject();
        reqRule.put("type", "required");
        reqRule.put("message", "名称不能为空");
        rules1.add(reqRule);
        field1.put("validationRules", rules1);
        com.alibaba.fastjson.JSONArray transforms1 = new com.alibaba.fastjson.JSONArray();
        transforms1.add("trim");
        field1.put("transforms", transforms1);
        directMappings.add(field1);

        JSONObject field2 = new JSONObject();
        field2.put("sourceField", "age");
        field2.put("targetField", "age");
        field2.put("dataType", "integer");
        field2.put("required", true);
        directMappings.add(field2);

        JSONObject field3 = new JSONObject();
        field3.put("sourceField", "score");
        field3.put("targetField", "score");
        field3.put("dataType", "decimal");
        directMappings.add(field3);

        config.put("directMappings", directMappings);

        JSONObject transformRules = new JSONObject();
        JSONObject trimRule = new JSONObject();
        trimRule.put("type", "function");
        trimRule.put("function", "String.trim()");
        transformRules.put("trim", trimRule);
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
                System.out.println("  转换错误 (" + errors.size() + "):");
                for (ErrorDetail error : errors) {
                    System.out.println("    - 字段: " + error.getField() + ", 规则: " + error.getRule() +
                            ", 消息: " + error.getMessage() + ", 值: " + error.getValue());
                }
            }

            if (validationErrors != null && !validationErrors.isEmpty()) {
                System.out.println("  校验失败 (" + validationErrors.size() + "):");
                for (ErrorDetail error : validationErrors) {
                    System.out.println("    - 字段: " + error.getField() + ", 规则: " + error.getRule() +
                            ", 消息: " + error.getMessage() + ", 值: " + error.getValue());
                }
            }
        } catch (Exception e) {
            System.out.println("  执行异常: " + e.getMessage());
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

    private static void printResult(String testName, Map<String, Object> result, String requestJson) {
        System.out.println("=== " + testName + " ===");
        System.out.println("请求: " + formatJson(requestJson));
        System.out.println("结果: success=" + result.get("success"));
        System.out.println("数据: " + result.get("resultData"));
        @SuppressWarnings("unchecked")
        List<ErrorDetail> errors = (List<ErrorDetail>) result.get("errors");
        if (!errors.isEmpty()) {
            System.out.println("转换错误: " + errors);
        }
        @SuppressWarnings("unchecked")
        List<ErrorDetail> validationErrors = (List<ErrorDetail>) result.get("validationErrors");
        if (!validationErrors.isEmpty()) {
            System.out.println("校验失败: " + validationErrors);
        }
        System.out.println();
    }
}
