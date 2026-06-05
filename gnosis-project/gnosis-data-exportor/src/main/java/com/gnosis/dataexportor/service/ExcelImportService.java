package com.gnosis.dataexportor.service;

import com.gnosis.dataexportor.dto.ImportRequest;
import com.gnosis.dataexportor.dto.ImportResult;
import com.gnosis.dataexportor.listener.ProgressListener;

import java.io.IOException;
import java.sql.SQLException;

public interface ExcelImportService {

    ImportResult importFromExcel(String excelPath, ImportRequest request, ProgressListener listener)
            throws IOException, SQLException;

    ImportResult importFromExcel(String excelPath, ImportRequest request)
            throws IOException, SQLException;

    /**
     * 从ZIP导入（含多个CSV，每个文件名=目标表名）
     */
    ImportResult importFromCsvZip(String zipPath, ImportRequest request, ProgressListener listener)
            throws IOException, SQLException;
}
