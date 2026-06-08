package com.gnosis.dataexportor.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.dataexportor.dto.*;
import com.gnosis.dataexportor.entity.DataExportTask;
import com.gnosis.dataexportor.service.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * 标准化导出任务API — 供系统内各业务模块调用
 *
 * <p>典型流程：</p>
 * <ol>
 *   <li>业务模块调用 POST /export/tasks/create 创建任务，获取 taskId</li>
 *   <li>业务模块异步执行导出，可选调用 POST /export/tasks/progress 更新进度</li>
 *   <li>导出完成后调用 POST /export/tasks/complete 提交文件下载URL</li>
 *   <li>失败时调用 POST /export/tasks/fail 记录错误</li>
 *   <li>用户通过 POST /export/tasks/download 下载文件</li>
 * </ol>
 */
@Api(tags = "导出任务管理（标准化API）")
@RestController
public class ExportTaskController {

    @Autowired
    private TaskService taskService;

    // ──────────────── 任务生命周期 ────────────────

    @ApiOperation("创建导出任务 — 业务模块调用此接口发起导出")
    @PostMapping("/data-exportor/export/tasks/create")
    public BaseResponse<String> create(@RequestBody CreateExportTaskRequest request) {
        try {
            DataExportTask task = new DataExportTask();
            task.setTaskName(request.getTaskName());
            task.setBusinessType(request.getBusinessType());
            task.setScene(request.getScene());
            task.setModule(request.getModule());
            task.setOperator(request.getOperator());
            task.setClientIp(request.getClientIp());
            task.setRequestParams(request.getRequestParams());
            task.setFileName(request.getFileName());
            task.setExportFormat(request.getExportFormat());
            task.setCreateUserId(request.getCreateUserId());
            task.setUpdateUserId(request.getCreateUserId());
            task.setStatus("PENDING");

            String taskId = taskService.createExportTask(task);
            return BaseResponse.success(taskId);
        } catch (Exception e) {
            return BaseResponse.error("创建任务失败: " + e.getMessage());
        }
    }

    @ApiOperation("更新导出进度")
    @PostMapping("/data-exportor/export/tasks/progress")
    public BaseResponse<String> progress(@RequestBody UpdateTaskProgressRequest request) {
        try {
            taskService.updateProgress(request.getTaskId(), request.getProcessedRows(),
                    request.getTotalRows(), request.getUpdateUserId());
            return BaseResponse.success("ok");
        } catch (Exception e) {
            return BaseResponse.error("更新进度失败: " + e.getMessage());
        }
    }

    @ApiOperation("完成导出任务 — 提交下载URL")
    @PostMapping("/data-exportor/export/tasks/complete")
    public BaseResponse<String> complete(@RequestBody CompleteExportTaskRequest request) {
        try {
            taskService.completeExportTask(request.getTaskId(), request.getFileName(),
                    request.getDownloadUrl(), request.getTotalRows(), request.getUpdateUserId());
            return BaseResponse.success("ok");
        } catch (Exception e) {
            return BaseResponse.error("完成任务失败: " + e.getMessage());
        }
    }

    @ApiOperation("标记导出任务失败")
    @PostMapping("/data-exportor/export/tasks/fail")
    public BaseResponse<String> fail(@RequestBody FailExportTaskRequest request) {
        try {
            taskService.failExportTask(request.getTaskId(), request.getErrorMessage(),
                    request.getUpdateUserId());
            return BaseResponse.success("ok");
        } catch (Exception e) {
            return BaseResponse.error("标记失败: " + e.getMessage());
        }
    }

    // ──────────────── 查询 ────────────────

    @ApiOperation("分页查询导出任务")
    @PostMapping("/data-exportor/export/tasks/page")
    public BaseResponse<TaskPageResponse<DataExportTask>> page(@RequestBody TaskPageQueryRequest request) {
        try {
            return BaseResponse.success(taskService.pageExportTasks(request));
        } catch (Exception e) {
            return BaseResponse.error("查询失败: " + e.getMessage());
        }
    }

    @ApiOperation("查询导出任务详情")
    @PostMapping("/data-exportor/export/tasks/detail")
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

    // ──────────────── 删除 ────────────────

    @ApiOperation("批量删除导出任务")
    @PostMapping("/data-exportor/export/tasks/delete")
    public BaseResponse<Integer> delete(@RequestBody Map<String, List<String>> body) {
        try {
            List<String> ids = body.get("ids");
            if (ids == null || ids.isEmpty()) {
                return BaseResponse.error("ids 不能为空");
            }
            int count = taskService.deleteExportTasks(ids);
            return BaseResponse.success(count);
        } catch (Exception e) {
            return BaseResponse.error("删除失败: " + e.getMessage());
        }
    }

    // ──────────────── 下载 ────────────────

    @ApiOperation("下载导出文件 — 根据任务ID下载")
    @PostMapping("/data-exportor/export/tasks/download")
    public void download(@RequestBody Map<String, String> body, HttpServletResponse response) {
        try {
            String taskId = body.get("taskId");
            if (taskId == null || taskId.trim().isEmpty()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"msg\":\"taskId 不能为空\"}");
                return;
            }

            DataExportTask task = taskService.getExportTaskDetail(taskId);
            if (task == null) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"msg\":\"任务不存在\"}");
                return;
            }

            if (task.getDownloadUrl() == null || task.getDownloadUrl().trim().isEmpty()) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"code\":500,\"msg\":\"该任务未上传文件\"}");
                return;
            }

            // 代理下载：从 downloadUrl 获取文件并流式返回
            URL url = new URL(task.getDownloadUrl());
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(60000);

            String fileName = task.getFileName() != null ? task.getFileName() : "download";
            response.setContentType(conn.getContentType());
            response.setHeader("Content-Disposition",
                    "attachment; filename=\"" + java.net.URLEncoder.encode(fileName, "UTF-8") + "\"");
            response.setContentLength(conn.getContentLength());

            try (OutputStream os = response.getOutputStream();
                 java.io.InputStream is = conn.getInputStream()) {
                byte[] buffer = new byte[8192];
                int n;
                while ((n = is.read(buffer)) != -1) {
                    os.write(buffer, 0, n);
                }
                os.flush();
            }
        } catch (Exception e) {
            response.setContentType("application/json;charset=UTF-8");
            try {
                response.getWriter().write("{\"code\":500,\"msg\":\"下载失败: "
                        + e.getMessage().replace("\"", "\\\"") + "\"}");
            } catch (IOException ignored) {
            }
        }
    }
}