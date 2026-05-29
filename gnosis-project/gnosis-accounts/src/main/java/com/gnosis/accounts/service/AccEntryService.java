package com.gnosis.accounts.service;

import com.gnosis.accounts.domain.AccEntry;
import com.gnosis.accounts.dto.entry.AccEntryQueryRequest;
import com.gnosis.accounts.dto.entry.AccEntryVO;
import com.gnosis.accounts.mapper.AccEntryMapper;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AccEntryService {

    @Autowired
    private AccEntryMapper entryMapper;

    public PageResult<AccEntryVO> pageList(PageRequest<AccEntryQueryRequest> pageRequest) {
        AccEntryQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new AccEntryQueryRequest();
        }
        query.setPageNum(pageRequest.getPageNum());
        query.setPageSize(pageRequest.getPageSize());

        Long total = entryMapper.countByCondition(query);
        if (total == 0) {
            PageResult<AccEntryVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<AccEntryVO>());
            return result;
        }

        List<AccEntry> entries = entryMapper.selectByCondition(query);
        List<AccEntryVO> voList = new ArrayList<>();
        for (AccEntry entry : entries) {
            voList.add(convertToVO(entry));
        }

        PageResult<AccEntryVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    public AccEntryVO detail(String id) {
        AccEntry entry = entryMapper.selectById(id);
        return entry != null ? convertToVO(entry) : null;
    }

    public List<AccEntryVO> selectByJournalId(String journalId) {
        List<AccEntry> entries = entryMapper.selectByJournalId(journalId);
        List<AccEntryVO> voList = new ArrayList<>();
        for (AccEntry entry : entries) {
            voList.add(convertToVO(entry));
        }
        return voList;
    }

    private AccEntryVO convertToVO(AccEntry entry) {
        AccEntryVO vo = new AccEntryVO();
        vo.setId(entry.getId());
        vo.setJournalId(entry.getJournalId());
        vo.setJournalNo(entry.getJournalNo());
        vo.setAccountId(entry.getAccountId());
        vo.setAccountNo(entry.getAccountNo());
        vo.setAccountName(entry.getAccountName());
        vo.setSubjectCode(entry.getSubjectCode());
        vo.setSubjectName(entry.getSubjectName());
        vo.setEntryDirection(entry.getEntryDirection());
        vo.setAmount(entry.getAmount());
        vo.setDescription(entry.getDescription());
        vo.setCreateUserId(entry.getCreateUserId());
        vo.setUpdateUserId(entry.getUpdateUserId());
        vo.setCreateTime(entry.getCreateTime());
        vo.setUpdateTime(entry.getUpdateTime());
        return vo;
    }
}
