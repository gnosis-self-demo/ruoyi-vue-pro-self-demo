package com.gnosis.datamapping.engine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.datamapping.transform.TransformEngine;
import com.gnosis.datamapping.validator.ValidationEngine;
import com.gnosis.datamapping.engine.ValidationError;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

public class MappingEngine {

    private TransformEngine transformEngine;
    private ValidationEngine validationEngine;

    public MappingEngine() {
        this.transformEngine = new TransformEngine();
        this.validationEngine = new ValidationEngine();
    }

    public Map<String, Object> execute(String configJsonStr, String requestJsonStr) {
        JSONObject config = JSON.parseObject(configJsonStr);
        Object requestData = JSON.parse(requestJsonStr);

        Map<String, Object> result = new LinkedHashMap<>();
        List<ErrorDetail> errors = new ArrayList<>();
        List<ErrorDetail> validationErrors = new ArrayList<>();

        String jsonRootPath = config.getJSONObject("apiConfig").getString("jsonRootPath");
        JSONObject rootData = resolveData(requestData, jsonRootPath);

        JSONObject output = new JSONObject();

        executeDirectMappings(config, rootData, output, errors, validationErrors);
        executeCrossNodeMappings(config, requestData, output, errors);
        executeListExtractionMappings(config, rootData, output, errors);
        executeStringToListMappings(config, rootData, output, errors);

        result.put("success", errors.isEmpty() && validationErrors.isEmpty());
        result.put("resultData", output);
        result.put("errors", errors);
        result.put("validationErrors", validationErrors);

        return result;
    }

