package com.gnosis.dataexportor.listener;

public interface ProgressListener {

    void onStart(long totalRows, int totalSheets);

    void onProgress(long processedRows, long totalRows, int batchIndex);

    void onSheetSwitch(String sheetName, int sheetIndex, long rowsInSheet);

    void onComplete(long totalRows, int totalSheets, long durationMs);

    boolean onError(String errorMsg, long rowNumber, Throwable cause);
}