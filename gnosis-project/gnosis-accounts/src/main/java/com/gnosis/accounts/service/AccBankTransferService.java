package com.gnosis.accounts.service;

import com.gnosis.accounts.domain.AccBankTransfer;
import com.gnosis.accounts.dto.transfer.AccBankTransferCreateRequest;
import com.gnosis.accounts.dto.transfer.AccBankTransferIdsRequest;
import com.gnosis.accounts.dto.transfer.AccBankTransferQueryRequest;
import com.gnosis.accounts.dto.transfer.AccBankTransferUpdateRequest;
import com.gnosis.accounts.dto.transfer.AccBankTransferVO;
import com.gnosis.accounts.mapper.AccBankTransferMapper;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AccBankTransferService {

    @Autowired
    private AccBankTransferMapper transferMapper;

    public PageResult<AccBankTransferVO> pageList(PageRequest<AccBankTransferQueryRequest> pageRequest) {
        AccBankTransferQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new AccBankTransferQueryRequest();
        }
        query.setPageNum(pageRequest.getPageNum());
        query.setPageSize(pageRequest.getPageSize());

        Long total = transferMapper.countByCondition(query);
        if (total == 0) {
            PageResult<AccBankTransferVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<AccBankTransferVO>());
            return result;
        }

        List<AccBankTransfer> transfers = transferMapper.selectByCondition(query);
        List<AccBankTransferVO> voList = new ArrayList<>();
        for (AccBankTransfer transfer : transfers) {
            voList.add(convertToVO(transfer));
        }

        PageResult<AccBankTransferVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    public AccBankTransferVO detail(String id) {
        AccBankTransfer transfer = transferMapper.selectById(id);
        return transfer != null ? convertToVO(transfer) : null;
    }

    public int create(AccBankTransferCreateRequest request) {
        AccBankTransfer transfer = new AccBankTransfer();
        transfer.setId(UUID.randomUUID().toString().replace("-", ""));
        transfer.setTransferNo("TRF" + System.currentTimeMillis());
        transfer.setTransferDate(request.getTransferDate());
        transfer.setChannelCode(request.getChannelCode());
        transfer.setChannelName(request.getChannelName());
        transfer.setTransferType(request.getTransferType());
        transfer.setTransferAmount(request.getTransferAmount());
        transfer.setFromAccountId(request.getFromAccountId());
        transfer.setFromAccountName(request.getFromAccountName());
        transfer.setToAccountId(request.getToAccountId());
        transfer.setToAccountName(request.getToAccountName());
        transfer.setTransferStatus("PENDING");
        transfer.setDescription(request.getDescription());
        transfer.setStatus(1);
        transfer.setCreateUserId(request.getCreateUserId());
        transfer.setUpdateUserId(request.getCreateUserId());
        transfer.setCreateTime(new Date());
        transfer.setUpdateTime(new Date());

        return transferMapper.insert(transfer);
    }

    public int update(AccBankTransferUpdateRequest request) {
        AccBankTransfer transfer = transferMapper.selectById(request.getId());
        if (transfer == null) {
            return 0;
        }

        transfer.setTransferDate(request.getTransferDate());
        transfer.setChannelCode(request.getChannelCode());
        transfer.setChannelName(request.getChannelName());
        transfer.setTransferType(request.getTransferType());
        transfer.setTransferAmount(request.getTransferAmount());
        transfer.setFromAccountId(request.getFromAccountId());
        transfer.setFromAccountName(request.getFromAccountName());
        transfer.setToAccountId(request.getToAccountId());
        transfer.setToAccountName(request.getToAccountName());
        transfer.setTransferStatus(request.getTransferStatus());
        transfer.setDescription(request.getDescription());
        transfer.setUpdateUserId(request.getUpdateUserId());
        transfer.setUpdateTime(new Date());

        return transferMapper.updateById(transfer);
    }

    public int delete(String id) {
        return transferMapper.deleteById(id);
    }

    public int batchDelete(AccBankTransferIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return transferMapper.deleteByIds(request.getIds());
    }

    public int batchEnable(AccBankTransferIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return transferMapper.batchUpdateStatus(request.getIds(), 1);
    }

    public int batchDisable(AccBankTransferIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return transferMapper.batchUpdateStatus(request.getIds(), 0);
    }

    @org.springframework.transaction.annotation.Transactional
    public int executeTransfer(String id, String operatorId) {
        AccBankTransfer transfer = transferMapper.selectById(id);
        if (transfer == null) {
            return 0;
        }
        transfer.setTransferStatus("EXECUTED");
        transfer.setUpdateUserId(operatorId);
        transfer.setUpdateTime(new Date());
        return transferMapper.updateById(transfer);
    }

    private AccBankTransferVO convertToVO(AccBankTransfer transfer) {
        AccBankTransferVO vo = new AccBankTransferVO();
        vo.setId(transfer.getId());
        vo.setTransferNo(transfer.getTransferNo());
        vo.setTransferDate(transfer.getTransferDate());
        vo.setChannelCode(transfer.getChannelCode());
        vo.setChannelName(transfer.getChannelName());
        vo.setTransferType(transfer.getTransferType());
        vo.setTransferAmount(transfer.getTransferAmount());
        vo.setFromAccountId(transfer.getFromAccountId());
        vo.setFromAccountName(transfer.getFromAccountName());
        vo.setToAccountId(transfer.getToAccountId());
        vo.setToAccountName(transfer.getToAccountName());
        vo.setTransferStatus(transfer.getTransferStatus());
        vo.setJournalId(transfer.getJournalId());
        vo.setDescription(transfer.getDescription());
        vo.setStatus(transfer.getStatus());
        vo.setCreateUserId(transfer.getCreateUserId());
        vo.setUpdateUserId(transfer.getUpdateUserId());
        vo.setCreateTime(transfer.getCreateTime());
        vo.setUpdateTime(transfer.getUpdateTime());
        return vo;
    }
}
