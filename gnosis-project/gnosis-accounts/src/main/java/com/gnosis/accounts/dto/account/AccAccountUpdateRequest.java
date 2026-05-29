package com.gnosis.accounts.dto.account;

import java.io.Serializable;
import java.math.BigDecimal;

public class AccAccountUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String accountNo;
    private String accountName;
    private String subjectId;
    private String accountType;
    private String channelCode;
    private String customerId;
    private String customerName;
    private String currency;
    private String balanceDirection;
    private String description;
    private String updateUserId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getAccountNo() { return accountNo; }
    public void setAccountNo(String accountNo) { this.accountNo = accountNo; }
    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }
    public String getSubjectId() { return subjectId; }
    public void setSubjectId(String subjectId) { this.subjectId = subjectId; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getBalanceDirection() { return balanceDirection; }
    public void setBalanceDirection(String balanceDirection) { this.balanceDirection = balanceDirection; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(String updateUserId) { this.updateUserId = updateUserId; }
}
