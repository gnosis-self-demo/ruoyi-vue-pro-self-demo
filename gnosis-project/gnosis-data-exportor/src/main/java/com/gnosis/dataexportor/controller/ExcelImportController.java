package com.gnosis.dataexportor.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.dataexportor.dto.ImportExecuteRequest;
import com.gnosis.dataexportor.dto.ImportRequest;
import com.gnosis.dataexportor.dto.ImportResult;
import com.gnosis.dataexportor.service.ExcelImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Api(tags = "数据导入管理")
@RestController
public class ExcelImportController {

    @Autowired
    private ExcelImportService excelImportService;

    @ApiOperation("执行数据导入")
    @PostMapping("/data-exportor/import/execute")
    public BaseResponse<ImportResult> execute(@RequestBody ImportExecuteRequest body) {
        try {
            if (body.getJdbcUrl() == null || body.getJdbcUrl().trim().isEmpty()) {
                return BaseResponse.error("jdbcUrl 不能为空");
            }
            if (body.getUsername() == null || body.getUsername().trim().isEmpty()) {
                return BaseResponse.error("username 不能为空");
            }

            byte[] fileBytes = body.decodeFileData();
            String fileName = body.getFileName() != null ? body.getFileName() : "import.xlsx";

            Path tempDir = Files.createTempDirectory("excel_import_");
            String tempFilePath = tempDir.resolve(UUID.randomUUID().toString() + "_" + fileName).toString();
            File tempFile = new File(tempFilePath);
            Files.write(tempFile.toPath(), fileBytes,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            try {
                ImportRequest request = body.toImportRequest();
                ImportResult result;

                // 根据文件扩展名路由：.zip → CSV多表导入，.xlsx/.xls → Excel导入
                String lowerName = fileName.toLowerCase();
                if (lowerName.endsWith(".zip")) {
                    result = excelImportService.importFromCsvZip(tempFilePath, request,
                            new com.gnosis.dataexportor.listener.LoggingProgressListener());
                } else {
                    if (request.getTableName() == null || request.getTableName().trim().isEmpty()) {
                        return BaseResponse.error("Excel导入需要指定 tableName");
                    }
                    result = excelImportService.importFromExcel(tempFilePath, request);
                }

                return BaseResponse.success(result);
            } finally {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                    Files.deleteIfExists(tempDir);
                } catch (IOException ignored) {
                }
            }
        } catch (Exception e) {
            return BaseResponse.error("导入失败: " + e.getMessage());
        }
    }

    @ApiOperation("下载导入模板")
    @PostMapping("/data-exportor/import/downloadTemplate")
    public BaseResponse<String> downloadTemplate(@RequestBody ImportExecuteRequest body) {
        return BaseResponse.success("模板下载功能待实现");
    }

    @ApiOperation("查询导入任务记录")
    @PostMapping("/data-exportor/import/page")
    public BaseResponse<String> page(@RequestBody ImportExecuteRequest body) {
        return BaseResponse.success("分页查询功能待实现");
    }

    @ApiOperation("查询导入任务详情")
    @PostMapping("/data-exportor/import/detail")
    public BaseResponse<String> detail(@RequestBody ImportExecuteRequest body) {
        return BaseResponse.success("详情查询功能待实现");
    }
}
