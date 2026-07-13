package com.gnosis.signature.service;

import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.signature.domain.SignatureSupplier;
import com.gnosis.signature.dto.supplier.SignatureSupplierCreateRequest;
import com.gnosis.signature.dto.supplier.SignatureSupplierIdsRequest;
import com.gnosis.signature.dto.supplier.SignatureSupplierQueryRequest;
import com.gnosis.signature.dto.supplier.SignatureSupplierUpdateRequest;
import com.gnosis.signature.dto.supplier.SignatureSupplierVO;
import com.gnosis.signature.mapper.SignatureSupplierMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 供应商服务
 */
@Service
public class SignatureSupplierService {

    @Autowired
    private SignatureSupplierMapper mapper;

    /**
     * 分页查询供应商列表
     */
    public PageResult<SignatureSupplierVO> pageList(PageRequest<SignatureSupplierQueryRequest> pageRequest) {
        if (pageRequest == null) {
            pageRequest = new PageRequest<>();
        }
        SignatureSupplierQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new SignatureSupplierQueryRequest();
        }
        if (query.getPageNum() == null) {
            query.setPageNum(pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 0);
        }
        if (query.getPageSize() == null) {
            query.setPageSize(pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10);
        }

        Long total = mapper.countByCondition(query);
        if (total == 0) {
            PageResult<SignatureSupplierVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<>());
            return result;
        }

        List<SignatureSupplier> list = mapper.selectByCondition(query);
        List<SignatureSupplierVO> voList = new ArrayList<>();
        for (SignatureSupplier entity : list) {
            voList.add(convertToVO(entity));
        }

        PageResult<SignatureSupplierVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    /**
     * 根据ID查询供应商详情
     */
    public SignatureSupplierVO detail(String id) {
        SignatureSupplier entity = mapper.selectById(id);
        if (entity == null) {
            return null;
        }
        return convertToVO(entity);
    }

    /**
     * 创建供应商
     */
    public int create(SignatureSupplierCreateRequest request) {
        SignatureSupplier entity = new SignatureSupplier();
        entity.setId(UUID.randomUUID().toString());
        entity.setSupplierCode(request.getSupplierCode());
        entity.setSupplierName(request.getSupplierName());
        entity.setSupplierType(request.getSupplierType());
        entity.setApiUrl(request.getApiUrl());
        entity.setApiKey(request.getApiKey());
        entity.setApiSecret(request.getApiSecret());
        entity.setContactName(request.getContactName());
        entity.setContactPhone(request.getContactPhone());
        entity.setContactEmail(request.getContactEmail());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        entity.setCreateUserId(request.getCreateUserId());
        entity.setUpdateUserId(request.getUpdateUserId());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        return mapper.insert(entity);
    }

    /**
     * 更新供应商
     */
    public int update(SignatureSupplierUpdateRequest request) {
        SignatureSupplier entity = mapper.selectById(request.getId());
        if (entity == null) {
            return 0;
        }
        entity.setSupplierCode(request.getSupplierCode());
        entity.setSupplierName(request.getSupplierName());
        entity.setSupplierType(request.getSupplierType());
        entity.setApiUrl(request.getApiUrl());
        entity.setApiKey(request.getApiKey());
        entity.setApiSecret(request.getApiSecret());
        entity.setContactName(request.getContactName());
        entity.setContactPhone(request.getContactPhone());
        entity.setContactEmail(request.getContactEmail());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        entity.setUpdateUserId(request.getUpdateUserId());
        entity.setUpdateTime(new Date());
        return mapper.updateById(entity);
    }

    /**
     * 删除供应商
     */
    public int delete(String id) {
        return mapper.deleteById(id);
    }

    /**
     * 批量删除供应商
     */
    public int batchDelete(SignatureSupplierIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return mapper.deleteByIds(request.getIds());
    }

    /**
     * 批量启用供应商
     */
    public int batchEnable(SignatureSupplierIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return mapper.batchUpdateStatus(request.getIds(), 1);
    }

    /**
     * 批量禁用供应商
     */
    public int batchDisable(SignatureSupplierIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return mapper.batchUpdateStatus(request.getIds(), 0);
    }

    /**
     * 获取可用供应商列表
     */
    public List<SignatureSupplier> getAvailableSuppliers() {
        SignatureSupplierQueryRequest query = new SignatureSupplierQueryRequest();
        query.setStatus(1);
        return mapper.selectByCondition(query);
    }

    /**
     * 转换为VO
     */
    private SignatureSupplierVO convertToVO(SignatureSupplier entity) {
        SignatureSupplierVO vo = new SignatureSupplierVO();
        vo.setId(entity.getId());
        vo.setSupplierCode(entity.getSupplierCode());
        vo.setSupplierName(entity.getSupplierName());
        vo.setSupplierType(entity.getSupplierType());
        vo.setApiUrl(entity.getApiUrl());
        vo.setApiKey(entity.getApiKey());
        vo.setApiSecret(entity.getApiSecret());
        vo.setContactName(entity.getContactName());
        vo.setContactPhone(entity.getContactPhone());
        vo.setContactEmail(entity.getContactEmail());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        vo.setCreateUserId(entity.getCreateUserId());
        vo.setUpdateUserId(entity.getUpdateUserId());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}
