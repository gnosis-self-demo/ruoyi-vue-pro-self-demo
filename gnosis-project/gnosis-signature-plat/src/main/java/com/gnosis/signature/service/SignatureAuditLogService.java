package com.gnosis.signature.service;

import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.signature.domain.SignatureAuditLog;
import com.gnosis.signature.dto.audit.SignatureAuditLogCreateRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogIdsRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogQueryRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogVO;
import com.gnosis.signature.mapper.SignatureAuditLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 审计日志服务
 */
@Service
public class SignatureAuditLogService {

    @Autowired
    private SignatureAuditLogMapper mapper;

    /**
     * 分页查询审计日志列表
     */
    public PageResult<SignatureAuditLogVO> pageList(PageRequest<SignatureAuditLogQueryRequest> pageRequest) {
        if (pageRequest == null) {
            pageRequest = new PageRequest<>();
        }
        SignatureAuditLogQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new SignatureAuditLogQueryRequest();
        }
        if (query.getPageNum() == null) {
            query.setPageNum(pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 0);
        }
        if (query.getPageSize() == null) {
            query.setPageSize(pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10);
        }

        Long total = mapper.countByCondition(query);
        if (total == 0) {
            PageResult<SignatureAuditLogVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<>());
            return result;
        }

        List<SignatureAuditLog> list = mapper.selectByCondition(query);
        List<SignatureAuditLogVO> voList = new ArrayList<>();
        for (SignatureAuditLog entity : list) {
            voList.add(convertToVO(entity));
        }

        PageResult<SignatureAuditLogVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    /**
     * 根据ID查询审计日志详情
     */
    public SignatureAuditLogVO detail(String id) {
        SignatureAuditLog entity = mapper.selectById(id);
        if (entity == null) {
            return null;
        }
        return convertToVO(entity);
    }

    /**
     * 创建审计日志
     */
    public int create(SignatureAuditLogCreateRequest request) {
        SignatureAuditLog entity = new SignatureAuditLog();
        entity.setId(UUID.randomUUID().toString());
        entity.setOperationType(request.getOperationType());
        entity.setOperationModule(request.getOperationModule());
        entity.setOperationDesc(request.getOperationDesc());
        entity.setRequestParams(request.getRequestParams());
        entity.setResponseResult(request.getResponseResult());
        entity.setOperationIp(request.getOperationIp());
        entity.setOperationResult(request.getOperationResult());
        entity.setErrorMsg(request.getErrorMsg());
        entity.setCreateUserId(request.getCreateUserId());
        entity.setUpdateUserId(request.getUpdateUserId());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        return mapper.insert(entity);
    }

    /**
     * 批量删除审计日志
     */
    public int batchDelete(SignatureAuditLogIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return mapper.deleteByIds(request.getIds());
    }

    /**
     * 转换为VO
     */
    private SignatureAuditLogVO convertToVO(SignatureAuditLog entity) {
        SignatureAuditLogVO vo = new SignatureAuditLogVO();
        vo.setId(entity.getId());
        vo.setOperationType(entity.getOperationType());
        vo.setOperationModule(entity.getOperationModule());
        vo.setOperationDesc(entity.getOperationDesc());
        vo.setRequestParams(entity.getRequestParams());
        vo.setResponseResult(entity.getResponseResult());
        vo.setOperationIp(entity.getOperationIp());
        vo.setOperationResult(entity.getOperationResult());
        vo.setErrorMsg(entity.getErrorMsg());
        vo.setCreateUserId(entity.getCreateUserId());
        vo.setUpdateUserId(entity.getUpdateUserId());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
