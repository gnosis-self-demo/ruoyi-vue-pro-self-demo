package com.gnosis.accounts.dto.entry;

import java.io.Serializable;

public class AccEntryQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String journalId;
    private String journalNo;
    private String accountId;
    private String entryDirection;
    private Integer pageNum;
    private Integer pageSize;

    public String getJournalId() { return journalId; }
    public void setJournalId(String journalId) { this.journalId = journalId; }
    public String getJournalNo() { return journalNo; }
    public void setJournalNo(String journalNo) { this.journalNo = journalNo; }
    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }
    public String getEntryDirection() { return entryDirection; }
    public void setEntryDirection(String entryDirection) { this.entryDirection = entryDirection; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
