package com.gnosis.openplat.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * API与系统关系配置
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenplatApiSystemRelation extends BaseDomain {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 关系ID
     */
    private String relationId;
    
    /**
     * API配置ID
     */
    private String apiId;
    
    /**
     * 系统ID
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
     * 关联的API配置信息
     */
    private OpenplatApiConfig apiConfig;
    
    /**
     * 关联的系统信息
     */
    private OpenplatSystem system;
}