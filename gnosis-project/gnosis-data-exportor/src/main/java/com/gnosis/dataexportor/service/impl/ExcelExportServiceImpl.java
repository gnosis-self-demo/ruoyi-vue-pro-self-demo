package com.gnosis.dataexportor.service.impl;

import com.gnosis.dataexportor.calculator.BatchSizeCalculator;
import com.gnosis.dataexportor.dto.ExportRequest;
import com.gnosis.dataexportor.listener.LoggingProgressListener;
import com.gnosis.dataexportor.listener.ProgressListener;
import com.gnosis.dataexportor.service.ExcelExportService;
import com.gnosis.dataexportor.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    private static final int MAX_SHEET_ROWS = 1048576;
    private static final int MAX_RETRY_COUNT = 3;
    private static final int FALLBACK_BATCH_SIZE = 10000;
    private static final int SXSSF_WINDOW_SIZE = 100;

    @Override
    public void exportToExcel(ExportRequest request, HttpServletResponse response) throws IOException, SQLException {
        exportToExcel(request, response, new LoggingProgressListener());
    }

    @Override
    public void exportToExcel(ExportRequest request, HttpServletResponse response, ProgressListener listener)
            throws IOException, SQLException {

        List<ExportRequest.SqlEntry> entries = buildEntries(request);
        String jdbcUrl = validateConnectionParam(request.getJdbcUrl(), "jdbcUrl");
        String username = validateConnectionParam(request.getUsername(), "username");
        String password = request.getPassword() != null ? request.getPassword() : "";

        long startTime = System.currentTimeMillis();
        SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSF_WINDOW_SIZE);
        try {
            long totalExportedRows = 0;
            int totalSheets = 0;
            int globalSheetIndex = 0;

            for (ExportRequest.SqlEntry entry : entries) {
                ExportResult result = exportSingleSql(workbook, entry, globalSheetIndex, listener,
                        jdbcUrl, username, password);
                totalExportedRows += result.exportedRows;
                totalSheets += result.sheetCount;
                globalSheetIndex += result.sheetCount;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=export_data.xlsx");

            try (OutputStream os = response.getOutputStream()) {
                workbook.write(os);
                os.flush();
            }

            long durationMs = System.currentTimeMillis() - startTime;
            listener.onComplete(totalExportedRows, totalSheets, durationMs);
            log.info("导出完成，总行数：{}，总Sheet数：{}，耗时：{} ms", totalExportedRows, totalSheets, durationMs);
        } finally {
            workbook.dispose();
            workbook.close();
        }
    }

    private String validateConnectionParam(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("数据库连接参数 " + name + " 不能为空");
        }
        return value.trim();
    }

    private Connection createConnection(String jdbcUrl, String username, String password) throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    /**
     * 导出单条SQL到workbook（可能产生多个Sheet）
     */
    private ExportResult exportSingleSql(SXSSFWorkbook workbook, ExportRequest.SqlEntry entry,
                                          int startSheetIndex, ProgressListener listener,
                                          String jdbcUrl, String username, String password) {
        String sql = entry.getSql();
        String baseSheetName = entry.getSheetName();
        if (baseSheetName == null || baseSheetName.isEmpty()) {
            baseSheetName = "Sheet";
        }

        SqlUtils.checkOrderBy(sql);

        long totalRows = detectTotalRows(sql, jdbcUrl, username, password);
        boolean totalRowsUnknown = (totalRows < 0);
        int batchSize = calculateBatchSize(totalRows);

        log.info("[{}] 总行数：{}，batchSize = {}",
                baseSheetName, totalRowsUnknown ? "未知" : totalRows, batchSize);

        long exportedRows = 0;
        long offset = 0;
        int sheetSeq = 0;
        int currentSheetRowCount = 0;
        Sheet currentSheet = null;
        List<String> columnNames = null;

        while (totalRowsUnknown || offset < totalRows) {
            String pagingSql = SqlUtils.buildPagingSql(sql, batchSize, offset);
            log.debug("执行分页查询，offset={}, batchSize={}", offset, batchSize);

            List<Object[]> batchData = executePagingQuery(pagingSql, jdbcUrl, username, password);

            if (totalRowsUnknown && batchData.isEmpty()) {
                break;
            }

            if (columnNames == null && !batchData.isEmpty()) {
                columnNames = resolveColumnNames(sql, entry.getColumnNames(), jdbcUrl, username, password);
            }

            for (Object[] rowData : batchData) {
                if (currentSheet == null || currentSheetRowCount >= MAX_SHEET_ROWS) {
                    sheetSeq++;
                    String sheetName = sheetSeq > 1
                            ? baseSheetName + "_" + sheetSeq
                            : baseSheetName;
                    currentSheet = workbook.createSheet(sheetName);
                    currentSheetRowCount = 0;
                    writeHeaderRow(currentSheet, columnNames);
                    currentSheetRowCount = 1;
                    log.info("创建Sheet：{}", sheetName);
                }

                writeDataRow(currentSheet, currentSheetRowCount, rowData);
                currentSheetRowCount++;
                exportedRows++;
            }

            listener.onProgress(exportedRows, totalRowsUnknown ? -1 : totalRows,
                    (int) (offset / batchSize) + 1);

            offset += batchSize;

            if (totalRowsUnknown && batchData.size() < batchSize) {
                break;
            }
        }

        log.info("[{}] 导出完成，行数：{}，Sheet数：{}", baseSheetName, exportedRows, sheetSeq);

        ExportResult result = new ExportResult();
        result.exportedRows = exportedRows;
        result.sheetCount = sheetSeq;
        return result;
    }

    private List<ExportRequest.SqlEntry> buildEntries(ExportRequest request) {
        if (request.getSqlEntries() != null && !request.getSqlEntries().isEmpty()) {
            return request.getSqlEntries();
        }
        ExportRequest.SqlEntry single = new ExportRequest.SqlEntry();
        single.setSql(request.getSql());
        single.setSheetName(request.getSheetNamePrefix());
        return Collections.singletonList(single);
    }

    private long detectTotalRows(String sql, String jdbcUrl, String username, String password) {
        String countSql = SqlUtils.wrapCountSql(sql);
        try (Connection conn = createConnection(jdbcUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(countSql)) {
            pstmt.setQueryTimeout(30);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
            return 0L;
        } catch (Exception e) {
            log.warn("COUNT查询异常（可能超时），采用保守batchSize={}继续导出", FALLBACK_BATCH_SIZE, e);
            return -1L;
        }
    }

    private int calculateBatchSize(long totalRows) {
        if (totalRows <= 0) {
            return FALLBACK_BATCH_SIZE;
        }
        return BatchSizeCalculator.calculateExportBatchSize(totalRows);
    }

    private List<Object[]> executePagingQuery(String sql, String jdbcUrl, String username, String password) {
        int retryCount = 0;
        while (retryCount <= MAX_RETRY_COUNT) {
            try (Connection conn = createConnection(jdbcUrl, username, password);
                 PreparedStatement pstmt = createPagingStatement(conn, sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    List<Object[]> result = new ArrayList<>();
                    while (rs.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 0; i < columnCount; i++) {
                            row[i] = rs.getObject(i + 1);
                        }
                        result.add(row);
                    }
                    return result;
                }
            } catch (Exception e) {
                retryCount++;
                if (retryCount > MAX_RETRY_COUNT) {
                    log.error("分页查询失败，已重试{}次", MAX_RETRY_COUNT, e);
                    throw new RuntimeException("分页查询失败: " + e.getMessage(), e);
                }
                log.warn("分页查询失败，第{}次重试...", retryCount);
                try {
                    Thread.sleep(1000L * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("查询被中断", ie);
                }
            }
        }
        return Collections.emptyList();
    }

    private PreparedStatement createPagingStatement(Connection conn, String sql) throws SQLException {
        conn.setAutoCommit(false);
        return conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
    }

    private List<String> resolveColumnNames(String sql, List<String> customColumnNames,
                                             String jdbcUrl, String username, String password) {
        if (customColumnNames != null && !customColumnNames.isEmpty()) {
            return customColumnNames;
        }

        List<String> columnNames = new ArrayList<>();
        String limitedSql = SqlUtils.buildPagingSql(sql, 1, 0);

        try (Connection conn = createConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(limitedSql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnLabel(i));
            }
        } catch (SQLException e) {
            log.warn("获取列名失败", e);
        }

        return columnNames;
    }

    private void writeHeaderRow(Sheet sheet, List<String> columnNames) {
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columnNames.size(); i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columnNames.get(i));
        }
    }

    private void writeDataRow(Sheet sheet, int rowIndex, Object[] rowData) {
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < rowData.length; i++) {
            Cell cell = row.createCell(i);
            Object value = rowData[i];
            if (value == null) {
                cell.setCellValue("");
            } else if (value instanceof Number) {
                cell.setCellValue(((Number) value).doubleValue());
            } else if (value instanceof java.util.Date) {
                cell.setCellValue(value.toString());
            } else if (value instanceof java.sql.Timestamp) {
                cell.setCellValue(value.toString());
            } else {
                cell.setCellValue(value.toString());
            }
        }
    }

    private static class ExportResult {
        long exportedRows;
        int sheetCount;
    }
}
