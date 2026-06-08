package com.gnosis.dataexportor.dto;

import lombok.Data;

@Data
public class CompleteExportTaskRequest {

    /** 任务ID */
    private String taskId;

    /** 总行数 */
    private Long totalRows;

    /** 文件名 */
    private String fileName;

    /** 文件下载URL */
    private String downloadUrl;

    /** 更新人ID */
    private String updateUserId;
}