package com.gnosis.dataexportor.service;

import com.gnosis.dataexportor.dto.ExportRequest;
import com.gnosis.dataexportor.listener.ProgressListener;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public interface ExcelExportService {

    void exportToExcel(ExportRequest request, HttpServletResponse response, ProgressListener listener)
            throws IOException, SQLException;

    void exportToExcel(ExportRequest request, HttpServletResponse response)
            throws IOException, SQLException;
}