package com.gnosis.dataexportor.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ImportResult {
    private long totalRows;
    private long successRows;
    private long failedRows;
    private long skippedRows;
    private int totalSheets;
    private long durationMs;
    private List<ErrorRecord> errors = new ArrayList<>();
}