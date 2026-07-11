package com.gnosis.notice.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 模板解析工具
 * 支持 ${variableName} 格式的变量占位符
 */
public class TemplateParser {

    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^}]+)\\}");

    /**
     * 解析模板
     * @param template 模板内容
     * @param params 参数映射
     * @return 解析后的内容
     */
    public static String parse(String template, Map<String, Object> params) {
        if (template == null || template.isEmpty()) {
            return template;
        }
        if (params == null || params.isEmpty()) {
            return template;
        }

        Matcher matcher = PATTERN.matcher(template);
        StringBuffer sb = new StringBuffer();

        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = params.get(key);
            String replacement = value != null ? value.toString() : "";
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * 提取模板中的变量名
     * @param template 模板内容
     * @return 变量名数组
     */
    public static String[] extractVariables(String template) {
        if (template == null || template.isEmpty()) {
            return new String[0];
        }

        // Java 8 compatible
        List<String> variables = new ArrayList<>();
        Matcher matcher = PATTERN.matcher(template);
        while (matcher.find()) {
            String varName = matcher.group(1);
            if (!variables.contains(varName)) {
                variables.add(varName);
            }
        }
        return variables.toArray(new String[0]);
    }
}
