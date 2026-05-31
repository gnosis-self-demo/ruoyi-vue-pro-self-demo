package com.gnosis.dataexportor.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlUtils {

    private SqlUtils() {
    }

    public static String wrapCountSql(String originalSql) {
        return "SELECT COUNT(*) FROM (" + originalSql + ") AS t";
    }

    public static boolean hasOrderBy(String sql) {
        if (sql == null) {
            return false;
        }
        String upperSql = sql.toUpperCase().trim();
        return upperSql.contains("ORDER BY");
    }

    public static String buildPagingSql(String originalSql, int limit, long offset) {
        String trimmedSql = originalSql.trim();
        if (trimmedSql.endsWith(";")) {
            trimmedSql = trimmedSql.substring(0, trimmedSql.length() - 1);
        }
        return trimmedSql + " LIMIT " + limit + " OFFSET " + offset;
    }

    public static void checkOrderBy(String sql) {
        if (!hasOrderBy(sql)) {
            log.warn("SQL未包含ORDER BY子句，分页导出可能导致数据重复或遗漏。建议添加ORDER BY确保数据一致性。");
        }
    }
}