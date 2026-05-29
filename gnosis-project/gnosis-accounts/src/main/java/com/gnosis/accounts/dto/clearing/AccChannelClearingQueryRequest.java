package com.gnosis.accounts.dto.clearing;

import java.io.Serializable;

public class AccChannelClearingQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String clearingNo;
    private String channelCode;
    private String clearingType;
    private String clearingStatus;
    private String clearingDateStart;
    private String clearingDateEnd;
    private Integer status;
    private Integer pageNum;
    private Integer pageSize;

    public String getClearingNo() { return clearingNo; }
    public void setClearingNo(String clearingNo) { this.clearingNo = clearingNo; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getClearingType() { return clearingType; }
    public void setClearingType(String clearingType) { this.clearingType = clearingType; }
    public String getClearingStatus() { return clearingStatus; }
    public void setClearingStatus(String clearingStatus) { this.clearingStatus = clearingStatus; }
    public String getClearingDateStart() { return clearingDateStart; }
    public void setClearingDateStart(String clearingDateStart) { this.clearingDateStart = clearingDateStart; }
    public String getClearingDateEnd() { return clearingDateEnd; }
    public void setClearingDateEnd(String clearingDateEnd) { this.clearingDateEnd = clearingDateEnd; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
