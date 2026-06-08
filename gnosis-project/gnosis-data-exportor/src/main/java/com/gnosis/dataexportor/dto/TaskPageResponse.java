package com.gnosis.dataexportor.dto;

import lombok.Data;

import java.util.List;

@Data
public class TaskPageResponse<T> {

    /** 总记录数 */
    private Long total;

    /** 当前页码 */
    private Integer pageNum;

    /** 每页大小 */
    private Integer pageSize;

    /** 数据列表 */
    private List<T> records;
}