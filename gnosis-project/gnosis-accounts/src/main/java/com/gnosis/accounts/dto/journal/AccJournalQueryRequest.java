package com.gnosis.accounts.dto.journal;

import java.io.Serializable;

public class AccJournalQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String journalNo;
    private String businessType;
    private String businessId;
    private String transactionType;
    private String status;
    private String accountingDateStart;
    private String accountingDateEnd;
    private Integer pageNum;
    private Integer pageSize;

    public String getJournalNo() { return journalNo; }
    public void setJournalNo(String journalNo) { this.journalNo = journalNo; }
    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public String getBusinessId() { return businessId; }
    public void setBusinessId(String businessId) { this.businessId = businessId; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getAccountingDateStart() { return accountingDateStart; }
    public void setAccountingDateStart(String accountingDateStart) { this.accountingDateStart = accountingDateStart; }
    public String getAccountingDateEnd() { return accountingDateEnd; }
    public void setAccountingDateEnd(String accountingDateEnd) { this.accountingDateEnd = accountingDateEnd; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
