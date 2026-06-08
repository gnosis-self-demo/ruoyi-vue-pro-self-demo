package com.gnosis.dataexportor.service.impl;

import com.gnosis.dataexportor.dto.TaskPageQueryRequest;
import com.gnosis.dataexportor.dto.TaskPageResponse;
import com.gnosis.dataexportor.entity.DataExportTask;
import com.gnosis.dataexportor.entity.DataImportTask;
import com.gnosis.dataexportor.mapper.DataExportTaskMapper;
import com.gnosis.dataexportor.mapper.DataImportTaskMapper;
import com.gnosis.dataexportor.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class TaskServiceImpl implements TaskService {

    @Autowired
    private DataExportTaskMapper dataExportTaskMapper;

    @Autowired
    private DataImportTaskMapper dataImportTaskMapper;

    // ──────────────── 导出任务 ────────────────

    @Override
    public String createExportTask(DataExportTask task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID().toString());
        }
        if (task.getStatus() == null) {
            task.setStatus("PENDING");
        }
        Date now = new Date();
        if (task.getCreateTime() == null) {
            task.setCreateTime(now);
        }
        if (task.getUpdateTime() == null) {
            task.setUpdateTime(now);
        }
        dataExportTaskMapper.insert(task);
        return task.getId();
    }

    @Override
    public void saveExportTask(DataExportTask task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID().toString());
        }
        if (task.getCreateTime() == null) {
            task.setCreateTime(new Date());
        }
        if (task.getUpdateTime() == null) {
            task.setUpdateTime(new Date());
        }
        dataExportTaskMapper.insert(task);
    }

    @Override
    public void updateExportTask(DataExportTask task) {
        task.setUpdateTime(new Date());
        dataExportTaskMapper.updateById(task);
    }

    @Override
    public void completeExportTask(String taskId, String fileName, String downloadUrl,
                                    Long totalRows, String updateUserId) {
        DataExportTask task = new DataExportTask();
        task.setId(taskId);
        task.setFileName(fileName);
        task.setDownloadUrl(downloadUrl);
        task.setTotalRows(totalRows);
        task.setStatus("SUCCESS");
        task.setDurationMs(System.currentTimeMillis() -
                (dataExportTaskMapper.selectById(taskId).getCreateTime().getTime()));
        task.setUpdateUserId(updateUserId);
        task.setUpdateTime(new Date());
        dataExportTaskMapper.updateById(task);
    }

    @Override
    public void failExportTask(String taskId, String errorMessage, String updateUserId) {
        DataExportTask task = new DataExportTask();
        task.setId(taskId);
        task.setStatus("FAILED");
        task.setErrorMessage(errorMessage);
        task.setUpdateUserId(updateUserId);
        task.setUpdateTime(new Date());
        dataExportTaskMapper.updateById(task);
    }

    @Override
    public void updateProgress(String taskId, Long processedRows, Long totalRows, String updateUserId) {
        DataExportTask task = new DataExportTask();
        task.setId(taskId);
        task.setStatus("RUNNING");
        task.setProcessedRows(processedRows);
        task.setTotalRows(totalRows);
        task.setUpdateUserId(updateUserId);
        task.setUpdateTime(new Date());
        dataExportTaskMapper.updateById(task);
    }

    @Override
    public TaskPageResponse<DataExportTask> pageExportTasks(TaskPageQueryRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("taskName", request.getTaskName());
        params.put("businessType", request.getBusinessType());
        params.put("scene", request.getScene());
        params.put("module", request.getModule());
        params.put("operator", request.getOperator());
        params.put("status", request.getStatus());
        params.put("format", request.getFormat());
        params.put("createUserId", request.getCreateUserId());
        params.put("createTimeStart", request.getCreateTimeStart());
        params.put("createTimeEnd", request.getCreateTimeEnd());

        int pageNum = request.getPageNum() != null ? request.getPageNum() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
        params.put("limit", pageSize);
        params.put("offset", (pageNum - 1) * pageSize);

        Long total = dataExportTaskMapper.selectCount(params);
        List<DataExportTask> records = dataExportTaskMapper.selectPage(params);

        TaskPageResponse<DataExportTask> response = new TaskPageResponse<>();
        response.setTotal(total);
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        response.setRecords(records);
        return response;
    }

    @Override
    public DataExportTask getExportTaskDetail(String id) {
        return dataExportTaskMapper.selectById(id);
    }

    @Override
    public int deleteExportTasks(List<String> ids) {
        return dataExportTaskMapper.deleteByIds(ids);
    }

    // ──────────────── 导入任务 ────────────────

    @Override
    public void saveImportTask(DataImportTask task) {
        if (task.getId() == null) {
            task.setId(UUID.randomUUID().toString());
        }
        if (task.getCreateTime() == null) {
            task.setCreateTime(new Date());
        }
        if (task.getUpdateTime() == null) {
            task.setUpdateTime(new Date());
        }
        dataImportTaskMapper.insert(task);
    }

    @Override
    public void updateImportTask(DataImportTask task) {
        task.setUpdateTime(new Date());
        dataImportTaskMapper.updateById(task);
    }

    @Override
    public TaskPageResponse<DataImportTask> pageImportTasks(TaskPageQueryRequest request) {
        Map<String, Object> params = new HashMap<>();
        params.put("taskName", request.getTaskName());
        params.put("status", request.getStatus());
        params.put("format", request.getFormat());
        params.put("createUserId", request.getCreateUserId());
        params.put("createTimeStart", request.getCreateTimeStart());
        params.put("createTimeEnd", request.getCreateTimeEnd());

        int pageNum = request.getPageNum() != null ? request.getPageNum() : 1;
        int pageSize = request.getPageSize() != null ? request.getPageSize() : 10;
        params.put("limit", pageSize);
        params.put("offset", (pageNum - 1) * pageSize);

        Long total = dataImportTaskMapper.selectCount(params);
        List<DataImportTask> records = dataImportTaskMapper.selectPage(params);

        TaskPageResponse<DataImportTask> response = new TaskPageResponse<>();
        response.setTotal(total);
        response.setPageNum(pageNum);
        response.setPageSize(pageSize);
        response.setRecords(records);
        return response;
    }

    @Override
    public DataImportTask getImportTaskDetail(String id) {
        return dataImportTaskMapper.selectById(id);
    }

    @Override
    public int deleteImportTasks(List<String> ids) {
        return dataImportTaskMapper.deleteBatchIds(ids);
    }

    // ──────────────── 下载导入模板 ────────────────

    @Override
    public void downloadTemplate(String tableName, String jdbcUrl, String username, String password,
                                  HttpServletResponse response) throws IOException {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("tableName 不能为空");
        }

        try (Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM " + tableName.trim() + " WHERE 1=0")) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            response.setContentType("text/csv;charset=UTF-8");
            response.setHeader("Content-Disposition",
                    "attachment; filename=" + tableName.trim() + "_template.csv");

            try (Writer writer = new OutputStreamWriter(response.getOutputStream(), StandardCharsets.UTF_8)) {
                writer.write('\uFEFF'); // UTF-8 BOM
                for (int i = 1; i <= columnCount; i++) {
                    if (i > 1) writer.write(',');
                    writer.write(ExcelExportServiceImpl.escapeCsvField(metaData.getColumnLabel(i)));
                }
                writer.write("\r\n");
                writer.flush();
            }
        } catch (Exception e) {
            log.error("下载模板失败", e);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":500,\"msg\":\"下载模板失败: "
                    + e.getMessage().replace("\"", "\\\"") + "\"}");
        }
    }
}