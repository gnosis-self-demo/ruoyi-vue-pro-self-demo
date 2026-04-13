package com.gnosis.paramcheck.mapper;

import com.gnosis.paramcheck.domain.ValidationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ValidationLogMapper {

    ValidationLog selectByLogId(@Param("logId") Long logId);

    List<ValidationLog> selectAll();

    List<ValidationLog> selectByCondition(@Param("flowId") String flowId,
                                          @Param("requestId") String requestId,
                                          @Param("modeType") String modeType,
                                          @Param("isActive") Boolean isActive);

    int insert(ValidationLog log);

    int deleteByLogId(@Param("logId") Long logId);

    int batchDelete(@Param("logIds") List<Long> logIds);

    int activate(@Param("logId") Long logId);

    int deactivate(@Param("logId") Long logId);

    int batchActivate(@Param("logIds") List<Long> logIds);

    int batchDeactivate(@Param("logIds") List<Long> logIds);

    int countByCondition(@Param("flowId") String flowId,
                         @Param("requestId") String requestId,
                         @Param("modeType") String modeType,
                         @Param("isActive") Boolean isActive);

    List<ValidationLog> selectByConditionWithPaging(@Param("flowId") String flowId,
                                                     @Param("requestId") String requestId,
                                                     @Param("modeType") String modeType,
                                                     @Param("isActive") Boolean isActive,
                                                     @Param("offset") int offset,
                                                     @Param("pageSize") int pageSize);
}
