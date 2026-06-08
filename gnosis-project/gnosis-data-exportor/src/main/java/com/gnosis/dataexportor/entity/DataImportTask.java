package com.gnosis.dataexportor.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("data_import_task")
public class DataImportTask {

    @TableId
    private String id;

    /** 任务名称 */
    private String taskName;

    /** 目标表名 */
    private String tableName;

    /** 导入文件名 */
    private String fileName;

    /** 导入格式: EXCEL, ZIP */
    private String importFormat;

    /** 总处理行数 */
    private Long totalRows;

    /** 成功行数 */
    private Long successRows;

    /** 失败行数 */
    private Long failedRows;

    /** 跳过行数 */
    private Long skippedRows;

    /** 总条目数 */
    private Integer totalSheets;

    /** 状态: SUCCESS, FAILED, RUNNING */
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