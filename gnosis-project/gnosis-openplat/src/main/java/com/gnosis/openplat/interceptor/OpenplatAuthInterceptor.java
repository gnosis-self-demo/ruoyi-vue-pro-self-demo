package com.gnosis.openplat.interceptor;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.domain.OpenplatApiSystemRelation;
import com.gnosis.openplat.domain.OpenplatSystem;
import com.gnosis.openplat.dto.CommonResponse;
import com.gnosis.openplat.service.OpenplatApiConfigService;
import com.gnosis.openplat.service.OpenplatApiSystemRelationService;
import com.gnosis.openplat.service.OpenplatSystemService;
import lombok.extern.slf4j.Slf4j;
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
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 开放平台鉴权拦截器
 * 拦截所有/openplat接口，进行授权检查
 */
@Slf4j
@Component
public class OpenplatAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private OpenplatApiConfigService apiConfigService;
    
    @Autowired
    private OpenplatApiSystemRelationService apiSystemRelationService;

    @Autowired
    private OpenplatSystemService systemService;

    // 防重放缓存：用于存储已使用的 nonce，过期时间设置为 5 分钟（同时间戳有效期）
    private final Cache<String, Boolean> nonceCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    // 限流缓存：用于存储 API + AppId 的请求计数
    private final Cache<String, AtomicInteger> rateLimitCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.MINUTES)
            .build();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 只拦截/openplat开头的接口
        if (!request.getRequestURI().startsWith("/openplat")) {
            return true;
        }

        // 获取请求路径和方法
        String requestPath = request.getRequestURI();
        String requestMethod = request.getMethod();

        // 检查API配置
        OpenplatApiConfig apiConfig = apiConfigService.getByApiPath(requestPath);
        if (apiConfig == null) {
            sendError(response, "API不存在");
            return false;
        }

        // 检查API状态
        if (!"ENABLED".equals(apiConfig.getStatus())) {
            sendError(response, "API已禁用");
            return false;
        }

        // 检查请求方法是否匹配
        if (!apiConfig.getApiMethod().equalsIgnoreCase(requestMethod)) {
            sendError(response, "请求方法不匹配");
            return false;
        }

        // 不需要认证的直接放行
        if (apiConfig.getNeedAuth() != null && !apiConfig.getNeedAuth()) {
            return true;
        }

        // 从请求体中获取通用校验数据
        JSONObject requestBody = getRequestBody(request);
        if (requestBody == null) {
            sendError(response, "请求体不能为空");
            return false;
        }

        // 提取鉴权参数
        String appId = requestBody.getString("appId");
        Long timestamp = requestBody.getLong("timestamp");
        String nonce = requestBody.getString("nonce");
        String signature = requestBody.getString("signature");

        // 验证参数完整性
        if (appId == null || timestamp == null || nonce == null || signature == null) {
            sendError(response, "鉴权参数不完整 (appId, timestamp, nonce, signature)");
            return false;
        }

        // 验证时间戳是否过期（5分钟）
        if (Math.abs(System.currentTimeMillis() - timestamp) > 5 * 60 * 1000) {
            sendError(response, "请求已过期");
            return false;
        }

        // 根据 appId 获取系统信息
        OpenplatSystem system = systemService.getByAppId(appId);
        if (system == null || !"ENABLED".equals(system.getStatus())) {
            sendError(response, "应用不存在或已被禁用");
            return false;
        }

        // 验证签名
        if (!verifySignature(requestBody, system.getAppSecret())) {
            sendError(response, "签名验证失败");
            return false;
        }

        // 防重放校验
        if (apiConfig.getNeedAntiReplay() != null && apiConfig.getNeedAntiReplay()) {
            String nonceKey = appId + ":" + nonce;
            if (nonceCache.getIfPresent(nonceKey) != null) {
                sendError(response, "重复的请求 (nonce 已使用)");
                return false;
            }
            nonceCache.put(nonceKey, true);
        }

        // 验证系统是否有权限访问此API
        OpenplatApiSystemRelation relation = apiSystemRelationService.getByApiPathAndSystemId(requestPath, system.getId());
        if (relation == null || !"ENABLED".equals(relation.getStatus())) {
            sendError(response, "系统无权限访问此API");
            return false;
        }

        // 限流校验
        if (apiConfig.getRateLimit() != null && apiConfig.getRateLimit() > 0) {
            String limitKey = requestPath + ":" + appId;
            AtomicInteger count = rateLimitCache.get(limitKey, k -> new AtomicInteger(0));
            if (count.incrementAndGet() > apiConfig.getRateLimit()) {
                sendError(response, "请求过于频繁，触发限流 (阈值: " + apiConfig.getRateLimit() + "/分钟)");
                return false;
            }
        }

        return true;
    }

    /**
     * 验证签名
     */
    private boolean verifySignature(JSONObject requestBody, String appSecret) {
        String signature = requestBody.getString("signature");
        String appId = requestBody.getString("appId");
        Long timestamp = requestBody.getLong("timestamp");
        String nonce = requestBody.getString("nonce");
        
        // 构建签名字符串
        StringBuilder sb = new StringBuilder();
        sb.append("appId=").append(appId)
          .append("&timestamp=").append(timestamp)
          .append("&nonce=").append(nonce);

        // 添加业务数据参数 (TreeMap 保证有序)
        JSONObject data = requestBody.getJSONObject("data");
        if (data != null && !data.isEmpty()) {
            Map<String, Object> sortedParams = new TreeMap<>(data.getInnerMap());
            for (Map.Entry<String, Object> entry : sortedParams.entrySet()) {
                sb.append("&").append(entry.getKey()).append("=").append(entry.getValue());
            }
        }

        // 添加应用密钥
        sb.append("&appSecret=").append(appSecret);

        // 计算 MD5
        String calculatedSignature = SecureUtil.md5(sb.toString());
        return calculatedSignature.equalsIgnoreCase(signature);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }

    private JSONObject getRequestBody(HttpServletRequest request) throws IOException {
        // 由于是拦截器，可能需要包装 request 以便多次读取 InputStream
        // 但如果已经在 Filter 中包装过或者使用 ContentCachingRequestWrapper 则没问题
        // 这里简单实现，实际生产环境建议使用 ContentCachingRequestWrapper
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        
        String body = sb.toString();
        if (body.isEmpty()) {
            return null;
        }
        
        return JSON.parseObject(body);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        CommonResponse<String> errorResponse = CommonResponse.error(message);
        response.getWriter().write(JSON.toJSONString(errorResponse));
    }
}
