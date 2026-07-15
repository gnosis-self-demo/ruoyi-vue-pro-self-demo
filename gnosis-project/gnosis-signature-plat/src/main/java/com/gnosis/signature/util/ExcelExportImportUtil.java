package com.gnosis.signature.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel导入导出工具类
 * 基于Apache POI实现通用的Excel导出和导入功能
 */
public class ExcelExportImportUtil {

    private ExcelExportImportUtil() {
    }

    /**
     * 导出数据到Excel
     *
     * @param response  HttpServletResponse
     * @param fileName  文件名(不含扩展名)
     * @param headers   表头(列名 -> 显示名)
     * @param dataList  数据列表(每行是一个 Map: 列名 -> 值)
     */
    public static void exportExcel(HttpServletResponse response, String fileName,
                                   LinkedHashMap<String, String> headers,
                                   List<Map<String, Object>> dataList) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(fileName);

        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);

        // 写表头
        Row headerRow = sheet.createRow(0);
        int colIndex = 0;
        List<String> keys = new ArrayList<String>(headers.keySet());
        for (String key : keys) {
            Cell cell = headerRow.createCell(colIndex);
            cell.setCellValue(headers.get(key));
            cell.setCellStyle(headerStyle);
            sheet.setColumnWidth(colIndex, 4000);
            colIndex++;
        }

        // 写数据
        for (int i = 0; i < dataList.size(); i++) {
            Row row = sheet.createRow(i + 1);
            Map<String, Object> data = dataList.get(i);
            for (int j = 0; j < keys.size(); j++) {
                Cell cell = row.createCell(j);
                Object value = data.get(keys.get(j));
                if (value != null) {
                    cell.setCellValue(String.valueOf(value));
                } else {
                    cell.setCellValue("");
                }
            }
        }

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment;filename=" + URLEncoder.encode(fileName + ".xlsx", "UTF-8"));
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition");

        OutputStream outputStream = response.getOutputStream();
        workbook.write(outputStream);
        outputStream.flush();
        outputStream.close();
        workbook.close();
    }

    /**
     * 从Excel导入数据
     *
     * @param inputStream 文件输入流
     * @param headers     表头(列名 -> 显示名)，用于映射列索引
     * @return 数据列表(每行是一个 Map: 列名 -> 值)
     */
    public static List<Map<String, Object>> importExcel(InputStream inputStream,
                                                        LinkedHashMap<String, String> headers) throws IOException {
        List<Map<String, Object>> resultList = new ArrayList<Map<String, Object>>();

        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        // 读取表头行，建立列名到索引的映射
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            workbook.close();
            return resultList;
        }

        List<String> keys = new ArrayList<String>(headers.keySet());
        Map<Integer, String> colIndexToKey = new java.util.HashMap<Integer, String>();

        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell == null) {
                continue;
            }
            String headerName = cell.getStringCellValue();
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                if (entry.getValue().equals(headerName)) {
                    colIndexToKey.put(i, entry.getKey());
                    break;
                }
            }
        }

        // 读取数据行
        int lastRowNum = sheet.getLastRowNum();
        for (int i = 1; i <= lastRowNum; i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            Map<String, Object> rowData = new LinkedHashMap<String, Object>();
            boolean hasData = false;
            for (Map.Entry<Integer, String> entry : colIndexToKey.entrySet()) {
                Cell cell = row.getCell(entry.getKey());
                String value = "";
                if (cell != null) {
                    cell.setCellType(org.apache.poi.ss.usermodel.CellType.STRING);
                    value = cell.getStringCellValue();
                    if (value != null && !value.isEmpty()) {
                        hasData = true;
                    }
                }
                rowData.put(entry.getValue(), value);
            }
            if (hasData) {
                resultList.add(rowData);
            }
        }

        workbook.close();
        return resultList;
    }
}
