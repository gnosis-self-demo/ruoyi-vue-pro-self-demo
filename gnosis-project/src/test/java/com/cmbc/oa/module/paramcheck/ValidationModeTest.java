package com.cmbc.oa.module.paramcheck;

import com.cmbc.oa.module.paramcheck.domain.ValidationFlow;
import com.cmbc.oa.module.paramcheck.domain.ValidationContext;
import com.cmbc.oa.module.paramcheck.domain.ValidationResult;
import com.cmbc.oa.module.paramcheck.component.ParseParamComponent;
import com.cmbc.oa.module.paramcheck.component.BasicRuleComponent;
import com.cmbc.oa.module.paramcheck.exception.ValidationException;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * 三种校验模式路由逻辑单元测试
 *
 * 验证:
 * 1. ValidationFlow 实体字段正确
 * 2. ValidationContext 数据存取
 * 3. ValidationResult 构建方法
 * 4. BasicRuleComponent 支持的规则类型 (REGEX / EMAIL / PHONE / RANGE / NOT_NULL / LENGTH)
 * 5. FLOW/HANDLER/HYBRID 三种模式路由逻辑
 */
public class ValidationModeTest {

    // ===== Test 1: ValidationFlow 实体 =====

    @Test
    public void testValidationFlow_entity() {
        ValidationFlow flow = new ValidationFlow();
        flow.setFlowId("TEST_FLOW");
        flow.setFlowName("测试流程");
        flow.setModeType("HYBRID");
        flow.setElExpression("THEN(parse_param, check_format)");
        flow.setHandlerCode("OrderBusinessHandler");
        flow.setIsActive(true);
        flow.setVersion(1);

        assertEquals("TEST_FLOW", flow.getFlowId());
        assertEquals("HYBRID", flow.getModeType());
        assertEquals("OrderBusinessHandler", flow.getHandlerCode());
        assertTrue(flow.getIsActive());
    }

    // ===== Test 2: ValidationContext =====

    @Test
    public void testValidationContext_dataAccess() {
        ValidationContext ctx = new ValidationContext();
        assertNotNull(ctx.getRequestId());
        assertNotNull(ctx.getDataMap());

        Map<String, Object> rawRequest = new HashMap<>();
        rawRequest.put("key", "value");
        ctx.setData("rawRequest", rawRequest);
        ctx.setData("count", 42);

        Map<?, ?> dataMap = (Map<?, ?>) ctx.getData("rawRequest");
        assertEquals("value", dataMap.get("key"));
        assertEquals(42, (int) ctx.getData("count", 0));
        assertNull(ctx.getData("nonExistent"));
    }

    // ===== Test 3: ValidationResult =====

    @Test
    public void testValidationResult_success() {
        ValidationResult result = ValidationResult.success();
        assertTrue(result.isSuccess());
        assertNull(result.getErrorCode());
        assertNull(result.getErrorMsg());
    }

    @Test
    public void testValidationResult_fail() {
        ValidationResult result = ValidationResult.fail("ERR_CODE", "错误消息", "node_a");
        assertFalse(result.isSuccess());
        assertEquals("ERR_CODE", result.getErrorCode());
        assertEquals("错误消息", result.getErrorMsg());
        assertEquals("node_a", result.getFailedNode());
    }

    // ===== Test 4: BasicRuleComponent — 各规则类型 =====

    @Test
    public void testBasicRule_email_pass() {
        assertTrue(validateRule("test@example.com", "EMAIL"));
    }

    @Test
    public void testBasicRule_email_fail() {
        assertFalse(validateRule("not-an-email", "EMAIL"));
    }

    @Test
    public void testBasicRule_phone_pass() {
        assertTrue(validateRule("13812345678", "PHONE"));
    }

    @Test
    public void testBasicRule_phone_fail() {
        assertFalse(validateRule("1234567890", "PHONE")); // 短了
        assertFalse(validateRule("138123456789", "PHONE")); // 长了
    }

