package com.gnosis.datamapping.mapper;

import com.gnosis.datamapping.domain.DataMappingTestCase;
import com.gnosis.datamapping.dto.DataMappingTestCaseQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DataMappingTestCaseMapper {

    int insert(DataMappingTestCase testCase);

    int updateById(DataMappingTestCase testCase);

    DataMappingTestCase selectById(@Param("id") String id);

    List<DataMappingTestCase> selectByCondition(DataMappingTestCaseQueryRequest query);

    Long countByCondition(DataMappingTestCaseQueryRequest query);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);

    int updateExecuteResult(@Param("id") String id, 
                            @Param("actualResultJson") String actualResultJson,
                            @Param("isPassed") Integer isPassed, 
                            @Param("diffInfo") String diffInfo);

    List<DataMappingTestCase> selectByConfigId(@Param("configId") String configId);
}
