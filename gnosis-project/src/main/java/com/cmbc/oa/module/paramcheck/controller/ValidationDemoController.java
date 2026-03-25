package com.cmbc.oa.module.paramcheck.controller;

import com.cmbc.oa.module.paramcheck.annotation.ParamCheck;
import com.cmbc.oa.module.paramcheck.domain.ValidationResult;
import com.cmbc.oa.module.paramcheck.service.DynamicValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数校验示例 Controller
 * 
 * 提供三个接口分别测试三种校验模式:
 * - HYBRID: 订单创建
 * - HANDLER: 用户注册
 * - FLOW:   简单登录
 */
@RestController
@RequestMapping("/api")
public class ValidationDemoController {

    @Autowired
    private DynamicValidationService validationService;

    /**
     * 测试 HYBRID 混合模式
     * 流程: THEN(parse_param, check_format, check_db_user) -> OrderBusinessHandler
     * 
     * 传参示例:
     * {
     *   "orderNo": "ORD1234567890",
     *   "amount": 500.00,
     *   "userId": "U001",
     *   "items": [{"skuId": "SKU001", "qty": 2}]
     * }
     */
    @PostMapping("/order/create")
    @ParamCheck(flowId = "ORDER_CREATE_FLOW", throwOnFail = false)
    public Map<String, Object> createOrder(@RequestBody Map<String, Object> orderData) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("orderId", "ORD" + System.currentTimeMillis());
        result.put("message", "订单创建成功");
        return result;
    }

    /**
     * 测试 HANDLER 纯接口模式
     * 流程: 直接调用 UserRegisterHandler
     * 
     * 传参示例:
     * {
     *   "username": "testuser",
     *   "password": "Password123",
     *   "email": "test@example.com"
     * }
     */
    @PostMapping("/user/register")
    @ParamCheck(flowId = "USER_REGISTER_FLOW", throwOnFail = false)
    public Map<String, Object> registerUser(@RequestBody Map<String, Object> userData) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("userId", "U" + System.currentTimeMillis());
        result.put("message", "用户注册成功");
        return result;
    }

    /**
     * 测试 FLOW 纯编排模式
     * 流程: THEN(parse_param, check_format)
     * 
     * 传参示例:
     * {
     *   "email": "user@example.com",
     *   "phone": "13812345678"
     * }
     */
    @PostMapping("/login")
    @ParamCheck(flowId = "SIMPLE_CHECK_FLOW", throwOnFail = false)
    public Map<String, Object> login(@RequestBody Map<String, Object> loginData) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("token", "token_" + System.currentTimeMillis());
        result.put("message", "登录成功");
        return result;
    }

    /**
     * 手动调用校验（返回 ValidationResult）
     */
    @PostMapping("/validate")
    public ValidationResult validate(@RequestParam String flowId, 
                                    @RequestBody Map<String, Object> data) {
        return validationService.execute(flowId, data);
    }

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> result = new HashMap<>();
        result.put("status", "UP");
        result.put("module", "paramcheck");
        result.put("timestamp", System.currentTimeMillis());
        return result;
    }
}
