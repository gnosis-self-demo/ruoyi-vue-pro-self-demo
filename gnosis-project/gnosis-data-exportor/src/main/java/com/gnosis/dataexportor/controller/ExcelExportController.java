package com.gnosis.dataexportor.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.dataexportor.dto.ExportRequest;
import com.gnosis.dataexportor.dto.TaskPageQueryRequest;
import com.gnosis.dataexportor.dto.TaskPageResponse;
import com.gnosis.dataexportor.entity.DataExportTask;
import com.gnosis.dataexportor.service.ExcelExportService;
import com.gnosis.dataexportor.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.Map;

@Api(tags = "数据导出管理")
@RestController
public class ExcelExportController {

    @Autowired
    private ExcelExportService excelExportService;

    @Autowired
    private TaskService taskService;

    @ApiOperation("执行数据导出")
    @PostMapping("/data-exportor/export/execute")
    public void execute(@RequestBody ExportRequest request, HttpServletResponse response) {
        String userId = request.getUserId() != null ? request.getUserId() : "system";
        String taskName = request.getTaskName() != null ? request.getTaskName() : "数据导出";

        // 记录任务
        DataExportTask task = new DataExportTask();
        task.setTaskName(taskName);
        task.setStatus("RUNNING");
        task.setCreateUserId(userId);
        task.setUpdateUserId(userId);
        if (request.getSqlEntries() != null && !request.getSqlEntries().isEmpty()) {
            StringBuilder sqls = new StringBuilder();
            StringBuilder names = new StringBuilder();
            for (ExportRequest.SqlEntry e : request.getSqlEntries()) {
                if (sqls.length() > 0) sqls.append("; ");
                sqls.append(e.getSql());
                if (names.length() > 0) names.append(", ");
                names.append(e.getSheetName());
            }
            task.setSqlContent(sqls.toString());
            task.setSheetName(names.toString());
            task.setExportFormat(request.getSqlEntries().size() > 1 ? "ZIP" : "CSV");
            task.setFileName(request.getSqlEntries().size() > 1 ? "export_data.zip" : "export_data.csv");
        } else if (request.getSql() != null) {
            task.setSqlContent(request.getSql());
            task.setSheetName(request.getSheetNamePrefix() != null ? request.getSheetNamePrefix() : "Sheet");
            task.setExportFormat("CSV");
            task.setFileName("export_data.csv");
        }
        try {
            taskService.saveExportTask(task);
        } catch (Exception e) {
            // 记录任务失败不影响导出
        }

        long startTime = System.currentTimeMillis();
        try {
            excelExportService.exportToExcel(request, response);

            // 更新任务为成功
            task.setStatus("SUCCESS");
            task.setDurationMs(System.currentTimeMillis() - startTime);
            taskService.updateExportTask(task);
        } catch (Exception e) {
            // 更新任务为失败
            task.setStatus("FAILED");
            task.setErrorMessage(e.getMessage());
            task.setDurationMs(System.currentTimeMillis() - startTime);
            try {
                taskService.updateExportTask(task);
            } catch (Exception ignored) {
            }

            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":500,\"msg\":\"导出失败: "
                        + e.getMessage().replace("\"", "\\\"") + "\"}");
            } catch (Exception ignored) {
            }
        }
    }

    @ApiOperation("查询导出任务记录")
    @PostMapping("/data-exportor/export/page")
    public BaseResponse<TaskPageResponse<DataExportTask>> page(@RequestBody TaskPageQueryRequest request) {
        try {
            return BaseResponse.success(taskService.pageExportTasks(request));
        } catch (Exception e) {
            return BaseResponse.error("查询失败: " + e.getMessage());
        }
    }

    @ApiOperation("查询导出任务详情")
    @PostMapping("/data-exportor/export/detail")
    public BaseResponse<DataExportTask> detail(@RequestBody Map<String, String> body) {
        try {
            String id = body.get("id");
            if (id == null || id.trim().isEmpty()) {
                return BaseResponse.error("id 不能为空");
            }
            DataExportTask task = taskService.getExportTaskDetail(id);
            if (task == null) {
                return BaseResponse.error("任务不存在");
            }
            return BaseResponse.success(task);
        } catch (Exception e) {
            return BaseResponse.error("查询失败: " + e.getMessage());
        }
    }
}