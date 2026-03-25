package com.cmbc.oa.module.paramcheck.component;

import com.cmbc.oa.module.paramcheck.exception.ValidationException;
import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * LiteFlow 组件: 基础规则校验节点
 * 节点 ID: check_format
 *
 * 支持规则类型: REGEX / EMAIL / PHONE / RANGE / NOT_NULL / LENGTH
 * 规则来源: requestData 中的 check_format_rules 字段
 * (由 DynamicValidationService 从 DB component_config 注入)
 */
@Component("check_format")
public class BasicRuleComponent extends NodeComponent {

    @Override
    public void process() {
        Object requestData = this.getRequestData();
        if (requestData == null) {
            throw new ValidationException("FORMAT_ERROR", "无待校验数据");
        }

        // 从请求数据中获取规则列表
        List<Object> rules = extractRuleList(requestData);
        if (rules == null || rules.isEmpty()) {
            return;
        }

        // 直接使用请求数据作为校验数据源
        Object dataSource = requestData;

        for (Object ruleObj : rules) {
            if (!(ruleObj instanceof Map)) continue;
            @SuppressWarnings("unchecked")
            Map<String, Object> rule = (Map<String, Object>) ruleObj;

            String path = (String) rule.get("path");
            String type = (String) rule.get("type");
            String msg = rule.containsKey("msg")
                    ? String.valueOf(rule.get("msg"))
                    : "校验失败";

            if (path == null || type == null) continue;

            Object value = readPath(dataSource, path);
            boolean passed = validateValue(value, type, rule);

            if (!passed) {
                throw new ValidationException("FORMAT_ERROR", msg, "check_format");
            }
        }
    }

    @SuppressWarnings("unchecked")
    private List<Object> extractRuleList(Object requestData) {
        if (requestData instanceof Map) {
            Object rules = ((Map<?, ?>) requestData).get("check_format_rules");
            if (rules instanceof List) {
                return (List<Object>) rules;
            }
        }
        return new ArrayList<>();
    }

    private Object readPath(Object data, String jsonPath) {
        try {
            return com.jayway.jsonpath.JsonPath.read(data, jsonPath);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean validateValue(Object value, String type, Map<String, Object> rule) {
        if (value == null) {
            return !"NOT_NULL".equalsIgnoreCase(type);
        }

        String strValue = String.valueOf(value);

        switch (type.toUpperCase()) {
            case "REGEX": {
                String pattern = (String) rule.get("pattern");
                if (pattern == null) return false;
                return Pattern.matches(pattern, strValue);
            }
            case "EMAIL":
                return Pattern.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$", strValue);
            case "PHONE":
                return Pattern.matches("^1[3-9]\\d{9}$", strValue);
            case "RANGE": {
                try {
                    double num = Double.parseDouble(strValue);
                    double min = rule.containsKey("min")
                            ? Double.parseDouble(String.valueOf(rule.get("min")))
                            : Double.MIN_VALUE;
                    double max = rule.containsKey("max")
                            ? Double.parseDouble(String.valueOf(rule.get("max")))
                            : Double.MAX_VALUE;
                    return num >= min && num <= max;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            case "LENGTH": {
                int len = strValue.length();
                int minLen = rule.containsKey("min")
                        ? Integer.parseInt(String.valueOf(rule.get("min")))
                        : 0;
                int maxLen = rule.containsKey("max")
                        ? Integer.parseInt(String.valueOf(rule.get("max")))
                        : Integer.MAX_VALUE;
                return len >= minLen && len <= maxLen;
            }
            case "NOT_NULL":
                return !strValue.trim().isEmpty();
            default:
                return true;
        }
    }
}
