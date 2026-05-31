package com.gnosis.dataexportor.calculator;

public class BatchSizeCalculator {

    private static final int MAX_SHEET_ROWS = 1048576;

    private BatchSizeCalculator() {
    }

    public static int calculateExportBatchSize(long totalRows) {
        if (totalRows <= 5000) {
            return (int) totalRows;
        } else if (totalRows <= 100000) {
            return 5000;
        } else if (totalRows <= 500000) {
            return 10000;
        } else if (totalRows <= 2000000) {
            return 20000;
        } else {
            return 50000;
        }
    }

    public static int calculateImportBatchSize(long totalRows) {
        if (totalRows <= 5000) {
            return (int) totalRows;
        }
        return 5000;
    }

    public static int calculateSheetsNeeded(long totalRows) {
        if (totalRows <= 0) {
            return 1;
        }
        return (int) Math.ceil((double) totalRows / MAX_SHEET_ROWS);
    }

    public static int getMaxSheetRows() {
        return MAX_SHEET_ROWS;
    }
}