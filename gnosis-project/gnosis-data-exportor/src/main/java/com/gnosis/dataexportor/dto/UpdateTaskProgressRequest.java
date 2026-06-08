package com.gnosis.dataexportor.dto;

import lombok.Data;

@Data
public class UpdateTaskProgressRequest {

    /** 任务ID */
    private String taskId;

    /** 已处理行数 */
    private Long processedRows;

    /** 总行数 */
    private Long totalRows;

    /** 更新人ID */
    private String updateUserId;
}