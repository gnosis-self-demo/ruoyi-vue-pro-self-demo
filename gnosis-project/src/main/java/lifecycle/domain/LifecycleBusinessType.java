package lifecycle.domain;

import lifecycle.domain.base.BaseEntity;

/**
 * 业务类型实体
 * 对应表：lifecycle_business_type
 */
public class LifecycleBusinessType extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 业务类型名称
     */
    private String name;
    
    /**
     * 业务类型编码
     */
    private String code;
    
    /**
     * 描述
     */
    private String description;
    
    /**
     * 入口权限配置 (JSON)
     */
    private String entryPermissionConfig;
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getEntryPermissionConfig() {
        return entryPermissionConfig;
    }
    
    public void setEntryPermissionConfig(String entryPermissionConfig) {
        this.entryPermissionConfig = entryPermissionConfig;
    }
}
