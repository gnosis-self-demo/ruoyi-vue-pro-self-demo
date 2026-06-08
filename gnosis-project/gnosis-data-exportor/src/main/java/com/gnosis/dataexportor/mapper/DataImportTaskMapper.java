package com.gnosis.dataexportor.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gnosis.dataexportor.entity.DataImportTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DataImportTaskMapper extends BaseMapper<DataImportTask> {

    /**
     * 分页查询导入任务
     */
    List<DataImportTask> selectPage(@Param("params") Map<String, Object> params);

    /**
     * 统计导入任务总数
     */
    Long selectCount(@Param("params") Map<String, Object> params);
}