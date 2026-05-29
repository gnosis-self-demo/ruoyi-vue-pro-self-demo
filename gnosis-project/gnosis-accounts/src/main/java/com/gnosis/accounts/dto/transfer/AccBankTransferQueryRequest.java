package com.gnosis.accounts.dto.transfer;

import java.io.Serializable;

public class AccBankTransferQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String transferNo;
    private String channelCode;
    private String transferType;
    private String transferStatus;
    private String transferDateStart;
    private String transferDateEnd;
    private Integer status;
    private Integer pageNum;
    private Integer pageSize;

    public String getTransferNo() { return transferNo; }
    public void setTransferNo(String transferNo) { this.transferNo = transferNo; }
    public String getChannelCode() { return channelCode; }
    public void setChannelCode(String channelCode) { this.channelCode = channelCode; }
    public String getTransferType() { return transferType; }
    public void setTransferType(String transferType) { this.transferType = transferType; }
    public String getTransferStatus() { return transferStatus; }
    public void setTransferStatus(String transferStatus) { this.transferStatus = transferStatus; }
    public String getTransferDateStart() { return transferDateStart; }
    public void setTransferDateStart(String transferDateStart) { this.transferDateStart = transferDateStart; }
    public String getTransferDateEnd() { return transferDateEnd; }
    public void setTransferDateEnd(String transferDateEnd) { this.transferDateEnd = transferDateEnd; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
