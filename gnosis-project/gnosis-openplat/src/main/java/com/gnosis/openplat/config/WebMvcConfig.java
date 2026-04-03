package com.gnosis.openplat.config;

import com.gnosis.openplat.interceptor.OpenplatAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 * 注册拦截器和CORS配置
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
                .excludePathPatterns(
                    "/openplat/api-config/**", // 排除API配置管理接口
                    "/openplat/api-system-relation/**", // 排除API与系统关系配置管理接口
                    "/openplat/system/**" // 排除系统管理接口
                ); // 排除管理接口，方便配置
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 允许所有跨域请求
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}