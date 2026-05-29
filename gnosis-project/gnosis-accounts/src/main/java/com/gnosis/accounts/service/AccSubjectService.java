package com.gnosis.accounts.service;

import com.gnosis.accounts.domain.AccSubject;
import com.gnosis.accounts.dto.subject.AccSubjectCreateRequest;
import com.gnosis.accounts.dto.subject.AccSubjectIdsRequest;
import com.gnosis.accounts.dto.subject.AccSubjectQueryRequest;
import com.gnosis.accounts.dto.subject.AccSubjectUpdateRequest;
import com.gnosis.accounts.dto.subject.AccSubjectVO;
import com.gnosis.accounts.mapper.AccSubjectMapper;
import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class AccSubjectService {

    @Autowired
    private AccSubjectMapper subjectMapper;

    public PageResult<AccSubjectVO> pageList(PageRequest<AccSubjectQueryRequest> pageRequest) {
        AccSubjectQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new AccSubjectQueryRequest();
        }
        query.setPageNum(pageRequest.getPageNum());
        query.setPageSize(pageRequest.getPageSize());

        Long total = subjectMapper.countByCondition(query);
        if (total == 0) {
            PageResult<AccSubjectVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<AccSubjectVO>());
            return result;
        }

        List<AccSubject> subjects = subjectMapper.selectByCondition(query);
        List<AccSubjectVO> voList = new ArrayList<>();
        for (AccSubject subject : subjects) {
            voList.add(convertToVO(subject));
        }

        PageResult<AccSubjectVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    public AccSubjectVO detail(String id) {
        AccSubject subject = subjectMapper.selectById(id);
        return subject != null ? convertToVO(subject) : null;
    }

    public int create(AccSubjectCreateRequest request) {
        AccSubject subject = new AccSubject();
        subject.setId(UUID.randomUUID().toString().replace("-", ""));
        subject.setSubjectCode(request.getSubjectCode());
        subject.setSubjectName(request.getSubjectName());
        subject.setSubjectType(request.getSubjectType());
        subject.setSubjectCategory(request.getSubjectCategory());
        subject.setParentId(request.getParentId());
        subject.setLevel(request.getLevel());
        subject.setBalanceDirection(request.getBalanceDirection());
        subject.setDescription(request.getDescription());
        subject.setStatus(1);
        subject.setCreateUserId(request.getCreateUserId());
        subject.setUpdateUserId(request.getCreateUserId());
        subject.setCreateTime(new Date());
        subject.setUpdateTime(new Date());

        return subjectMapper.insert(subject);
    }

    public int update(AccSubjectUpdateRequest request) {
        AccSubject subject = subjectMapper.selectById(request.getId());
        if (subject == null) {
            return 0;
        }

        subject.setSubjectCode(request.getSubjectCode());
        subject.setSubjectName(request.getSubjectName());
        subject.setSubjectType(request.getSubjectType());
        subject.setSubjectCategory(request.getSubjectCategory());
        subject.setParentId(request.getParentId());
        subject.setLevel(request.getLevel());
        subject.setBalanceDirection(request.getBalanceDirection());
        subject.setDescription(request.getDescription());
        subject.setUpdateUserId(request.getUpdateUserId());
        subject.setUpdateTime(new Date());

        return subjectMapper.updateById(subject);
    }

    public int delete(String id) {
        return subjectMapper.deleteById(id);
    }

    public int batchDelete(AccSubjectIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return subjectMapper.deleteByIds(request.getIds());
    }

    public int batchEnable(AccSubjectIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return subjectMapper.batchUpdateStatus(request.getIds(), 1);
    }

    public int batchDisable(AccSubjectIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return subjectMapper.batchUpdateStatus(request.getIds(), 0);
    }

    private AccSubjectVO convertToVO(AccSubject subject) {
        AccSubjectVO vo = new AccSubjectVO();
        vo.setId(subject.getId());
        vo.setSubjectCode(subject.getSubjectCode());
        vo.setSubjectName(subject.getSubjectName());
        vo.setSubjectType(subject.getSubjectType());
        vo.setSubjectCategory(subject.getSubjectCategory());
        vo.setParentId(subject.getParentId());
        vo.setLevel(subject.getLevel());
        vo.setBalanceDirection(subject.getBalanceDirection());
        vo.setDescription(subject.getDescription());
        vo.setStatus(subject.getStatus());
        vo.setCreateUserId(subject.getCreateUserId());
        vo.setUpdateUserId(subject.getUpdateUserId());
        vo.setCreateTime(subject.getCreateTime());
        vo.setUpdateTime(subject.getUpdateTime());
        return vo;
    }
}
