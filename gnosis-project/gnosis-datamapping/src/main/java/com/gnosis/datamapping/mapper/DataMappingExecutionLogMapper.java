package com.gnosis.datamapping.mapper;

import com.gnosis.datamapping.domain.DataMappingExecutionLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DataMappingExecutionLogMapper {

    int insert(DataMappingExecutionLog log);

    DataMappingExecutionLog selectById(@Param("id") String id);

    List<DataMappingExecutionLog> selectByConfigId(@Param("configId") String configId,
                                                    @Param("offset") Integer offset,
                                                    @Param("limit") Integer limit);

    Long countByConfigId(@Param("configId") String configId);
}
