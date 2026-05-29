package com.gnosis.accounts.domain;

import com.gnosis.common.domain.BaseEntity;
import java.math.BigDecimal;
import java.util.Date;

public class AccChannelClearing extends BaseEntity {

    private String clearingNo;
    private Date clearingDate;
    private String channelCode;
    private String channelName;
    private String clearingType;
    private BigDecimal clearingAmount;
    private String clearingStatus;
    private String journalId;
    private String description;
    private Integer status;

    public String getClearingNo() { return clearingNo; }
    public void setClearingNo(String clearingNo) { this.clearingNo = clearingNo; }
    public Date getClearingDate() { return clearingDate; }
    public void setClearingDate(Date clearingDate) { this.clearingDate = clearingDate; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getChannelName() { return channelName; }
    public void setChannelName(String channelName) { this.channelName = channelName; }
    public String getClearingType() { return clearingType; }
    public void setClearingType(String clearingType) { this.clearingType = clearingType; }
    public BigDecimal getClearingAmount() { return clearingAmount; }
    public void setClearingAmount(BigDecimal clearingAmount) { this.clearingAmount = clearingAmount; }
    public String getClearingStatus() { return clearingStatus; }
    public void setClearingStatus(String clearingStatus) { this.clearingStatus = clearingStatus; }
    public String getJournalId() { return journalId; }
    public void setJournalId(String journalId) { this.journalId = journalId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}
