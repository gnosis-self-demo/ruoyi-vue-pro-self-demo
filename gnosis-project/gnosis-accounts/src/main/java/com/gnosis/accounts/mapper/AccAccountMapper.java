package com.gnosis.accounts.mapper;

import com.gnosis.accounts.domain.AccAccount;
import com.gnosis.accounts.dto.account.AccAccountQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface AccAccountMapper {

    int insert(AccAccount record);

    int updateById(AccAccount record);

    int updateBalance(@Param("id") String id, @Param("amount") BigDecimal amount);

    AccAccount selectById(@Param("id") String id);

    AccAccount selectByAccountNo(@Param("accountNo") String accountNo);

    List<AccAccount> selectByCondition(AccAccountQueryRequest query);

    Long countByCondition(AccAccountQueryRequest query);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);

    List<AccAccount> selectByAccountType(@Param("accountType") String accountType);

    List<AccAccount> selectByChannelCode(@Param("channelCode") String channelCode, @Param("accountType") String accountType);

    AccAccount selectByCustomerId(@Param("customerId") String customerId);
}
