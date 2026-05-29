package com.gnosis.accounts.dto.entry;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccEntryVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String journalId;
    private String journalNo;
    private String accountId;
    private String accountNo;
    private String accountName;
    private String subjectCode;
    private String subjectName;
    private String entryDirection;
    private BigDecimal amount;
    private String description;
    private String createUserId;
    private String updateUserId;
    private Date createTime;
    private Date updateTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getJournalId() { return journalId; }
    public void setJournalId(String journalId) { this.journalId = journalId; }
    public String getJournalNo() { return journalNo; }
    public void setJournalNo(String journalNo) { this.journalNo = journalNo; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }
    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }
    public String getEntryDirection() { return entryDirection; }
    public void setEntryDirection(String entryDirection) { this.entryDirection = entryDirection; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreateUserId() { return createUserId; }
    public void setCreateUserId(String createUserId) { this.createUserId = createUserId; }
    public String getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(String updateUserId) { this.updateUserId = updateUserId; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
