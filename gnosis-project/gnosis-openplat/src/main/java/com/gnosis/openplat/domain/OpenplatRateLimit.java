package com.gnosis.openplat.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 限流配置信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenplatRateLimit extends BaseDomain {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 限流名称
     */
    private String limitName;
    
    /**
     * 限流类型：APP/API/IP
     */
    private String limitType;
    
    /**
     * 限流键
     */
    private String limitKey;
    
    /**
     * 速率限制
     */
    private Integer rateLimit;
    
    /**
     * 突发限制
     */
    private Integer burstLimit;
    
    /**
     * 时间窗口（秒）
     */
    private Integer windowSize;
    
    /**
     * 状态：ENABLED/DISABLED
     */
    private String status;
    
    /**
     * 描述
     */
    private String description;
}
