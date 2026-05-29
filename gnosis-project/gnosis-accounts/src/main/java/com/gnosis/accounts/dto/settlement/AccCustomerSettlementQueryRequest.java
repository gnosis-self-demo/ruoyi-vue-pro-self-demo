package com.gnosis.accounts.dto.settlement;

import java.io.Serializable;

public class AccCustomerSettlementQueryRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private String settlementNo;
    private String customerId;
    private String customerName;
    private String settlementType;
    private String settlementStatus;
    private String settlementDateStart;
    private String settlementDateEnd;
    private Integer status;
    private Integer pageNum;
    private Integer pageSize;

    public String getSettlementNo() { return settlementNo; }
    public void setSettlementNo(String settlementNo) { this.settlementNo = settlementNo; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getSettlementType() { return settlementType; }
    public void setSettlementType(String settlementType) { this.settlementType = settlementType; }
    public String getSettlementStatus() { return settlementStatus; }
    public void setSettlementStatus(String settlementStatus) { this.settlementStatus = settlementStatus; }
    public String getSettlementDateStart() { return settlementDateStart; }
    public void setSettlementDateStart(String settlementDateStart) { this.settlementDateStart = settlementDateStart; }
    public String getSettlementDateEnd() { return settlementDateEnd; }
    public void setSettlementDateEnd(String settlementDateEnd) { this.settlementDateEnd = settlementDateEnd; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public Integer getPageNum() { return pageNum; }
    public void setPageNum(Integer pageNum) { this.pageNum = pageNum; }
    public Integer getPageSize() { return pageSize; }
    public void setPageSize(Integer pageSize) { this.pageSize = pageSize; }
}
