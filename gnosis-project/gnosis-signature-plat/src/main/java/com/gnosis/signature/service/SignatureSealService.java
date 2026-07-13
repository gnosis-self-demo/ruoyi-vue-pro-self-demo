package com.gnosis.signature.service;

import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.signature.domain.SignatureSeal;
import com.gnosis.signature.dto.seal.SignatureSealCreateRequest;
import com.gnosis.signature.dto.seal.SignatureSealIdsRequest;
import com.gnosis.signature.dto.seal.SignatureSealQueryRequest;
import com.gnosis.signature.dto.seal.SignatureSealUpdateRequest;
import com.gnosis.signature.dto.seal.SignatureSealVO;
import com.gnosis.signature.mapper.SignatureSealMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 印章服务
 */
@Service
public class SignatureSealService {

    private static final Logger log = LoggerFactory.getLogger(SignatureSealService.class);

    @Autowired
    private SignatureSealMapper sealMapper;

    /**
     * 分页查询印章列表
     */
    public PageResult<SignatureSealVO> pageList(PageRequest<SignatureSealQueryRequest> pageRequest) {
        if (pageRequest == null) {
            pageRequest = new PageRequest<SignatureSealQueryRequest>();
            pageRequest.setQuery(new SignatureSealQueryRequest());
        }
        SignatureSealQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new SignatureSealQueryRequest();
            pageRequest.setQuery(query);
        }
        if (pageRequest.getPageNum() != null) {
            query.setPageNum(pageRequest.getPageNum());
        }
        if (pageRequest.getPageSize() != null) {
            query.setPageSize(pageRequest.getPageSize());
        }
        if (query.getPageNum() == null) {
            query.setPageNum(0);
        }
        if (query.getPageSize() == null) {
            query.setPageSize(10);
        }

        // 查询总数
        Long total = sealMapper.countByCondition(query);
        if (total == 0) {
            PageResult<SignatureSealVO> result = new PageResult<SignatureSealVO>();
            result.setTotal(0L);
            result.setList(new ArrayList<SignatureSealVO>());
            return result;
        }

        // 查询列表
        List<SignatureSeal> seals = sealMapper.selectByCondition(query);
        List<SignatureSealVO> voList = new ArrayList<SignatureSealVO>();
        for (SignatureSeal seal : seals) {
            voList.add(convertToVO(seal));
        }

        PageResult<SignatureSealVO> result = new PageResult<SignatureSealVO>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    /**
     * 根据ID查询印章详情
     */
    public SignatureSealVO detail(String id) {
        SignatureSeal seal = sealMapper.selectById(id);
        if (seal == null) {
            return null;
        }
        return convertToVO(seal);
    }

    /**
     * 创建印章
     */
    public int create(SignatureSealCreateRequest request) {
        SignatureSeal seal = new SignatureSeal();
        seal.setId(UUID.randomUUID().toString());
        seal.setSealCode(request.getSealCode());
        seal.setSealName(request.getSealName());
        seal.setSealType(request.getSealType());
        seal.setSealImagePath(request.getSealImagePath());
        seal.setSealImageData(request.getSealImageData());
        seal.setAuthorizeUserId(request.getAuthorizeUserId());
        seal.setAuthorizeTime(request.getAuthorizeTime());
        seal.setExpireTime(request.getExpireTime());
        seal.setDescription(request.getDescription());
        seal.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        seal.setCreateUserId(request.getCreateUserId());
        seal.setUpdateUserId(request.getUpdateUserId());
        seal.setCreateTime(new Date());
        seal.setUpdateTime(new Date());

        return sealMapper.insert(seal);
    }

    /**
     * 更新印章
     */
    public int update(SignatureSealUpdateRequest request) {
        SignatureSeal seal = sealMapper.selectById(request.getId());
        if (seal == null) {
            return 0;
        }

        seal.setSealCode(request.getSealCode());
        seal.setSealName(request.getSealName());
        seal.setSealType(request.getSealType());
        seal.setSealImagePath(request.getSealImagePath());
        seal.setSealImageData(request.getSealImageData());
        seal.setAuthorizeUserId(request.getAuthorizeUserId());
        seal.setAuthorizeTime(request.getAuthorizeTime());
        seal.setExpireTime(request.getExpireTime());
        seal.setDescription(request.getDescription());
        seal.setStatus(request.getStatus());
        seal.setUpdateUserId(request.getUpdateUserId());
        seal.setUpdateTime(new Date());

        return sealMapper.updateById(seal);
    }

    /**
     * 删除印章
     */
    public int delete(String id) {
        return sealMapper.deleteById(id);
    }

    /**
     * 批量删除印章
     */
    public int batchDelete(SignatureSealIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return sealMapper.deleteByIds(request.getIds());
    }

    /**
     * 批量启用印章
     */
    public int batchEnable(SignatureSealIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return sealMapper.batchUpdateStatus(request.getIds(), 1);
    }

    /**
     * 批量禁用印章
     */
    public int batchDisable(SignatureSealIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return sealMapper.batchUpdateStatus(request.getIds(), 0);
    }

    /**
     * 转换为VO
     */
    private SignatureSealVO convertToVO(SignatureSeal seal) {
        SignatureSealVO vo = new SignatureSealVO();
        vo.setId(seal.getId());
        vo.setSealCode(seal.getSealCode());
        vo.setSealName(seal.getSealName());
        vo.setSealType(seal.getSealType());
        vo.setSealImagePath(seal.getSealImagePath());
        vo.setSealImageData(seal.getSealImageData());
        vo.setAuthorizeUserId(seal.getAuthorizeUserId());
        vo.setAuthorizeTime(seal.getAuthorizeTime());
        vo.setExpireTime(seal.getExpireTime());
        vo.setDescription(seal.getDescription());
        vo.setStatus(seal.getStatus());
        vo.setCreateUserId(seal.getCreateUserId());
        vo.setUpdateUserId(seal.getUpdateUserId());
        vo.setCreateTime(seal.getCreateTime());
        vo.setUpdateTime(seal.getUpdateTime());
        return vo;
    }
}
