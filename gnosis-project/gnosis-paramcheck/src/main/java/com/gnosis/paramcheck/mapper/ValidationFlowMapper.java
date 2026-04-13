package com.gnosis.paramcheck.mapper;

import com.gnosis.paramcheck.domain.ValidationFlow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ValidationFlowMapper {

    ValidationFlow selectByFlowId(@Param("flowId") String flowId);

    List<ValidationFlow> selectAll();

    List<ValidationFlow> selectActive();

    List<ValidationFlow> selectByCondition(@Param("flowId") String flowId,
                                           @Param("flowName") String flowName,
                                           @Param("businessType") String businessType,
                                           @Param("modeType") String modeType,
                                           @Param("isActive") Boolean isActive);

    int insert(ValidationFlow flow);

    int updateByFlowId(ValidationFlow flow);

    int deleteByFlowId(@Param("flowId") String flowId);

    int batchDelete(@Param("flowIds") List<String> flowIds);

    int activate(@Param("flowId") String flowId);

    int deactivate(@Param("flowId") String flowId);

    int batchActivate(@Param("flowIds") List<String> flowIds);

    int batchDeactivate(@Param("flowIds") List<String> flowIds);

    int incrementVersion(@Param("flowId") String flowId);

    int countByCondition(@Param("flowId") String flowId,
                         @Param("flowName") String flowName,
                         @Param("businessType") String businessType,
                         @Param("modeType") String modeType,
                         @Param("isActive") Boolean isActive);

    List<ValidationFlow> selectByConditionWithPaging(@Param("flowId") String flowId,
                                                      @Param("flowName") String flowName,
                                                      @Param("businessType") String businessType,
                                                      @Param("modeType") String modeType,
                                                      @Param("isActive") Boolean isActive,
                                                      @Param("offset") int offset,
                                                      @Param("pageSize") int pageSize);
}
