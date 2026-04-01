package com.gnosis.openplat.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.openplat.dto.CommonResponse;
import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.domain.OpenplatApiSystemRelation;
import com.gnosis.openplat.service.OpenplatApiConfigService;
import com.gnosis.openplat.service.OpenplatApiSystemRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 开放平台鉴权拦截器
 * 拦截所有/openplat接口，进行授权检查
 */
@Component
public class OpenplatAuthInterceptor implements HandlerInterceptor {

    @Autowired
    private OpenplatApiConfigService apiConfigService;
    
    @Autowired
    private OpenplatApiSystemRelationService apiSystemRelationService;

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
        if (!apiConfig.getApiMethod().equals(requestMethod)) {
            sendError(response, "请求方法不匹配");
            return false;
        }

        // 从请求体中获取通用校验数据
        JSONObject requestBody = getRequestBody(request);
        if (requestBody == null) {
            sendError(response, "请求体不能为空");
            return false;
        }

        // 提取通用校验数据
        Long timestamp = requestBody.getLong("timestamp");
        String nonce = requestBody.getString("nonce");
        String systemId = requestBody.getString("systemId");

        // 验证参数
        if (timestamp == null || nonce == null || systemId == null) {
            sendError(response, "通用校验参数不完整");
            return false;
        }

        // 验证时间戳是否过期（5分钟）
        if (System.currentTimeMillis() - timestamp > 5 * 60 * 1000) {
            sendError(response, "请求已过期");
            return false;
        }

        // 验证系统是否有权限访问此API（通过关系配置）
        OpenplatApiSystemRelation relation = apiSystemRelationService.getByApiPathAndSystemId(requestPath, systemId);
        if (relation == null) {
            sendError(response, "系统无权限访问此API");
            return false;
        }

        // 验证关系状态
        if (!"ENABLED".equals(relation.getStatus())) {
            sendError(response, "系统访问此API的权限已被禁用");
            return false;
        }

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
     * 发送错误响应
     */
    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        CommonResponse<String> errorResponse = CommonResponse.error(message);
        response.getWriter().write(JSON.toJSONString(errorResponse));
    }
}