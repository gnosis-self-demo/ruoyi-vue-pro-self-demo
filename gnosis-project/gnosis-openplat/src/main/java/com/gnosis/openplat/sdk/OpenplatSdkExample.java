package com.gnosis.openplat.sdk;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 开放平台 SDK 使用示例
 */
public class OpenplatSdkExample {

    public static void main(String[] args) {
        // 初始化SDK
        String baseUrl = "http://localhost:8083";
        String appId = "app_srm_001";
        String appSecret = "secret_srm_2024";
        OpenplatSdk sdk = new OpenplatSdk(baseUrl, appId, appSecret);

        // 示例1: 获取系统列表
        System.out.println("=== 示例1: 获取系统列表 ===");
        JSONObject systemList = sdk.getSystemList();
        System.out.println("系统列表响应: " + systemList.toJSONString());

        // 示例2: 获取API配置列表
        System.out.println("\n=== 示例2: 获取API配置列表 ===");
        JSONObject apiConfigList = sdk.getApiConfigList();
        System.out.println("API配置列表响应: " + apiConfigList.toJSONString());

        // 示例3: 检查API配置是否存在
        System.out.println("\n=== 示例3: 检查API配置是否存在 ===");
        boolean exists = sdk.checkApiConfig("ORDER_CREATE", "/api/v1/order/create");
        System.out.println("API配置是否存在: " + exists);

        // 示例4: 发送自定义请求
        System.out.println("\n=== 示例4: 发送自定义请求 ===");
        Map<String, Object> params = new HashMap<>();
        params.put("apiCode", "ORDER_CREATE");
        params.put("apiName", "Order creation interface");
        params.put("apiPath", "/api/v1/order/create");
        params.put("apiMethod", "POST");
        params.put("systemId", "sys_001");
        params.put("needAuth", true);
        params.put("rateLimit", 1000);
        params.put("status", "ENABLED");
        params.put("description", "Create purchase order");

        JSONObject customResponse = sdk.sendRequest("/openplat/api-config", "POST", params);
        System.out.println("自定义请求响应: " + customResponse.toJSONString());
    }
}