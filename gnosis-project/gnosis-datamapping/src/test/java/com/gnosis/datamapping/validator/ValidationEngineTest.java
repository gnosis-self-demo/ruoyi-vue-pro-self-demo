package com.gnosis.datamapping.validator;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.datamapping.engine.ValidationError;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * ValidationEngine 完整单元测试
 * <p>
 * 测试所有校验规则类型：
 * - required: 必填校验
 * - maxLength / minLength: 长度校验
 * - pattern: 正则表达式校验
 * - min / max: 数值范围校验
 * - enum: 枚举范围校验
 * - dateFormat: 日期格式校验
 * - dateRange: 日期范围校验
 * - boolean: 布尔值校验
 * - integer: 整数校验
 * - decimalPrecision: 小数精度校验
 */
public class ValidationEngineTest {

    private ValidationEngine validationEngine;

    @Before
    public void setUp() {
        validationEngine = new ValidationEngine();
    }

    // ==================== required 校验 ====================

    @Test
    public void testRequired_NonEmptyString_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "required");
        ValidationError error = validationEngine.validate("required", rule, "test");
        assertNull(error);
    }

    @Test
    public void testRequired_Zero_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "required");
        ValidationError error = validationEngine.validate("required", rule, 0);
        assertNull(error);
    }

    @Test
    public void testRequired_False_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "required");
        ValidationError error = validationEngine.validate("required", rule, false);
        assertNull(error);
    }

    @Test
    public void testRequired_NullValue_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "required");
        ValidationError error = validationEngine.validate("required", rule, null);
        assertNotNull(error);
        assertEquals("required", error.getType());
        assertEquals("字段不能为空", error.getMessage());
    }

    @Test
    public void testRequired_EmptyString_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "required");
        ValidationError error = validationEngine.validate("required", rule, "");
        assertNotNull(error);
        assertEquals("required", error.getType());
    }

    @Test
    public void testRequired_WhitespaceOnly_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "required");
        ValidationError error = validationEngine.validate("required", rule, "   ");
        assertNotNull(error);
        assertEquals("required", error.getType());
    }

    // ==================== maxLength 校验 ====================

    @Test
    public void testMaxLength_WithinLimit_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "maxLength");
        rule.put("value", 10);
        ValidationError error = validationEngine.validate("maxLength", rule, "test");
        assertNull(error);
    }

    @Test
    public void testMaxLength_AtLimit_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "maxLength");
        rule.put("value", 4);
        ValidationError error = validationEngine.validate("maxLength", rule, "test");
        assertNull(error);
    }

    @Test
    public void testMaxLength_ExceedLimit_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "maxLength");
        rule.put("value", 3);
        ValidationError error = validationEngine.validate("maxLength", rule, "test");
        assertNotNull(error);
        assertEquals("maxLength", error.getType());
        assertTrue(error.getMessage().contains("3"));
    }

    @Test
    public void testMaxLength_NullValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "maxLength");
        rule.put("value", 5);
        ValidationError error = validationEngine.validate("maxLength", rule, null);
        assertNull(error);
    }

    @Test
    public void testMaxLength_NumberValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "maxLength");
        rule.put("value", 3);
        // 123 转字符串后长度为3
        ValidationError error = validationEngine.validate("maxLength", rule, 123);
        assertNull(error);
    }

    // ==================== minLength 校验 ====================

    @Test
    public void testMinLength_WithinLimit_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "minLength");
        rule.put("value", 3);
        ValidationError error = validationEngine.validate("minLength", rule, "test");
        assertNull(error);
    }

    @Test
    public void testMinLength_AtLimit_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "minLength");
        rule.put("value", 4);
        ValidationError error = validationEngine.validate("minLength", rule, "test");
        assertNull(error);
    }

    @Test
    public void testMinLength_BelowLimit_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "minLength");
        rule.put("value", 5);
        ValidationError error = validationEngine.validate("minLength", rule, "test");
        assertNotNull(error);
        assertEquals("minLength", error.getType());
        assertTrue(error.getMessage().contains("5"));
    }

    @Test
    public void testMinLength_NullValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "minLength");
        rule.put("value", 5);
        ValidationError error = validationEngine.validate("minLength", rule, null);
        assertNull(error);
    }

    // ==================== pattern 校验 ====================

    @Test
    public void testPattern_EmailValid_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "pattern");
        rule.put("value", "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        ValidationError error = validationEngine.validate("pattern", rule, "test@example.com");
        assertNull(error);
    }

    @Test
    public void testPattern_EmailInvalid_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "pattern");
        rule.put("value", "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
        ValidationError error = validationEngine.validate("pattern", rule, "not-an-email");
        assertNotNull(error);
        assertEquals("pattern", error.getType());
    }

    @Test
    public void testPattern_PhoneValid_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "pattern");
        rule.put("value", "^1[3-9]\\d{9}$");
        ValidationError error = validationEngine.validate("pattern", rule, "13800138000");
        assertNull(error);
    }

    @Test
    public void testPattern_PhoneInvalid_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "pattern");
        rule.put("value", "^1[3-9]\\d{9}$");
        ValidationError error = validationEngine.validate("pattern", rule, "12345678901");
        assertNotNull(error);
        assertEquals("pattern", error.getType());
    }

    @Test
    public void testPattern_NullValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "pattern");
        rule.put("value", "^test$");
        ValidationError error = validationEngine.validate("pattern", rule, null);
        assertNull(error);
    }

    // ==================== min 校验 ====================

    @Test
    public void testMin_AboveMin_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "min");
        rule.put("value", new BigDecimal("10"));
        ValidationError error = validationEngine.validate("min", rule, 15);
        assertNull(error);
    }

    @Test
    public void testMin_AtMin_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "min");
        rule.put("value", new BigDecimal("10"));
        ValidationError error = validationEngine.validate("min", rule, 10);
        assertNull(error);
    }

    @Test
    public void testMin_BelowMin_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "min");
        rule.put("value", new BigDecimal("10"));
        ValidationError error = validationEngine.validate("min", rule, 5);
        assertNotNull(error);
        assertEquals("min", error.getType());
    }

    @Test
    public void testMin_StringValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "min");
        rule.put("value", new BigDecimal("10"));
        ValidationError error = validationEngine.validate("min", rule, "15");
        assertNull(error);
    }

    @Test
    public void testMin_InvalidFormat_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "min");
        rule.put("value", new BigDecimal("10"));
        ValidationError error = validationEngine.validate("min", rule, "not-a-number");
        assertNotNull(error);
        assertEquals("min", error.getType());
    }

    @Test
    public void testMin_NullValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "min");
        rule.put("value", new BigDecimal("10"));
        ValidationError error = validationEngine.validate("min", rule, null);
        assertNull(error);
    }

    // ==================== max 校验 ====================

    @Test
    public void testMax_BelowMax_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "max");
        rule.put("value", new BigDecimal("100"));
        ValidationError error = validationEngine.validate("max", rule, 50);
        assertNull(error);
    }

    @Test
    public void testMax_AtMax_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "max");
        rule.put("value", new BigDecimal("100"));
        ValidationError error = validationEngine.validate("max", rule, 100);
        assertNull(error);
    }

    @Test
    public void testMax_ExceedMax_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "max");
        rule.put("value", new BigDecimal("100"));
        ValidationError error = validationEngine.validate("max", rule, 150);
        assertNotNull(error);
        assertEquals("max", error.getType());
    }

    @Test
    public void testMax_NullValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "max");
        rule.put("value", new BigDecimal("100"));
        ValidationError error = validationEngine.validate("max", rule, null);
        assertNull(error);
    }

    // ==================== enum 校验 ====================

    @Test
    public void testEnum_ValidValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "enum");
        JSONArray values = new JSONArray();
        values.add("active");
        values.add("inactive");
        values.add("pending");
        rule.put("values", values);
        ValidationError error = validationEngine.validate("enum", rule, "active");
        assertNull(error);
    }

    @Test
    public void testEnum_InvalidValue_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "enum");
        JSONArray values = new JSONArray();
        values.add("active");
        values.add("inactive");
        values.add("pending");
        rule.put("values", values);
        ValidationError error = validationEngine.validate("enum", rule, "unknown");
        assertNotNull(error);
        assertEquals("enum", error.getType());
        assertEquals("值不在枚举范围内", error.getMessage());
    }

    @Test
    public void testEnum_NumericValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "enum");
        JSONArray values = new JSONArray();
        values.add(1);
        values.add(2);
        values.add(3);
        rule.put("values", values);
        ValidationError error = validationEngine.validate("enum", rule, 2);
        assertNull(error);
    }

    @Test
    public void testEnum_NullValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "enum");
        JSONArray values = new JSONArray();
        values.add("active");
        rule.put("values", values);
        ValidationError error = validationEngine.validate("enum", rule, null);
        assertNull(error);
    }

    // ==================== dateFormat 校验 ====================

    @Test
    public void testDateFormat_ValidDate_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateFormat");
        rule.put("value", "yyyy-MM-dd");
        ValidationError error = validationEngine.validate("dateFormat", rule, "2026-04-23");
        assertNull(error);
    }

    @Test
    public void testDateFormat_InvalidDate_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateFormat");
        rule.put("value", "yyyy-MM-dd");
        ValidationError error = validationEngine.validate("dateFormat", rule, "not-a-date");
        assertNotNull(error);
        assertEquals("dateFormat", error.getType());
    }

    @Test
    public void testDateFormat_DifferentFormat_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateFormat");
        rule.put("value", "yyyy/MM/dd");
        ValidationError error = validationEngine.validate("dateFormat", rule, "2026/04/23");
        assertNull(error);
    }

    @Test
    public void testDateFormat_WrongFormat_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateFormat");
        rule.put("value", "yyyy-MM-dd");
        ValidationError error = validationEngine.validate("dateFormat", rule, "2026/04/23");
        assertNotNull(error);
        assertEquals("dateFormat", error.getType());
    }

    @Test
    public void testDateFormat_NullValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateFormat");
        rule.put("value", "yyyy-MM-dd");
        ValidationError error = validationEngine.validate("dateFormat", rule, null);
        assertNull(error);
    }

    // ==================== dateRange 校验 ====================

    @Test
    public void testDateRange_WithinRange_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateRange");
        rule.put("min", "2026-01-01");
        rule.put("max", "2026-12-31");
        ValidationError error = validationEngine.validate("dateRange", rule, "2026-06-15");
        assertNull(error);
    }

    @Test
    public void testDateRange_AtMinBoundary_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateRange");
        rule.put("min", "2026-01-01");
        rule.put("max", "2026-12-31");
        ValidationError error = validationEngine.validate("dateRange", rule, "2026-01-01");
        assertNull(error);
    }

    @Test
    public void testDateRange_AtMaxBoundary_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateRange");
        rule.put("min", "2026-01-01");
        rule.put("max", "2026-12-31");
        ValidationError error = validationEngine.validate("dateRange", rule, "2026-12-31");
        assertNull(error);
    }

    @Test
    public void testDateRange_BeforeMin_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateRange");
        rule.put("min", "2026-01-01");
        rule.put("max", "2026-12-31");
        ValidationError error = validationEngine.validate("dateRange", rule, "2025-12-31");
        assertNotNull(error);
        assertEquals("dateRange", error.getType());
        assertTrue(error.getMessage().contains("2026-01-01"));
    }

    @Test
    public void testDateRange_AfterMax_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateRange");
        rule.put("min", "2026-01-01");
        rule.put("max", "2026-12-31");
        ValidationError error = validationEngine.validate("dateRange", rule, "2027-01-01");
        assertNotNull(error);
        assertEquals("dateRange", error.getType());
        assertTrue(error.getMessage().contains("2026-12-31"));
    }

    @Test
    public void testDateRange_OnlyMin_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateRange");
        rule.put("min", "2026-01-01");
        ValidationError error = validationEngine.validate("dateRange", rule, "2027-06-15");
        assertNull(error);
    }

    @Test
    public void testDateRange_OnlyMax_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateRange");
        rule.put("max", "2026-12-31");
        ValidationError error = validationEngine.validate("dateRange", rule, "2025-01-01");
        assertNull(error);
    }

    @Test
    public void testDateRange_InvalidDate_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateRange");
        rule.put("min", "2026-01-01");
        rule.put("max", "2026-12-31");
        ValidationError error = validationEngine.validate("dateRange", rule, "not-a-date");
        assertNotNull(error);
        assertEquals("dateRange", error.getType());
    }

    @Test
    public void testDateRange_NullValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "dateRange");
        rule.put("min", "2026-01-01");
        rule.put("max", "2026-12-31");
        ValidationError error = validationEngine.validate("dateRange", rule, null);
        assertNull(error);
    }

    // ==================== boolean 校验 ====================

    @Test
    public void testBoolean_TrueValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "boolean");
        ValidationError error = validationEngine.validate("boolean", rule, true);
        assertNull(error);
    }

    @Test
    public void testBoolean_FalseValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "boolean");
        ValidationError error = validationEngine.validate("boolean", rule, false);
        assertNull(error);
    }

    @Test
    public void testBoolean_StringTrue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "boolean");
        ValidationError error = validationEngine.validate("boolean", rule, "true");
        assertNull(error);
    }

    @Test
    public void testBoolean_StringFalse_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "boolean");
        ValidationError error = validationEngine.validate("boolean", rule, "false");
        assertNull(error);
    }

    @Test
    public void testBoolean_String1_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "boolean");
        ValidationError error = validationEngine.validate("boolean", rule, "1");
        assertNull(error);
    }

    @Test
    public void testBoolean_String0_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "boolean");
        ValidationError error = validationEngine.validate("boolean", rule, "0");
        assertNull(error);
    }

    @Test
    public void testBoolean_InvalidString_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "boolean");
        ValidationError error = validationEngine.validate("boolean", rule, "yes");
        assertNotNull(error);
        assertEquals("boolean", error.getType());
        assertEquals("值必须为布尔类型", error.getMessage());
    }

    @Test
    public void testBoolean_NullValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "boolean");
        ValidationError error = validationEngine.validate("boolean", rule, null);
        assertNull(error);
    }

    // ==================== integer 校验 ====================

    @Test
    public void testInteger_ValidInt_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "integer");
        ValidationError error = validationEngine.validate("integer", rule, 42);
        assertNull(error);
    }

    @Test
    public void testInteger_StringInt_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "integer");
        ValidationError error = validationEngine.validate("integer", rule, "123");
        assertNull(error);
    }

    @Test
    public void testInteger_NegativeInt_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "integer");
        ValidationError error = validationEngine.validate("integer", rule, -100);
        assertNull(error);
    }

    @Test
    public void testInteger_Zero_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "integer");
        ValidationError error = validationEngine.validate("integer", rule, 0);
        assertNull(error);
    }

    @Test
    public void testInteger_Decimal_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "integer");
        ValidationError error = validationEngine.validate("integer", rule, "12.5");
        assertNotNull(error);
        assertEquals("integer", error.getType());
        assertEquals("值必须为整数", error.getMessage());
    }

    @Test
    public void testInteger_NonNumeric_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "integer");
        ValidationError error = validationEngine.validate("integer", rule, "abc");
        assertNotNull(error);
        assertEquals("integer", error.getType());
    }

    @Test
    public void testInteger_NullValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "integer");
        ValidationError error = validationEngine.validate("integer", rule, null);
        assertNull(error);
    }

    // ==================== decimalPrecision 校验 ====================

    @Test
    public void testDecimalPrecision_WithinPrecision_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "decimalPrecision");
        rule.put("value", 2);
        ValidationError error = validationEngine.validate("decimalPrecision", rule, "123.45");
        assertNull(error);
    }

    @Test
    public void testDecimalPrecision_AtPrecision_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "decimalPrecision");
        rule.put("value", 2);
        ValidationError error = validationEngine.validate("decimalPrecision", rule, "123.00");
        assertNull(error);
    }

    @Test
    public void testDecimalPrecision_ExceedPrecision_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "decimalPrecision");
        rule.put("value", 2);
        ValidationError error = validationEngine.validate("decimalPrecision", rule, "123.456");
        assertNotNull(error);
        assertEquals("decimalPrecision", error.getType());
        assertTrue(error.getMessage().contains("2"));
    }

    @Test
    public void testDecimalPrecision_Integer_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "decimalPrecision");
        rule.put("value", 2);
        ValidationError error = validationEngine.validate("decimalPrecision", rule, "100");
        assertNull(error);
    }

    @Test
    public void testDecimalPrecision_InvalidNumber_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "decimalPrecision");
        rule.put("value", 2);
        ValidationError error = validationEngine.validate("decimalPrecision", rule, "not-a-number");
        assertNotNull(error);
        assertEquals("decimalPrecision", error.getType());
    }

    @Test
    public void testDecimalPrecision_NullValue_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "decimalPrecision");
        rule.put("value", 2);
        ValidationError error = validationEngine.validate("decimalPrecision", rule, null);
        assertNull(error);
    }

    // ==================== 未知规则类型 ====================

    @Test
    public void testUnknownRuleType_ReturnsNull() {
        JSONObject rule = new JSONObject();
        rule.put("type", "unknownRule");
        ValidationError error = validationEngine.validate("unknownRule", rule, "value");
        assertNull(error);
    }

    // ==================== ValidationError 实体测试 ====================

    @Test
    public void testValidationError_GettersSetters() {
        ValidationError error = new ValidationError("test", "Test error");
        assertEquals("test", error.getType());
        assertEquals("Test error", error.getMessage());

        error.setType("updated");
        error.setMessage("Updated error");
        assertEquals("updated", error.getType());
        assertEquals("Updated error", error.getMessage());
    }

    // ==================== 边界条件测试 ====================

    @Test
    public void testMaxLength_ZeroLength() {
        JSONObject rule = new JSONObject();
        rule.put("type", "maxLength");
        rule.put("value", 0);
        ValidationError error = validationEngine.validate("maxLength", rule, "test");
        assertNotNull(error);
    }

    @Test
    public void testMinLength_ZeroLength() {
        JSONObject rule = new JSONObject();
        rule.put("type", "minLength");
        rule.put("value", 0);
        ValidationError error = validationEngine.validate("minLength", rule, "test");
        assertNull(error);
    }

    @Test
    public void testEnum_EmptyValuesArray_Fail() {
        JSONObject rule = new JSONObject();
        rule.put("type", "enum");
        rule.put("values", new JSONArray());
        ValidationError error = validationEngine.validate("enum", rule, "any");
        assertNotNull(error);
    }

    @Test
    public void testEnum_NullValuesArray_Pass() {
        JSONObject rule = new JSONObject();
        rule.put("type", "enum");
        ValidationError error = validationEngine.validate("enum", rule, "any");
        assertNull(error);
    }
}
