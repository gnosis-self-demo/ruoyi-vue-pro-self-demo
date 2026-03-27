package openplat.domain;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 调用日志信息
 */
@Data
public class OpenplatAccessLog implements Serializable {
    
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
     * API 编码
     */
    private String apiCode;
    
    /**
     * 请求方法
     */
    private String requestMethod;
    
    /**
     * 请求路径
     */
    private String requestPath;
    
    /**
     * 请求参数
     */
    private String requestParams;
    
    /**
     * 请求体
     */
    private String requestBody;
    
    /**
     * 响应码
     */
    private Integer responseCode;
    
    /**
     * 响应体
     */
    private String responseBody;
    
    /**
     * 耗时（毫秒）
     */
    private Integer costTime;
    
    /**
     * IP 地址
     */
    private String ipAddress;
    
    /**
     * 用户代理
     */
    private String userAgent;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
    /**
     * 创建时间
     */
    private Date createTime;
}
