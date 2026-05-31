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
}