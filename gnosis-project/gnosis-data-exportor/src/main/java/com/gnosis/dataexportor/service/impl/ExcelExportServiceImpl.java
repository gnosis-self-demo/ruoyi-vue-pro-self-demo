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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    private static final int MAX_SHEET_ROWS = 1048576;
    private static final int COUNT_TIMEOUT_SECONDS = 30;
    private static final int MAX_RETRY_COUNT = 3;
    private static final int FALLBACK_BATCH_SIZE = 10000;
    private static final int SXSSF_WINDOW_SIZE = 100;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void exportToExcel(ExportRequest request, HttpServletResponse response) throws IOException, SQLException {
        exportToExcel(request, response, new LoggingProgressListener());
    }

    @Override
    @Transactional
    public void exportToExcel(ExportRequest request, HttpServletResponse response, ProgressListener listener)
            throws IOException, SQLException {
        String sql = request.getSql();
        String sheetNamePrefix = request.getSheetNamePrefix();
        if (sheetNamePrefix == null || sheetNamePrefix.isEmpty()) {
            sheetNamePrefix = "Sheet";
        }
        List<String> customColumnNames = request.getColumnNames();

        long startTime = System.currentTimeMillis();

        SqlUtils.checkOrderBy(sql);

        long totalRows = detectTotalRows(sql);
        int batchSize = calculateBatchSize(totalRows);
        int sheetsNeeded = BatchSizeCalculator.calculateSheetsNeeded(totalRows);

        log.info("总行数检测完成：{} 行，自动设置 batchSize = {}，Sheet数 = {}", totalRows, batchSize, sheetsNeeded);

        listener.onStart(totalRows, sheetsNeeded);

        String countSql = SqlUtils.wrapCountSql(sql);
        log.info("COUNT SQL: {}", countSql);

        SXSSFWorkbook workbook = new SXSSFWorkbook(SXSSF_WINDOW_SIZE);
        try {
            long exportedRows = 0;
            long offset = 0;
            int currentSheetIndex = 0;
            int currentSheetRowCount = 0;
            Sheet currentSheet = null;
            List<String> columnNames = null;

            while (offset < totalRows) {
                String pagingSql = SqlUtils.buildPagingSql(sql, batchSize, offset);
                log.debug("执行分页查询，offset={}, batchSize={}", offset, batchSize);

                List<Object[]> batchData = executePagingQuery(pagingSql);

                if (columnNames == null && !batchData.isEmpty()) {
                    columnNames = resolveColumnNames(pagingSql, customColumnNames);
                }

                for (Object[] rowData : batchData) {
                    if (currentSheet == null || currentSheetRowCount >= MAX_SHEET_ROWS) {
                        if (currentSheet != null) {
                            String prevSheetName = sheetNamePrefix + "_" + (currentSheetIndex);
                            listener.onSheetSwitch(prevSheetName, currentSheetIndex, currentSheetRowCount);
                        }
                        currentSheetIndex++;
                        currentSheet = workbook.createSheet(sheetNamePrefix + "_" + currentSheetIndex);
                        currentSheetRowCount = 0;
                        writeHeaderRow(currentSheet, columnNames);
                        currentSheetRowCount = 1;
                        log.info("创建Sheet：{}", sheetNamePrefix + "_" + currentSheetIndex);
                    }

                    writeDataRow(currentSheet, currentSheetRowCount, rowData);
                    currentSheetRowCount++;
                    exportedRows++;
                }

                listener.onProgress(exportedRows, totalRows, (int) (offset / batchSize) + 1);

                offset += batchSize;
            }

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=export_data.xlsx");

            try (OutputStream os = response.getOutputStream()) {
                workbook.write(os);
                os.flush();
            }

            long durationMs = System.currentTimeMillis() - startTime;
            listener.onComplete(exportedRows, sheetsNeeded, durationMs);

            log.info("导出完成，总行数：{}，总Sheet数：{}，耗时：{} ms", exportedRows, sheetsNeeded, durationMs);
        } finally {
            workbook.dispose();
            workbook.close();
        }
    }

    private long detectTotalRows(String sql) {
        String countSql = SqlUtils.wrapCountSql(sql);
        try {
            Long count = jdbcTemplate.query(countSql, rs -> {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0L;
            });
            return count != null ? count : 0L;
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

    private List<Object[]> executePagingQuery(String sql) {
        List<Object[]> result = new ArrayList<>();
        int retryCount = 0;

        while (retryCount <= MAX_RETRY_COUNT) {
            try {
                jdbcTemplate.query(sql, rs -> {
                    ResultSetMetaData metaData = rs.getMetaData();
                    int columnCount = metaData.getColumnCount();
                    while (rs.next()) {
                        Object[] row = new Object[columnCount];
                        for (int i = 0; i < columnCount; i++) {
                            row[i] = rs.getObject(i + 1);
                        }
                        result.add(row);
                    }
                });
                return result;
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
        return result;
    }

    private List<String> resolveColumnNames(String sql, List<String> customColumnNames) {
        if (customColumnNames != null && !customColumnNames.isEmpty()) {
            return customColumnNames;
        }

        List<String> columnNames = new ArrayList<>();
        String limitedSql = SqlUtils.buildPagingSql(sql, 1, 0);

        jdbcTemplate.query(limitedSql, rs -> {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(metaData.getColumnLabel(i));
            }
        });

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
}