package com.gnosis.paramcheck.service;

import com.gnosis.paramcheck.domain.BusinessType;
import com.gnosis.paramcheck.dto.PageRequest;
import com.gnosis.paramcheck.dto.PageResult;
import com.gnosis.paramcheck.mapper.BusinessTypeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessTypeService {

    private static final Logger log = LoggerFactory.getLogger(BusinessTypeService.class);

    @Autowired
    private BusinessTypeMapper businessTypeMapper;

    public BusinessType findByCode(String code) {
        return businessTypeMapper.selectByCode(code);
    }

    public List<BusinessType> findAll() {
        return businessTypeMapper.selectAll();
    }

    public List<BusinessType> findAllActive() {
        return businessTypeMapper.selectActive();
    }

    public PageResult<BusinessType> findPage(String code, String name, Boolean isActive, PageRequest pageRequest) {
        int total = businessTypeMapper.countByCondition(code, name, isActive);
        List<BusinessType> records = businessTypeMapper.selectByConditionWithPaging(
                code, name, isActive, pageRequest.getOffset(), pageRequest.getPageSize());
        return new PageResult<>(records, total, pageRequest.getPage(), pageRequest.getPageSize());
    }

    public void save(BusinessType businessType) {
        BusinessType existing = businessTypeMapper.selectByCode(businessType.getCode());
        if (existing != null) {
            businessTypeMapper.updateByCode(businessType);
        } else {
            if (businessType.getIsActive() == null) {
                businessType.setIsActive(true);
            }
            if (businessType.getCreateUserId() == null) {
                businessType.setCreateUserId("admin");
            }
            if (businessType.getUpdateUserId() == null) {
                businessType.setUpdateUserId("admin");
            }
            businessTypeMapper.insert(businessType);
        }
    }

    public void delete(String code) {
        businessTypeMapper.deleteByCode(code);
    }

    public void batchDelete(List<String> codes) {
        if (codes != null && !codes.isEmpty()) {
            businessTypeMapper.batchDelete(codes);
        }
    }

    public void activate(String code) {
        businessTypeMapper.activate(code);
    }

    public void deactivate(String code) {
        businessTypeMapper.deactivate(code);
    }

    public void batchActivate(List<String> codes) {
        if (codes != null && !codes.isEmpty()) {
            businessTypeMapper.batchActivate(codes);
        }
    }

    public void batchDeactivate(List<String> codes) {
        if (codes != null && !codes.isEmpty()) {
            businessTypeMapper.batchDeactivate(codes);
        }
    }
}
