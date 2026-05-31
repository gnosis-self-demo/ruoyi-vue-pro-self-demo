package com.gnosis.dataexportor.dto;

import lombok.Data;

@Data
public class ErrorRecord {
    private int sheetIndex;
    private long rowNumber;
    private String rowData;
    private String errorMessage;
}