package com.gnosis.accounts.service;

import com.gnosis.accounts.domain.AccAccount;
import com.gnosis.accounts.domain.AccChannelClearing;
import com.gnosis.accounts.domain.AccCustomerSettlement;
import com.gnosis.accounts.domain.AccBankTransfer;
import com.gnosis.accounts.domain.AccEntry;
import com.gnosis.accounts.domain.AccJournal;
import com.gnosis.accounts.mapper.AccAccountMapper;
import com.gnosis.accounts.mapper.AccChannelClearingMapper;
import com.gnosis.accounts.mapper.AccCustomerSettlementMapper;
import com.gnosis.accounts.mapper.AccBankTransferMapper;
import com.gnosis.accounts.mapper.AccEntryMapper;
import com.gnosis.accounts.mapper.AccJournalMapper;
import com.gnosis.accounts.api.EntryItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AccAccountingEngineService {

    private static final String ACC_CHANNEL_A_PENDING_COLLECTION = "acc_channel_a_pending_collection";
    private static final String ACC_CLEARED_INCOME = "acc_cleared_income";
    private static final String ACC_CHANNEL_A_BANK = "acc_channel_a_bank";
    private static final String ACC_PENDING_COLLECTION = "acc_pending_collection";
    private static final String ACC_PENDING_REFUND = "acc_pending_refund";
    private static final String ACC_PENDING_PAYMENT = "acc_pending_payment";
    private static final String ACC_CHANNEL_A_PENDING_PAYMENT = "acc_channel_a_pending_payment";
    private static final String ACC_CLEARED_EXPENSE = "acc_cleared_expense";
    private static final String ACC_MERCHANT_DEMO = "acc_merchant_demo";
    private static final String ACC_FEE_INCOME = "acc_fee_income";
    private static final String ACC_FEE_EXPENSE = "acc_fee_expense";

    @Autowired
    private AccJournalMapper journalMapper;

    @Autowired
    private AccEntryMapper entryMapper;

    @Autowired
    private AccAccountMapper accountMapper;

    @Autowired
    private AccChannelClearingMapper clearingMapper;

    @Autowired
    private AccBankTransferMapper transferMapper;

    @Autowired
    private AccCustomerSettlementMapper settlementMapper;

    @Transactional
    public String collectionEntry(String channelCode, String customerId, BigDecimal amount, BigDecimal fee, String businessId, String operatorId) {
        String journalId = UUID.randomUUID().toString().replace("-", "");
        String journalNo = "JRN" + System.currentTimeMillis();

        AccJournal journal = new AccJournal();
        journal.setId(journalId);
        journal.setJournalNo(journalNo);
        journal.setBusinessType("COLLECTION");
        journal.setBusinessId(businessId);
        journal.setTransactionType("COLLECTION");
        journal.setTotalDebit(amount);
        journal.setTotalCredit(amount);
        journal.setAccountingDate(new Date());
        journal.setStatus("POSTED");
        journal.setDescription("收单入账-渠道:" + channelCode + "-客户:" + customerId);
        journal.setCreateUserId(operatorId);
        journal.setUpdateUserId(operatorId);
        journal.setCreateTime(new Date());
        journal.setUpdateTime(new Date());
        journalMapper.insert(journal);

        List<AccEntry> entries = new ArrayList<AccEntry>();

        AccEntry debitEntry = new AccEntry();
        debitEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        debitEntry.setJournalId(journalId);
        debitEntry.setJournalNo(journalNo);
        debitEntry.setAccountId(ACC_CHANNEL_A_PENDING_COLLECTION);
        AccAccount pendingCollectionAccount = accountMapper.selectById(ACC_CHANNEL_A_PENDING_COLLECTION);
        if (pendingCollectionAccount != null) {
            debitEntry.setAccountNo(pendingCollectionAccount.getAccountNo());
            debitEntry.setAccountName(pendingCollectionAccount.getAccountName());
        }
        debitEntry.setEntryDirection("DEBIT");
        debitEntry.setAmount(amount);
        debitEntry.setDescription("待清算收单-渠道" + channelCode);
        debitEntry.setCreateUserId(operatorId);
        debitEntry.setUpdateUserId(operatorId);
        debitEntry.setCreateTime(new Date());
        debitEntry.setUpdateTime(new Date());
        entries.add(debitEntry);

        BigDecimal collectionAmount = amount.subtract(fee);
        AccEntry creditEntry = new AccEntry();
        creditEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        creditEntry.setJournalId(journalId);
        creditEntry.setJournalNo(journalNo);
        creditEntry.setAccountId(ACC_PENDING_COLLECTION);
        AccAccount pendingSettlementAccount = accountMapper.selectById(ACC_PENDING_COLLECTION);
        if (pendingSettlementAccount != null) {
            creditEntry.setAccountNo(pendingSettlementAccount.getAccountNo());
            creditEntry.setAccountName(pendingSettlementAccount.getAccountName());
        }
        creditEntry.setEntryDirection("CREDIT");
        creditEntry.setAmount(collectionAmount);
        creditEntry.setDescription("收单待结算-客户:" + customerId);
        creditEntry.setCreateUserId(operatorId);
        creditEntry.setUpdateUserId(operatorId);
        creditEntry.setCreateTime(new Date());
        creditEntry.setUpdateTime(new Date());
        entries.add(creditEntry);

        if (fee != null && fee.compareTo(BigDecimal.ZERO) > 0) {
            AccEntry feeEntry = new AccEntry();
            feeEntry.setId(UUID.randomUUID().toString().replace("-", ""));
            feeEntry.setJournalId(journalId);
            feeEntry.setJournalNo(journalNo);
            feeEntry.setAccountId(ACC_FEE_INCOME);
            AccAccount feeIncomeAccount = accountMapper.selectById(ACC_FEE_INCOME);
            if (feeIncomeAccount != null) {
                feeEntry.setAccountNo(feeIncomeAccount.getAccountNo());
                feeEntry.setAccountName(feeIncomeAccount.getAccountName());
            }
            feeEntry.setEntryDirection("CREDIT");
            feeEntry.setAmount(fee);
            feeEntry.setDescription("手续费收入");
            feeEntry.setCreateUserId(operatorId);
            feeEntry.setUpdateUserId(operatorId);
            feeEntry.setCreateTime(new Date());
            feeEntry.setUpdateTime(new Date());
            entries.add(feeEntry);
        }

        entryMapper.batchInsert(entries);

        accountMapper.updateBalance(ACC_CHANNEL_A_PENDING_COLLECTION, amount);
        accountMapper.updateBalance(ACC_PENDING_COLLECTION, collectionAmount);
        if (fee != null && fee.compareTo(BigDecimal.ZERO) > 0) {
            accountMapper.updateBalance(ACC_FEE_INCOME, fee);
        }

        return journalId;
    }

    @Transactional
    public String refundEntry(String channelCode, String customerId, BigDecimal amount, String businessId, String operatorId) {
        String journalId = UUID.randomUUID().toString().replace("-", "");
        String journalNo = "JRN" + System.currentTimeMillis();

        AccJournal journal = new AccJournal();
        journal.setId(journalId);
        journal.setJournalNo(journalNo);
        journal.setBusinessType("REFUND");
        journal.setBusinessId(businessId);
        journal.setTransactionType("REFUND");
        journal.setTotalDebit(amount);
        journal.setTotalCredit(amount);
        journal.setAccountingDate(new Date());
        journal.setStatus("POSTED");
        journal.setDescription("退款入账-渠道:" + channelCode + "-客户:" + customerId);
        journal.setCreateUserId(operatorId);
        journal.setUpdateUserId(operatorId);
        journal.setCreateTime(new Date());
        journal.setUpdateTime(new Date());
        journalMapper.insert(journal);

        List<AccEntry> entries = new ArrayList<AccEntry>();

        AccEntry debitEntry = new AccEntry();
        debitEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        debitEntry.setJournalId(journalId);
        debitEntry.setJournalNo(journalNo);
        debitEntry.setAccountId(ACC_MERCHANT_DEMO);
        AccAccount merchantAccount = accountMapper.selectById(ACC_MERCHANT_DEMO);
        if (merchantAccount != null) {
            debitEntry.setAccountNo(merchantAccount.getAccountNo());
            debitEntry.setAccountName(merchantAccount.getAccountName());
        }
        debitEntry.setEntryDirection("DEBIT");
        debitEntry.setAmount(amount);
        debitEntry.setDescription("商户账户退款-客户:" + customerId);
        debitEntry.setCreateUserId(operatorId);
        debitEntry.setUpdateUserId(operatorId);
        debitEntry.setCreateTime(new Date());
        debitEntry.setUpdateTime(new Date());
        entries.add(debitEntry);

        AccEntry creditEntry = new AccEntry();
        creditEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        creditEntry.setJournalId(journalId);
        creditEntry.setJournalNo(journalNo);
        creditEntry.setAccountId(ACC_PENDING_REFUND);
        AccAccount pendingRefundAccount = accountMapper.selectById(ACC_PENDING_REFUND);
        if (pendingRefundAccount != null) {
            creditEntry.setAccountNo(pendingRefundAccount.getAccountNo());
            creditEntry.setAccountName(pendingRefundAccount.getAccountName());
        }
        creditEntry.setEntryDirection("CREDIT");
        creditEntry.setAmount(amount);
        creditEntry.setDescription("退款待结算-客户:" + customerId);
        creditEntry.setCreateUserId(operatorId);
        creditEntry.setUpdateUserId(operatorId);
        creditEntry.setCreateTime(new Date());
        creditEntry.setUpdateTime(new Date());
        entries.add(creditEntry);

        entryMapper.batchInsert(entries);

        accountMapper.updateBalance(ACC_MERCHANT_DEMO, amount.negate());
        accountMapper.updateBalance(ACC_PENDING_REFUND, amount);

        return journalId;
    }

    @Transactional
    public String paymentEntry(String channelCode, String customerId, BigDecimal amount, BigDecimal fee, String businessId, String operatorId) {
        String journalId = UUID.randomUUID().toString().replace("-", "");
        String journalNo = "JRN" + System.currentTimeMillis();

        AccJournal journal = new AccJournal();
        journal.setId(journalId);
        journal.setJournalNo(journalNo);
        journal.setBusinessType("PAYMENT");
        journal.setBusinessId(businessId);
        journal.setTransactionType("PAYMENT");
        journal.setTotalDebit(amount);
        journal.setTotalCredit(amount);
        journal.setAccountingDate(new Date());
        journal.setStatus("POSTED");
        journal.setDescription("付款入账-渠道:" + channelCode + "-客户:" + customerId);
        journal.setCreateUserId(operatorId);
        journal.setUpdateUserId(operatorId);
        journal.setCreateTime(new Date());
        journal.setUpdateTime(new Date());
        journalMapper.insert(journal);

        List<AccEntry> entries = new ArrayList<AccEntry>();

        AccEntry debitEntry = new AccEntry();
        debitEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        debitEntry.setJournalId(journalId);
        debitEntry.setJournalNo(journalNo);
        debitEntry.setAccountId(ACC_MERCHANT_DEMO);
        AccAccount merchantAccount = accountMapper.selectById(ACC_MERCHANT_DEMO);
        if (merchantAccount != null) {
            debitEntry.setAccountNo(merchantAccount.getAccountNo());
            debitEntry.setAccountName(merchantAccount.getAccountName());
        }
        debitEntry.setEntryDirection("DEBIT");
        debitEntry.setAmount(amount);
        debitEntry.setDescription("商户账户付款-客户:" + customerId);
        debitEntry.setCreateUserId(operatorId);
        debitEntry.setUpdateUserId(operatorId);
        debitEntry.setCreateTime(new Date());
        debitEntry.setUpdateTime(new Date());
        entries.add(debitEntry);

        BigDecimal paymentAmount = amount.subtract(fee);
        AccEntry creditEntry = new AccEntry();
        creditEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        creditEntry.setJournalId(journalId);
        creditEntry.setJournalNo(journalNo);
        creditEntry.setAccountId(ACC_PENDING_PAYMENT);
        AccAccount pendingPaymentAccount = accountMapper.selectById(ACC_PENDING_PAYMENT);
        if (pendingPaymentAccount != null) {
            creditEntry.setAccountNo(pendingPaymentAccount.getAccountNo());
            creditEntry.setAccountName(pendingPaymentAccount.getAccountName());
        }
        creditEntry.setEntryDirection("CREDIT");
        creditEntry.setAmount(paymentAmount);
        creditEntry.setDescription("付款待结算-客户:" + customerId);
        creditEntry.setCreateUserId(operatorId);
        creditEntry.setUpdateUserId(operatorId);
        creditEntry.setCreateTime(new Date());
        creditEntry.setUpdateTime(new Date());
        entries.add(creditEntry);

        if (fee != null && fee.compareTo(BigDecimal.ZERO) > 0) {
            AccEntry feeEntry = new AccEntry();
            feeEntry.setId(UUID.randomUUID().toString().replace("-", ""));
            feeEntry.setJournalId(journalId);
            feeEntry.setJournalNo(journalNo);
            feeEntry.setAccountId(ACC_FEE_INCOME);
            AccAccount feeIncomeAccount = accountMapper.selectById(ACC_FEE_INCOME);
            if (feeIncomeAccount != null) {
                feeEntry.setAccountNo(feeIncomeAccount.getAccountNo());
                feeEntry.setAccountName(feeIncomeAccount.getAccountName());
            }
            feeEntry.setEntryDirection("CREDIT");
            feeEntry.setAmount(fee);
            feeEntry.setDescription("手续费收入");
            feeEntry.setCreateUserId(operatorId);
            feeEntry.setUpdateUserId(operatorId);
            feeEntry.setCreateTime(new Date());
            feeEntry.setUpdateTime(new Date());
            entries.add(feeEntry);
        }

        entryMapper.batchInsert(entries);

        accountMapper.updateBalance(ACC_MERCHANT_DEMO, amount.negate());
        accountMapper.updateBalance(ACC_PENDING_PAYMENT, paymentAmount);
        if (fee != null && fee.compareTo(BigDecimal.ZERO) > 0) {
            accountMapper.updateBalance(ACC_FEE_INCOME, fee);
        }

        return journalId;
    }

    @Transactional
    public String executeChannelClearing(String channelCode, Date clearingDate, String operatorId) {
        AccAccount pendingCollectionAccount = accountMapper.selectById(ACC_CHANNEL_A_PENDING_COLLECTION);
        if (pendingCollectionAccount == null) {
            throw new RuntimeException("待清算收单账户不存在");
        }
        BigDecimal clearingAmount = pendingCollectionAccount.getBalance();
        if (clearingAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("待清算金额为零，无需清算");
        }

        String journalId = UUID.randomUUID().toString().replace("-", "");
        String journalNo = "JRN" + System.currentTimeMillis();

        AccJournal journal = new AccJournal();
        journal.setId(journalId);
        journal.setJournalNo(journalNo);
        journal.setBusinessType("CHANNEL_CLEARING");
        journal.setTransactionType("CLEARING");
        journal.setTotalDebit(clearingAmount);
        journal.setTotalCredit(clearingAmount);
        journal.setAccountingDate(clearingDate);
        journal.setStatus("POSTED");
        journal.setDescription("渠道清算-渠道:" + channelCode);
        journal.setCreateUserId(operatorId);
        journal.setUpdateUserId(operatorId);
        journal.setCreateTime(new Date());
        journal.setUpdateTime(new Date());
        journalMapper.insert(journal);

        List<AccEntry> entries = new ArrayList<AccEntry>();

        AccEntry debitEntry = new AccEntry();
        debitEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        debitEntry.setJournalId(journalId);
        debitEntry.setJournalNo(journalNo);
        debitEntry.setAccountId(ACC_CLEARED_INCOME);
        AccAccount clearedIncomeAccount = accountMapper.selectById(ACC_CLEARED_INCOME);
        if (clearedIncomeAccount != null) {
            debitEntry.setAccountNo(clearedIncomeAccount.getAccountNo());
            debitEntry.setAccountName(clearedIncomeAccount.getAccountName());
        }
        debitEntry.setEntryDirection("DEBIT");
        debitEntry.setAmount(clearingAmount);
        debitEntry.setDescription("入款已清算-渠道:" + channelCode);
        debitEntry.setCreateUserId(operatorId);
        debitEntry.setUpdateUserId(operatorId);
        debitEntry.setCreateTime(new Date());
        debitEntry.setUpdateTime(new Date());
        entries.add(debitEntry);

        AccEntry creditEntry = new AccEntry();
        creditEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        creditEntry.setJournalId(journalId);
        creditEntry.setJournalNo(journalNo);
        creditEntry.setAccountId(ACC_CHANNEL_A_PENDING_COLLECTION);
        creditEntry.setAccountNo(pendingCollectionAccount.getAccountNo());
        creditEntry.setAccountName(pendingCollectionAccount.getAccountName());
        creditEntry.setEntryDirection("CREDIT");
        creditEntry.setAmount(clearingAmount);
        creditEntry.setDescription("待清算收单转已清算-渠道:" + channelCode);
        creditEntry.setCreateUserId(operatorId);
        creditEntry.setUpdateUserId(operatorId);
        creditEntry.setCreateTime(new Date());
        creditEntry.setUpdateTime(new Date());
        entries.add(creditEntry);

        entryMapper.batchInsert(entries);

        accountMapper.updateBalance(ACC_CLEARED_INCOME, clearingAmount);
        accountMapper.updateBalance(ACC_CHANNEL_A_PENDING_COLLECTION, clearingAmount.negate());

        AccChannelClearing clearing = new AccChannelClearing();
        clearing.setId(UUID.randomUUID().toString().replace("-", ""));
        clearing.setClearingNo("CLR" + System.currentTimeMillis());
        clearing.setClearingDate(clearingDate);
        clearing.setChannelCode(channelCode);
        clearing.setClearingType("INCOME");
        clearing.setClearingAmount(clearingAmount);
        clearing.setClearingStatus("EXECUTED");
        clearing.setJournalId(journalId);
        clearing.setStatus(1);
        clearing.setCreateUserId(operatorId);
        clearing.setUpdateUserId(operatorId);
        clearing.setCreateTime(new Date());
        clearing.setUpdateTime(new Date());
        clearingMapper.insert(clearing);

        return journalId;
    }

    @Transactional
    public String executeBankTransfer(String channelCode, Date transferDate, String operatorId) {
        AccAccount clearedIncomeAccount = accountMapper.selectById(ACC_CLEARED_INCOME);
        if (clearedIncomeAccount == null) {
            throw new RuntimeException("入款已清算账户不存在");
        }
        BigDecimal transferAmount = clearedIncomeAccount.getBalance();
        if (transferAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("已清算金额为零，无需结转");
        }

        String journalId = UUID.randomUUID().toString().replace("-", "");
        String journalNo = "JRN" + System.currentTimeMillis();

        AccJournal journal = new AccJournal();
        journal.setId(journalId);
        journal.setJournalNo(journalNo);
        journal.setBusinessType("BANK_TRANSFER");
        journal.setTransactionType("TRANSFER");
        journal.setTotalDebit(transferAmount);
        journal.setTotalCredit(transferAmount);
        journal.setAccountingDate(transferDate);
        journal.setStatus("POSTED");
        journal.setDescription("银存结转-渠道:" + channelCode);
        journal.setCreateUserId(operatorId);
        journal.setUpdateUserId(operatorId);
        journal.setCreateTime(new Date());
        journal.setUpdateTime(new Date());
        journalMapper.insert(journal);

        List<AccEntry> entries = new ArrayList<AccEntry>();

        AccEntry debitEntry = new AccEntry();
        debitEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        debitEntry.setJournalId(journalId);
        debitEntry.setJournalNo(journalNo);
        debitEntry.setAccountId(ACC_CHANNEL_A_BANK);
        AccAccount bankAccount = accountMapper.selectById(ACC_CHANNEL_A_BANK);
        if (bankAccount != null) {
            debitEntry.setAccountNo(bankAccount.getAccountNo());
            debitEntry.setAccountName(bankAccount.getAccountName());
        }
        debitEntry.setEntryDirection("DEBIT");
        debitEntry.setAmount(transferAmount);
        debitEntry.setDescription("银存账户-渠道:" + channelCode);
        debitEntry.setCreateUserId(operatorId);
        debitEntry.setUpdateUserId(operatorId);
        debitEntry.setCreateTime(new Date());
        debitEntry.setUpdateTime(new Date());
        entries.add(debitEntry);

        AccEntry creditEntry = new AccEntry();
        creditEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        creditEntry.setJournalId(journalId);
        creditEntry.setJournalNo(journalNo);
        creditEntry.setAccountId(ACC_CLEARED_INCOME);
        creditEntry.setAccountNo(clearedIncomeAccount.getAccountNo());
        creditEntry.setAccountName(clearedIncomeAccount.getAccountName());
        creditEntry.setEntryDirection("CREDIT");
        creditEntry.setAmount(transferAmount);
        creditEntry.setDescription("已清算转银存-渠道:" + channelCode);
        creditEntry.setCreateUserId(operatorId);
        creditEntry.setUpdateUserId(operatorId);
        creditEntry.setCreateTime(new Date());
        creditEntry.setUpdateTime(new Date());
        entries.add(creditEntry);

        entryMapper.batchInsert(entries);

        accountMapper.updateBalance(ACC_CHANNEL_A_BANK, transferAmount);
        accountMapper.updateBalance(ACC_CLEARED_INCOME, transferAmount.negate());

        AccBankTransfer transfer = new AccBankTransfer();
        transfer.setId(UUID.randomUUID().toString().replace("-", ""));
        transfer.setTransferNo("TRF" + System.currentTimeMillis());
        transfer.setTransferDate(transferDate);
        transfer.setChannelCode(channelCode);
        transfer.setTransferType("INCOME");
        transfer.setTransferAmount(transferAmount);
        transfer.setFromAccountId(ACC_CLEARED_INCOME);
        transfer.setFromAccountName(clearedIncomeAccount.getAccountName());
        transfer.setToAccountId(ACC_CHANNEL_A_BANK);
        if (bankAccount != null) {
            transfer.setToAccountName(bankAccount.getAccountName());
        }
        transfer.setTransferStatus("EXECUTED");
        transfer.setJournalId(journalId);
        transfer.setStatus(1);
        transfer.setCreateUserId(operatorId);
        transfer.setUpdateUserId(operatorId);
        transfer.setCreateTime(new Date());
        transfer.setUpdateTime(new Date());
        transferMapper.insert(transfer);

        return journalId;
    }

    @Transactional
    public String executeCustomerSettlement(String customerId, Date settlementDate, String operatorId) {
        AccAccount pendingCollectionAccount = accountMapper.selectById(ACC_PENDING_COLLECTION);
        if (pendingCollectionAccount == null) {
            throw new RuntimeException("收单待结算账户不存在");
        }
        BigDecimal settlementAmount = pendingCollectionAccount.getBalance();
        if (settlementAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("待结算金额为零，无需结算");
        }

        String journalId = UUID.randomUUID().toString().replace("-", "");
        String journalNo = "JRN" + System.currentTimeMillis();

        AccJournal journal = new AccJournal();
        journal.setId(journalId);
        journal.setJournalNo(journalNo);
        journal.setBusinessType("CUSTOMER_SETTLEMENT");
        journal.setTransactionType("SETTLEMENT");
        journal.setTotalDebit(settlementAmount);
        journal.setTotalCredit(settlementAmount);
        journal.setAccountingDate(settlementDate);
        journal.setStatus("POSTED");
        journal.setDescription("客资结算-客户:" + customerId);
        journal.setCreateUserId(operatorId);
        journal.setUpdateUserId(operatorId);
        journal.setCreateTime(new Date());
        journal.setUpdateTime(new Date());
        journalMapper.insert(journal);

        List<AccEntry> entries = new ArrayList<AccEntry>();

        AccEntry debitEntry = new AccEntry();
        debitEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        debitEntry.setJournalId(journalId);
        debitEntry.setJournalNo(journalNo);
        debitEntry.setAccountId(ACC_PENDING_COLLECTION);
        debitEntry.setAccountNo(pendingCollectionAccount.getAccountNo());
        debitEntry.setAccountName(pendingCollectionAccount.getAccountName());
        debitEntry.setEntryDirection("DEBIT");
        debitEntry.setAmount(settlementAmount);
        debitEntry.setDescription("收单待结算转出-客户:" + customerId);
        debitEntry.setCreateUserId(operatorId);
        debitEntry.setUpdateUserId(operatorId);
        debitEntry.setCreateTime(new Date());
        debitEntry.setUpdateTime(new Date());
        entries.add(debitEntry);

        AccEntry creditEntry = new AccEntry();
        creditEntry.setId(UUID.randomUUID().toString().replace("-", ""));
        creditEntry.setJournalId(journalId);
        creditEntry.setJournalNo(journalNo);
        creditEntry.setAccountId(ACC_MERCHANT_DEMO);
        AccAccount merchantAccount = accountMapper.selectById(ACC_MERCHANT_DEMO);
        if (merchantAccount != null) {
            creditEntry.setAccountNo(merchantAccount.getAccountNo());
            creditEntry.setAccountName(merchantAccount.getAccountName());
        }
        creditEntry.setEntryDirection("CREDIT");
        creditEntry.setAmount(settlementAmount);
        creditEntry.setDescription("商户账户入账-客户:" + customerId);
        creditEntry.setCreateUserId(operatorId);
        creditEntry.setUpdateUserId(operatorId);
        creditEntry.setCreateTime(new Date());
        creditEntry.setUpdateTime(new Date());
        entries.add(creditEntry);

        entryMapper.batchInsert(entries);

        accountMapper.updateBalance(ACC_PENDING_COLLECTION, settlementAmount.negate());
        accountMapper.updateBalance(ACC_MERCHANT_DEMO, settlementAmount);

        AccCustomerSettlement settlement = new AccCustomerSettlement();
        settlement.setId(UUID.randomUUID().toString().replace("-", ""));
        settlement.setSettlementNo("STL" + System.currentTimeMillis());
        settlement.setSettlementDate(settlementDate);
        settlement.setCustomerId(customerId);
        settlement.setSettlementType("COLLECTION");
        settlement.setSettlementAmount(settlementAmount);
        settlement.setFeeAmount(BigDecimal.ZERO);
        settlement.setActualAmount(settlementAmount);
        settlement.setFromAccountId(ACC_PENDING_COLLECTION);
        settlement.setFromAccountName(pendingCollectionAccount.getAccountName());
        settlement.setToAccountId(ACC_MERCHANT_DEMO);
        if (merchantAccount != null) {
            settlement.setToAccountName(merchantAccount.getAccountName());
        }
        settlement.setSettlementStatus("EXECUTED");
        settlement.setJournalId(journalId);
        settlement.setStatus(1);
        settlement.setCreateUserId(operatorId);
        settlement.setUpdateUserId(operatorId);
        settlement.setCreateTime(new Date());
        settlement.setUpdateTime(new Date());
        settlementMapper.insert(settlement);

        return journalId;
    }

    @Transactional
    public String customEntry(List<EntryItem> debitEntries, List<EntryItem> creditEntries, String businessType, String businessId, String operatorId) {
        BigDecimal totalDebit = BigDecimal.ZERO;
        for (EntryItem item : debitEntries) {
            totalDebit = totalDebit.add(item.getAmount());
        }

        BigDecimal totalCredit = BigDecimal.ZERO;
        for (EntryItem item : creditEntries) {
            totalCredit = totalCredit.add(item.getAmount());
        }

        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new RuntimeException("借贷不平衡: 借方合计=" + totalDebit + ", 贷方合计=" + totalCredit);
        }

        String journalId = UUID.randomUUID().toString().replace("-", "");
        String journalNo = "JRN" + System.currentTimeMillis();

        AccJournal journal = new AccJournal();
        journal.setId(journalId);
        journal.setJournalNo(journalNo);
        journal.setBusinessType(businessType);
        journal.setBusinessId(businessId);
        journal.setTransactionType("CUSTOM");
        journal.setTotalDebit(totalDebit);
        journal.setTotalCredit(totalCredit);
        journal.setAccountingDate(new Date());
        journal.setStatus("POSTED");
        journal.setDescription("自定义分录-" + businessType);
        journal.setCreateUserId(operatorId);
        journal.setUpdateUserId(operatorId);
        journal.setCreateTime(new Date());
        journal.setUpdateTime(new Date());
        journalMapper.insert(journal);

        List<AccEntry> entries = new ArrayList<AccEntry>();

        for (EntryItem item : debitEntries) {
            AccEntry entry = new AccEntry();
            entry.setId(UUID.randomUUID().toString().replace("-", ""));
            entry.setJournalId(journalId);
            entry.setJournalNo(journalNo);
            entry.setAccountId(item.getAccountId());
            AccAccount account = accountMapper.selectById(item.getAccountId());
            if (account != null) {
                entry.setAccountNo(account.getAccountNo());
                entry.setAccountName(account.getAccountName());
            }
            entry.setEntryDirection("DEBIT");
            entry.setAmount(item.getAmount());
            entry.setDescription(item.getDescription());
            entry.setCreateUserId(operatorId);
            entry.setUpdateUserId(operatorId);
            entry.setCreateTime(new Date());
            entry.setUpdateTime(new Date());
            entries.add(entry);
        }

        for (EntryItem item : creditEntries) {
            AccEntry entry = new AccEntry();
            entry.setId(UUID.randomUUID().toString().replace("-", ""));
            entry.setJournalId(journalId);
            entry.setJournalNo(journalNo);
            entry.setAccountId(item.getAccountId());
            AccAccount account = accountMapper.selectById(item.getAccountId());
            if (account != null) {
                entry.setAccountNo(account.getAccountNo());
                entry.setAccountName(account.getAccountName());
            }
            entry.setEntryDirection("CREDIT");
            entry.setAmount(item.getAmount());
            entry.setDescription(item.getDescription());
            entry.setCreateUserId(operatorId);
            entry.setUpdateUserId(operatorId);
            entry.setCreateTime(new Date());
            entry.setUpdateTime(new Date());
            entries.add(entry);
        }

        entryMapper.batchInsert(entries);

        for (EntryItem item : debitEntries) {
            AccAccount account = accountMapper.selectById(item.getAccountId());
            if (account != null && "DEBIT".equals(account.getBalanceDirection())) {
                accountMapper.updateBalance(item.getAccountId(), item.getAmount());
            } else {
                accountMapper.updateBalance(item.getAccountId(), item.getAmount().negate());
            }
        }

        for (EntryItem item : creditEntries) {
            AccAccount account = accountMapper.selectById(item.getAccountId());
            if (account != null && "CREDIT".equals(account.getBalanceDirection())) {
                accountMapper.updateBalance(item.getAccountId(), item.getAmount());
            } else {
                accountMapper.updateBalance(item.getAccountId(), item.getAmount().negate());
            }
        }

        return journalId;
    }
}
