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
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class ExcelImportServiceImpl implements ExcelImportService {

    private static final int DEFAULT_BATCH_SIZE = 5000;

    // ──────────────── Excel 导入 ────────────────

    @Override
    public ImportResult importFromExcel(String excelPath, ImportRequest request) throws IOException, SQLException {
        return importFromExcel(excelPath, request, new LoggingProgressListener());
    }

    @Override
    public ImportResult importFromExcel(String excelPath, ImportRequest request, ProgressListener listener)
            throws IOException, SQLException {
        long startTime = System.currentTimeMillis();
        ImportResult result = new ImportResult();

        String jdbcUrl = validateParam(request.getJdbcUrl(), "jdbcUrl");
        String username = validateParam(request.getUsername(), "username");
        String password = request.getPassword() != null ? request.getPassword() : "";

        log.info("开始Excel导入，文件: {}，目标表: {}", excelPath, request.getTableName());

        try (InputStream is = new FileInputStream(excelPath);
             Workbook workbook = new XSSFWorkbook(is)) {

            int totalSheets = workbook.getNumberOfSheets();
            result.setTotalSheets(totalSheets);

            long totalSheetRows = 0;
            for (int si = 0; si < totalSheets; si++) {
                Sheet sheet = workbook.getSheetAt(si);
                if (sheet.getLastRowNum() > 0) totalSheetRows += sheet.getLastRowNum();
            }

            int batchSize = request.getBatchSize() != null ? request.getBatchSize()
                    : BatchSizeCalculator.calculateImportBatchSize(totalSheetRows);
            listener.onStart(totalSheetRows, totalSheets);

            List<String> dbColumns = getTableColumns(request.getTableName(), jdbcUrl, username, password);

            for (int si = 0; si < totalSheets; si++) {
                Sheet sheet = workbook.getSheetAt(si);
                if (sheet.getLastRowNum() < 1) continue;

                Row headerRow = sheet.getRow(0);
                List<String> excelColumns = readExcelHeaderRow(headerRow);
                List<Integer> mapping = buildColumnMapping(excelColumns, dbColumns, request.getColumnMapping());
                String insertSql = buildInsertSql(request.getTableName(), dbColumns, mapping);

                processSheet(sheet, dbColumns, mapping, insertSql, batchSize, si, totalSheetRows,
                        result, listener, jdbcUrl, username, password);
            }

            result.setDurationMs(System.currentTimeMillis() - startTime);
            listener.onComplete(result.getTotalRows(), totalSheets, result.getDurationMs());
            log.info("Excel导入完成，总行数：{}，成功：{}，失败：{}，耗时：{} ms",
                    result.getTotalRows(), result.getSuccessRows(), result.getFailedRows(), result.getDurationMs());
        }
        return result;
    }

    private void processSheet(Sheet sheet, List<String> dbColumns, List<Integer> mapping,
                               String insertSql, int batchSize, int sheetIdx, long totalRows,
                               ImportResult result, ProgressListener listener,
                               String jdbcUrl, String username, String password) {
        int lastRowNum = sheet.getLastRowNum();
        log.info("处理Sheet[{}]，行数：{}", sheetIdx, lastRowNum);

        long processed = 0;
        List<List<Object>> batch = new ArrayList<>();

        for (int ri = 1; ri <= lastRowNum; ri++) {
            Row dataRow = sheet.getRow(ri);
            if (dataRow == null || isExcelRowEmpty(dataRow)) continue;

            try {
                List<Object> values = extractExcelRowValues(dataRow, dbColumns, mapping);
                batch.add(values);
                if (batch.size() >= batchSize) {
                    executeBatchInsert(insertSql, batch, jdbcUrl, username, password);
                    result.setSuccessRows(result.getSuccessRows() + batch.size());
                    processed += batch.size();
                    batch.clear();
                    listener.onProgress(result.getTotalRows(), totalRows, (int) (result.getTotalRows() / batchSize) + 1);
                }
                result.setTotalRows(result.getTotalRows() + 1);
            } catch (Exception e) {
                result.setFailedRows(result.getFailedRows() + 1);
                ErrorRecord er = new ErrorRecord();
                er.setSheetIndex(sheetIdx);
                er.setRowNumber(ri);
                er.setErrorMessage(e.getMessage());
                result.getErrors().add(er);
                if (!listener.onError("行数据解析失败", ri, e)) break;
            }
        }
        if (!batch.isEmpty()) {
            executeBatchInsert(insertSql, batch, jdbcUrl, username, password);
            result.setSuccessRows(result.getSuccessRows() + batch.size());
            batch.clear();
        }
        log.info("Sheet[{}]完成，成功：{} 行", sheetIdx, processed);
    }

    // ──────────────── CSV/ZIP 导入 ────────────────

    @Override
    public ImportResult importFromCsvZip(String zipPath, ImportRequest request, ProgressListener listener)
            throws IOException, SQLException {
        long startTime = System.currentTimeMillis();
        ImportResult result = new ImportResult();

        String jdbcUrl = validateParam(request.getJdbcUrl(), "jdbcUrl");
        String username = validateParam(request.getUsername(), "username");
        String password = request.getPassword() != null ? request.getPassword() : "";

        log.info("开始ZIP导入，文件: {}", zipPath);

        try (FileInputStream fis = new FileInputStream(zipPath);
             ZipInputStream zis = new ZipInputStream(fis, StandardCharsets.UTF_8)) {

            // 先遍历所有条目统计信息
            List<ZipEntryData> entries = new ArrayList<>();
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                if (!ze.isDirectory() && ze.getName().toLowerCase().endsWith(".csv")) {
                    ZipEntryData ed = new ZipEntryData();
                    ed.name = ze.getName();
                    // 从文件名提取表名：去除路径和.csv后缀
                    String baseName = ze.getName();
                    int lastSep = Math.max(baseName.lastIndexOf('/'), baseName.lastIndexOf('\\'));
                    if (lastSep >= 0) baseName = baseName.substring(lastSep + 1);
                    ed.tableName = baseName.replaceAll("\\.csv$", "");
                    entries.add(ed);
                }
                zis.closeEntry();
            }

            int totalSheets = entries.size();
            result.setTotalSheets(totalSheets);
            listener.onStart(-1, totalSheets);
            log.info("ZIP包含{}个CSV文件", totalSheets);

            // 重新打开ZIP进行导入
            for (ZipEntryData ed : entries) {
                importCsvEntry(zipPath, ed, result, listener, jdbcUrl, username, password, totalSheets);
            }
        }

        result.setDurationMs(System.currentTimeMillis() - startTime);
        listener.onComplete(result.getTotalRows(), result.getTotalSheets(), result.getDurationMs());
        log.info("ZIP导入完成，总行数：{}，成功：{}，失败：{}，耗时：{} ms",
                result.getTotalRows(), result.getSuccessRows(), result.getFailedRows(), result.getDurationMs());
        return result;
    }

    private void importCsvEntry(String zipPath, ZipEntryData entryData, ImportResult result,
                                 ProgressListener listener, String jdbcUrl, String username, String password,
                                 int totalSheets) throws IOException, SQLException {
        log.info("处理CSV: {} → 表: {}", entryData.name, entryData.tableName);

        // 先读取CSV全部行（CSV文件通常不大，可以在内存中处理）
        List<List<String>> allRows = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(zipPath);
             ZipInputStream zis = new ZipInputStream(fis, StandardCharsets.UTF_8)) {
            ZipEntry ze;
            while ((ze = zis.getNextEntry()) != null) {
                if (ze.getName().equals(entryData.name)) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(zis, StandardCharsets.UTF_8))) {
                        // 跳过BOM
                        reader.mark(1);
                        int first = reader.read();
                        if (first != '\uFEFF') reader.reset();

                        String line;
                        while ((line = reader.readLine()) != null) {
                            List<String> fields = parseCsvLine(line);
                            if (!isCsvRowEmpty(fields)) {
                                allRows.add(fields);
                            }
                        }
                    }
                    break;
                }
                zis.closeEntry();
            }
        }

        if (allRows.isEmpty()) {
            log.info("CSV无数据，跳过");
            return;
        }

        // 第一行是表头
        List<String> header = allRows.get(0);
        List<String> dbColumns = getTableColumns(entryData.tableName, jdbcUrl, username, password);
        List<Integer> mapping = buildColumnMapping(header, dbColumns, null);
        String insertSql = buildInsertSql(entryData.tableName, dbColumns, mapping);

        int batchSize = DEFAULT_BATCH_SIZE;
        List<List<Object>> batch = new ArrayList<>();

        for (int ri = 1; ri < allRows.size(); ri++) {
            List<String> fields = allRows.get(ri);
            // 补齐缺失的列
            while (fields.size() < header.size()) fields.add("");

            try {
                List<Object> values = mapCsvValues(fields, dbColumns, mapping);
                batch.add(values);
                if (batch.size() >= batchSize) {
                    executeBatchInsert(insertSql, batch, jdbcUrl, username, password);
                    result.setSuccessRows(result.getSuccessRows() + batch.size());
                    batch.clear();
                    listener.onProgress(result.getTotalRows(), -1, (int) (result.getTotalRows() / batchSize) + 1);
                }
                result.setTotalRows(result.getTotalRows() + 1);
            } catch (Exception e) {
                result.setFailedRows(result.getFailedRows() + 1);
                ErrorRecord er = new ErrorRecord();
                er.setSheetIndex(0);
                er.setRowNumber(ri);
                er.setErrorMessage(e.getMessage());
                result.getErrors().add(er);
                if (!listener.onError("行数据解析失败", ri, e)) break;
            }
        }
        if (!batch.isEmpty()) {
            executeBatchInsert(insertSql, batch, jdbcUrl, username, password);
            result.setSuccessRows(result.getSuccessRows() + batch.size());
        }

        log.info("CSV导入完成，表：{}，成功：{} 行", entryData.tableName, allRows.size() - 1);
    }

    // ──────────────── CSV 解析 (RFC 4180) ────────────────

    static List<String> parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        sb.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
        }
        fields.add(sb.toString());
        return fields;
    }

    private boolean isCsvRowEmpty(List<String> fields) {
        for (String f : fields) {
            if (f != null && !f.isEmpty()) return false;
        }
        return true;
    }

    private List<Object> mapCsvValues(List<String> fields, List<String> dbColumns, List<Integer> mapping) {
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < dbColumns.size(); i++) {
            Integer idx = mapping.get(i);
            values.add((idx != null && idx >= 0 && idx < fields.size()) ? fields.get(idx) : null);
        }
        return values;
    }

    // ──────────────── 共用方法 ────────────────

    private List<String> readExcelHeaderRow(Row headerRow) {
        List<String> columns = new ArrayList<>();
        if (headerRow == null) return columns;
        int cellCount = headerRow.getLastCellNum();
        for (int i = 0; i < cellCount; i++) columns.add(ExcelUtils.getCellStringValue(headerRow.getCell(i)));
        return columns;
    }

    private List<String> getTableColumns(String tableName, String jdbcUrl, String username, String password)
            throws SQLException {
        List<String> columns = new ArrayList<>();
        try (Connection conn = createConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName + " WHERE 1=0")) {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) columns.add(metaData.getColumnName(i));
        }
        return columns;
    }

    private List<Integer> buildColumnMapping(List<String> sourceColumns, List<String> dbColumns,
                                              Map<String, String> explicitMapping) {
        Map<String, Integer> dbIdx = new HashMap<>();
        for (int i = 0; i < dbColumns.size(); i++) dbIdx.put(dbColumns.get(i).toLowerCase(), i);

        List<Integer> mapping = new ArrayList<>();
        for (int i = 0; i < dbColumns.size(); i++) mapping.add(-1);

        for (int si = 0; si < sourceColumns.size(); si++) {
            String sc = sourceColumns.get(si);
            if (sc == null || sc.isEmpty()) continue;

            String target = (explicitMapping != null && explicitMapping.containsKey(sc))
                    ? explicitMapping.get(sc) : sc.toLowerCase();
            Integer di = dbIdx.get(target.toLowerCase());
            if (di != null && di < mapping.size()) mapping.set(di, si);
        }
        return mapping;
    }

    private String buildInsertSql(String tableName, List<String> dbColumns, List<Integer> mapping) {
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        List<String> cols = new ArrayList<>();
        for (int i = 0; i < dbColumns.size(); i++) {
            if (mapping.get(i) >= 0) cols.add(dbColumns.get(i));
        }
        for (int i = 0; i < cols.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append(cols.get(i));
        }
        sql.append(") VALUES (");
        for (int i = 0; i < cols.size(); i++) {
            if (i > 0) sql.append(", ");
            sql.append("?");
        }
        sql.append(")");
        return sql.toString();
    }

    private List<Object> extractExcelRowValues(Row row, List<String> dbColumns, List<Integer> mapping) {
        List<Object> values = new ArrayList<>();
        for (int i = 0; i < dbColumns.size(); i++) {
            Integer idx = mapping.get(i);
            values.add((idx != null && idx >= 0) ? ExcelUtils.getCellStringValue(row.getCell(idx)) : null);
        }
        return values;
    }

    private void executeBatchInsert(String sql, List<List<Object>> batchRows,
                                     String jdbcUrl, String username, String password) {
        try (Connection conn = createConnection(jdbcUrl, username, password);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            conn.setAutoCommit(false);
            for (List<Object> row : batchRows) {
                int idx = 1;
                for (Object value : row) {
                    if (value != null) pstmt.setString(idx, value.toString());
                    else pstmt.setNull(idx, java.sql.Types.VARCHAR);
                    idx++;
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("批量插入失败: " + e.getMessage(), e);
        }
    }

    private boolean isExcelRowEmpty(Row row) {
        int cc = row.getLastCellNum();
        for (int i = 0; i < cc; i++) {
            String v = ExcelUtils.getCellStringValue(row.getCell(i));
            if (v != null && !v.isEmpty()) return false;
        }
        return true;
    }

    private String validateParam(String value, String name) {
        if (value == null || value.trim().isEmpty())
            throw new IllegalArgumentException("数据库连接参数 " + name + " 不能为空");
        return value.trim();
    }

    private Connection createConnection(String jdbcUrl, String username, String password) throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
    }

    private static class ZipEntryData {
        String name;
        String tableName;
    }
}
