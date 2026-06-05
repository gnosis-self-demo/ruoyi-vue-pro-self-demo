package com.gnosis.dataexportor.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlUtils {

    private SqlUtils() {
    }

    /**
     * 清理SQL：去除尾部分号，检测多语句注入。
     * 多条SQL请使用 ExportRequest.sqlEntries 分别指定。
     */
    public static String sanitizeSql(String originalSql) {
        if (originalSql == null || originalSql.trim().isEmpty()) {
            throw new IllegalArgumentException("SQL不能为空");
        }
        String sql = originalSql.trim();
        // 去除尾部分号
        while (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1).trim();
        }
        // 检测中间是否还有分号（多语句）
        if (sql.contains(";")) {
            throw new IllegalArgumentException(
                    "SQL包含多条语句（检测到中间有分号）。"
                    + "请使用 ExportRequest.sqlEntries 为每条SQL分别指定 sheetName。"
                    + " 当前SQL: " + originalSql);
        }
        return sql;
    }

    public static String wrapCountSql(String originalSql) {
        String sql = sanitizeSql(originalSql);
        return "SELECT COUNT(*) FROM (" + sql + ") AS t";
    }

    public static boolean hasOrderBy(String sql) {
        if (sql == null) {
            return false;
        }
        String upperSql = sql.toUpperCase().trim();
        return upperSql.contains("ORDER BY");
    }

    public static String buildPagingSql(String originalSql, int limit, long offset) {
        String sql = sanitizeSql(originalSql);
        return sql + " LIMIT " + limit + " OFFSET " + offset;
    }

    public static void checkOrderBy(String sql) {
        if (!hasOrderBy(sql)) {
            log.warn("SQL未包含ORDER BY子句，分页导出可能导致数据重复或遗漏。建议添加ORDER BY确保数据一致性。");
        }
    }
}