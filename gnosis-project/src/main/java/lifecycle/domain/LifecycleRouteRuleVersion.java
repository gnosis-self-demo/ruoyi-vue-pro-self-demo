package lifecycle.domain;

import lifecycle.domain.base.BaseEntity;

/**
 * 规则版本历史实体类
 */
public class LifecycleRouteRuleVersion extends BaseEntity {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 规则 ID
     */
    private String ruleId;
    
    /**
     * 版本号
     */
    private Integer versionNumber;
    
    /**
     * 规则内容（JSON）
     */
    private String ruleContent;
    
    /**
     * 变更类型：CREATE/UPDATE/ROLLBACK
     */
    private String changeType;
    
    /**
     * 变更原因
     */
    private String changeReason;
    
    /**
     * 是否当前版本：0-否，1-是
     */
    private Integer isCurrentVersion;
    
    /**
     * 回滚来源版本
     */
    private Integer rollbackFromVersion;
    
    /**
     * 备注
     */
    private String remark;
    
    public String getRuleId() {
        return ruleId;
    }
    
    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }
    
    public Integer getVersionNumber() {
        return versionNumber;
    }
    
    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
    }
    
    public String getRuleContent() {
        return ruleContent;
    }
    
    public void setRuleContent(String ruleContent) {
        this.ruleContent = ruleContent;
    }
    
    public String getChangeType() {
        return changeType;
    }
    
    public void setChangeType(String changeType) {
        this.changeType = changeType;
    }
    
    public String getChangeReason() {
        return changeReason;
    }
    
    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }
    
    public Integer getIsCurrentVersion() {
        return isCurrentVersion;
    }
    
    public void setIsCurrentVersion(Integer isCurrentVersion) {
        this.isCurrentVersion = isCurrentVersion;
    }
    
    public Integer getRollbackFromVersion() {
        return rollbackFromVersion;
    }
    
    public void setRollbackFromVersion(Integer rollbackFromVersion) {
        this.rollbackFromVersion = rollbackFromVersion;
    }
    
    public String getRemark() {
        return remark;
    }
    
    public void setRemark(String remark) {
        this.remark = remark;
    }
}
