package com.gnosis.process.mapper;

import com.gnosis.process.domain.ProcessOrchestrationConfig;
import com.gnosis.process.dto.ProcessOrchestrationConfigQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProcessOrchestrationConfigMapper {

    ProcessOrchestrationConfig selectById(@Param("id") String id);

    List<ProcessOrchestrationConfig> selectList(ProcessOrchestrationConfigQueryRequest request);

    Long selectCount(ProcessOrchestrationConfigQueryRequest request);

    List<ProcessOrchestrationConfig> selectByBusinessConfigId(@Param("businessConfigId") String businessConfigId);

    List<ProcessOrchestrationConfig> selectByCurrentProcessAndEvent(@Param("businessConfigId") String businessConfigId, @Param("currentProcessDefKey") String currentProcessDefKey, @Param("triggerEvent") String triggerEvent);

    int insert(ProcessOrchestrationConfig config);

    int updateById(ProcessOrchestrationConfig config);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int batchUpdateActive(@Param("ids") List<String> ids, @Param("isActive") Boolean isActive, @Param("updateUserId") String updateUserId);
}
