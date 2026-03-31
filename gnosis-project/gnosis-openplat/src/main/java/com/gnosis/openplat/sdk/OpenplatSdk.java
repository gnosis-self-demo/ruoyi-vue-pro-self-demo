package com.gnosis.openplat.sdk;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * 开放平台 SDK
 * 用于模拟外部系统请求本系统，降低外部系统联调成本
 */
public class OpenplatSdk {

    private final String baseUrl;
    private final String appId;
    private final String appSecret;

    /**
     * 构造函数
     * @param baseUrl 开放平台基础URL
     * @param appId 应用ID
     * @param appSecret 应用密钥
     */
    public OpenplatSdk(String baseUrl, String appId, String appSecret) {
        this.baseUrl = baseUrl;
        this.appId = appId;
        this.appSecret = appSecret;
    }

    /**
     * 发送API请求
     * @param apiPath API路径
     * @param method 请求方法
     * @param params 请求参数
     * @return 响应结果
     */
    public JSONObject sendRequest(String apiPath, String method, Map<String, Object> params) {
        // 构建完整的请求URL
        String url = baseUrl + apiPath;

        // 生成时间戳和nonce
        long timestamp = System.currentTimeMillis();
        String nonce = RandomUtil.randomString(16);

        // 构建标准化请求体
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("appId", appId);
        requestBody.put("timestamp", timestamp);
        requestBody.put("nonce", nonce);
        requestBody.put("data", params != null ? params : new HashMap<>());

        // 构建签名
        String signature = generateSignature(requestBody, timestamp, nonce);
        requestBody.put("signature", signature);

        // 构建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        // 发送请求
        HttpResponse response;
        if ("GET".equalsIgnoreCase(method)) {
            // GET请求也使用POST方式发送，确保鉴权数据在body中
            response = HttpRequest.post(url)
                    .addHeaders(headers)
                    .body(JSON.toJSONString(requestBody))
                    .execute();
        } else {
            // 构建POST请求
            response = HttpRequest.post(url)
                    .addHeaders(headers)
                    .body(JSON.toJSONString(requestBody))
                    .execute();
        }

        // 解析响应
        String body = response.body();
        return JSON.parseObject(body);
    }

    /**
     * 生成签名
     * @param requestBody 标准化请求体
     * @param timestamp 时间戳
     * @param nonce 随机字符串
     * @return 签名
     */
    private String generateSignature(Map<String, Object> requestBody, long timestamp, String nonce) {
        // 构建签名字符串
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append("appId=").append(appId)
                .append("&timestamp=").append(timestamp)
                .append("&nonce=").append(nonce);

        // 添加业务数据参数
        Map<String, Object> data = (Map<String, Object>) requestBody.get("data");
        if (data != null && !data.isEmpty()) {
            data.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> {
                        signatureBuilder.append("&").append(entry.getKey())
                                .append("=").append(entry.getValue());
                    });
        }

        // 添加应用密钥
        signatureBuilder.append("&appSecret=").append(appSecret);

        // 计算MD5签名
        return SecureUtil.md5(signatureBuilder.toString());
    }

    /**
     * 检查API配置是否存在
     * @param apiCode API编码
     * @param apiPath API路径
     * @return 是否存在
     */
    public boolean checkApiConfig(String apiCode, String apiPath) {
        Map<String, Object> params = new HashMap<>();
        params.put("apiCode", apiCode);
        params.put("apiPath", apiPath);

        JSONObject response = sendRequest("/openplat/api-config/check", "GET", params);
        return response.getBoolean("data");
    }

    /**
     * 获取系统列表
     * @return 系统列表
     */
    public JSONObject getSystemList() {
        return sendRequest("/openplat/system/list", "GET", null);
    }

    /**
     * 获取API配置列表
     * @return API配置列表
     */
    public JSONObject getApiConfigList() {
        return sendRequest("/openplat/api-config/list", "GET", null);
    }
}