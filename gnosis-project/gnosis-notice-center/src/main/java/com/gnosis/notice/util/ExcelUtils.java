package com.gnosis.notice.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel 导入导出工具类
 */
public class ExcelUtils {

    /**
     * 导出 Excel
     *
     * @param response 响应对象
     * @param fileName 文件名
     * @param sheetName Sheet名称
     * @param headers 表头
     * @param data 数据（每行是一个字符串数组）
     */
    public static void export(HttpServletResponse response, String fileName, String sheetName,
                              String[] headers, List<String[]> data) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet(sheetName);

        // 创建表头样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // 写入表头
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 写入数据
        if (data != null) {
            for (int i = 0; i < data.size(); i++) {
                Row row = sheet.createRow(i + 1);
                String[] rowData = data.get(i);
                for (int j = 0; j < rowData.length; j++) {
                    Cell cell = row.createCell(j);
                    cell.setCellValue(rowData[j]);
                }
            }
        }

        // 自动调整列宽
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // 输出响应
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String encodeFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodeFileName + ".xlsx");

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    /**
     * 读取 Excel 文件
     *
     * @param inputStream 输入流
     * @param hasHeader 是否有表头（第一行跳过）
     * @return 数据列表
     */
    public static List<String[]> read(InputStream inputStream, boolean hasHeader) throws IOException {
        List<String[]> data = new ArrayList<>();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        int startRow = hasHeader ? 1 : 0;
        for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) {
                continue;
            }
            int cellCount = row.getLastCellNum();
            String[] rowData = new String[cellCount];
            for (int j = 0; j < cellCount; j++) {
                Cell cell = row.getCell(j);
                rowData[j] = getCellValue(cell);
            }
            data.add(rowData);
        }
        workbook.close();
        return data;
    }

    /**
     * 获取单元格值（公共方法）
     */
    public static String getCellStringValue(Cell cell) {
        return getCellValue(cell);
    }

    /**
     * 获取单元格值
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                }
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }
}
