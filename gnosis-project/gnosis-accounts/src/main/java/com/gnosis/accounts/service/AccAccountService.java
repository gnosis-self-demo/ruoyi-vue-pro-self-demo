package com.gnosis.accounts.service;

import com.gnosis.accounts.domain.AccAccount;
import com.gnosis.accounts.dto.account.AccAccountCreateRequest;
import com.gnosis.accounts.dto.account.AccAccountIdsRequest;
import com.gnosis.accounts.dto.account.AccAccountQueryRequest;
import com.gnosis.accounts.dto.account.AccAccountUpdateRequest;
import com.gnosis.accounts.dto.account.AccAccountVO;
import com.gnosis.accounts.mapper.AccAccountMapper;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AccAccountService {

    @Autowired
    private AccAccountMapper accountMapper;

    public PageResult<AccAccountVO> pageList(PageRequest<AccAccountQueryRequest> pageRequest) {
        AccAccountQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new AccAccountQueryRequest();
        }
        query.setPageNum(pageRequest.getPageNum());
        query.setPageSize(pageRequest.getPageSize());

        Long total = accountMapper.countByCondition(query);
        if (total == 0) {
            PageResult<AccAccountVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<AccAccountVO>());
            return result;
        }

        List<AccAccount> accounts = accountMapper.selectByCondition(query);
        List<AccAccountVO> voList = new ArrayList<>();
        for (AccAccount account : accounts) {
            voList.add(convertToVO(account));
        }

        PageResult<AccAccountVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    public AccAccountVO detail(String id) {
        AccAccount account = accountMapper.selectById(id);
        return account != null ? convertToVO(account) : null;
    }

    public int create(AccAccountCreateRequest request) {
        AccAccount account = new AccAccount();
        account.setId(UUID.randomUUID().toString().replace("-", ""));
        account.setAccountNo(request.getAccountNo());
        account.setAccountName(request.getAccountName());
        account.setSubjectId(request.getSubjectId());
        account.setAccountType(request.getAccountType());
        account.setChannelCode(request.getChannelCode());
        account.setCustomerId(request.getCustomerId());
        account.setCustomerName(request.getCustomerName());
        account.setCurrency(request.getCurrency());
        account.setBalance(request.getBalance() != null ? request.getBalance() : BigDecimal.ZERO);
        account.setFrozenAmount(request.getFrozenAmount() != null ? request.getFrozenAmount() : BigDecimal.ZERO);
        account.setBalanceDirection(request.getBalanceDirection());
        account.setDescription(request.getDescription());
        account.setStatus(1);
        account.setCreateUserId(request.getCreateUserId());
        account.setUpdateUserId(request.getCreateUserId());
        account.setCreateTime(new Date());
        account.setUpdateTime(new Date());

        return accountMapper.insert(account);
    }

    public int update(AccAccountUpdateRequest request) {
        AccAccount account = accountMapper.selectById(request.getId());
        if (account == null) {
            return 0;
        }

        account.setAccountNo(request.getAccountNo());
        account.setAccountName(request.getAccountName());
        account.setSubjectId(request.getSubjectId());
        account.setAccountType(request.getAccountType());
        account.setChannelCode(request.getChannelCode());
        account.setCustomerId(request.getCustomerId());
        account.setCustomerName(request.getCustomerName());
        account.setCurrency(request.getCurrency());
        account.setBalanceDirection(request.getBalanceDirection());
        account.setDescription(request.getDescription());
        account.setUpdateUserId(request.getUpdateUserId());
        account.setUpdateTime(new Date());

        return accountMapper.updateById(account);
    }

    public int delete(String id) {
        return accountMapper.deleteById(id);
    }

    public int batchDelete(AccAccountIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return accountMapper.deleteByIds(request.getIds());
    }

    public int batchEnable(AccAccountIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return accountMapper.batchUpdateStatus(request.getIds(), 1);
    }

    public int batchDisable(AccAccountIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return accountMapper.batchUpdateStatus(request.getIds(), 0);
    }

    public BigDecimal getAccountBalance(String id) {
        AccAccount account = accountMapper.selectById(id);
        return account != null ? account.getBalance() : BigDecimal.ZERO;
    }

    public int updateBalance(String id, BigDecimal amount) {
        return accountMapper.updateBalance(id, amount);
    }

    private AccAccountVO convertToVO(AccAccount account) {
        AccAccountVO vo = new AccAccountVO();
        vo.setId(account.getId());
        vo.setAccountNo(account.getAccountNo());
        vo.setAccountName(account.getAccountName());
        vo.setSubjectId(account.getSubjectId());
        vo.setAccountType(account.getAccountType());
        vo.setChannelCode(account.getChannelCode());
        vo.setCustomerId(account.getCustomerId());
        vo.setCustomerName(account.getCustomerName());
        vo.setCurrency(account.getCurrency());
        vo.setBalance(account.getBalance());
        vo.setFrozenAmount(account.getFrozenAmount());
        vo.setBalanceDirection(account.getBalanceDirection());
        vo.setDescription(account.getDescription());
        vo.setStatus(account.getStatus());
        vo.setCreateUserId(account.getCreateUserId());
        vo.setUpdateUserId(account.getUpdateUserId());
        vo.setCreateTime(account.getCreateTime());
        vo.setUpdateTime(account.getUpdateTime());
        return vo;
    }
}
