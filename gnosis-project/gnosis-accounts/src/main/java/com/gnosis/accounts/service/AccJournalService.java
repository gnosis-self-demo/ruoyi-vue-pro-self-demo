package com.gnosis.accounts.service;

import com.gnosis.accounts.domain.AccEntry;
import com.gnosis.accounts.domain.AccJournal;
import com.gnosis.accounts.dto.journal.AccJournalCreateRequest;
import com.gnosis.accounts.dto.journal.AccJournalIdsRequest;
import com.gnosis.accounts.dto.journal.AccJournalQueryRequest;
import com.gnosis.accounts.dto.journal.AccJournalVO;
import com.gnosis.accounts.mapper.AccEntryMapper;
import com.gnosis.accounts.mapper.AccJournalMapper;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AccJournalService {

    @Autowired
    private AccJournalMapper journalMapper;

    @Autowired
    private AccEntryMapper entryMapper;

    public PageResult<AccJournalVO> pageList(PageRequest<AccJournalQueryRequest> pageRequest) {
        AccJournalQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new AccJournalQueryRequest();
        }
        query.setPageNum(pageRequest.getPageNum());
        query.setPageSize(pageRequest.getPageSize());

        Long total = journalMapper.countByCondition(query);
        if (total == 0) {
            PageResult<AccJournalVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<AccJournalVO>());
            return result;
        }

        List<AccJournal> journals = journalMapper.selectByCondition(query);
        List<AccJournalVO> voList = new ArrayList<>();
        for (AccJournal journal : journals) {
            voList.add(convertToVO(journal));
        }

        PageResult<AccJournalVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    public AccJournalVO detail(String id) {
        AccJournal journal = journalMapper.selectById(id);
        return journal != null ? convertToVO(journal) : null;
    }

    @Transactional
    public int create(AccJournalCreateRequest request) {
        AccJournal journal = new AccJournal();
        String journalId = UUID.randomUUID().toString().replace("-", "");
        journal.setId(journalId);
        journal.setJournalNo("JRN" + System.currentTimeMillis());
        journal.setBusinessType(request.getBusinessType());
        journal.setBusinessId(request.getBusinessId());
        journal.setTransactionType(request.getTransactionType());
        journal.setTotalDebit(request.getTotalDebit());
        journal.setTotalCredit(request.getTotalCredit());
        journal.setAccountingDate(request.getAccountingDate());
        journal.setStatus("DRAFT");
        journal.setDescription(request.getDescription());
        journal.setCreateUserId(request.getCreateUserId());
        journal.setUpdateUserId(request.getCreateUserId());
        journal.setCreateTime(new Date());
        journal.setUpdateTime(new Date());

        int result = journalMapper.insert(journal);

        return result;
    }

    public int delete(String id) {
        entryMapper.deleteByJournalId(id);
        return journalMapper.deleteById(id);
    }

    public int batchDelete(AccJournalIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        for (String id : request.getIds()) {
            entryMapper.deleteByJournalId(id);
        }
        return journalMapper.deleteByIds(request.getIds());
    }

    @Transactional
    public int post(String id) {
        AccJournal journal = journalMapper.selectById(id);
        if (journal == null) {
            return 0;
        }
        journal.setStatus("POSTED");
        journal.setUpdateTime(new Date());
        return journalMapper.updateById(journal);
    }

    @Transactional
    public int reverse(String id) {
        AccJournal journal = journalMapper.selectById(id);
        if (journal == null) {
            return 0;
        }
        journal.setStatus("REVERSED");
        journal.setUpdateTime(new Date());
        return journalMapper.updateById(journal);
    }

    private AccJournalVO convertToVO(AccJournal journal) {
        AccJournalVO vo = new AccJournalVO();
        vo.setId(journal.getId());
        vo.setJournalNo(journal.getJournalNo());
        vo.setBusinessType(journal.getBusinessType());
        vo.setBusinessId(journal.getBusinessId());
        vo.setTransactionType(journal.getTransactionType());
        vo.setTotalDebit(journal.getTotalDebit());
        vo.setTotalCredit(journal.getTotalCredit());
        vo.setAccountingDate(journal.getAccountingDate());
        vo.setStatus(journal.getStatus());
        vo.setDescription(journal.getDescription());
        vo.setCreateUserId(journal.getCreateUserId());
        vo.setUpdateUserId(journal.getUpdateUserId());
        vo.setCreateTime(journal.getCreateTime());
        vo.setUpdateTime(journal.getUpdateTime());
        return vo;
    }
}
