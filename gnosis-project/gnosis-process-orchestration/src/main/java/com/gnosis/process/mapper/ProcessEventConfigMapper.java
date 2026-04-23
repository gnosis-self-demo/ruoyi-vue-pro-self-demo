package com.gnosis.process.mapper;

import com.gnosis.process.domain.ProcessEventConfig;
import com.gnosis.process.dto.ProcessEventConfigQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProcessEventConfigMapper {

    ProcessEventConfig selectById(@Param("id") String id);

    List<ProcessEventConfig> selectList(ProcessEventConfigQueryRequest request);

    Long selectCount(ProcessEventConfigQueryRequest request);

    List<ProcessEventConfig> selectAllForExport(ProcessEventConfigQueryRequest request);

    List<ProcessEventConfig> selectByProcessDefKey(@Param("processDefKey") String processDefKey);

    ProcessEventConfig selectByProcessAndNodeAndAction(@Param("processDefKey") String processDefKey, @Param("nodeDefKey") String nodeDefKey, @Param("submitAction") String submitAction, @Param("matchBusiness") String matchBusiness);

    int insert(ProcessEventConfig config);

    int updateById(ProcessEventConfig config);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int batchUpdateActive(@Param("ids") List<String> ids, @Param("isActive") Boolean isActive, @Param("updateUserId") String updateUserId);
}
