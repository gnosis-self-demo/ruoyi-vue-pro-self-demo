package com.gnosis.dataexportor.dto;

import lombok.Data;

@Data
public class FailExportTaskRequest {

    /** 任务ID */
    private String taskId;

    /** 错误信息 */
    private String errorMessage;

    /** 更新人ID */
    private String updateUserId;
}