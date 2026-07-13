package com.gnosis.signature.service;

import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.signature.domain.SignatureTemplate;
import com.gnosis.signature.dto.template.SignatureTemplateCreateRequest;
import com.gnosis.signature.dto.template.SignatureTemplateIdsRequest;
import com.gnosis.signature.dto.template.SignatureTemplateQueryRequest;
import com.gnosis.signature.dto.template.SignatureTemplateUpdateRequest;
import com.gnosis.signature.dto.template.SignatureTemplateVO;
import com.gnosis.signature.mapper.SignatureTemplateMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 签章模板服务
 */
@Service
public class SignatureTemplateService {

    @Autowired
    private SignatureTemplateMapper templateMapper;

    /**
     * 分页查询模板列表
     */
    public PageResult<SignatureTemplateVO> pageList(PageRequest<SignatureTemplateQueryRequest> request) {
        SignatureTemplateQueryRequest query = request.getQuery();
        if (query == null) {
            query = new SignatureTemplateQueryRequest();
            request.setQuery(query);
        }
        if (request.getPageNum() != null) {
            query.setPageNum(request.getPageNum());
        } else {
            query.setPageNum(0);
        }
        if (request.getPageSize() != null) {
            query.setPageSize(request.getPageSize());
        } else {
            query.setPageSize(10);
        }

        Long total = templateMapper.countByCondition(query);
        if (total == 0) {
            PageResult<SignatureTemplateVO> result = new PageResult<SignatureTemplateVO>();
            result.setTotal(0L);
            result.setList(new ArrayList<SignatureTemplateVO>());
            return result;
        }

        List<SignatureTemplate> templates = templateMapper.selectByCondition(query);
        List<SignatureTemplateVO> voList = new ArrayList<SignatureTemplateVO>();
        for (SignatureTemplate template : templates) {
            voList.add(convertToVO(template));
        }

        PageResult<SignatureTemplateVO> result = new PageResult<SignatureTemplateVO>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    /**
     * 根据ID查询模板详情
     */
    public SignatureTemplateVO detail(String id) {
        SignatureTemplate template = templateMapper.selectById(id);
        if (template == null) {
            return null;
        }
        return convertToVO(template);
    }

    /**
     * 创建模板
     */
    public int create(SignatureTemplateCreateRequest request) {
        SignatureTemplate template = new SignatureTemplate();
        template.setId(UUID.randomUUID().toString().replace("-", ""));
        template.setTemplateCode(request.getTemplateCode());
        template.setTemplateName(request.getTemplateName());
        template.setTemplateType(request.getTemplateType());
        template.setTemplateContent(request.getTemplateContent());
        template.setSignPosition(request.getSignPosition());
        template.setDescription(request.getDescription());
        template.setStatus(request.getStatus());
        template.setCreateUserId(request.getCreateUserId());
        template.setUpdateUserId(request.getUpdateUserId());
        template.setCreateTime(new Date());
        template.setUpdateTime(new Date());
        return templateMapper.insert(template);
    }

    /**
     * 更新模板
     */
    public int update(SignatureTemplateUpdateRequest request) {
        SignatureTemplate template = templateMapper.selectById(request.getId());
        if (template == null) {
            return 0;
        }
        template.setTemplateCode(request.getTemplateCode());
        template.setTemplateName(request.getTemplateName());
        template.setTemplateType(request.getTemplateType());
        template.setTemplateContent(request.getTemplateContent());
        template.setSignPosition(request.getSignPosition());
        template.setDescription(request.getDescription());
        template.setStatus(request.getStatus());
        template.setUpdateUserId(request.getUpdateUserId());
        template.setUpdateTime(new Date());
        return templateMapper.updateById(template);
    }

    /**
     * 删除模板
     */
    public int delete(String id) {
        return templateMapper.deleteById(id);
    }

    /**
     * 批量删除模板
     */
    public int batchDelete(SignatureTemplateIdsRequest request) {
        List<String> ids = request.getIds();
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return templateMapper.deleteByIds(ids);
    }

    /**
     * 批量启用模板
     */
    public int batchEnable(SignatureTemplateIdsRequest request) {
        List<String> ids = request.getIds();
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return templateMapper.batchUpdateStatus(ids, 1);
    }

    /**
     * 批量禁用模板
     */
    public int batchDisable(SignatureTemplateIdsRequest request) {
        List<String> ids = request.getIds();
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return templateMapper.batchUpdateStatus(ids, 0);
    }

    /**
     * 转换为VO
     */
    private SignatureTemplateVO convertToVO(SignatureTemplate template) {
        SignatureTemplateVO vo = new SignatureTemplateVO();
        vo.setId(template.getId());
        vo.setTemplateCode(template.getTemplateCode());
        vo.setTemplateName(template.getTemplateName());
        vo.setTemplateType(template.getTemplateType());
        vo.setTemplateContent(template.getTemplateContent());
        vo.setSignPosition(template.getSignPosition());
        vo.setDescription(template.getDescription());
        vo.setStatus(template.getStatus());
        vo.setCreateUserId(template.getCreateUserId());
        vo.setUpdateUserId(template.getUpdateUserId());
        vo.setCreateTime(template.getCreateTime());
        vo.setUpdateTime(template.getUpdateTime());
        return vo;
    }
}
