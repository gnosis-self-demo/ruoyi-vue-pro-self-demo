package com.gnosis.accounts.service;

import com.gnosis.accounts.domain.AccCustomerSettlement;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementCreateRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementIdsRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementQueryRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementUpdateRequest;
import com.gnosis.accounts.dto.settlement.AccCustomerSettlementVO;
import com.gnosis.accounts.mapper.AccCustomerSettlementMapper;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AccCustomerSettlementService {

    @Autowired
    private AccCustomerSettlementMapper settlementMapper;

    public PageResult<AccCustomerSettlementVO> pageList(PageRequest<AccCustomerSettlementQueryRequest> pageRequest) {
        AccCustomerSettlementQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new AccCustomerSettlementQueryRequest();
        }
        query.setPageNum(pageRequest.getPageNum());
        query.setPageSize(pageRequest.getPageSize());

        Long total = settlementMapper.countByCondition(query);
        if (total == 0) {
            PageResult<AccCustomerSettlementVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<AccCustomerSettlementVO>());
            return result;
        }

        List<AccCustomerSettlement> settlements = settlementMapper.selectByCondition(query);
        List<AccCustomerSettlementVO> voList = new ArrayList<>();
        for (AccCustomerSettlement settlement : settlements) {
            voList.add(convertToVO(settlement));
        }

        PageResult<AccCustomerSettlementVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    public AccCustomerSettlementVO detail(String id) {
        AccCustomerSettlement settlement = settlementMapper.selectById(id);
        return settlement != null ? convertToVO(settlement) : null;
    }

    public int create(AccCustomerSettlementCreateRequest request) {
        AccCustomerSettlement settlement = new AccCustomerSettlement();
        settlement.setId(UUID.randomUUID().toString().replace("-", ""));
        settlement.setSettlementNo("STL" + System.currentTimeMillis());
        settlement.setSettlementDate(request.getSettlementDate());
        settlement.setCustomerId(request.getCustomerId());
        settlement.setCustomerName(request.getCustomerName());
        settlement.setSettlementType(request.getSettlementType());
        settlement.setSettlementAmount(request.getSettlementAmount());
        settlement.setFeeAmount(request.getFeeAmount());
        settlement.setActualAmount(request.getActualAmount());
        settlement.setFromAccountId(request.getFromAccountId());
        settlement.setFromAccountName(request.getFromAccountName());
        settlement.setToAccountId(request.getToAccountId());
        settlement.setToAccountName(request.getToAccountName());
        settlement.setSettlementStatus("PENDING");
        settlement.setDescription(request.getDescription());
        settlement.setStatus(1);
        settlement.setCreateUserId(request.getCreateUserId());
        settlement.setUpdateUserId(request.getCreateUserId());
        settlement.setCreateTime(new Date());
        settlement.setUpdateTime(new Date());

        return settlementMapper.insert(settlement);
    }

    public int update(AccCustomerSettlementUpdateRequest request) {
        AccCustomerSettlement settlement = settlementMapper.selectById(request.getId());
        if (settlement == null) {
            return 0;
        }

        settlement.setSettlementDate(request.getSettlementDate());
        settlement.setCustomerId(request.getCustomerId());
        settlement.setCustomerName(request.getCustomerName());
        settlement.setSettlementType(request.getSettlementType());
        settlement.setSettlementAmount(request.getSettlementAmount());
        settlement.setFeeAmount(request.getFeeAmount());
        settlement.setActualAmount(request.getActualAmount());
        settlement.setFromAccountId(request.getFromAccountId());
        settlement.setFromAccountName(request.getFromAccountName());
        settlement.setToAccountId(request.getToAccountId());
        settlement.setToAccountName(request.getToAccountName());
        settlement.setSettlementStatus(request.getSettlementStatus());
        settlement.setDescription(request.getDescription());
        settlement.setUpdateUserId(request.getUpdateUserId());
        settlement.setUpdateTime(new Date());

        return settlementMapper.updateById(settlement);
    }

    public int delete(String id) {
        return settlementMapper.deleteById(id);
    }

    public int batchDelete(AccCustomerSettlementIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return settlementMapper.deleteByIds(request.getIds());
    }

    public int batchEnable(AccCustomerSettlementIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return settlementMapper.batchUpdateStatus(request.getIds(), 1);
    }

    public int batchDisable(AccCustomerSettlementIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return settlementMapper.batchUpdateStatus(request.getIds(), 0);
    }

    @org.springframework.transaction.annotation.Transactional
    public int executeSettlement(String id, String operatorId) {
        AccCustomerSettlement settlement = settlementMapper.selectById(id);
        if (settlement == null) {
            return 0;
        }
        settlement.setSettlementStatus("EXECUTED");
        settlement.setUpdateUserId(operatorId);
        settlement.setUpdateTime(new Date());
        return settlementMapper.updateById(settlement);
    }

    private AccCustomerSettlementVO convertToVO(AccCustomerSettlement settlement) {
        AccCustomerSettlementVO vo = new AccCustomerSettlementVO();
        vo.setId(settlement.getId());
        vo.setSettlementNo(settlement.getSettlementNo());
        vo.setSettlementDate(settlement.getSettlementDate());
        vo.setCustomerId(settlement.getCustomerId());
        vo.setCustomerName(settlement.getCustomerName());
        vo.setSettlementType(settlement.getSettlementType());
        vo.setSettlementAmount(settlement.getSettlementAmount());
        vo.setFeeAmount(settlement.getFeeAmount());
        vo.setActualAmount(settlement.getActualAmount());
        vo.setFromAccountId(settlement.getFromAccountId());
        vo.setFromAccountName(settlement.getFromAccountName());
        vo.setToAccountId(settlement.getToAccountId());
        vo.setToAccountName(settlement.getToAccountName());
        vo.setSettlementStatus(settlement.getSettlementStatus());
        vo.setJournalId(settlement.getJournalId());
        vo.setDescription(settlement.getDescription());
        vo.setStatus(settlement.getStatus());
        vo.setCreateUserId(settlement.getCreateUserId());
        vo.setUpdateUserId(settlement.getUpdateUserId());
        vo.setCreateTime(settlement.getCreateTime());
        vo.setUpdateTime(settlement.getUpdateTime());
        return vo;
    }
}
