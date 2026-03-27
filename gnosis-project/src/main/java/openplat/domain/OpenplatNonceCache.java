package openplat.domain;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 防重放缓存信息
 */
@Data
public class OpenplatNonceCache implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    private String id;
    
    /**
     * 应用 ID
     */
    private String appId;
    
    /**
     * 随机数
     */
    private String nonce;
    
    /**
     * 过期时间
     */
    private Date expireTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
}
