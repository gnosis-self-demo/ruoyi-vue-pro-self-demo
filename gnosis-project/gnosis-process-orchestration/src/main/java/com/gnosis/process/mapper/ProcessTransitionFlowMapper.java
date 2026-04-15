package com.gnosis.process.mapper;

import com.gnosis.process.domain.ProcessTransitionFlow;
import com.gnosis.process.dto.ProcessTransitionFlowQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProcessTransitionFlowMapper {

    ProcessTransitionFlow selectById(@Param("id") String id);

    List<ProcessTransitionFlow> selectList(ProcessTransitionFlowQueryRequest request);

    Long selectCount(ProcessTransitionFlowQueryRequest request);

    List<ProcessTransitionFlow> selectByProcessInstanceId(@Param("processInstanceId") String processInstanceId);

    List<ProcessTransitionFlow> selectByBusinessId(@Param("businessId") String businessId);

    int insert(ProcessTransitionFlow flow);
}
