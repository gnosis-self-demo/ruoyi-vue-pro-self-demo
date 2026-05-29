package com.gnosis.accounts.dto.clearing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccChannelClearingUpdateRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private Date clearingDate;
    private String channelCode;
    private String channelName;
    private String clearingType;
    private BigDecimal clearingAmount;
    private String clearingStatus;
    private String description;
    private String updateUserId;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
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
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(String updateUserId) { this.updateUserId = updateUserId; }
}
