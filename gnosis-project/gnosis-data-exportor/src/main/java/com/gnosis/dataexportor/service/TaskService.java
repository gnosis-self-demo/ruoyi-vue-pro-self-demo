package com.gnosis.dataexportor.service;

import com.gnosis.dataexportor.dto.TaskPageQueryRequest;
import com.gnosis.dataexportor.dto.TaskPageResponse;
import com.gnosis.dataexportor.entity.DataExportTask;
import com.gnosis.dataexportor.entity.DataImportTask;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface TaskService {

    // ──────────────── 导出任务 CRUD ────────────────

    /** 创建导出任务（返回任务ID） */
    String createExportTask(DataExportTask task);

    /** 保存导出任务 */
    void saveExportTask(DataExportTask task);

    /** 更新导出任务 */
    void updateExportTask(DataExportTask task);

    /** 完成任务（设置下载URL） */
    void completeExportTask(String taskId, String fileName, String downloadUrl,
                            Long totalRows, String updateUserId);

    /** 标记失败 */
    void failExportTask(String taskId, String errorMessage, String updateUserId);

    /** 更新进度 */
    void updateProgress(String taskId, Long processedRows, Long totalRows, String updateUserId);

    /** 分页查询导出任务 */
    TaskPageResponse<DataExportTask> pageExportTasks(TaskPageQueryRequest request);

    /** 查询导出任务详情 */
    DataExportTask getExportTaskDetail(String id);

    /** 批量删除导出任务 */
    int deleteExportTasks(List<String> ids);

    // ──────────────── 导入任务 CRUD ────────────────

    /** 保存导入任务 */
    void saveImportTask(DataImportTask task);

    /** 更新导入任务 */
    void updateImportTask(DataImportTask task);

    /** 分页查询导入任务 */
    TaskPageResponse<DataImportTask> pageImportTasks(TaskPageQueryRequest request);

    /** 查询导入任务详情 */
    DataImportTask getImportTaskDetail(String id);

    /** 批量删除导入任务 */
    int deleteImportTasks(List<String> ids);

    // ──────────────── 工具 ────────────────

    /** 下载导入模板 */
    void downloadTemplate(String tableName, String jdbcUrl, String username, String password,
                          HttpServletResponse response) throws IOException;
}