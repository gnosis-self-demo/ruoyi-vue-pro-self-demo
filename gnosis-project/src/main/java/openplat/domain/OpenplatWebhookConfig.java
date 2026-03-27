package openplat.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Webhook 配置信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OpenplatWebhookConfig extends BaseDomain {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * Webhook 名称
     */
    private String webhookName;
    
    /**
     * 事件类型
     */
    private String eventType;
    
    /**
     * 回调地址
     */
    private String callbackUrl;
    
    /**
     * 签名密钥
     */
    private String secret;
    
    /**
     * 重试次数
     */
    private Integer retryTimes;
    
    /**
     * 重试间隔（秒）
     */
    private Integer retryInterval;
    
    /**
     * 超时时间（秒）
     */
    private Integer timeout;
    
    /**
     * 状态：ENABLED/DISABLED
     */
    private String status;
    
    /**
     * 描述
     */
    private String description;
}
