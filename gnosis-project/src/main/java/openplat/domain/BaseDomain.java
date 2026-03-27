package openplat.domain;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类
 */
@Data
public class BaseDomain implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    private String id;
    
    /**
     * 创建人 ID
     */
    private String createUserId;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新人 ID
     */
    private String updateUserId;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
