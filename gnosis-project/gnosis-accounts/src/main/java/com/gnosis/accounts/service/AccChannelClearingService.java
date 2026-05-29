package com.gnosis.accounts.service;

import com.gnosis.accounts.domain.AccChannelClearing;
import com.gnosis.accounts.dto.clearing.AccChannelClearingCreateRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingIdsRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingQueryRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingUpdateRequest;
import com.gnosis.accounts.dto.clearing.AccChannelClearingVO;
import com.gnosis.accounts.mapper.AccChannelClearingMapper;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AccChannelClearingService {

    @Autowired
    private AccChannelClearingMapper clearingMapper;

    public PageResult<AccChannelClearingVO> pageList(PageRequest<AccChannelClearingQueryRequest> pageRequest) {
        AccChannelClearingQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new AccChannelClearingQueryRequest();
        }
        query.setPageNum(pageRequest.getPageNum());
        query.setPageSize(pageRequest.getPageSize());

        Long total = clearingMapper.countByCondition(query);
        if (total == 0) {
            PageResult<AccChannelClearingVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<AccChannelClearingVO>());
            return result;
        }

        List<AccChannelClearing> clearings = clearingMapper.selectByCondition(query);
        List<AccChannelClearingVO> voList = new ArrayList<>();
        for (AccChannelClearing clearing : clearings) {
            voList.add(convertToVO(clearing));
        }

        PageResult<AccChannelClearingVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    public AccChannelClearingVO detail(String id) {
        AccChannelClearing clearing = clearingMapper.selectById(id);
        return clearing != null ? convertToVO(clearing) : null;
    }

    public int create(AccChannelClearingCreateRequest request) {
        AccChannelClearing clearing = new AccChannelClearing();
        clearing.setId(UUID.randomUUID().toString().replace("-", ""));
        clearing.setClearingNo("CLR" + System.currentTimeMillis());
        clearing.setClearingDate(request.getClearingDate());
        clearing.setChannelCode(request.getChannelCode());
        clearing.setChannelName(request.getChannelName());
        clearing.setClearingType(request.getClearingType());
        clearing.setClearingAmount(request.getClearingAmount());
        clearing.setClearingStatus("PENDING");
        clearing.setDescription(request.getDescription());
        clearing.setStatus(1);
        clearing.setCreateUserId(request.getCreateUserId());
        clearing.setUpdateUserId(request.getCreateUserId());
        clearing.setCreateTime(new Date());
        clearing.setUpdateTime(new Date());

        return clearingMapper.insert(clearing);
    }

    public int update(AccChannelClearingUpdateRequest request) {
        AccChannelClearing clearing = clearingMapper.selectById(request.getId());
        if (clearing == null) {
            return 0;
        }

        clearing.setClearingDate(request.getClearingDate());
        clearing.setChannelCode(request.getChannelCode());
        clearing.setChannelName(request.getChannelName());
        clearing.setClearingType(request.getClearingType());
        clearing.setClearingAmount(request.getClearingAmount());
        clearing.setClearingStatus(request.getClearingStatus());
        clearing.setDescription(request.getDescription());
        clearing.setUpdateUserId(request.getUpdateUserId());
        clearing.setUpdateTime(new Date());

        return clearingMapper.updateById(clearing);
    }

    public int delete(String id) {
        return clearingMapper.deleteById(id);
    }

    public int batchDelete(AccChannelClearingIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return clearingMapper.deleteByIds(request.getIds());
    }

    public int batchEnable(AccChannelClearingIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return clearingMapper.batchUpdateStatus(request.getIds(), 1);
    }

    public int batchDisable(AccChannelClearingIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return clearingMapper.batchUpdateStatus(request.getIds(), 0);
    }

    @org.springframework.transaction.annotation.Transactional
    public int executeClearing(String id, String operatorId) {
        AccChannelClearing clearing = clearingMapper.selectById(id);
        if (clearing == null) {
            return 0;
        }
        clearing.setClearingStatus("EXECUTED");
        clearing.setUpdateUserId(operatorId);
        clearing.setUpdateTime(new Date());
        return clearingMapper.updateById(clearing);
    }

    private AccChannelClearingVO convertToVO(AccChannelClearing clearing) {
        AccChannelClearingVO vo = new AccChannelClearingVO();
        vo.setId(clearing.getId());
        vo.setClearingNo(clearing.getClearingNo());
        vo.setClearingDate(clearing.getClearingDate());
        vo.setChannelCode(clearing.getChannelCode());
        vo.setChannelName(clearing.getChannelName());
        vo.setClearingType(clearing.getClearingType());
        vo.setClearingAmount(clearing.getClearingAmount());
        vo.setClearingStatus(clearing.getClearingStatus());
        vo.setJournalId(clearing.getJournalId());
        vo.setDescription(clearing.getDescription());
        vo.setStatus(clearing.getStatus());
        vo.setCreateUserId(clearing.getCreateUserId());
        vo.setUpdateUserId(clearing.getUpdateUserId());
        vo.setCreateTime(clearing.getCreateTime());
        vo.setUpdateTime(clearing.getUpdateTime());
        return vo;
    }
}
