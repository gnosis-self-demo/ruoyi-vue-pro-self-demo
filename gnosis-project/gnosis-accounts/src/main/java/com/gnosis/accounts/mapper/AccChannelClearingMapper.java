package com.gnosis.accounts.mapper;

import com.gnosis.accounts.domain.AccChannelClearing;
import com.gnosis.accounts.dto.clearing.AccChannelClearingQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccChannelClearingMapper {

    int insert(AccChannelClearing record);

    int updateById(AccChannelClearing record);

    AccChannelClearing selectById(@Param("id") String id);

    List<AccChannelClearing> selectByCondition(AccChannelClearingQueryRequest query);

    Long countByCondition(AccChannelClearingQueryRequest query);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);
}
