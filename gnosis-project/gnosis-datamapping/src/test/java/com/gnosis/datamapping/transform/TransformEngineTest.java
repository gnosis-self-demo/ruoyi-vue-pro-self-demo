package com.gnosis.datamapping.transform;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * TransformEngine 完整单元测试
 * <p>
 * 测试所有转换类型：
 * - 内置转换: trim, uppercase, lowercase, round, dateFormat
 * - 函数转换: String.trim(), String.toUpperCase(), String.toLowerCase(), Math.round
 * - Lookup转换: 大小写敏感/不敏感的映射查找
 * - Regex转换: 正则表达式替换
 * - 链式规则: 多个转换按顺序执行
 */
public class TransformEngineTest {

    private TransformEngine transformEngine;

    @Before
    public void setUp() {
        transformEngine = new TransformEngine();
    }

    // ==================== 内置转换: trim ====================

    @Test
    public void testBuiltin_Trim_WithSpaces() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("trim", "  hello world  ", transformRules);
        assertEquals("hello world", result);
    }

    @Test
    public void testBuiltin_Trim_NoSpaces() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("trim", "hello", transformRules);
        assertEquals("hello", result);
    }

    @Test
    public void testBuiltin_Trim_OnlySpaces() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("trim", "     ", transformRules);
        assertEquals("", result);
    }

    @Test
    public void testBuiltin_Trim_NullValue() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("trim", null, transformRules);
        assertNull(result);
    }

    // ==================== 内置转换: uppercase ====================

    @Test
    public void testBuiltin_Uppercase_LowercaseInput() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("uppercase", "hello", transformRules);
        assertEquals("HELLO", result);
    }

    @Test
    public void testBuiltin_Uppercase_MixedCaseInput() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("uppercase", "Hello World", transformRules);
        assertEquals("HELLO WORLD", result);
    }

    @Test
    public void testBuiltin_Uppercase_AlreadyUppercase() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("uppercase", "HELLO", transformRules);
        assertEquals("HELLO", result);
    }

    @Test
    public void testBuiltin_Uppercase_NullValue() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("uppercase", null, transformRules);
        assertNull(result);
    }

    // ==================== 内置转换: lowercase ====================

    @Test
    public void testBuiltin_Lowercase_UppercaseInput() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("lowercase", "HELLO", transformRules);
        assertEquals("hello", result);
    }

    @Test
    public void testBuiltin_Lowercase_MixedCaseInput() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("lowercase", "Hello World", transformRules);
        assertEquals("hello world", result);
    }

    @Test
    public void testBuiltin_Lowercase_AlreadyLowercase() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("lowercase", "hello", transformRules);
        assertEquals("hello", result);
    }

    @Test
    public void testBuiltin_Lowercase_NullValue() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("lowercase", null, transformRules);
        assertNull(result);
    }

    // ==================== 内置转换: round ====================

    @Test
    public void testBuiltin_Round_DefaultPrecision() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("round", "123.4567", transformRules);
        BigDecimal bd = (BigDecimal) result;
        assertEquals(0, bd.compareTo(new BigDecimal("123.46")));
    }

    @Test
    public void testBuiltin_Round_WithPrecision() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("round(3)", "123.45678", transformRules);
        BigDecimal bd = (BigDecimal) result;
        assertEquals(0, bd.compareTo(new BigDecimal("123.457")));
    }

    @Test
    public void testBuiltin_Round_PrecisionZero() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("round(0)", "123.456", transformRules);
        BigDecimal bd = (BigDecimal) result;
        assertEquals(0, bd.compareTo(new BigDecimal("123")));
    }

    @Test
    public void testBuiltin_Round_HalfUp() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("round", "123.455", transformRules);
        BigDecimal bd = (BigDecimal) result;
        // HALF_UP: 123.455 -> 123.46
        assertEquals(0, bd.compareTo(new BigDecimal("123.46")));
    }

    @Test
    public void testBuiltin_Round_InvalidInput() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("round", "not-a-number", transformRules);
        assertEquals("not-a-number", result);
    }

    @Test
    public void testBuiltin_Round_NullValue() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("round", null, transformRules);
        assertNull(result);
    }

    // ==================== 内置转换: dateFormat ====================

    @Test
    public void testBuiltin_DateFormat_DefaultFormat() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("dateFormat", "2026-04-23", transformRules);
        assertEquals("2026-04-23", result);
    }

    @Test
    public void testBuiltin_DateFormat_CustomFormat() {
        JSONObject transformRules = null;
        // Input must match the format for parsing to work
        Object result = transformEngine.apply("dateFormat:yyyy/MM/dd", "2026/04/23", transformRules);
        assertEquals("2026/04/23", result);
    }

    @Test
    public void testBuiltin_DateFormat_InvalidInput() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("dateFormat", "not-a-date", transformRules);
        assertEquals("not-a-date", result);
    }

    @Test
    public void testBuiltin_DateFormat_NullValue() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("dateFormat", null, transformRules);
        assertNull(result);
    }

    // ==================== 内置转换: Unknown ====================

    @Test
    public void testBuiltin_UnknownTransform_ReturnsOriginalValue() {
        JSONObject transformRules = null;
        Object result = transformEngine.apply("unknownTransform", "value", transformRules);
        assertEquals("value", result);
    }

    // ==================== 函数转换 ====================

    @Test
    public void testFunction_StringTrim() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "function");
        rule.put("function", "String.trim()");
        transformRules.put("myTrim", rule);

        Object result = transformEngine.apply("myTrim", "  hello  ", transformRules);
        assertEquals("hello", result);
    }

    @Test
    public void testFunction_StringToUpperCase() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "function");
        rule.put("function", "String.toUpperCase()");
        transformRules.put("myUpper", rule);

        Object result = transformEngine.apply("myUpper", "hello", transformRules);
        assertEquals("HELLO", result);
    }

    @Test
    public void testFunction_StringToLowerCase() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "function");
        rule.put("function", "String.toLowerCase()");
        transformRules.put("myLower", rule);

        Object result = transformEngine.apply("myLower", "HELLO", transformRules);
        assertEquals("hello", result);
    }

    @Test
    public void testFunction_MathRound_DefaultPrecision() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "function");
        rule.put("function", "Math.round");
        transformRules.put("myRound", rule);

        Object result = transformEngine.apply("myRound", "123.456", transformRules);
        BigDecimal bd = (BigDecimal) result;
        assertEquals(0, bd.compareTo(new BigDecimal("123.46")));
    }

    @Test
    public void testFunction_MathRound_WithPrecision() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "function");
        rule.put("function", "Math.round");
        JSONObject params = new JSONObject();
        params.put("precision", 3);
        rule.put("parameters", params);
        transformRules.put("myRound3", rule);

        Object result = transformEngine.apply("myRound3", "123.4567", transformRules);
        BigDecimal bd = (BigDecimal) result;
        assertEquals(0, bd.compareTo(new BigDecimal("123.457")));
    }

    @Test
    public void testFunction_UnknownFunction_ReturnsOriginalValue() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "function");
        rule.put("function", "Unknown.function()");
        transformRules.put("unknown", rule);

        Object result = transformEngine.apply("unknown", "value", transformRules);
        assertEquals("value", result);
    }

    @Test
    public void testFunction_NullValue() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "function");
        rule.put("function", "String.trim()");
        transformRules.put("myTrim", rule);

        Object result = transformEngine.apply("myTrim", null, transformRules);
        assertNull(result);
    }

    // ==================== Lookup 转换 ====================

    @Test
    public void testLookup_CaseSensitive_Found() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "lookup");
        JSONObject mapping = new JSONObject();
        mapping.put("CN", "China");
        mapping.put("US", "United States");
        rule.put("mapping", mapping);
        rule.put("ignoreCase", false);
        transformRules.put("countryLookup", rule);

        Object result = transformEngine.apply("countryLookup", "CN", transformRules);
        assertEquals("China", result);
    }

    @Test
    public void testLookup_CaseSensitive_NotFound() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "lookup");
        JSONObject mapping = new JSONObject();
        mapping.put("CN", "China");
        mapping.put("US", "United States");
        rule.put("mapping", mapping);
        rule.put("ignoreCase", false);
        rule.put("defaultValue", "Unknown");
        transformRules.put("countryLookup", rule);

        Object result = transformEngine.apply("countryLookup", "cn", transformRules);
        assertEquals("Unknown", result);
    }

    @Test
    public void testLookup_CaseInsensitive_Found() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "lookup");
        JSONObject mapping = new JSONObject();
        mapping.put("CN", "China");
        mapping.put("US", "United States");
        rule.put("mapping", mapping);
        rule.put("ignoreCase", true);
        transformRules.put("countryLookup", rule);

        Object result = transformEngine.apply("countryLookup", "cn", transformRules);
        assertEquals("China", result);
    }

    @Test
    public void testLookup_CaseInsensitive_MixedCase() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "lookup");
        JSONObject mapping = new JSONObject();
        mapping.put("CN", "China");
        mapping.put("US", "United States");
        rule.put("mapping", mapping);
        rule.put("ignoreCase", true);
        transformRules.put("countryLookup", rule);

        Object result = transformEngine.apply("countryLookup", "Cn", transformRules);
        assertEquals("China", result);
    }

    @Test
    public void testLookup_NotFound_NoDefault() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "lookup");
        JSONObject mapping = new JSONObject();
        mapping.put("CN", "China");
        rule.put("mapping", mapping);
        rule.put("ignoreCase", false);
        transformRules.put("countryLookup", rule);

        Object result = transformEngine.apply("countryLookup", "UK", transformRules);
        assertNull(result);
    }

    @Test
    public void testLookup_NullValue() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "lookup");
        JSONObject mapping = new JSONObject();
        mapping.put("CN", "China");
        rule.put("mapping", mapping);
        transformRules.put("countryLookup", rule);

        Object result = transformEngine.apply("countryLookup", null, transformRules);
        assertNull(result);
    }

    @Test
    public void testLookup_NumericValue() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "lookup");
        JSONObject mapping = new JSONObject();
        mapping.put("1", "One");
        mapping.put("2", "Two");
        rule.put("mapping", mapping);
        rule.put("ignoreCase", false);
        transformRules.put("numberLookup", rule);

        Object result = transformEngine.apply("numberLookup", 1, transformRules);
        assertEquals("One", result);
    }

    // ==================== Regex 转换 ====================

    @Test
    public void testRegex_ReplaceAll() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "regex");
        rule.put("pattern", "\\s+");
        rule.put("replacement", "-");
        transformRules.put("spaceToDash", rule);

        Object result = transformEngine.apply("spaceToDash", "hello   world  test", transformRules);
        assertEquals("hello-world-test", result);
    }

    @Test
    public void testRegex_RemoveDigits() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "regex");
        rule.put("pattern", "\\d");
        rule.put("replacement", "");
        transformRules.put("removeDigits", rule);

        Object result = transformEngine.apply("removeDigits", "abc123def456", transformRules);
        assertEquals("abcdef", result);
    }

    @Test
    public void testRegex_NoMatch_ReturnsOriginal() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "regex");
        rule.put("pattern", "XYZ");
        rule.put("replacement", "ABC");
        transformRules.put("noMatch", rule);

        Object result = transformEngine.apply("noMatch", "hello world", transformRules);
        assertEquals("hello world", result);
    }

    @Test
    public void testRegex_NullPattern_ReturnsOriginal() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "regex");
        rule.put("replacement", "ABC");
        transformRules.put("nullPattern", rule);

        Object result = transformEngine.apply("nullPattern", "hello", transformRules);
        assertEquals("hello", result);
    }

    @Test
    public void testRegex_NullReplacement_ReturnsOriginal() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "regex");
        rule.put("pattern", "test");
        transformRules.put("nullReplacement", rule);

        Object result = transformEngine.apply("nullReplacement", "hello test world", transformRules);
        assertEquals("hello test world", result);
    }

    @Test
    public void testRegex_NullValue() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "regex");
        rule.put("pattern", "test");
        rule.put("replacement", "replaced");
        transformRules.put("myRegex", rule);

        Object result = transformEngine.apply("myRegex", null, transformRules);
        assertNull(result);
    }

    // ==================== 未知规则类型 ====================

    @Test
    public void testUnknownTransformType_ReturnsOriginalValue() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "unknownType");
        transformRules.put("unknown", rule);

        Object result = transformEngine.apply("unknown", "value", transformRules);
        assertEquals("value", result);
    }

    // ==================== 规则不存在时回退到内置转换 ====================

    @Test
    public void testRuleNotFound_FallsBackToBuiltin() {
        // "trim" 不在 transformRules 中，应回退到内置 trim
        JSONObject transformRules = new JSONObject();
        Object result = transformEngine.apply("trim", "  hello  ", transformRules);
        assertEquals("hello", result);
    }

    @Test
    public void testRuleNotFound_FallsBackToBuiltin_Uppercase() {
        JSONObject transformRules = new JSONObject();
        Object result = transformEngine.apply("uppercase", "hello", transformRules);
        assertEquals("HELLO", result);
    }

    @Test
    public void testRuleNotFound_FallsBackToBuiltin_Unknown() {
        // "unknownTransform" 既不在 transformRules 中，也不是内置转换
        JSONObject transformRules = new JSONObject();
        Object result = transformEngine.apply("unknownTransform", "value", transformRules);
        assertEquals("value", result);
    }

    // ==================== 综合测试 ====================

    @Test
    public void testCombined_MultipleTransformsSequential() {
        // 模拟 MappingEngine 中链式调用多个 transform
        JSONObject transformRules = new JSONObject();

        Object value = "  HELLO WORLD  ";

        // 第1个转换: trim
        value = transformEngine.apply("trim", value, transformRules);
        assertEquals("HELLO WORLD", value);

        // 第2个转换: lowercase
        value = transformEngine.apply("lowercase", value, transformRules);
        assertEquals("hello world", value);
    }

    @Test
    public void testCombined_FunctionThenLookup() {
        JSONObject transformRules = new JSONObject();
        JSONObject rule = new JSONObject();
        rule.put("type", "lookup");
        JSONObject mapping = new JSONObject();
        mapping.put("hello", "greeting");
        mapping.put("world", "planet");
        rule.put("mapping", mapping);
        rule.put("ignoreCase", false);
        transformRules.put("wordLookup", rule);

        // 先 trim，再 lookup
        Object value = "  hello  ";
        value = transformEngine.apply("trim", value, transformRules);
        assertEquals("hello", value);

        value = transformEngine.apply("wordLookup", value, transformRules);
        assertEquals("greeting", value);
    }
}
