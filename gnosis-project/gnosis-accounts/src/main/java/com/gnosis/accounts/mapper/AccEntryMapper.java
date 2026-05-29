package com.gnosis.accounts.mapper;

import com.gnosis.accounts.domain.AccEntry;
import com.gnosis.accounts.dto.entry.AccEntryQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccEntryMapper {

    int insert(AccEntry record);

    int batchInsert(@Param("list") List<AccEntry> list);

    AccEntry selectById(@Param("id") String id);

    List<AccEntry> selectByCondition(AccEntryQueryRequest query);

    Long countByCondition(AccEntryQueryRequest query);

    List<AccEntry> selectByJournalId(@Param("journalId") String journalId);

    int deleteByJournalId(@Param("journalId") String journalId);
}
