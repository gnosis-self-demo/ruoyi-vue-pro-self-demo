package com.gnosis.datamapping;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.datamapping.engine.ErrorDetail;
import com.gnosis.datamapping.engine.MappingEngine;

import java.util.List;
import java.util.Map;

/**
 * 场景2：根节点字段 → 指定节点字段
 * 场景3：指定节点字段 → 根节点字段
 */
public class Scenario2And3CrossNodeMappingTest {

    private static MappingEngine engine = new MappingEngine();

    public static void main(String[] args) {

        System.out.println("=== 场景2：根节点字段 → 指定节点字段 ===");

        String config2 = buildRootToNodeConfig();
        String request2 = "{\n" +
                "  \"contractNo\": \"HT2024001\",\n" +
                "  \"contractName\": \"2024年度采购框架合同\",\n" +
                "  \"contractAmount\": 1500000.567,\n" +
                "  \"attachments\": [\n" +
                "    {\"fileName\": \"合同正文.pdf\", \"fileUrl\": \"https://example.com/file1.pdf\"},\n" +
                "    {\"fileName\": \"补充协议.docx\", \"fileUrl\": \"https://example.com/file2.docx\"}\n" +
                "  ]\n" +
                "}";
        executeAndPrint("根节点→指定节点", config2, request2);

        System.out.println("\n=== 场景3：指定节点字段 → 根节点字段 ===");

        String config3 = buildNodeToRootConfig();
        String request3 = "{\n" +
                "  \"basicInfo\": {\n" +
                "    \"contractName\": \"2024年度采购框架合同\",\n" +
                "    \"signDate\": \"2024-01-15\"\n" +
                "  },\n" +
                "  \"extendedInfo\": {\n" +
                "    \"projectCode\": \"PROJ-2024-001\",\n" +
                "    \"department\": \"采购部\"\n" +
                "  },\n" +
                "  \"paymentInfo\": {\n" +
                "    \"totalAmount\": 1650000.00\n" +
                "  },\n" +
                "  \"parties\": [\n" +
                "    {\"partyName\": \"XX科技有限公司\", \"partyType\": \"甲方\"},\n" +
                "    {\"partyName\": \"YY供应商有限公司\", \"partyType\": \"乙方\"}\n" +
                "  ]\n" +
                "}";
        executeAndPrint("指定节点→根节点", config3, request3);
    }

    private static String buildRootToNodeConfig() {
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
        apiConfig.put("jsonRootPath", "$");
        config.put("apiConfig", apiConfig);

        JSONArray crossNodeMappings = new JSONArray();

        JSONObject mapping1 = new JSONObject();
        mapping1.put("direction", "rootToNode");
        mapping1.put("sourcePath", "$.contractNo");
        mapping1.put("targetPath", "$.basicInfo.contractNo");
        mapping1.put("dataType", "string");
        JSONArray transforms1 = new JSONArray();
        transforms1.add("trim");
        mapping1.put("transforms", transforms1);
        mapping1.put("description", "合同编号 → basicInfo节点");
        crossNodeMappings.add(mapping1);

        JSONObject mapping2 = new JSONObject();
        mapping2.put("direction", "rootToNode");
        mapping2.put("sourcePath", "$.contractName");
        mapping2.put("targetPath", "$.basicInfo.contractName");
        mapping2.put("dataType", "string");
        mapping2.put("transforms", new JSONArray().fluentAdd("trim"));
        mapping2.put("description", "合同名称 → basicInfo节点");
        crossNodeMappings.add(mapping2);

        JSONObject mapping3 = new JSONObject();
        mapping3.put("direction", "rootToNode");
        mapping3.put("sourcePath", "$.contractAmount");
        mapping3.put("targetPath", "$.paymentInfo.amount");
        mapping3.put("dataType", "decimal");
        mapping3.put("transforms", new JSONArray().fluentAdd("round(2)"));
        mapping3.put("description", "合同金额 → paymentInfo节点");
        crossNodeMappings.add(mapping3);

        JSONObject mapping4 = new JSONObject();
        mapping4.put("direction", "rootToNode");
        mapping4.put("sourcePath", "$.attachments");
        mapping4.put("targetPath", "$.documentInfo.attachments");
        mapping4.put("dataType", "array");
        mapping4.put("description", "附件数组 → documentInfo节点");
        crossNodeMappings.add(mapping4);

        config.put("crossNodeMappings", crossNodeMappings);

        JSONObject transformRules = new JSONObject();
        JSONObject trimRule = new JSONObject();
        trimRule.put("type", "function");
        trimRule.put("function", "String.trim()");
        transformRules.put("trim", trimRule);

        JSONObject roundRule = new JSONObject();
        roundRule.put("type", "function");
        roundRule.put("function", "Math.round(value, precision)");
        roundRule.put("parameters", new JSONObject().fluentPut("precision", 2));
        transformRules.put("round", roundRule);

        config.put("transformRules", transformRules);

        return config.toJSONString();
    }

