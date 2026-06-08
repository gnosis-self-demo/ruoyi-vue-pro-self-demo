package com.gnosis.dataexportor.dto;

import lombok.Data;

@Data
public class CreateExportTaskRequest {

    /** 任务名称 */
    private String taskName;

    /** 业务类型（如：订单管理、用户管理、报表统计） */
    private String businessType;

    /** 业务场景（如：日常导出、数据备份、审计导出） */
    private String scene;

    /** 所属模块 */
    private String module;

    /** 操作人（显示名称） */
    private String operator;

    /** 客户端IP */
    private String clientIp;

    /** 请求参数快照（JSON字符串） */
    private String requestParams;

    /** 导出文件名 */
    private String fileName;

    /** 导出格式: CSV, ZIP, EXCEL */
    private String exportFormat;

    /** 创建人ID */
    private String createUserId;
}