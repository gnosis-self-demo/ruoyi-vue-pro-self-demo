package com.cmbc.oa.module.paramcheck.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * 缓存配置
 * 启用Spring内置缓存，用于校验结果和配置缓存
 */
@Configuration
@EnableCaching
public class CacheConfig {

    // 使用Spring默认缓存机制
    // 可根据实际环境配置不同的缓存实现
    // 例如：本地缓存、Redis、EhCache等
}

