package com.gnosis.dataexportor.controller;

import com.gnosis.common.dto.CommonResponse;
import com.gnosis.dataexportor.dto.ImportRequest;
import com.gnosis.dataexportor.dto.ImportResult;
import com.gnosis.dataexportor.service.ExcelImportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Api(tags = "数据导入管理")
@RestController
@RequestMapping("/data-exportor/import")
public class ExcelImportController {

    @Autowired
    private ExcelImportService excelImportService;

    @ApiOperation("执行数据导入")
    @PostMapping("/execute")
    public CommonResponse<ImportResult> execute(@RequestParam("file") MultipartFile file,
                                                 @RequestParam(value = "tableName", required = false) String tableName,
                                                 @RequestParam(value = "batchSize", required = false) Integer batchSize) {
        try {
            Path tempDir = Files.createTempDirectory("excel_import_");
            String tempFilePath = tempDir.resolve(UUID.randomUUID().toString() + ".xlsx").toString();
            File tempFile = new File(tempFilePath);
            file.transferTo(tempFile);

            try {
                ImportRequest request = new ImportRequest();
                request.setTableName(tableName);
                request.setBatchSize(batchSize);

                ImportResult result = excelImportService.importFromExcel(tempFilePath, request);
                return CommonResponse.success(result);
            } finally {
                try {
                    Files.deleteIfExists(tempFile.toPath());
                    Files.deleteIfExists(tempDir);
                } catch (IOException ignored) {
                }
            }
        } catch (Exception e) {
            return CommonResponse.error("导入失败: " + e.getMessage());
        }
    }

    @ApiOperation("下载导入模板")
    @PostMapping("/downloadTemplate")
    public CommonResponse<String> downloadTemplate() {
        return CommonResponse.success("模板下载功能待实现");
    }

    @ApiOperation("查询导入任务记录")
    @PostMapping("/page")
    public CommonResponse<String> page(@RequestParam(value = "tableName", required = false) String tableName) {
        return CommonResponse.success("分页查询功能待实现");
    }

    @ApiOperation("查询导入任务详情")
    @PostMapping("/detail")
    public CommonResponse<String> detail(@RequestParam(value = "taskId", required = false) String taskId) {
        return CommonResponse.success("详情查询功能待实现");
    }
}