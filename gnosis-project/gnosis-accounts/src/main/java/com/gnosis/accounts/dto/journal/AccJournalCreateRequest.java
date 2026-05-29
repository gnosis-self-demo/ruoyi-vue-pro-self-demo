package com.gnosis.accounts.dto.journal;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccJournalCreateRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String businessType;
    private String businessId;
    private String transactionType;
    private BigDecimal totalDebit;
    private BigDecimal totalCredit;
    private Date accountingDate;
    private String description;
    private String createUserId;

    public String getBusinessType() { return businessType; }
    public void setBusinessType(String businessType) { this.businessType = businessType; }
    public String getBusinessId() { return businessId; }
    public void setBusinessId(String businessId) { this.businessId = businessId; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public BigDecimal getTotalDebit() { return totalDebit; }
    public void setTotalDebit(BigDecimal totalDebit) { this.totalDebit = totalDebit; }
    public BigDecimal getTotalCredit() { return totalCredit; }
    public void setTotalCredit(BigDecimal totalCredit) { this.totalCredit = totalCredit; }
    public Date getAccountingDate() { return accountingDate; }
    public void setAccountingDate(Date accountingDate) { this.accountingDate = accountingDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreateUserId() { return createUserId; }
    public void setCreateUserId(String createUserId) { this.createUserId = createUserId; }
}
