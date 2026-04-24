package com.gnosis.datamapping.service;

import com.alibaba.fastjson.JSON;
import com.gnosis.datamapping.domain.DataMappingConfig;
import com.gnosis.datamapping.dto.DataMappingConfigIdsRequest;
import com.gnosis.datamapping.dto.DataMappingConfigCreateRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class DataMappingImportExportService {

    @Autowired
    private DataMappingConfigService configService;

    public void exportConfigs(DataMappingConfigIdsRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=data_mapping_config.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("数据映射配置");

        Row headerRow = sheet.createRow(0);
        String[] headers = {"配置名称", "配置编码", "系统ID", "系统名称", "配置版本", "API版本", 
                           "目标接口地址", "JSON根路径", "配置JSON", "描述", "状态", "创建人ID", "创建时间"};
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }

        List<DataMappingConfig> configs = getConfigsToExport(request);
        for (int i = 0; i < configs.size(); i++) {
            DataMappingConfig config = configs.get(i);
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(config.getConfigName());
            row.createCell(1).setCellValue(config.getConfigCode());
            row.createCell(2).setCellValue(config.getSystemId() != null ? config.getSystemId() : "");
            row.createCell(3).setCellValue(config.getSystemName() != null ? config.getSystemName() : "");
            row.createCell(4).setCellValue(config.getConfigVersion() != null ? config.getConfigVersion() : "");
            row.createCell(5).setCellValue(config.getApiVersion() != null ? config.getApiVersion() : "");
            row.createCell(6).setCellValue(config.getTargetEndpoint() != null ? config.getTargetEndpoint() : "");
            row.createCell(7).setCellValue(config.getJsonRootPath() != null ? config.getJsonRootPath() : "");
            row.createCell(8).setCellValue(config.getConfigJson() != null ? config.getConfigJson() : "");
            row.createCell(9).setCellValue(config.getDescription() != null ? config.getDescription() : "");
            row.createCell(10).setCellValue(config.getStatus() != null ? (config.getStatus() == 1 ? "启用" : "禁用") : "");
            row.createCell(11).setCellValue(config.getCreateUserId() != null ? config.getCreateUserId() : "");
            row.createCell(12).setCellValue(config.getCreateTime() != null ? config.getCreateTime().toString() : "");
        }

        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }

    public void importConfigs(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            Workbook workbook = WorkbookFactory.create(is);
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                DataMappingConfigCreateRequest request = new DataMappingConfigCreateRequest();
                request.setConfigName(getCellStringValue(row.getCell(0)));
                request.setConfigCode(getCellStringValue(row.getCell(1)));
                request.setSystemId(getCellStringValue(row.getCell(2)));
                request.setSystemName(getCellStringValue(row.getCell(3)));
                request.setConfigVersion(getCellStringValue(row.getCell(4)));
                request.setApiVersion(getCellStringValue(row.getCell(5)));
                request.setTargetEndpoint(getCellStringValue(row.getCell(6)));
                request.setJsonRootPath(getCellStringValue(row.getCell(7)));
                request.setConfigJson(getCellStringValue(row.getCell(8)));
                request.setDescription(getCellStringValue(row.getCell(9)));
                request.setCreateUserId("admin");

                String statusStr = getCellStringValue(row.getCell(10));
                if ("启用".equals(statusStr)) {
                } else {
                }

                configService.create(request);
            }
        }
    }

    private List<DataMappingConfig> getConfigsToExport(DataMappingConfigIdsRequest request) {
        return null;
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) return "";
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }
}
