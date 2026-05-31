package com.gnosis.dataexportor.dto;

import lombok.Data;

@Data
public class ExportResponse {
    private String filePath;
    private long totalRows;
    private int totalSheets;
    private long durationMs;
}