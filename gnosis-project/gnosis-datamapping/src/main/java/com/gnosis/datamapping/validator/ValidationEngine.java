package com.gnosis.datamapping.validator;

import com.alibaba.fastjson.JSONObject;
import com.gnosis.datamapping.engine.ValidationError;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

public class ValidationEngine {

    public ValidationError validate(String ruleType, JSONObject ruleConfig, Object value) {
        if (value == null && !"required".equals(ruleType)) {
            return null;
        }

        switch (ruleType) {
            case "required":
                return validateRequired(value);
            case "maxLength":
                return validateMaxLength(ruleConfig.getIntValue("value"), value);
            case "minLength":
                return validateMinLength(ruleConfig.getIntValue("value"), value);
            case "pattern":
                return validatePattern(ruleConfig.getString("value"), value);
            case "min":
                return validateMin(ruleConfig.getBigDecimal("value"), value);
            case "max":
                return validateMax(ruleConfig.getBigDecimal("value"), value);
            case "enum":
                return validateEnum(ruleConfig.getJSONArray("values"), value);
            case "dateFormat":
                return validateDateFormat(ruleConfig.getString("value"), value);
            case "dateRange":
                return validateDateRange(ruleConfig.getString("min"), ruleConfig.getString("max"), value);
            case "boolean":
                return validateBoolean(value);
            case "integer":
                return validateInteger(value);
            case "decimalPrecision":
                return validateDecimalPrecision(ruleConfig.getIntValue("value"), value);
            default:
                return null;
        }
    }

    private ValidationError validateRequired(Object value) {
        if (value == null || (value instanceof String && ((String) value).trim().isEmpty())) {
            return new ValidationError("required", "字段不能为空");
        }
        return null;
    }

    private ValidationError validateMaxLength(int maxLength, Object value) {
        if (value != null && String.valueOf(value).length() > maxLength) {
            return new ValidationError("maxLength", "长度不能超过" + maxLength);
        }
        return null;
    }

    private ValidationError validateMinLength(int minLength, Object value) {
        if (value != null && String.valueOf(value).length() < minLength) {
            return new ValidationError("minLength", "长度不能小于" + minLength);
        }
        return null;
    }

    private ValidationError validatePattern(String pattern, Object value) {
        if (value != null && !Pattern.matches(pattern, String.valueOf(value))) {
            return new ValidationError("pattern", "不符合正则表达式");
        }
        return null;
    }

    private ValidationError validateMin(BigDecimal min, Object value) {
        if (value != null) {
            try {
                BigDecimal val = new BigDecimal(String.valueOf(value));
                if (val.compareTo(min) < 0) {
                    return new ValidationError("min", "值不能小于" + min);
                }
            } catch (NumberFormatException e) {
                return new ValidationError("min", "数值格式错误");
            }
        }
        return null;
    }

    private ValidationError validateMax(BigDecimal max, Object value) {
        if (value != null) {
            try {
                BigDecimal val = new BigDecimal(String.valueOf(value));
                if (val.compareTo(max) > 0) {
                    return new ValidationError("max", "值不能大于" + max);
                }
            } catch (NumberFormatException e) {
                return new ValidationError("max", "数值格式错误");
            }
        }
        return null;
    }

    private ValidationError validateEnum(com.alibaba.fastjson.JSONArray values, Object value) {
        if (value != null && values != null) {
            boolean found = false;
            for (int i = 0; i < values.size(); i++) {
                if (String.valueOf(value).equals(String.valueOf(values.get(i)))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return new ValidationError("enum", "值不在枚举范围内");
            }
        }
        return null;
    }

    private ValidationError validateDateFormat(String format, Object value) {
        if (value != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(format);
                sdf.parse(String.valueOf(value));
            } catch (Exception e) {
                return new ValidationError("dateFormat", "日期格式错误，应为" + format);
            }
        }
        return null;
    }

    private ValidationError validateDateRange(String min, String max, Object value) {
        if (value != null) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Date val = sdf.parse(String.valueOf(value));
                if (min != null && val.before(sdf.parse(min))) {
                    return new ValidationError("dateRange", "日期不能早于" + min);
                }
                if (max != null && val.after(sdf.parse(max))) {
                    return new ValidationError("dateRange", "日期不能晚于" + max);
                }
            } catch (Exception e) {
                return new ValidationError("dateRange", "日期格式错误");
            }
        }
        return null;
    }

    private ValidationError validateBoolean(Object value) {
        if (value != null && !(value instanceof Boolean)) {
            String str = String.valueOf(value).toLowerCase();
            if (!"true".equals(str) && !"false".equals(str) && !"0".equals(str) && !"1".equals(str)) {
                return new ValidationError("boolean", "值必须为布尔类型");
            }
        }
        return null;
    }

    private ValidationError validateInteger(Object value) {
        if (value != null) {
            try {
                Integer.parseInt(String.valueOf(value));
            } catch (NumberFormatException e) {
                return new ValidationError("integer", "值必须为整数");
            }
        }
        return null;
    }

    private ValidationError validateDecimalPrecision(int precision, Object value) {
        if (value != null) {
            try {
                BigDecimal val = new BigDecimal(String.valueOf(value));
                int scale = val.scale();
                if (scale > precision) {
                    return new ValidationError("decimalPrecision", "小数精度不能超过" + precision + "位");
                }
            } catch (NumberFormatException e) {
                return new ValidationError("decimalPrecision", "数值格式错误");
            }
        }
        return null;
    }
}
