package com.gnosis.openplat.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * API 文档信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenplatApiDoc extends BaseDomain {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 关联 API ID
     */
    private String apiId;
    
    /**
     * 文档标题
     */
    private String docTitle;
    
    /**
     * 文档内容
     */
    private String docContent;
    
    /**
     * 请求参数说明
     */
    private String requestParams;
    
    /**
     * 响应参数说明
     */
    private String responseParams;
    
    /**
     * 错误码定义
     */
    private String errorCodes;
    
    /**
     * 版本
     */
    private String version;
}