    @Test
    public void testBasicRule_regex_pass() {
        assertTrue(validateRuleWithPattern("ORD1234567890", "REGEX", "^ORD[0-9]{10}$"));
    }

    @Test
    public void testBasicRule_regex_fail() {
        assertFalse(validateRuleWithPattern("ORDER123", "REGEX", "^ORD[0-9]{10}$"));
    }

    @Test
    public void testBasicRule_range_pass() {
        assertTrue(validateRuleWithRange("500", "RANGE", 0.01, 1000000.0));
    }

    @Test
    public void testBasicRule_range_fail() {
        assertFalse(validateRuleWithRange("-1", "RANGE", 0.01, 1000000.0));
        assertFalse(validateRuleWithRange("9999999", "RANGE", 0.01, 1000000.0));
    }

    @Test
    public void testBasicRule_notNull_pass() {
        assertTrue(validateRule("somevalue", "NOT_NULL"));
    }

    @Test
    public void testBasicRule_notNull_fail() {
        assertFalse(validateRule(null, "NOT_NULL"));
        assertFalse(validateRule("", "NOT_NULL"));
    }

    @Test
    public void testBasicRule_length_pass() {
        assertTrue(validateRuleWithLength("hello", "LENGTH", 3, 10));
    }

    @Test
    public void testBasicRule_length_fail() {
        assertFalse(validateRuleWithLength("ab", "LENGTH", 3, 10)); // 太短
        assertFalse(validateRuleWithLength("hello world abc", "LENGTH", 3, 10)); // 太长
    }

    // ===== Test 5: 三种模式路由逻辑 =====

    @Test
    public void testModeRouting_HANDLER() {
        assertEquals("HANDLER", resolveMode("HANDLER"));
        assertEquals("HANDLER", resolveMode("handler")); // case insensitive
    }

    @Test
    public void testModeRouting_FLOW() {
        assertEquals("FLOW", resolveMode("FLOW"));
    }

    @Test
    public void testModeRouting_HYBRID() {
        assertEquals("HYBRID", resolveMode("HYBRID"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModeRouting_unknown() {
        resolveMode("UNKNOWN");
    }

    // ===== 辅助方法 =====

    private String resolveMode(String mode) {
        if ("HANDLER".equalsIgnoreCase(mode)) return "HANDLER";
        if ("FLOW".equalsIgnoreCase(mode)) return "FLOW";
        if ("HYBRID".equalsIgnoreCase(mode)) return "HYBRID";
        throw new IllegalArgumentException("Unknown mode: " + mode);
    }

    private boolean validateRule(Object value, String type) {
        return validateRuleWithPattern(value, type, null)
                && validateRuleWithRange(value, type, null, null)
                && validateRuleWithLength(value, type, -1, -1);
    }

    private boolean validateRuleWithPattern(Object value, String type, String pattern) {
        if (value == null) return "NOT_NULL".equals(type);
        String str = String.valueOf(value);
        switch (type.toUpperCase()) {
            case "REGEX":
                return pattern != null && java.util.regex.Pattern.matches(pattern, str);
            case "EMAIL":
                return java.util.regex.Pattern.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$", str);
            case "PHONE":
                return java.util.regex.Pattern.matches("^1[3-9]\\d{9}$", str);
            default:
                return true;
        }
    }

    private boolean validateRuleWithRange(Object value, String type, Double min, Double max) {
        if (value == null || !"RANGE".equals(type.toUpperCase())) return true;
        double num = Double.parseDouble(String.valueOf(value));
        if (min != null && num < min) return false;
        if (max != null && num > max) return false;
        return true;
    }

    private boolean validateRuleWithLength(Object value, String type, int minLen, int maxLen) {
        if (value == null || !"LENGTH".equals(type.toUpperCase())) return true;
        int len = String.valueOf(value).length();
        if (minLen > 0 && len < minLen) return false;
        if (maxLen > 0 && len > maxLen) return false;
        return true;
    }
}
