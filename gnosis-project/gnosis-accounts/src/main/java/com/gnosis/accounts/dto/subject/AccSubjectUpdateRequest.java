package com.gnosis.accounts.dto.subject;

import java.io.Serializable;

public class AccSubjectUpdateRequest implements Serializable {
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
    private String updateUserId;

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
    public String getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(String updateUserId) { this.updateUserId = updateUserId; }
}
