package com.gnosis.dataexportor.dto;

import lombok.Data;

import java.util.Base64;
import java.util.Map;

@Data
public class ImportExecuteRequest {

    /** JDBC连接地址 */
    private String jdbcUrl;

    /** 数据库用户名 */
    private String username;

    /** 数据库密码 */
    private String password;

    /** 目标数据库表名 */
    private String tableName;

    /** 列映射（Excel列名 → 数据库字段名，可选） */
    private Map<String, String> columnMapping;

    /** 批次大小（null则自动计算） */
    private Integer batchSize;

    /** 文件名（含扩展名，如 data.xlsx） */
    private String fileName;

    /** 文件内容（base64编码） */
    private String fileData;

    /**
     * 将base64解码为字节数组
     */
    public byte[] decodeFileData() {
        if (fileData == null || fileData.isEmpty()) {
            throw new IllegalArgumentException("fileData 不能为空");
        }
        return Base64.getDecoder().decode(fileData);
    }

    /**
     * 转为 ImportRequest
     */
    public ImportRequest toImportRequest() {
        ImportRequest r = new ImportRequest();
        r.setJdbcUrl(jdbcUrl);
        r.setUsername(username);
        r.setPassword(password);
        r.setTableName(tableName);
        r.setColumnMapping(columnMapping);
        r.setBatchSize(batchSize);
        return r;
    }
}
