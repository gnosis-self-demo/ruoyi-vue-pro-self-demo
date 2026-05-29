package com.gnosis.accounts.mapper;

import com.gnosis.accounts.domain.AccJournal;
import com.gnosis.accounts.dto.journal.AccJournalQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccJournalMapper {

    int insert(AccJournal record);

    int updateById(AccJournal record);

    AccJournal selectById(@Param("id") String id);

    AccJournal selectByJournalNo(@Param("journalNo") String journalNo);

    List<AccJournal> selectByCondition(AccJournalQueryRequest query);

    Long countByCondition(AccJournalQueryRequest query);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);
}
