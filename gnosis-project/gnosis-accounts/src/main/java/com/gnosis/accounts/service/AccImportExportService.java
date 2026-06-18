package com.gnosis.accounts.service;

import com.gnosis.accounts.domain.AccAccount;
import com.gnosis.accounts.domain.AccBankTransfer;
import com.gnosis.accounts.domain.AccChannelClearing;
import com.gnosis.accounts.domain.AccCustomerSettlement;
import com.gnosis.accounts.domain.AccJournal;
import com.gnosis.accounts.domain.AccSubject;
import com.gnosis.accounts.dto.account.AccAccountIdsRequest;
import com.gnosis.accounts.dto.account.AccAccountCreateRequest;
import com.gnosis.accounts.dto.journal.AccJournalIdsRequest;
import com.gnosis.accounts.dto.journal.AccJournalCreateRequest;
import com.gnosis.accounts.dto.subject.AccSubjectIdsRequest;
import com.gnosis.accounts.dto.subject.AccSubjectCreateRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingCreateRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingIdsRequest;
import com.gnosis.accounts.dto.transfer.AccBankTransferCreateRequest;
import com.gnosis.accounts.dto.transfer.AccBankTransferIdsRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementCreateRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementIdsRequest;
import com.gnosis.accounts.mapper.AccAccountMapper;
import com.gnosis.accounts.mapper.AccBankTransferMapper;
import com.gnosis.accounts.mapper.AccChannelClearingMapper;
import com.gnosis.accounts.mapper.AccCustomerSettlementMapper;
import com.gnosis.accounts.mapper.AccJournalMapper;
import com.gnosis.accounts.mapper.AccSubjectMapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AccImportExportService {

    @Autowired
    private AccSubjectService subjectService;

    @Autowired
    private AccAccountService accountService;

    @Autowired
    private AccJournalService journalService;

    @Autowired
    private AccSubjectMapper subjectMapper;

    @Autowired
    private AccAccountMapper accountMapper;

    @Autowired
    private AccJournalMapper journalMapper;

    @Autowired
    private AccChannelClearingMapper clearingMapper;

    @Autowired
    private AccBankTransferMapper transferMapper;

    @Autowired
    private AccCustomerSettlementMapper settlementMapper;

    @Autowired
    private AccChannelClearingService clearingService;

    @Autowired
    private AccBankTransferService transferService;

    @Autowired
    private AccCustomerSettlementService settlementService;

    public void exportSubjects(AccSubjectIdsRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=acc_subjects.xls");

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("会计科目");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"科目编码", "科目名称", "科目类型", "科目分类", "上级科目ID", "层级",
                           "余额方向", "描述", "状态", "创建人ID", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<AccSubject> subjects = getSubjectsToExport(request);
        for (int i = 0; i < subjects.size(); i++) {
            AccSubject subject = subjects.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(subject.getSubjectCode() != null ? subject.getSubjectCode() : "");
            row.createCell(1).setCellValue(subject.getSubjectName() != null ? subject.getSubjectName() : "");
            row.createCell(2).setCellValue(subject.getSubjectType() != null ? subject.getSubjectType() : "");
            row.createCell(3).setCellValue(subject.getSubjectCategory() != null ? subject.getSubjectCategory() : "");
            row.createCell(4).setCellValue(subject.getParentId() != null ? subject.getParentId() : "");
            row.createCell(5).setCellValue(subject.getLevel() != null ? subject.getLevel() : 0);
            row.createCell(6).setCellValue(subject.getBalanceDirection() != null ? subject.getBalanceDirection() : "");
            row.createCell(7).setCellValue(subject.getDescription() != null ? subject.getDescription() : "");
            row.createCell(8).setCellValue(subject.getStatus() != null ? (subject.getStatus() == 1 ? "启用" : "禁用") : "");
            row.createCell(9).setCellValue(subject.getCreateUserId() != null ? subject.getCreateUserId() : "");
            row.createCell(10).setCellValue(subject.getCreateTime() != null ? subject.getCreateTime().toString() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void exportAccounts(AccAccountIdsRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=acc_accounts.xls");

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("账户");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"账户编号", "账户名称", "科目ID", "账户类型", "渠道编码", "客户ID",
                           "客户名称", "币种", "余额", "冻结金额", "余额方向", "描述", "状态", "创建人ID", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<AccAccount> accounts = getAccountsToExport(request);
        for (int i = 0; i < accounts.size(); i++) {
            AccAccount account = accounts.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(account.getAccountNo() != null ? account.getAccountNo() : "");
            row.createCell(1).setCellValue(account.getAccountName() != null ? account.getAccountName() : "");
            row.createCell(2).setCellValue(account.getSubjectId() != null ? account.getSubjectId() : "");
            row.createCell(3).setCellValue(account.getAccountType() != null ? account.getAccountType() : "");
            row.createCell(4).setCellValue(account.getChannelCode() != null ? account.getChannelCode() : "");
            row.createCell(5).setCellValue(account.getCustomerId() != null ? account.getCustomerId() : "");
            row.createCell(6).setCellValue(account.getCustomerName() != null ? account.getCustomerName() : "");
            row.createCell(7).setCellValue(account.getCurrency() != null ? account.getCurrency() : "");
            row.createCell(8).setCellValue(account.getBalance() != null ? account.getBalance().doubleValue() : 0);
            row.createCell(9).setCellValue(account.getFrozenAmount() != null ? account.getFrozenAmount().doubleValue() : 0);
            row.createCell(10).setCellValue(account.getBalanceDirection() != null ? account.getBalanceDirection() : "");
            row.createCell(11).setCellValue(account.getDescription() != null ? account.getDescription() : "");
            row.createCell(12).setCellValue(account.getStatus() != null ? (account.getStatus() == 1 ? "启用" : "禁用") : "");
            row.createCell(13).setCellValue(account.getCreateUserId() != null ? account.getCreateUserId() : "");
            row.createCell(14).setCellValue(account.getCreateTime() != null ? account.getCreateTime().toString() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void exportJournals(AccJournalIdsRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=acc_journals.xls");

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("会计流水");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"流水号", "业务类型", "业务ID", "交易类型", "借方合计", "贷方合计",
                           "记账日期", "状态", "描述", "创建人ID", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<AccJournal> journals = getJournalsToExport(request);
        for (int i = 0; i < journals.size(); i++) {
            AccJournal journal = journals.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(journal.getJournalNo() != null ? journal.getJournalNo() : "");
            row.createCell(1).setCellValue(journal.getBusinessType() != null ? journal.getBusinessType() : "");
            row.createCell(2).setCellValue(journal.getBusinessId() != null ? journal.getBusinessId() : "");
            row.createCell(3).setCellValue(journal.getTransactionType() != null ? journal.getTransactionType() : "");
            row.createCell(4).setCellValue(journal.getTotalDebit() != null ? journal.getTotalDebit().doubleValue() : 0);
            row.createCell(5).setCellValue(journal.getTotalCredit() != null ? journal.getTotalCredit().doubleValue() : 0);
            row.createCell(6).setCellValue(journal.getAccountingDate() != null ? journal.getAccountingDate().toString() : "");
            row.createCell(7).setCellValue(journal.getStatus() != null ? journal.getStatus() : "");
            row.createCell(8).setCellValue(journal.getDescription() != null ? journal.getDescription() : "");
            row.createCell(9).setCellValue(journal.getCreateUserId() != null ? journal.getCreateUserId() : "");
            row.createCell(10).setCellValue(journal.getCreateTime() != null ? journal.getCreateTime().toString() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void importSubjects(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                AccSubjectCreateRequest request = new AccSubjectCreateRequest();
                request.setSubjectCode(getCellStringValue(row.getCell(0)));
                request.setSubjectName(getCellStringValue(row.getCell(1)));
                request.setSubjectType(getCellStringValue(row.getCell(2)));
                request.setSubjectCategory(getCellStringValue(row.getCell(3)));
                request.setParentId(getCellStringValue(row.getCell(4)));
                String levelStr = getCellStringValue(row.getCell(5));
                request.setLevel(levelStr.isEmpty() ? 1 : Integer.parseInt(levelStr));
                request.setBalanceDirection(getCellStringValue(row.getCell(6)));
                request.setDescription(getCellStringValue(row.getCell(7)));
                request.setCreateUserId("admin");

                subjectService.create(request);
            }
        }
    }

    public void importAccounts(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                AccAccountCreateRequest request = new AccAccountCreateRequest();
                request.setAccountNo(getCellStringValue(row.getCell(0)));
                request.setAccountName(getCellStringValue(row.getCell(1)));
                request.setSubjectId(getCellStringValue(row.getCell(2)));
                request.setAccountType(getCellStringValue(row.getCell(3)));
                request.setChannelCode(getCellStringValue(row.getCell(4)));
                request.setCustomerId(getCellStringValue(row.getCell(5)));
                request.setCustomerName(getCellStringValue(row.getCell(6)));
                request.setCurrency(getCellStringValue(row.getCell(7)));
                String balanceStr = getCellStringValue(row.getCell(8));
                request.setBalance(balanceStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(balanceStr));
                String frozenStr = getCellStringValue(row.getCell(9));
                request.setFrozenAmount(frozenStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(frozenStr));
                request.setBalanceDirection(getCellStringValue(row.getCell(10)));
                request.setDescription(getCellStringValue(row.getCell(11)));
                request.setCreateUserId("admin");

                accountService.create(request);
            }
        }
    }

    public void importJournals(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                AccJournalCreateRequest request = new AccJournalCreateRequest();
                request.setBusinessType(getCellStringValue(row.getCell(1)));
                request.setBusinessId(getCellStringValue(row.getCell(2)));
                request.setTransactionType(getCellStringValue(row.getCell(3)));
                String debitStr = getCellStringValue(row.getCell(4));
                request.setTotalDebit(debitStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(debitStr));
                String creditStr = getCellStringValue(row.getCell(5));
                request.setTotalCredit(creditStr.isEmpty() ? BigDecimal.ZERO : new BigDecimal(creditStr));
                request.setDescription(getCellStringValue(row.getCell(8)));
                request.setCreateUserId("admin");

                journalService.create(request);
            }
        }
    }

    private List<AccSubject> getSubjectsToExport(AccSubjectIdsRequest request) {
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            List<AccSubject> list = new ArrayList<>();
            for (String id : request.getIds()) {
                AccSubject subject = subjectMapper.selectById(id);
                if (subject != null) {
                    list.add(subject);
                }
            }
            return list;
        }
        return new ArrayList<AccSubject>();
    }

    private List<AccAccount> getAccountsToExport(AccAccountIdsRequest request) {
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            List<AccAccount> list = new ArrayList<>();
            for (String id : request.getIds()) {
                AccAccount account = accountMapper.selectById(id);
                if (account != null) {
                    list.add(account);
                }
            }
            return list;
        }
        return new ArrayList<AccAccount>();
    }

    private List<AccJournal> getJournalsToExport(AccJournalIdsRequest request) {
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            List<AccJournal> list = new ArrayList<>();
            for (String id : request.getIds()) {
                AccJournal journal = journalMapper.selectById(id);
                if (journal != null) {
                    list.add(journal);
                }
            }
            return list;
        }
        return new ArrayList<AccJournal>();
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    public void exportClearings(AccChannelClearingIdsRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=acc_clearings.xls");

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("渠道清算");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"清算单号", "清算日期", "渠道编码", "渠道名称", "清算类型",
                           "清算金额", "清算状态", "描述", "创建人ID", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<AccChannelClearing> clearings = getClearingsToExport(request);
        for (int i = 0; i < clearings.size(); i++) {
            AccChannelClearing clearing = clearings.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(clearing.getClearingNo() != null ? clearing.getClearingNo() : "");
            row.createCell(1).setCellValue(clearing.getClearingDate() != null ? clearing.getClearingDate().toString() : "");
            row.createCell(2).setCellValue(clearing.getChannelCode() != null ? clearing.getChannelCode() : "");
            row.createCell(3).setCellValue(clearing.getChannelName() != null ? clearing.getChannelName() : "");
            row.createCell(4).setCellValue(clearing.getClearingType() != null ? clearing.getClearingType() : "");
            row.createCell(5).setCellValue(clearing.getClearingAmount() != null ? clearing.getClearingAmount().doubleValue() : 0);
            row.createCell(6).setCellValue(clearing.getClearingStatus() != null ? clearing.getClearingStatus() : "");
            row.createCell(7).setCellValue(clearing.getDescription() != null ? clearing.getDescription() : "");
            row.createCell(8).setCellValue(clearing.getCreateUserId() != null ? clearing.getCreateUserId() : "");
            row.createCell(9).setCellValue(clearing.getCreateTime() != null ? clearing.getCreateTime().toString() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void exportTransfers(AccBankTransferIdsRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=acc_transfers.xls");

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("银存结转");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"结转单号", "结转日期", "渠道编码", "渠道名称", "结转类型",
                           "结转金额", "转出账户", "转入账户", "结转状态", "描述", "创建人ID", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<AccBankTransfer> transfers = getTransfersToExport(request);
        for (int i = 0; i < transfers.size(); i++) {
            AccBankTransfer transfer = transfers.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(transfer.getTransferNo() != null ? transfer.getTransferNo() : "");
            row.createCell(1).setCellValue(transfer.getTransferDate() != null ? transfer.getTransferDate().toString() : "");
            row.createCell(2).setCellValue(transfer.getChannelCode() != null ? transfer.getChannelCode() : "");
            row.createCell(3).setCellValue(transfer.getChannelName() != null ? transfer.getChannelName() : "");
            row.createCell(4).setCellValue(transfer.getTransferType() != null ? transfer.getTransferType() : "");
            row.createCell(5).setCellValue(transfer.getTransferAmount() != null ? transfer.getTransferAmount().doubleValue() : 0);
            row.createCell(6).setCellValue(transfer.getFromAccountName() != null ? transfer.getFromAccountName() : "");
            row.createCell(7).setCellValue(transfer.getToAccountName() != null ? transfer.getToAccountName() : "");
            row.createCell(8).setCellValue(transfer.getTransferStatus() != null ? transfer.getTransferStatus() : "");
            row.createCell(9).setCellValue(transfer.getDescription() != null ? transfer.getDescription() : "");
            row.createCell(10).setCellValue(transfer.getCreateUserId() != null ? transfer.getCreateUserId() : "");
            row.createCell(11).setCellValue(transfer.getCreateTime() != null ? transfer.getCreateTime().toString() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void exportSettlements(AccCustomerSettlementIdsRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=acc_settlements.xls");

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet("客资结算");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"结算单号", "结算日期", "客户ID", "客户名称", "结算类型",
                           "结算金额", "手续费", "实际金额", "转出账户", "转入账户", "结算状态", "描述", "创建人ID", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<AccCustomerSettlement> settlements = getSettlementsToExport(request);
        for (int i = 0; i < settlements.size(); i++) {
            AccCustomerSettlement settlement = settlements.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(settlement.getSettlementNo() != null ? settlement.getSettlementNo() : "");
            row.createCell(1).setCellValue(settlement.getSettlementDate() != null ? settlement.getSettlementDate().toString() : "");
            row.createCell(2).setCellValue(settlement.getCustomerId() != null ? settlement.getCustomerId() : "");
            row.createCell(3).setCellValue(settlement.getCustomerName() != null ? settlement.getCustomerName() : "");
            row.createCell(4).setCellValue(settlement.getSettlementType() != null ? settlement.getSettlementType() : "");
            row.createCell(5).setCellValue(settlement.getSettlementAmount() != null ? settlement.getSettlementAmount().doubleValue() : 0);
            row.createCell(6).setCellValue(settlement.getFeeAmount() != null ? settlement.getFeeAmount().doubleValue() : 0);
            row.createCell(7).setCellValue(settlement.getActualAmount() != null ? settlement.getActualAmount().doubleValue() : 0);
            row.createCell(8).setCellValue(settlement.getFromAccountName() != null ? settlement.getFromAccountName() : "");
            row.createCell(9).setCellValue(settlement.getToAccountName() != null ? settlement.getToAccountName() : "");
            row.createCell(10).setCellValue(settlement.getSettlementStatus() != null ? settlement.getSettlementStatus() : "");
            row.createCell(11).setCellValue(settlement.getDescription() != null ? settlement.getDescription() : "");
            row.createCell(12).setCellValue(settlement.getCreateUserId() != null ? settlement.getCreateUserId() : "");
            row.createCell(13).setCellValue(settlement.getCreateTime() != null ? settlement.getCreateTime().toString() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void importClearings(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                AccChannelClearingCreateRequest request = new AccChannelClearingCreateRequest();
                request.setChannelCode(getCellStringValue(row.getCell(2)));
                request.setChannelName(getCellStringValue(row.getCell(3)));
                request.setClearingType(getCellStringValue(row.getCell(4)));
                String amountStr = getCellStringValue(row.getCell(5));
                request.setClearingAmount(amountStr.isEmpty() ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(amountStr));
                request.setDescription(getCellStringValue(row.getCell(7)));
                request.setCreateUserId("admin");

                clearingService.create(request);
            }
        }
    }

    public void importTransfers(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                AccBankTransferCreateRequest request = new AccBankTransferCreateRequest();
                request.setChannelCode(getCellStringValue(row.getCell(2)));
                request.setChannelName(getCellStringValue(row.getCell(3)));
                request.setTransferType(getCellStringValue(row.getCell(4)));
                String amountStr = getCellStringValue(row.getCell(5));
                request.setTransferAmount(amountStr.isEmpty() ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(amountStr));
                request.setFromAccountName(getCellStringValue(row.getCell(6)));
                request.setToAccountName(getCellStringValue(row.getCell(7)));
                request.setDescription(getCellStringValue(row.getCell(9)));
                request.setCreateUserId("admin");

                transferService.create(request);
            }
        }
    }

    public void importSettlements(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }

                AccCustomerSettlementCreateRequest request = new AccCustomerSettlementCreateRequest();
                request.setCustomerId(getCellStringValue(row.getCell(2)));
                request.setCustomerName(getCellStringValue(row.getCell(3)));
                request.setSettlementType(getCellStringValue(row.getCell(4)));
                String amountStr = getCellStringValue(row.getCell(5));
                request.setSettlementAmount(amountStr.isEmpty() ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(amountStr));
                String feeStr = getCellStringValue(row.getCell(6));
                request.setFeeAmount(feeStr.isEmpty() ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(feeStr));
                String actualStr = getCellStringValue(row.getCell(7));
                request.setActualAmount(actualStr.isEmpty() ? java.math.BigDecimal.ZERO : new java.math.BigDecimal(actualStr));
                request.setFromAccountName(getCellStringValue(row.getCell(8)));
                request.setToAccountName(getCellStringValue(row.getCell(9)));
                request.setDescription(getCellStringValue(row.getCell(11)));
                request.setCreateUserId("admin");

                settlementService.create(request);
            }
        }
    }

    private List<AccChannelClearing> getClearingsToExport(AccChannelClearingIdsRequest request) {
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            List<AccChannelClearing> list = new ArrayList<>();
            for (String id : request.getIds()) {
                AccChannelClearing clearing = clearingMapper.selectById(id);
                if (clearing != null) {
                    list.add(clearing);
                }
            }
            return list;
        }
        return new ArrayList<AccChannelClearing>();
    }

    private List<AccBankTransfer> getTransfersToExport(AccBankTransferIdsRequest request) {
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            List<AccBankTransfer> list = new ArrayList<>();
            for (String id : request.getIds()) {
                AccBankTransfer transfer = transferMapper.selectById(id);
                if (transfer != null) {
                    list.add(transfer);
                }
            }
            return list;
        }
        return new ArrayList<AccBankTransfer>();
    }

    private List<AccCustomerSettlement> getSettlementsToExport(AccCustomerSettlementIdsRequest request) {
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            List<AccCustomerSettlement> list = new ArrayList<>();
            for (String id : request.getIds()) {
                AccCustomerSettlement settlement = settlementMapper.selectById(id);
                if (settlement != null) {
                    list.add(settlement);
                }
            }
            return list;
        }
        return new ArrayList<AccCustomerSettlement>();
    }
}
