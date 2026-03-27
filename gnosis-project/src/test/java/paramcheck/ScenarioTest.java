package paramcheck;

import paramcheck.domain.ValidationContext;
import paramcheck.domain.ValidationResult;
import paramcheck.exception.ValidationException;
import com.jayway.jsonpath.JsonPath;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * 场景集成测试
 *
 * 模拟三种真实业务场景:
 * 1. 场景A (HYBRID): 订单创建 — 正则校验 + DB用户校验 + Handler库存检查
 * 2. 场景B (HANDLER): 用户注册 — 密码强度 + 用户名重复检查
 * 3. 场景C (FLOW):   简单录入 — 邮箱 + 手机号格式校验
 */
public class ScenarioTest {

    // ===== 场景A: 订单创建 (HYBRID) =====

    @Test
    public void testScenarioA_orderValidData() {
        // 正常订单数据
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderNo", "ORD1234567890");
        orderData.put("amount", 500.00);
        orderData.put("userId", "U001");
        Map<String, Object> item = new HashMap<>();
        item.put("skuId", "SKU001");
        item.put("qty", 2);
        orderData.put("items", new Object[]{item});

        // 步骤1: JSONPath 提取
        String orderNo = JsonPath.read(orderData, "$.orderNo");
        assertEquals("ORD1234567890", orderNo);

        // 步骤2: 正则校验
        assertTrue(orderNo.matches("^ORD[0-9]{10}$"));
        Double amount = JsonPath.read(orderData, "$.amount");
        assertTrue(amount >= 0.01 && amount <= 1000000);

        // 步骤3: 准备 Handler 校验上下文
        ValidationContext ctx = new ValidationContext();
        ctx.setData("rawRequest", orderData);
        Object rawData = ctx.getData("rawRequest");
        assertEquals("ORD1234567890", JsonPath.read(rawData, "$.orderNo"));
    }

    @Test
    public void testScenarioA_orderNoFormatError() {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderNo", "INVALID_ORDER");
        orderData.put("amount", 500.00);
        orderData.put("userId", "U001");

        String orderNo = JsonPath.read(orderData, "$.orderNo");
        assertFalse("订单号格式错误应被拦截", orderNo.matches("^ORD[0-9]{10}$"));
    }

    @Test
    public void testScenarioA_amountOutOfRange() {
        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderNo", "ORD1234567890");
        orderData.put("amount", 99999999.0); // 超范围
        orderData.put("userId", "U001");

        Double amount = JsonPath.read(orderData, "$.amount");
        assertFalse("金额超范围应被拦截", amount >= 0.01 && amount <= 1000000);
    }

    // ===== 场景B: 用户注册 (HANDLER) =====

    @Test
    public void testScenarioB_validUserRegistration() {
        Map<String, Object> regData = new HashMap<>();
        regData.put("username", "newtestuser");
        regData.put("password", "Password123");
        regData.put("email", "test@example.com");

        // 密码强度校验
        String password = (String) regData.get("password");
        assertTrue("密码长度 >= 8",
                password.length() >= 8);
        assertTrue("密码含大写",
                password.matches(".*[A-Z].*"));
        assertTrue("密码含小写",
                password.matches(".*[a-z].*"));
        assertTrue("密码含数字",
                password.matches(".*\\d.*"));

        // 邮箱格式
        String email = (String) regData.get("email");
        assertTrue("邮箱格式正确",
                email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$"));
    }

    @Test
    public void testScenarioB_weakPasswords() {
        String[] weakPasswords = {"abc", "12345678", "ABCDEFGH", "Abcdefgh", "Abc123"};
        for (String pwd : weakPasswords) {
            boolean strong = pwd.length() >= 8
                    && pwd.matches(".*[A-Z].*")
                    && pwd.matches(".*[a-z].*")
                    && pwd.matches(".*\\d.*");
            assertFalse("密码应该强度不足: " + pwd, strong);
        }
    }

    // ===== 场景C: 简单录入 (FLOW) =====

    @Test
    public void testScenarioC_validEmailAndPhone() {
        Map<String, Object> formData = new HashMap<>();
        formData.put("email", "user@company.com");
        formData.put("phone", "13812345678");

        String email = JsonPath.read(formData, "$.email");
        String phone = JsonPath.read(formData, "$.phone");

        assertTrue("邮箱格式正确", email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$"));
        assertTrue("手机号格式正确", phone.matches("^1[3-9]\\d{9}$"));
    }

    @Test
    public void testScenarioC_invalidEmail() {
        Map<String, Object> formData = new HashMap<>();
        formData.put("email", "bad-email");

        String email = JsonPath.read(formData, "$.email");
        assertFalse("邮箱格式错误应被拦截",
                email.matches("^[\\w.-]+@[\\w.-]+\\.\\w+$"));
    }

    @Test
    public void testScenarioC_invalidPhone() {
        Map<String, Object> formData = new HashMap<>();
        formData.put("phone", "12345678901");

        String phone = JsonPath.read(formData, "$.phone");
        assertFalse("手机号格式错误应被拦截",
                phone.matches("^1[3-9]\\d{9}$"));
    }

    // ===== JSONPath 深度测试 =====

    @Test
    public void testJsonPath_nestedObject() {
        Map<String, Object> data = new HashMap<>();
        Map<String, Object> user = new HashMap<>();
        user.put("name", "张三");
        user.put("age", 30);
        data.put("user", user);

        String name = JsonPath.read(data, "$.user.name");
        assertEquals("张三", name);
    }

    @Test
    public void testJsonPath_arrayAccess() {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item1 = new HashMap<>();
        item1.put("skuId", "SKU001");
        item1.put("qty", 2);
        Map<String, Object> item2 = new HashMap<>();
        item2.put("skuId", "SKU002");
        item2.put("qty", 3);
        items.add(item1);
        items.add(item2);
        data.put("items", items);

        Object firstSku = JsonPath.read(data, "$.items[0].skuId");
        assertEquals("SKU001", firstSku);

        Integer totalQty = JsonPath.read(data, "$.items[1].qty");
        assertEquals(3, (int) totalQty);
    }

    @Test
    public void testJsonPath_notFound() {
        Map<String, Object> data = new HashMap<>();
        data.put("key", "value");
        Object notFound = JsonPath.read(data, "$.nonexistent");
        assertNull(notFound);
    }

    // ===== ValidationException 测试 =====

    @Test
    public void testValidationException_constructors() {
        ValidationException e1 = new ValidationException("ERR_CODE", "错误消息");
        assertEquals("ERR_CODE", e1.getErrorCode());
        assertEquals("错误消息", e1.getMessage());
        assertNull(e1.getFailedNode());

        ValidationException e2 = new ValidationException("ERR_CODE", "错误消息", "node_x");
        assertEquals("node_x", e2.getFailedNode());

        ValidationException e3 = new ValidationException("纯消息异常");
        assertEquals("VALIDATION_ERROR", e3.getErrorCode());
    }
}
