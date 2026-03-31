package com.gnosis.openplat.interceptor;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.openplat.dto.CommonResponse;
import com.gnosis.openplat.service.OpenplatAppAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 开放平台鉴权拦截器
 * 拦截所有/openplat接口，进行授权检查
 */
@Component
public class OpenplatAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private OpenplatAppAuthService appAuthService;

    // 用于存储nonce，防止重放攻击
    private final Map<String, Long> nonceCache = new ConcurrentHashMap<>();

    // 过期时间（毫秒）
    private static final long TIMEOUT = 5 * 60 * 1000;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只拦截/openplat开头的接口
        if (!request.getRequestURI().startsWith("/openplat")) {
            return true;
        }

        // 从请求体中获取鉴权数据
        JSONObject requestBody = getRequestBody(request);
        if (requestBody == null) {
            sendError(response, "请求体不能为空");
            return false;
        }

        // 提取鉴权数据
        String appId = requestBody.getString("appId");
        Long timestamp = requestBody.getLong("timestamp");
        String nonce = requestBody.getString("nonce");
        String signature = requestBody.getString("signature");

        // 验证参数
        if (appId == null || timestamp == null || nonce == null || signature == null) {
            sendError(response, "鉴权参数不完整");
            return false;
        }

        // 验证时间戳是否过期
        if (System.currentTimeMillis() - timestamp > TIMEOUT) {
            sendError(response, "请求已过期");
            return false;
        }

        // 验证nonce是否重复
        if (nonceCache.containsKey(nonce)) {
            sendError(response, "重复的请求");
            return false;
        }
        nonceCache.put(nonce, timestamp);

        // 获取应用密钥
        String appSecret = appAuthService.getAppSecretByAppId(appId);
        if (appSecret == null) {
            sendError(response, "无效的应用ID");
            return false;
        }

        // 验证签名
        String expectedSignature = generateSignature(requestBody, appId, appSecret);
        if (!signature.equals(expectedSignature)) {
            sendError(response, "签名验证失败");
            return false;
        }

        // 检查接口授权（这里简化实现，实际需要根据API配置检查）
        // TODO: 实现接口授权检查逻辑

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 空实现
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 空实现
    }

    /**
     * 从请求体中获取JSON数据
     */
    private JSONObject getRequestBody(HttpServletRequest request) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        reader.close();
        
        String body = sb.toString();
        if (body.isEmpty()) {
            return null;
        }
        
        return JSON.parseObject(body);
    }

    /**
     * 生成签名
     */
    private String generateSignature(JSONObject requestBody, String appId, String appSecret) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append("appId=").append(appId)
                .append("&timestamp=").append(requestBody.getLong("timestamp"))
                .append("&nonce=").append(requestBody.getString("nonce"));

        // 添加业务数据参数
        JSONObject data = requestBody.getJSONObject("data");
        if (data != null) {
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
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        CommonResponse<String> errorResponse = CommonResponse.error(message);
        response.getWriter().write(JSON.toJSONString(errorResponse));
    }
}