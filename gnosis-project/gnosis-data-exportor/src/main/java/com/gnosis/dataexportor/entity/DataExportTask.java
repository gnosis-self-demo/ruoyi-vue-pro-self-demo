package com.gnosis.dataexportor.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("data_export_task")
public class DataExportTask {

    @TableId
    private String id;

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

    /** 请求参数快照（JSON格式，用于审计追踪） */
    private String requestParams;

    /** SQL内容 */
    private String sqlContent;

    /** Sheet/文件名 */
    private String sheetName;

    /** 导出文件名 */
    private String fileName;

    /** 导出格式: CSV, ZIP */
    private String exportFormat;

    /** 总行数 */
    private Long totalRows;

    /** 当前进度（已处理行数） */
    private Long processedRows;

    /** 文件下载URL */
    private String downloadUrl;

    /** 状态: PENDING, RUNNING, SUCCESS, FAILED */
    private String status;

    /** 错误信息 */
    private String errorMessage;

    /** 耗时(毫秒) */
    private Long durationMs;

    /** 创建人ID */
    private String createUserId;

    /** 创建时间 */
    private Date createTime;

    /** 更新人ID */
    private String updateUserId;

    /** 更新时间 */
    private Date updateTime;
}