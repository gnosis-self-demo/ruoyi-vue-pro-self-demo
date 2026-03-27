package lifecycle.domain.base;

import java.io.Serializable;
import java.util.Date;

/**
 * 基础实体类
 * 包含通用审计字段
 */
public class BaseEntity implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    protected String id;
    
    /**
     * 创建人 ID
     */
    protected String createUserId;
    
    /**
     * 更新人 ID
     */
    protected String updateUserId;
    
    /**
     * 创建时间
     */
    protected Date createTime;
    
    /**
     * 更新时间
     */
    protected Date updateTime;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getCreateUserId() {
        return createUserId;
    }
    
    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
    
    public String getUpdateUserId() {
        return updateUserId;
    }
    
    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
    
    public Date getUpdateTime() {
        return updateTime;
    }
    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
