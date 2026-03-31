package com.gnosis.openplat.config;

import com.gnosis.openplat.interceptor.OpenplatAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 注册拦截器
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private OpenplatAuthInterceptor openplatAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册开放平台鉴权拦截器，拦截所有/openplat接口
        registry.addInterceptor(openplatAuthInterceptor)
                .addPathPatterns("/openplat/**")
                .excludePathPatterns("/openplat/api-config/check"); // 排除检查接口，方便测试
    }
}