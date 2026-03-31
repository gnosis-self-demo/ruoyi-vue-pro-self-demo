package com.gnosis.openplat.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 应用认证信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenplatAppAuth extends BaseDomain {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 应用 ID
     */
    private String appId;
    
    /**
     * 应用密钥
     */
    private String appSecret;
    
    /**
     * 关联系统 ID
     */
    private String systemId;
    
    /**
     * 状态：ENABLED/DISABLED
     */
    private String status;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 关联系统信息
     */
    private transient OpenplatSystem system;
}
