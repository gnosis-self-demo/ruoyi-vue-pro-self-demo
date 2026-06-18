package com.gnosis.accounts.api;

import com.gnosis.accounts.service.AccAccountService;
import com.gnosis.accounts.service.AccAccountingEngineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Component
public class AccountsApi {

    @Autowired
    private AccAccountingEngineService engineService;

    @Autowired
    private AccAccountService accountService;

    public String collectionEntry(String channelCode, String customerId, BigDecimal amount, BigDecimal fee, String businessId, String operatorId) {
        return engineService.collectionEntry(channelCode, customerId, amount, fee, businessId, operatorId);
    }

    public String refundEntry(String channelCode, String customerId, BigDecimal amount, String businessId, String operatorId) {
        return engineService.refundEntry(channelCode, customerId, amount, businessId, operatorId);
    }

    public String paymentEntry(String channelCode, String customerId, BigDecimal amount, BigDecimal fee, String businessId, String operatorId) {
        return engineService.paymentEntry(channelCode, customerId, amount, fee, businessId, operatorId);
    }

    public String executeChannelClearing(String channelCode, Date clearingDate, String operatorId) {
        return engineService.executeChannelClearing(channelCode, clearingDate, operatorId);
    }

    public String executeBankTransfer(String channelCode, Date transferDate, String operatorId) {
        return engineService.executeBankTransfer(channelCode, transferDate, operatorId);
    }

    public String executeCustomerSettlement(String customerId, Date settlementDate, String operatorId) {
        return engineService.executeCustomerSettlement(customerId, settlementDate, operatorId);
    }

    public BigDecimal getAccountBalance(String accountId) {
        return accountService.getAccountBalance(accountId);
    }

    public BigDecimal getCustomerBalance(String customerId) {
        return accountService.getCustomerBalance(customerId);
    }

    public BigDecimal getChannelPendingAmount(String channelCode) {
        return accountService.getChannelPendingAmount(channelCode);
    }

    public String customEntry(List<EntryItem> debitEntries, List<EntryItem> creditEntries, String businessType, String businessId, String operatorId) {
        return engineService.customEntry(debitEntries, creditEntries, businessType, businessId, operatorId);
    }
}
