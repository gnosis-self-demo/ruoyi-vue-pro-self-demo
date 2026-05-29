package com.gnosis.accounts.mapper;

import com.gnosis.accounts.domain.AccCustomerSettlement;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccCustomerSettlementMapper {

    int insert(AccCustomerSettlement record);

    int updateById(AccCustomerSettlement record);

    AccCustomerSettlement selectById(@Param("id") String id);

    List<AccCustomerSettlement> selectByCondition(AccCustomerSettlementQueryRequest query);

    Long countByCondition(AccCustomerSettlementQueryRequest query);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);
}
