package com.gnosis.accounts.dto.transfer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccBankTransferUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private Date transferDate;
    private String channelCode;
    private String channelName;
    private String transferType;
    private BigDecimal transferAmount;
    private String fromAccountId;
    private String fromAccountName;
    private String toAccountId;
    private String toAccountName;
    private String transferStatus;
    private String description;
    private String updateUserId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Date getTransferDate() { return transferDate; }
    public void setTransferDate(Date transferDate) { this.transferDate = transferDate; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
    public String getTransferType() { return transferType; }
    public void setTransferType(String transferType) { this.transferType = transferType; }
    public BigDecimal getTransferAmount() { return transferAmount; }
    public void setTransferAmount(BigDecimal transferAmount) { this.transferAmount = transferAmount; }
    public String getFromAccountId() { return fromAccountId; }
    public void setFromAccountId(String fromAccountId) { this.fromAccountId = fromAccountId; }
    public String getFromAccountName() { return fromAccountName; }
    public void setFromAccountName(String fromAccountName) { this.fromAccountName = fromAccountName; }
    public String getToAccountId() { return toAccountId; }
    public void setToAccountId(String toAccountId) { this.toAccountId = toAccountId; }
    public String getToAccountName() { return toAccountName; }
    public void setToAccountName(String toAccountName) { this.toAccountName = toAccountName; }
    public String getTransferStatus() { return transferStatus; }
    public void setTransferStatus(String transferStatus) { this.transferStatus = transferStatus; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(String updateUserId) { this.updateUserId = updateUserId; }
}
