package com.gnosis.dataexportor.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ImportRequest {
    private String tableName;
    private Map<String, String> columnMapping;
    private Integer batchSize;
}