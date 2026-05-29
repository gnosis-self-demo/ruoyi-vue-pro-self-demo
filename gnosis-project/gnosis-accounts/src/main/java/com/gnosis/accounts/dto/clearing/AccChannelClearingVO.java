package com.gnosis.accounts.dto.clearing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class AccChannelClearingVO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
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
    private String createUserId;
    private String updateUserId;
    private Date createTime;
    private Date updateTime;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
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
    public String getCreateUserId() { return createUserId; }
    public void setCreateUserId(String createUserId) { this.createUserId = createUserId; }
    public String getUpdateUserId() { return updateUserId; }
    public void setUpdateUserId(String updateUserId) { this.updateUserId = updateUserId; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}
