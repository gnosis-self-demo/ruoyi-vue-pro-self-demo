package openplat.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * API 配置信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenplatApiConfig extends BaseDomain {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * API 编码
     */
    private String apiCode;
    
    /**
     * API 名称
     */
    private String apiName;
    
    /**
     * API 路径
     */
    private String apiPath;
    
    /**
     * 请求方法
     */
    private String apiMethod;
    
    /**
     * 示例文档链接
     */
    private String exampleUrl;
    
    /**
     * 是否需要认证
     */
    private Boolean needAuth;
    
    /**
     * 是否需要时间戳
     */
    private Boolean needTimestamp;
    
    /**
     * 是否需要 nonce
     */
    private Boolean needNonce;
    
    /**
     * 限流阈值（次/分钟）
     */
    private Integer rateLimit;
    
    /**
     * 状态：ENABLED/DISABLED
     */
    private String status;
    
    /**
     * 描述
     */
    private String description;
}
