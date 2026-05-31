package com.gnosis.dataexportor.listener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoggingProgressListener implements ProgressListener {

    @Override
    public void onStart(long totalRows, int totalSheets) {
        log.info("任务开始，总行数：{}，总Sheet数：{}", totalRows, totalSheets);
    }

    @Override
    public void onProgress(long processedRows, long totalRows, int batchIndex) {
        if (totalRows > 0 && processedRows % 10000 == 0) {
            double percent = (processedRows * 100.0) / totalRows;
            log.info("进度：{} / {} 行 ({:.2f}%)，批次：{}",
                    processedRows, totalRows, percent, batchIndex);
        }
    }

    @Override
    public void onSheetSwitch(String sheetName, int sheetIndex, long rowsInSheet) {
        log.info("切换到Sheet：{}（第{}个Sheet），已写入 {} 行",
                sheetName, sheetIndex, rowsInSheet);
    }

    @Override
    public void onComplete(long totalRows, int totalSheets, long durationMs) {
        log.info("任务完成，总行数：{}，总Sheet数：{}，耗时：{} ms",
                totalRows, totalSheets, durationMs);
    }

    @Override
    public boolean onError(String errorMsg, long rowNumber, Throwable cause) {
        log.error("第 {} 行发生错误：{}", rowNumber, errorMsg, cause);
        return false;
    }
}