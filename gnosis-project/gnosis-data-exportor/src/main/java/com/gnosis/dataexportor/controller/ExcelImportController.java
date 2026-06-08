package com.gnosis.dataexportor.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.dataexportor.dto.ImportExecuteRequest;
import com.gnosis.dataexportor.dto.ImportRequest;
import com.gnosis.dataexportor.dto.ImportResult;
import com.gnosis.dataexportor.dto.TaskPageQueryRequest;
import com.gnosis.dataexportor.dto.TaskPageResponse;
import com.gnosis.dataexportor.entity.DataImportTask;
import com.gnosis.dataexportor.service.ExcelImportService;
import com.gnosis.dataexportor.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.UUID;

@Api(tags = "数据导入管理")
@RestController
public class ExcelImportController {

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private TaskService taskService;

    @ApiOperation("执行数据导入")
    @PostMapping("/data-exportor/import/execute")
    public BaseResponse<ImportResult> execute(@RequestBody ImportExecuteRequest body) {
        String userId = body.getUserId() != null ? body.getUserId() : "system";
        String taskName = body.getTaskName() != null ? body.getTaskName() : "数据导入";

        try {
            if (body.getJdbcUrl() == null || body.getJdbcUrl().trim().isEmpty()) {
                return BaseResponse.error("jdbcUrl 不能为空");
            }
            if (body.getUsername() == null || body.getUsername().trim().isEmpty()) {
                return BaseResponse.error("username 不能为空");
            }

            byte[] fileBytes = body.decodeFileData();
            String fileName = body.getFileName() != null ? body.getFileName() : "import.xlsx";
            boolean isZip = fileName.toLowerCase().endsWith(".zip");

            Path tempDir = Files.createTempDirectory("excel_import_");
            String tempFilePath = tempDir.resolve(UUID.randomUUID().toString() + "_" + fileName).toString();
            File tempFile = new File(tempFilePath);
            Files.write(tempFile.toPath(), fileBytes,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

            // 记录任务
            DataImportTask task = new DataImportTask();
            task.setTaskName(taskName);
            task.setFileName(fileName);
            task.setImportFormat(isZip ? "ZIP" : "EXCEL");
            task.setTableName(body.getTableName());
            task.setStatus("RUNNING");
            task.setCreateUserId(userId);
            task.setUpdateUserId(userId);
            try {
                taskService.saveImportTask(task);
            } catch (Exception e) {
                // 记录任务失败不影响导入
            }

            long startTime = System.currentTimeMillis();
            try {
                ImportRequest request = body.toImportRequest();
                ImportResult result;

                if (isZip) {
                    result = excelImportService.importFromCsvZip(tempFilePath, request,
                            new com.gnosis.dataexportor.listener.LoggingProgressListener());
                } else {
                    if (request.getTableName() == null || request.getTableName().trim().isEmpty()) {
                        return BaseResponse.error("Excel导入需要指定 tableName");
                    }
                    result = excelImportService.importFromExcel(tempFilePath, request);
                }

                // 更新任务为成功
                task.setStatus("SUCCESS");
                task.setTotalRows(result.getTotalRows());
                task.setSuccessRows(result.getSuccessRows());
                task.setFailedRows(result.getFailedRows());
                task.setSkippedRows(result.getSkippedRows());
                task.setTotalSheets(result.getTotalSheets());
                task.setDurationMs(System.currentTimeMillis() - startTime);
                try {
                    taskService.updateImportTask(task);
                } catch (Exception ignored) {
                }

                return BaseResponse.success(result);
            } catch (Exception e) {
                // 更新任务为失败
                task.setStatus("FAILED");
                task.setErrorMessage(e.getMessage());
                task.setDurationMs(System.currentTimeMillis() - startTime);
                try {
                    taskService.updateImportTask(task);
                } catch (Exception ignored) {
                }
                throw e;
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
    public void downloadTemplate(@RequestBody Map<String, String> body, HttpServletResponse response) {
        try {
            String tableName = body.get("tableName");
            String jdbcUrl = body.get("jdbcUrl");
            String username = body.get("username");
            String password = body.get("password") != null ? body.get("password") : "";

            if (tableName == null || tableName.trim().isEmpty()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"msg\":\"tableName 不能为空\"}");
                return;
            }
            if (jdbcUrl == null || jdbcUrl.trim().isEmpty()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"msg\":\"jdbcUrl 不能为空\"}");
                return;
            }
            if (username == null || username.trim().isEmpty()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"msg\":\"username 不能为空\"}");
                return;
            }

            taskService.downloadTemplate(tableName, jdbcUrl, username, password, response);
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":500,\"msg\":\"下载模板失败: "
                        + e.getMessage().replace("\"", "\\\"") + "\"}");
            } catch (Exception ignored) {
            }
        }
    }

    @ApiOperation("查询导入任务记录")
    @PostMapping("/data-exportor/import/page")
    public BaseResponse<TaskPageResponse<DataImportTask>> page(@RequestBody TaskPageQueryRequest request) {
        try {
            return BaseResponse.success(taskService.pageImportTasks(request));
        } catch (Exception e) {
            return BaseResponse.error("查询失败: " + e.getMessage());
        }
    }

    @ApiOperation("查询导入任务详情")
    @PostMapping("/data-exportor/import/detail")
    public BaseResponse<DataImportTask> detail(@RequestBody Map<String, String> body) {
        try {
            String id = body.get("id");
            if (id == null || id.trim().isEmpty()) {
                return BaseResponse.error("id 不能为空");
            }
            DataImportTask task = taskService.getImportTaskDetail(id);
            if (task == null) {
                return BaseResponse.error("任务不存在");
            }
            return BaseResponse.success(task);
        } catch (Exception e) {
            return BaseResponse.error("查询失败: " + e.getMessage());
        }
    }
}