    private JSONObject resolveData(Object data, String jsonPath) {
        if (jsonPath == null || "$".equals(jsonPath)) {
            if (data instanceof JSONObject) {
                return (JSONObject) data;
            }
            return JSON.parseObject(JSON.toJSONString(data));
        }
        try {
            com.jayway.jsonpath.Configuration conf = com.jayway.jsonpath.Configuration.defaultConfiguration();
            Object value = com.jayway.jsonpath.JsonPath.using(conf).parse(data).read(jsonPath);
            if (value instanceof JSONObject) {
                return (JSONObject) value;
            }
            return JSON.parseObject(JSON.toJSONString(value));
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private void executeDirectMappings(JSONObject config, JSONObject sourceData, JSONObject target,
                                        List<ErrorDetail> errors, List<ErrorDetail> validationErrors) {
        JSONArray directMappings = config.getJSONArray("directMappings");
        if (directMappings == null) return;

        JSONObject transformRules = config.getJSONObject("transformRules");

        for (int i = 0; i < directMappings.size(); i++) {
            JSONObject mapping = directMappings.getJSONObject(i);
            String sourceField = mapping.getString("sourceField");
            String targetField = mapping.getString("targetField");
            String dataType = mapping.getString("dataType");
            boolean required = mapping.getBooleanValue("required");
            Object defaultValue = mapping.get("defaultValue");
            JSONArray validationRules = mapping.getJSONArray("validationRules");
            JSONArray transforms = mapping.getJSONArray("transforms");

            Object sourceValue = sourceData.get(sourceField);

            if (sourceValue == null) {
                if (required) {
                    validationErrors.add(new ErrorDetail(sourceField, "required", "字段不能为空", null));
                } else if (defaultValue != null) {
                    target.put(targetField, defaultValue);
                }
                continue;
            }

            List<String> transformNames = new ArrayList<>();
            if (transforms != null) {
                for (int t = 0; t < transforms.size(); t++) {
                    transformNames.add(transforms.getString(t));
                }
            }

            Object transformedValue = transformValue(sourceValue, dataType, transformNames, transformRules, errors, sourceField);

            if (validationRules != null) {
                for (int v = 0; v < validationRules.size(); v++) {
                    JSONObject rule = validationRules.getJSONObject(v);
                    String ruleType = rule.getString("type");
                    String ruleMessage = rule.getString("message");
                    if (ruleMessage == null) ruleMessage = ruleType + "校验失败";
                    com.gnosis.datamapping.engine.ValidationError ve = validationEngine.validate(ruleType, rule, transformedValue);
                    if (ve != null) {
                        validationErrors.add(new ErrorDetail(sourceField, ruleType, ruleMessage, transformedValue));
                    }
                }
            }

            target.put(targetField, transformedValue);
        }
    }

    private void executeCrossNodeMappings(JSONObject config, Object requestData, JSONObject target,
                                          List<ErrorDetail> errors) {
        JSONArray crossNodeMappings = config.getJSONArray("crossNodeMappings");
        if (crossNodeMappings == null) return;

        JSONObject transformRules = config.getJSONObject("transformRules");

        for (int i = 0; i < crossNodeMappings.size(); i++) {
            JSONObject mapping = crossNodeMappings.getJSONObject(i);
            String direction = mapping.getString("direction");
            String sourcePath = mapping.getString("sourcePath");
            String targetPath = mapping.getString("targetPath");
            String dataType = mapping.getString("dataType");
            JSONArray transforms = mapping.getJSONArray("transforms");

            Object sourceValue;
            try {
                sourceValue = com.jayway.jsonpath.JsonPath.read(requestData, sourcePath);
            } catch (Exception e) {
                sourceValue = null;
            }

            if (sourceValue == null) continue;

            List<String> transformNames = new ArrayList<>();
            if (transforms != null) {
                for (int t = 0; t < transforms.size(); t++) {
                    transformNames.add(transforms.getString(t));
                }
            }

            Object transformedValue = transformValue(sourceValue, dataType, transformNames, transformRules, errors, sourcePath);

            setJsonPathValue(target, requestData, targetPath, transformedValue, direction);
        }
    }

    private void setJsonPathValue(JSONObject target, Object requestData, String targetPath,
                                  Object value, String direction) {
        String normalizedPath = targetPath.replaceFirst("^\\$\\.?", "");
        if (normalizedPath.isEmpty()) return;

        String[] parts = normalizedPath.split("\\.");
        JSONObject current = target;

        for (int i = 0; i < parts.length; i++) {
            if (i == parts.length - 1) {
                current.put(parts[i], value);
            } else {
                if (!current.containsKey(parts[i])) {
                    current.put(parts[i], new JSONObject());
                }
                current = current.getJSONObject(parts[i]);
            }
        }
    }

    private void executeListExtractionMappings(JSONObject config, JSONObject sourceData, JSONObject target,
                                               List<ErrorDetail> errors) {
        JSONArray listExtractionMappings = config.getJSONArray("listExtractionMappings");
        if (listExtractionMappings == null) return;

        JSONObject transformRules = config.getJSONObject("transformRules");

        for (int i = 0; i < listExtractionMappings.size(); i++) {
            JSONObject mapping = listExtractionMappings.getJSONObject(i);
            String type = mapping.getString("type");
            String sourcePath = mapping.getString("sourcePath");
            String targetPath = mapping.getString("targetPath");

            try {
                Object sourceArray = com.jayway.jsonpath.JsonPath.read(sourceData, sourcePath);

                if ("extractByIndex".equals(type)) {
                    int index = mapping.getIntValue("index");
                    JSONArray itemMappings = mapping.getJSONArray("itemMappings");
                    Object item = extractByIndex(sourceArray, index);
                    if (item != null && itemMappings != null) {
                        JSONObject mappedItem = mapListItem(item, itemMappings, transformRules, errors);
                        setJsonPathValue(target, null, targetPath, mappedItem, null);
                    } else if (item != null) {
                        setJsonPathValue(target, null, targetPath, item, null);
                    }
                } else if ("concatenateValues".equals(type)) {
                    String separator = mapping.getString("separator");
                    String result = concatenateValues(sourceArray, separator);
                    setJsonPathValue(target, null, targetPath, result, null);
                } else if ("concatenateWithFormat".equals(type)) {
                    String separator = mapping.getString("separator");
                    String formatTemplate = mapping.getString("formatTemplate");
                    String result = concatenateWithFormat(sourceArray, separator, formatTemplate);
                    setJsonPathValue(target, null, targetPath, result, null);
                } else if ("extractByCondition".equals(type)) {
                    String condition = mapping.getString("condition");
                    boolean takeFirst = mapping.getBooleanValue("takeFirst");
                    JSONArray itemMappings = mapping.getJSONArray("itemMappings");
                    Object filtered = extractByCondition(sourceArray, condition, takeFirst);
                    if (filtered != null && itemMappings != null) {
                        if (filtered instanceof List && !((List<?>) filtered).isEmpty()) {
                            Object firstItem = ((List<?>) filtered).get(0);
                            JSONObject mappedItem = mapListItem(firstItem, itemMappings, transformRules, errors);
                            setJsonPathValue(target, null, targetPath, mappedItem, null);
                        }
                    }
                }
            } catch (Exception e) {
                errors.add(new ErrorDetail(sourcePath, "extraction", "数组提取失败: " + e.getMessage(), null));
            }
        }
    }

    private Object extractByIndex(Object sourceArray, int index) {
        if (sourceArray instanceof JSONArray) {
            JSONArray arr = (JSONArray) sourceArray;
            if (index >= 0 && index < arr.size()) {
                return arr.get(index);
            } else if (index < 0 && index >= -arr.size()) {
                return arr.get(arr.size() + index);
            }
        }
        return null;
    }

    private String concatenateValues(Object sourceArray, String separator) {
        StringBuilder sb = new StringBuilder();
        if (sourceArray instanceof JSONArray) {
            JSONArray arr = (JSONArray) sourceArray;
            for (int i = 0; i < arr.size(); i++) {
                if (i > 0) sb.append(separator);
                sb.append(arr.get(i).toString());
            }
        }
        return sb.toString();
    }

    private String concatenateWithFormat(Object sourceArray, String separator, String formatTemplate) {
        StringBuilder sb = new StringBuilder();
        if (sourceArray instanceof JSONArray) {
            JSONArray arr = (JSONArray) sourceArray;
            for (int i = 0; i < arr.size(); i++) {
                if (i > 0) sb.append(separator);
                JSONObject item = arr.getJSONObject(i);
                String formatted = formatTemplate;
                for (String key : item.keySet()) {
                    formatted = formatted.replace("{" + key + "}", String.valueOf(item.get(key)));
                }
                sb.append(formatted);
            }
        }
        return sb.toString();
    }

    private Object extractByCondition(Object sourceArray, String condition, boolean takeFirst) {
        if (sourceArray instanceof JSONArray) {
            JSONArray arr = (JSONArray) sourceArray;
            JSONArray result = new JSONArray();
            String[] parts = condition.split("==");
            if (parts.length == 2) {
                String fieldPath = parts[0].trim().replace("$.", "");
                String expectedValue = parts[1].trim().replace("'", "");
                for (int i = 0; i < arr.size(); i++) {
                    JSONObject item = arr.getJSONObject(i);
                    Object fieldValue = item.get(fieldPath);
                    if (fieldValue != null && expectedValue.equals(String.valueOf(fieldValue))) {
                        result.add(item);
                        if (takeFirst) break;
                    }
                }
            }
            return takeFirst && !result.isEmpty() ? result.get(0) : result;
        }
        return null;
    }

    private JSONObject mapListItem(Object item, JSONArray itemMappings, JSONObject transformRules,
                                    List<ErrorDetail> errors) {
        JSONObject result = new JSONObject();
        if (!(item instanceof JSONObject)) return result;
        JSONObject itemObj = (JSONObject) item;

        for (int i = 0; i < itemMappings.size(); i++) {
            JSONObject mapping = itemMappings.getJSONObject(i);
            String source = mapping.getString("source").replace("$.", "");
            String target = mapping.getString("target").replace("$.", "");
            String dataType = mapping.getString("dataType");
            JSONArray transforms = mapping.getJSONArray("transforms");

            Object sourceValue = itemObj.get(source);
            if (sourceValue == null) continue;

            List<String> transformNames = new ArrayList<>();
            if (transforms != null) {
                for (int t = 0; t < transforms.size(); t++) {
                    transformNames.add(transforms.getString(t));
                }
            }

            Object transformedValue = transformValue(sourceValue, dataType, transformNames, transformRules, errors, source);
            result.put(target, transformedValue);
        }
        return result;
    }

    private void executeStringToListMappings(JSONObject config, JSONObject sourceData, JSONObject target,
                                              List<ErrorDetail> errors) {
        JSONArray stringToListMappings = config.getJSONArray("stringToListMappings");
        if (stringToListMappings == null) return;

        JSONObject transformRules = config.getJSONObject("transformRules");
        JSONObject enrichmentMappings = config.getJSONObject("enrichmentMappings");
        JSONObject dataSources = config.getJSONObject("dataSources");

        for (int i = 0; i < stringToListMappings.size(); i++) {
            JSONObject mapping = stringToListMappings.getJSONObject(i);
            String type = mapping.getString("type");
            String sourcePath = mapping.getString("sourcePath");
            String targetPath = mapping.getString("targetPath");

            try {
                Object sourceValue = com.jayway.jsonpath.JsonPath.read(sourceData, sourcePath);
                if (sourceValue == null) continue;
                String sourceStr = String.valueOf(sourceValue);

                JSONArray resultArray = new JSONArray();

                if ("stringSplitToList".equals(type)) {
                    String separator = mapping.getString("separator");
                    boolean trimItems = mapping.getBooleanValue("trimItems");
                    boolean removeEmpty = mapping.getBooleanValue("removeEmpty");

                    String[] parts = sourceStr.split(Pattern.quote(separator));
                    for (String part : parts) {
                        if (trimItems) part = part.trim();
                        if (removeEmpty && part.isEmpty()) continue;
                        resultArray.add(part);
                    }
                }

                JSONArray chainedMappings = mapping.getJSONArray("chainedMappings");
                if (chainedMappings != null) {
                    for (int c = 0; c < chainedMappings.size(); c++) {
                        JSONObject chained = chainedMappings.getJSONObject(c);
                        String chainedType = chained.getString("type");

                        if ("enrichListItems".equals(chainedType)) {
                            String enrichmentSource = chained.getString("enrichmentSource");
                            String keyField = chained.getString("keyField");
                            JSONArray targetFields = chained.getJSONArray("targetFields");

                            JSONArray enrichedArray = new JSONArray();
                            for (int j = 0; j < resultArray.size(); j++) {
                                String itemValue = resultArray.getString(j);
                                JSONObject enrichedItem = new JSONObject();
                                enrichedItem.put("partyName", itemValue);

                                JSONObject enrichmentConfig = enrichmentMappings.getJSONObject(enrichmentSource);
                                if (enrichmentConfig != null) {
                                    String dataSourceName = enrichmentConfig.getString("dataSource");
                                    JSONObject dataSourceConfig = dataSources.getJSONObject(dataSourceName);
                                    if (dataSourceConfig != null && "static".equals(dataSourceConfig.getString("type"))) {
                                        JSONObject staticData = dataSourceConfig.getJSONObject("staticData");
                                        if (staticData != null && staticData.containsKey(itemValue)) {
                                            JSONObject enriched = staticData.getJSONObject(itemValue);
                                            for (int f = 0; f < targetFields.size(); f++) {
                                                String fieldName = targetFields.getString(f);
                                                enrichedItem.put(fieldName, enriched.get(fieldName));
                                            }
                                        } else {
                                            JSONObject defaultValues = enrichmentConfig.getJSONObject("defaultValues");
                                            if (defaultValues != null) {
                                                for (int f = 0; f < targetFields.size(); f++) {
                                                    String fieldName = targetFields.getString(f);
                                                    enrichedItem.put(fieldName, defaultValues.get(fieldName));
                                                }
                                            }
                                        }
                                    }
                                }
                                enrichedArray.add(enrichedItem);
                            }
                            resultArray = enrichedArray;
                        }
                    }
                }

                setJsonPathValue(target, null, targetPath, resultArray, null);
            } catch (Exception e) {
                errors.add(new ErrorDetail(sourcePath, "stringSplit", "字符串转数组失败: " + e.getMessage(), null));
            }
        }
    }

    private Object transformValue(Object sourceValue, String dataType, List<String> transformNames,
                                   JSONObject transformRules, List<ErrorDetail> errors, String fieldName) {
        Object value = sourceValue;

        value = typeConversion(value, dataType, errors, fieldName);

        if (transformNames != null) {
            for (String transformName : transformNames) {
                value = transformEngine.apply(transformName, value, transformRules);
            }
        }

        return value;
    }

    private Object typeConversion(Object value, String dataType, List<ErrorDetail> errors, String fieldName) {
        if (value == null || dataType == null) return value;

        try {
            switch (dataType) {
                case "string":
                    return String.valueOf(value);
                case "integer":
                    if (value instanceof Number) {
                        return ((Number) value).intValue();
                    }
                    try {
                        return Integer.parseInt(String.valueOf(value).trim());
                    } catch (NumberFormatException e) {
                        errors.add(new ErrorDetail(fieldName, "typeConversion", "无法转换为Integer: " + value, value));
                        return value;
                    }
                case "decimal":
                    if (value instanceof Number) {
                        return new BigDecimal(String.valueOf(value)).setScale(2, RoundingMode.HALF_UP);
                    }
                    try {
                        return new BigDecimal(String.valueOf(value).trim()).setScale(2, RoundingMode.HALF_UP);
                    } catch (NumberFormatException e) {
                        errors.add(new ErrorDetail(fieldName, "typeConversion", "无法转换为Decimal: " + value, value));
                        return value;
                    }
                case "boolean":
                    if (value instanceof Boolean) return value;
                    return Boolean.parseBoolean(String.valueOf(value));
                case "date":
                    if (value instanceof Date) return value;
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                        return sdf.parse(String.valueOf(value));
                    } catch (Exception e) {
                        errors.add(new ErrorDetail(fieldName, "typeConversion", "无法转换为Date: " + value, value));
                        return value;
                    }
                default:
                    return value;
            }
        } catch (Exception e) {
            return value;
        }
    }
}
