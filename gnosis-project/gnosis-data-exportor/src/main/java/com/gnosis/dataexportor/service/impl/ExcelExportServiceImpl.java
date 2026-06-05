package com.gnosis.dataexportor.service.impl;

import com.gnosis.dataexportor.calculator.BatchSizeCalculator;
import com.gnosis.dataexportor.dto.ExportRequest;
import com.gnosis.dataexportor.listener.LoggingProgressListener;
import com.gnosis.dataexportor.listener.ProgressListener;
import com.gnosis.dataexportor.service.ExcelExportService;
import com.gnosis.dataexportor.util.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class ExcelExportServiceImpl implements ExcelExportService {

    private static final int MAX_RETRY_COUNT = 3;
    private static final int FALLBACK_BATCH_SIZE = 10000;

    @Override
    public void exportToExcel(ExportRequest request, HttpServletResponse response) throws IOException, SQLException {
        exportToExcel(request, response, new LoggingProgressListener());
    }

    @Override
    public void exportToExcel(ExportRequest request, HttpServletResponse response, ProgressListener listener)
            throws IOException, SQLException {

        List<ExportRequest.SqlEntry> entries = buildEntries(request);
        String jdbcUrl = validateParam(request.getJdbcUrl(), "jdbcUrl");
        String username = validateParam(request.getUsername(), "username");
        String password = request.getPassword() != null ? request.getPassword() : "";

        long startTime = System.currentTimeMillis();
        long totalExportedRows = 0;
        int totalEntries = entries.size();
        boolean multiple = totalEntries > 1;

        if (multiple) {
            // 多条SQL → ZIP，每个SQL一个CSV，sheetName作文件名
            response.setContentType("application/zip");
            response.setHeader("Content-Disposition", "attachment; filename=export_data.zip");
            try (ZipOutputStream zos = new ZipOutputStream(response.getOutputStream())) {
                for (ExportRequest.SqlEntry entry : entries) {
                    String csvName = entry.getSheetName() + ".csv";
                    zos.putNextEntry(new ZipEntry(csvName));
                    Writer writer = new OutputStreamWriter(zos, StandardCharsets.UTF_8);
                    writer.write('\uFEFF'); // UTF-8 BOM
                    long rows = exportSingleSqlToCsv(writer, entry, listener, jdbcUrl, username, password);
                    writer.flush();
                    zos.closeEntry();
                    totalExportedRows += rows;
                }
            }
        } else {
            // 单条SQL → CSV
            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=export_data.csv");
            try (Writer writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write('\uFEFF');
                totalExportedRows = exportSingleSqlToCsv(writer, entries.get(0), listener, jdbcUrl, username, password);
                writer.flush();
            }
        }

        long durationMs = System.currentTimeMillis() - startTime;
        listener.onComplete(totalExportedRows, totalEntries, durationMs);
        log.info("导出完成，总行数：{}，条目数：{}，耗时：{} ms", totalExportedRows, totalEntries, durationMs);
    }

    // ──────────────── single SQL → CSV ────────────────

    private long exportSingleSqlToCsv(Writer writer, ExportRequest.SqlEntry entry,
                                       ProgressListener listener,
                                       String jdbcUrl, String username, String password) {
        String sql = entry.getSql();
        SqlUtils.checkOrderBy(sql);

        long totalRows = detectTotalRows(sql, jdbcUrl, username, password);
        boolean totalRowsUnknown = (totalRows < 0);
        int batchSize = calculateBatchSize(totalRows);

        log.info("[{}] 总行数：{}，batchSize = {}", entry.getSheetName(),
                totalRowsUnknown ? "未知" : totalRows, batchSize);

        long exportedRows = 0;
        long offset = 0;
        List<String> columnNames = null;
        boolean headerWritten = false;

        while (totalRowsUnknown || offset < totalRows) {
            String pagingSql = SqlUtils.buildPagingSql(sql, batchSize, offset);
            List<Object[]> batchData = executePagingQuery(pagingSql, jdbcUrl, username, password);

            if (totalRowsUnknown && batchData.isEmpty()) break;

            if (columnNames == null && !batchData.isEmpty()) {
                columnNames = resolveColumnNames(sql, entry.getColumnNames(), jdbcUrl, username, password);
            }

            try {
                for (Object[] rowData : batchData) {
                    if (!headerWritten) {
                        writeCsvRow(writer, columnNames);
                        headerWritten = true;
                    }
                    writeCsvRow(writer, objectArrayToStringList(rowData));
                    exportedRows++;
                }
            } catch (IOException e) {
                throw new RuntimeException("CSV写入失败: " + e.getMessage(), e);
            }

            listener.onProgress(exportedRows, totalRowsUnknown ? -1 : totalRows,
                    (int) (offset / batchSize) + 1);
            offset += batchSize;
            if (totalRowsUnknown && batchData.size() < batchSize) break;
        }

        log.info("[{}] 导出完成，行数：{}", entry.getSheetName(), exportedRows);
        return exportedRows;
    }

    // ──────────────── CSV formatting (RFC 4180) ────────────────

    private void writeCsvRow(Writer writer, List<String> fields) throws IOException {
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) writer.write(',');
            writer.write(escapeCsvField(fields.get(i)));
        }
        writer.write("\r\n");
    }

    static String escapeCsvField(String value) {
        if (value == null) return "";
        boolean needsQuote = value.indexOf(',') >= 0
                || value.indexOf('"') >= 0
                || value.indexOf('\n') >= 0
                || value.indexOf('\r') >= 0;
        if (needsQuote) return '"' + value.replace("\"", "\"\"") + '"';
        return value;
    }

    private List<String> objectArrayToStringList(Object[] row) {
        List<String> result = new ArrayList<>(row.length);
        for (Object obj : row) result.add(obj == null ? "" : obj.toString());
        return result;
    }

    // ──────────────── DB helpers ────────────────

    private String validateParam(String value, String name) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("数据库连接参数 " + name + " 不能为空");
        }
        return value.trim();
    }

    private Connection createConnection(String jdbcUrl, String username, String password) throws SQLException {
        return DriverManager.getConnection(jdbcUrl, username, password);
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
                if (rs.next()) return rs.getLong(1);
            }
            return 0L;
        } catch (Exception e) {
            log.warn("COUNT查询异常，采用保守batchSize={}继续导出", FALLBACK_BATCH_SIZE, e);
            return -1L;
        }
    }

    private int calculateBatchSize(long totalRows) {
        if (totalRows <= 0) return FALLBACK_BATCH_SIZE;
        return BatchSizeCalculator.calculateExportBatchSize(totalRows);
    }

    private List<Object[]> executePagingQuery(String sql, String jdbcUrl, String username, String password) {
        int retryCount = 0;
        while (retryCount <= MAX_RETRY_COUNT) {
            try (Connection conn = createConnection(jdbcUrl, username, password);
                 PreparedStatement pstmt = createPagingStatement(conn, sql);
                 ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                List<Object[]> result = new ArrayList<>();
                while (rs.next()) {
                    Object[] row = new Object[columnCount];
                    for (int i = 0; i < columnCount; i++) row[i] = rs.getObject(i + 1);
                    result.add(row);
                }
                return result;
            } catch (Exception e) {
                retryCount++;
                if (retryCount > MAX_RETRY_COUNT) throw new RuntimeException("分页查询失败: " + e.getMessage(), e);
                log.warn("分页查询失败，第{}次重试...", retryCount);
                try { Thread.sleep(1000L * retryCount); } catch (InterruptedException ie) {
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
        if (customColumnNames != null && !customColumnNames.isEmpty()) return customColumnNames;
        List<String> columnNames = new ArrayList<>();
        String limitedSql = SqlUtils.buildPagingSql(sql, 1, 0);
        try (Connection conn = createConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(limitedSql)) {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++)
                columnNames.add(metaData.getColumnLabel(i));
        } catch (SQLException e) { log.warn("获取列名失败", e); }
        return columnNames;
    }
}
