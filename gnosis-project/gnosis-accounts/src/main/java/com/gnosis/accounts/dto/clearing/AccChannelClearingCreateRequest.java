package com.gnosis.accounts.dto.clearing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccChannelClearingCreateRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private Date clearingDate;
    private String channelCode;
    private String channelName;
    private String clearingType;
    private BigDecimal clearingAmount;
    private String description;
    private String createUserId;

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
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCreateUserId() { return createUserId; }
    public void setCreateUserId(String createUserId) { this.createUserId = createUserId; }
}
