package com.gnosis.accounts.dto.subject;

import java.io.Serializable;
import java.util.Date;

public class AccSubjectVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String subjectCode;
    private String subjectName;
    private String subjectType;
    private String subjectCategory;
    private String parentId;
    private Integer level;
    private String balanceDirection;
    private String description;
    private Integer status;
    private String createUserId;
    private String updateUserId;
    private Date createTime;
    private Date updateTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getSubjectType() { return subjectType; }
    public void setSubjectType(String subjectType) { this.subjectType = subjectType; }
    public String getSubjectCategory() { return subjectCategory; }
    public void setSubjectCategory(String subjectCategory) { this.subjectCategory = subjectCategory; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }
    public String getBalanceDirection() { return balanceDirection; }
    public void setBalanceDirection(String balanceDirection) { this.balanceDirection = balanceDirection; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getCreateUserId() { return createUserId; }
    public void setCreateUserId(String createUserId) { this.createUserId = createUserId; }
    public String getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(String updateUserId) { this.updateUserId = updateUserId; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
