package com.gnosis.datamapping.mapper;

import com.gnosis.datamapping.domain.DataMappingConfig;
import com.gnosis.datamapping.dto.DataMappingConfigQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DataMappingConfigMapper {

    int insert(DataMappingConfig config);

    int updateById(DataMappingConfig config);

    DataMappingConfig selectById(@Param("id") String id);

    DataMappingConfig selectByConfigCode(@Param("configCode") String configCode);

    List<DataMappingConfig> selectByCondition(DataMappingConfigQueryRequest query);

    Long countByCondition(DataMappingConfigQueryRequest query);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);
}
