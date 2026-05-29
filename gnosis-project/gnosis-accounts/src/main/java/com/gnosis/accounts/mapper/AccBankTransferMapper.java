package com.gnosis.accounts.mapper;

import com.gnosis.accounts.domain.AccBankTransfer;
import com.gnosis.accounts.dto.transfer.AccBankTransferQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccBankTransferMapper {

    int insert(AccBankTransfer record);

    int updateById(AccBankTransfer record);

    AccBankTransfer selectById(@Param("id") String id);

    List<AccBankTransfer> selectByCondition(AccBankTransferQueryRequest query);

    Long countByCondition(AccBankTransferQueryRequest query);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);
}
