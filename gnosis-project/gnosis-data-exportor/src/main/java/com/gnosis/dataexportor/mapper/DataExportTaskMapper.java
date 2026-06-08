package com.gnosis.dataexportor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gnosis.dataexportor.entity.DataExportTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataExportTaskMapper extends BaseMapper<DataExportTask> {

    /**
     * 分页查询导出任务
     */
    List<DataExportTask> selectPage(@Param("params") Map<String, Object> params);

    /**
     * 统计导出任务总数
     */
    Long selectCount(@Param("params") Map<String, Object> params);

    /**
     * 批量删除
     */
    int deleteByIds(@Param("ids") List<String> ids);
}