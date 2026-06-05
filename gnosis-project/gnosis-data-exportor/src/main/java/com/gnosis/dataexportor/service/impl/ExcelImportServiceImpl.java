package com.gnosis.dataexportor.service.impl;

import com.gnosis.dataexportor.calculator.BatchSizeCalculator;
import com.gnosis.dataexportor.dto.ErrorRecord;
import com.gnosis.dataexportor.dto.ImportRequest;
import com.gnosis.dataexportor.dto.ImportResult;
import com.gnosis.dataexportor.listener.LoggingProgressListener;
import com.gnosis.dataexportor.listener.ProgressListener;
import com.gnosis.dataexportor.service.ExcelImportService;
import com.gnosis.dataexportor.util.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private static final int DEFAULT_BATCH_SIZE = 5000;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public ImportResult importFromExcel(String excelPath, ImportRequest request) throws IOException, SQLException {
        return importFromExcel(excelPath, request, new LoggingProgressListener());
    }

    @Override
    public ImportResult importFromExcel(String excelPath, ImportRequest request, ProgressListener listener)
            throws IOException, SQLException {
        long startTime = System.currentTimeMillis();
        ImportResult result = new ImportResult();

        log.info("开始导入，文件: {}，目标表: {}", excelPath, request.getTableName());

        try (InputStream is = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(is)) {

            int totalSheets = workbook.getNumberOfSheets();
            result.setTotalSheets(totalSheets);

            long totalSheetRows = 0;
            for (int si = 0; si < totalSheets; si++) {
                Sheet sheet = workbook.getSheetAt(si);
                int lastRowNum = sheet.getLastRowNum();
                if (lastRowNum > 0) {
                    totalSheetRows += lastRowNum;
                }
            }

            int batchSize = request.getBatchSize() != null ? request.getBatchSize()
                    : BatchSizeCalculator.calculateImportBatchSize(totalSheetRows);

            log.info("检测到{}个Sheet，估算总行数：{}，batchSize：{}", totalSheets, totalSheetRows, batchSize);

            listener.onStart(totalSheetRows, totalSheets);

            List<String> dbColumns = getTableColumns(request.getTableName());

            for (int si = 0; si < totalSheets; si++) {
                Sheet sheet = workbook.getSheetAt(si);
                int lastRowNum = sheet.getLastRowNum();
                if (lastRowNum < 1) {
                    continue;
                }

                log.info("开始处理Sheet[{}]，行数：{}", si, lastRowNum);

                Row headerRow = sheet.getRow(0);
                List<String> excelColumns = readHeaderRow(headerRow);

                List<Integer> columnMappingIndexes = buildColumnMapping(excelColumns, dbColumns, request.getColumnMapping());

                String insertSql = buildInsertSql(request.getTableName(), dbColumns, columnMappingIndexes);

                long processedInSheet = 0;
                List<List<Object>> batchRows = new ArrayList<>();

                for (int ri = 1; ri <= lastRowNum; ri++) {
                    Row dataRow = sheet.getRow(ri);
                    if (dataRow == null || isRowEmpty(dataRow)) {
                        continue;
                    }

                    try {
                        List<Object> rowValues = extractRowValues(dataRow, dbColumns, columnMappingIndexes);
                        batchRows.add(rowValues);

                        if (batchRows.size() >= batchSize) {
                            executeBatchInsert(insertSql, batchRows, result);
                            result.setSuccessRows(result.getSuccessRows() + batchRows.size());
                            processedInSheet += batchRows.size();
                            batchRows.clear();

                            listener.onProgress(result.getTotalRows(), totalSheetRows,
                                    (int) (result.getTotalRows() / batchSize) + 1);
                        }

                        result.setTotalRows(result.getTotalRows() + 1);
                    } catch (Exception e) {
                        result.setFailedRows(result.getFailedRows() + 1);
                        ErrorRecord errorRecord = new ErrorRecord();
                        errorRecord.setSheetIndex(si);
                        errorRecord.setRowNumber(ri);
                        errorRecord.setErrorMessage(e.getMessage());
                        result.getErrors().add(errorRecord);

                        boolean shouldContinue = listener.onError("行数据解析失败: " + e.getMessage(), ri, e);
                        if (!shouldContinue) {
                            break;
                        }
                    }
                }

                if (!batchRows.isEmpty()) {
                    executeBatchInsert(insertSql, batchRows, result);
                    result.setSuccessRows(result.getSuccessRows() + batchRows.size());
                    batchRows.clear();
                }

                log.info("Sheet[{}]处理完成，成功：{} 行", si, processedInSheet);
            }

            long durationMs = System.currentTimeMillis() - startTime;
            result.setDurationMs(durationMs);

            listener.onComplete(result.getTotalRows(), totalSheets, durationMs);

            log.info("导入完成，总处理行数：{}，成功：{}，失败：{}，跳过：{}，耗时：{} ms",
                    result.getTotalRows(), result.getSuccessRows(), result.getFailedRows(),
                    result.getSkippedRows(), durationMs);
        }

        return result;
    }

    private List<String> readHeaderRow(Row headerRow) {
        List<String> columns = new ArrayList<>();
        if (headerRow == null) {
            return columns;
        }
        int cellCount = headerRow.getLastCellNum();
        for (int i = 0; i < cellCount; i++) {
            columns.add(ExcelUtils.getCellStringValue(headerRow.getCell(i)));
        }
        return columns;
    }

    private List<String> getTableColumns(String tableName) throws SQLException {
        List<String> columns = new ArrayList<>();
        jdbcTemplate.query("SELECT * FROM " + tableName + " WHERE 1=0",
                (org.springframework.jdbc.core.ResultSetExtractor<Void>) rs -> {
            java.sql.ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                columns.add(metaData.getColumnName(i));
            }
            return null;
        });
        return columns;
    }

    private List<Integer> buildColumnMapping(List<String> excelColumns, List<String> dbColumns,
                                              Map<String, String> explicitMapping) {
        Map<String, Integer> dbColumnIndex = new HashMap<>();
        for (int i = 0; i < dbColumns.size(); i++) {
            dbColumnIndex.put(dbColumns.get(i).toLowerCase(), i);
        }

        List<Integer> mapping = new ArrayList<>();
        for (int i = 0; i < dbColumns.size(); i++) {
            mapping.add(-1);
        }

        for (int ei = 0; ei < excelColumns.size(); ei++) {
            String excelCol = excelColumns.get(ei);
            if (excelCol == null || excelCol.isEmpty()) {
                continue;
            }

            String targetCol = null;

            if (explicitMapping != null && explicitMapping.containsKey(excelCol)) {
                targetCol = explicitMapping.get(excelCol);
            } else {
                targetCol = excelCol.toLowerCase();
            }

            Integer dbIdx = dbColumnIndex.get(targetCol.toLowerCase());
            if (dbIdx != null && dbIdx < mapping.size()) {
                mapping.set(dbIdx, ei);
            }
        }

        return mapping;
    }

    private String buildInsertSql(String tableName, List<String> dbColumns, List<Integer> mapping) {
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName).append(" (");

        List<String> mappedColumns = new ArrayList<>();
        for (int i = 0; i < dbColumns.size(); i++) {
            if (mapping.get(i) >= 0) {
                mappedColumns.add(dbColumns.get(i));
            }
        }

        for (int i = 0; i < mappedColumns.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(mappedColumns.get(i));
        }
        sql.append(") VALUES (");
        for (int i = 0; i < mappedColumns.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("?");
        }
        sql.append(")");

        return sql.toString();
    }

    private List<Object> extractRowValues(Row row, List<String> dbColumns, List<Integer> mapping) {
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < dbColumns.size(); i++) {
            Integer excelIdx = mapping.get(i);
            if (excelIdx != null && excelIdx >= 0) {
                values.add(ExcelUtils.getCellStringValue(row.getCell(excelIdx)));
            } else {
                values.add(null);
            }
        }
        return values;
    }

    @Transactional
    private void executeBatchInsert(String sql, List<List<Object>> batchRows, ImportResult result) {
        jdbcTemplate.execute((Connection conn) -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (List<Object> row : batchRows) {
                    int paramIdx = 1;
                    for (Object value : row) {
                        if (value != null) {
                            pstmt.setString(paramIdx, value.toString());
                        } else {
                            pstmt.setNull(paramIdx, java.sql.Types.VARCHAR);
                        }
                        paramIdx++;
                    }
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            } catch (SQLException e) {
                log.warn("批量插入中部分数据可能失败: {}", e.getMessage());
                throw e;
            }
            return null;
        });
    }

    private boolean isRowEmpty(Row row) {
        int cellCount = row.getLastCellNum();
        for (int i = 0; i < cellCount; i++) {
            String value = ExcelUtils.getCellStringValue(row.getCell(i));
            if (value != null && !value.isEmpty()) {
                return false;
            }
        }
        return true;
    }
}