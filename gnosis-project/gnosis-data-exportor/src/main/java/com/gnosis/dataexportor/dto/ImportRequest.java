package com.gnosis.dataexportor.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ImportRequest {

    /** JDBC连接地址 */
    private String jdbcUrl;

    /** 数据库用户名 */
    private String username;

    /** 数据库密码 */
    private String password;

    /** 目标数据库表名 */
    private String tableName;

    /** 列映射（Excel列名 → 数据库字段名） */
    private Map<String, String> columnMapping;

    /** 批次大小（null则自动计算） */
    private Integer batchSize;
}
