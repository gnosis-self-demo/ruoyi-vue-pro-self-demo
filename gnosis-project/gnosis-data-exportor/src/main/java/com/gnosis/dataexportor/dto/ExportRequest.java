package com.gnosis.dataexportor.dto;

import lombok.Data;

import java.util.List;

@Data
public class ExportRequest {
    private String sql;
    private String sheetNamePrefix;
    private List<String> columnNames;
}