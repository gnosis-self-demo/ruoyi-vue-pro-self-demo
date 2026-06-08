package com.gnosis.dataexportor.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExportRequest {

    /** JDBC连接地址 */
    private String jdbcUrl;

    /** 数据库用户名 */
    private String username;

    /** 数据库密码 */
    private String password;

    /** 单条SQL（兼容旧接口） */
    private String sql;

    /** 多条SQL，每条对应一个Sheet系列 */
    private List<SqlEntry> sqlEntries;

    /** Sheet名称前缀（单SQL模式使用） */
    private String sheetNamePrefix;

    /** 自定义列名 */
    private List<String> columnNames;

    /** 用户ID（记录任务用） */
    private String userId;

    /** 任务名称 */
    private String taskName;

    @Data
    public static class SqlEntry {
        /** SQL查询语句（建议包含ORDER BY） */
        private String sql;
        /** 基础Sheet名，溢出时自动追加 _1, _2... */
        private String sheetName;
        /** 自定义列名（可选，不填则从ResultSetMetaData获取） */
        private List<String> columnNames;
    }
}
