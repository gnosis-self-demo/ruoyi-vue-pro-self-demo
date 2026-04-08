package com.gnosis.openplat.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 对接系统信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenplatSystem extends BaseDomain {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 系统编码
     */
    private String systemCode;
    
    /**
     * 系统名称
     */
    private String systemName;
    
    /**
     * 系统类型
     */
    private String systemType;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * 应用密钥
     */
    private String appSecret;
    
    /**
     * 负责人
     */
    private String principal;
    
    /**
     * 状态：ENABLED/DISABLED
     */
    private String status;
    
    /**
     * 描述
     */
    private String description;
}
