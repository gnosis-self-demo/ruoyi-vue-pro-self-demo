package com.gnosis.datamapping.transform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TransformEngine {

    public Object apply(String transformName, Object value, JSONObject transformRules) {
        if (transformRules != null && transformRules.containsKey(transformName)) {
            JSONObject rule = transformRules.getJSONObject(transformName);
            String type = rule.getString("type");

            switch (type) {
                case "function":
                    return applyFunction(rule.getString("function"), rule.getJSONObject("parameters"), value);
                case "lookup":
                    return applyLookup(rule, value);
                case "regex":
                    return applyRegex(rule, value);
                default:
                    return value;
            }
        } else {
            return applyBuiltinTransform(transformName, value);
        }
    }

    private Object applyBuiltinTransform(String transformName, Object value) {
        if (value == null) return null;
        String str = String.valueOf(value);

        if ("trim".equals(transformName)) {
            return str.trim();
        } else if ("uppercase".equals(transformName)) {
            return str.toUpperCase();
        } else if ("lowercase".equals(transformName)) {
            return str.toLowerCase();
        } else if (transformName.startsWith("round")) {
            try {
                BigDecimal bd = new BigDecimal(str);
                int precision = 2;
                int start = transformName.indexOf('(');
                int end = transformName.indexOf(')');
                if (start != -1 && end != -1) {
                    precision = Integer.parseInt(transformName.substring(start + 1, end));
                }
                return bd.setScale(precision, RoundingMode.HALF_UP);
            } catch (Exception e) {
                return value;
            }
        } else if (transformName.startsWith("dateFormat")) {
            try {
                String format = "yyyy-MM-dd";
                int start = transformName.indexOf(':');
                if (start != -1) {
                    format = transformName.substring(start + 1);
                }
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                Date date = sdf.parse(str);
                return new SimpleDateFormat(format).format(date);
            } catch (Exception e) {
                return value;
            }
        }
        return value;
    }

    private Object applyFunction(String function, JSONObject parameters, Object value) {
        if (value == null || function == null) return value;

        if ("String.trim()".equals(function)) {
            return String.valueOf(value).trim();
        } else if ("String.toUpperCase()".equals(function)) {
            return String.valueOf(value).toUpperCase();
        } else if ("String.toLowerCase()".equals(function)) {
            return String.valueOf(value).toLowerCase();
        } else if (function.startsWith("Math.round")) {
            try {
                BigDecimal bd = new BigDecimal(String.valueOf(value));
                int precision = 2;
                if (parameters != null && parameters.containsKey("precision")) {
                    precision = parameters.getIntValue("precision");
                }
                return bd.setScale(precision, RoundingMode.HALF_UP);
            } catch (Exception e) {
                return value;
            }
        }
        return value;
    }

    private Object applyLookup(JSONObject rule, Object value) {
        if (value == null) return value;

        JSONObject mapping = rule.getJSONObject("mapping");
        if (mapping == null) return value;

        String key = String.valueOf(value);
        boolean ignoreCase = rule.getBooleanValue("ignoreCase");

        if (ignoreCase) {
            for (String mapKey : mapping.keySet()) {
                if (mapKey.equalsIgnoreCase(key)) {
                    return mapping.get(mapKey);
                }
            }
        } else {
            if (mapping.containsKey(key)) {
                return mapping.get(key);
            }
        }

        Object defaultValue = rule.get("defaultValue");
        if (defaultValue != null) {
            return defaultValue;
        }
        return value;
    }

    private Object applyRegex(JSONObject rule, Object value) {
        if (value == null) return value;

        String pattern = rule.getString("pattern");
        String replacement = rule.getString("replacement");

        if (pattern == null || replacement == null) return value;

        try {
            return String.valueOf(value).replaceAll(pattern, replacement);
        } catch (Exception e) {
            return value;
        }
    }
}
