package lifecycle.dto;

import java.io.Serializable;

/**
 * 规则版本历史 VO
 */
public class RuleVersionHistoryVO implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 主键 ID
     */
    private String id;
    
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
    
    /**
     * 创建人 ID
     */
    private String createUserId;
    
    /**
     * 创建时间
     */
    private java.util.Date createTime;
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
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
    
    public String getCreateUserId() {
        return createUserId;
    }
    
    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }
    
    public java.util.Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(java.util.Date createTime) {
        this.createTime = createTime;
    }
}
