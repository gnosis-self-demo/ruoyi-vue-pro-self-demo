package com.cmbc.oa.module.paramcheck;

import com.cmbc.oa.module.paramcheck.domain.ValidationFlow;
import com.cmbc.oa.module.paramcheck.domain.ValidationResult;
import com.cmbc.oa.module.paramcheck.service.DynamicValidationService;
import com.cmbc.oa.module.paramcheck.service.FlowRefreshService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 集成测试
 * 测试缓存、配置管理和错误处理等功能
 */
@SpringBootTest
@ActiveProfiles("test")
public class IntegrationTest {

    @Autowired
    private DynamicValidationService dynamicValidationService;

    @Autowired
    private FlowRefreshService flowRefreshService;

    /**
     * 测试缓存功能
     * 验证相同请求的校验结果是否被缓存
     */
    @Test
    public void testCacheFunctionality() {
        // 准备测试数据
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", "test123");
        requestData.put("amount", 1000);

        // 第一次执行（应该缓存结果）
        ValidationResult result1 = dynamicValidationService.execute("test_flow", requestData);
        assertNotNull(result1);

        // 第二次执行（应该使用缓存结果）
        ValidationResult result2 = dynamicValidationService.execute("test_flow", requestData);
        assertNotNull(result2);

        // 验证两次结果是否相同
        assertEquals(result1.isSuccess(), result2.isSuccess());
        if (!result1.isSuccess()) {
            assertEquals(result1.getErrorCode(), result2.getErrorCode());
            assertEquals(result1.getErrorMsg(), result2.getErrorMsg());
        }
    }

    /**
     * 测试错误处理
     * 验证错误信息是否包含详细信息
     */
    @Test
    public void testErrorHandling() {
        // 准备测试数据（故意使用无效数据）
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", ""); // 空用户ID
        requestData.put("amount", -100); // 负数金额

        // 执行校验
        ValidationResult result = dynamicValidationService.execute("test_flow", requestData);
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertNotNull(result.getErrorCode());
        assertNotNull(result.getErrorMsg());
    }

    /**
     * 测试流程刷新
     * 验证流程配置的动态刷新功能
     */
    @Test
    public void testFlowRefresh() {
        // 刷新流程
        flowRefreshService.refreshAllFlows();
        // 验证流程是否刷新成功（通过日志验证）
        // 这里主要测试方法调用是否成功，不抛出异常
        assertDoesNotThrow(() -> flowRefreshService.refreshAllFlows());
    }

    /**
     * 测试三种模式
     * 验证FLOW、HANDLER、HYBRID三种模式的执行
     */
    @Test
    public void testValidationModes() {
        // 准备测试数据
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("userId", "test123");
        requestData.put("amount", 1000);

        // 测试FLOW模式
        assertDoesNotThrow(() -> {
            ValidationResult result = dynamicValidationService.execute("test_flow_mode", requestData);
            assertNotNull(result);
        });

        // 测试HANDLER模式
        assertDoesNotThrow(() -> {
            ValidationResult result = dynamicValidationService.execute("test_handler_mode", requestData);
            assertNotNull(result);
        });

        // 测试HYBRID模式
        assertDoesNotThrow(() -> {
            ValidationResult result = dynamicValidationService.execute("test_hybrid_mode", requestData);
            assertNotNull(result);
        });
    }
}