    private static String buildNodeToRootConfig() {
        JSONObject config = new JSONObject();
        JSONObject metadata = new JSONObject();
        metadata.put("systemId", "ERP_CONTRACT");
        metadata.put("systemName", "ERP合同系统");
        metadata.put("configVersion", "1.0");
        config.put("configMetadata", metadata);

        JSONObject apiConfig = new JSONObject();
        apiConfig.put("apiVersion", "v1.0");
        apiConfig.put("jsonRootPath", "$");
        config.put("apiConfig", apiConfig);

        JSONArray crossNodeMappings = new JSONArray();

        JSONObject mapping1 = new JSONObject();
        mapping1.put("direction", "nodeToRoot");
        mapping1.put("sourcePath", "$.basicInfo.contractName");
        mapping1.put("targetPath", "$.contractName");
        mapping1.put("dataType", "string");
        mapping1.put("transforms", new JSONArray().fluentAdd("trim"));
        crossNodeMappings.add(mapping1);

        JSONObject mapping2 = new JSONObject();
        mapping2.put("direction", "nodeToRoot");
        mapping2.put("sourcePath", "$.extendedInfo.projectCode");
        mapping2.put("targetPath", "$.projectCode");
        mapping2.put("dataType", "string");
        mapping2.put("transforms", new JSONArray().fluentAdd("trim"));
        crossNodeMappings.add(mapping2);

        JSONObject mapping3 = new JSONObject();
        mapping3.put("direction", "nodeToRoot");
        mapping3.put("sourcePath", "$.extendedInfo.department");
        mapping3.put("targetPath", "$.department");
        mapping3.put("dataType", "string");
        mapping3.put("transforms", new JSONArray().fluentAdd("trim"));
        crossNodeMappings.add(mapping3);

        JSONObject mapping4 = new JSONObject();
        mapping4.put("direction", "nodeToRoot");
        mapping4.put("sourcePath", "$.paymentInfo.totalAmount");
        mapping4.put("targetPath", "$.totalAmount");
        mapping4.put("dataType", "decimal");
        mapping4.put("transforms", new JSONArray().fluentAdd("round(2)"));
        crossNodeMappings.add(mapping4);

        JSONObject mapping5 = new JSONObject();
        mapping5.put("direction", "nodeToRoot");
        mapping5.put("sourcePath", "$.parties[0].partyName");
        mapping5.put("targetPath", "$.principalParty");
        mapping5.put("dataType", "string");
        mapping5.put("transforms", new JSONArray().fluentAdd("trim"));
        crossNodeMappings.add(mapping5);

        config.put("crossNodeMappings", crossNodeMappings);

        JSONObject transformRules = new JSONObject();
        JSONObject trimRule = new JSONObject();
        trimRule.put("type", "function");
        trimRule.put("function", "String.trim()");
        transformRules.put("trim", trimRule);

        JSONObject roundRule = new JSONObject();
        roundRule.put("type", "function");
        roundRule.put("function", "Math.round(value, precision)");
        roundRule.put("parameters", new JSONObject().fluentPut("precision", 2));
        transformRules.put("round", roundRule);

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
