package com.gnosis.dataexportor.dto;

import lombok.Data;

@Data
public class TaskPageQueryRequest {

    /** 当前页码（从1开始） */
    private Integer pageNum = 1;

    /** 每页大小 */
    private Integer pageSize = 10;

    /** 任务名称（模糊查询） */
    private String taskName;

    /** 业务类型 */
    private String businessType;

    /** 业务场景 */
    private String scene;

    /** 所属模块 */
    private String module;

    /** 操作人（模糊查询） */
    private String operator;

    /** 状态 */
    private String status;

    /** 文件格式 */
    private String format;

    /** 创建人ID */
    private String createUserId;

    /** 创建时间-开始 */
    private String createTimeStart;

    /** 创建时间-结束 */
    private String createTimeEnd;
}