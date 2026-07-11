package com.gnosis.notice.service.impl;

import com.gnosis.common.domain.BaseEntity;
import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.domain.NoticeTemplate;
import com.gnosis.notice.dto.template.NoticeTemplateCreateRequest;
import com.gnosis.notice.dto.template.NoticeTemplateQueryRequest;
import com.gnosis.notice.dto.template.NoticeTemplateUpdateRequest;
import com.gnosis.notice.dto.template.NoticeTemplateVO;
import com.gnosis.notice.mapper.NoticeTemplateMapper;
import com.gnosis.notice.service.NoticeTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 消息模板服务实现
 */
@Service
public class NoticeTemplateServiceImpl implements NoticeTemplateService {

    @Autowired
    private NoticeTemplateMapper templateMapper;

    @Override
    public PageResult<NoticeTemplateVO> pageList(NoticeTemplateQueryRequest request) {
        if (request == null) {
            request = new NoticeTemplateQueryRequest();
        }
        if (request.getPageNum() == null) {
            request.setPageNum(0);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        // 查询总数
        Long total = templateMapper.countByCondition(request);
        if (total == 0) {
            PageResult<NoticeTemplateVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<>());
            return result;
        }

        // 查询列表
        List<NoticeTemplate> templates = templateMapper.selectByCondition(request);
        List<NoticeTemplateVO> voList = new ArrayList<>();
        for (NoticeTemplate template : templates) {
            voList.add(convertToVO(template));
        }

        PageResult<NoticeTemplateVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    @Override
    public NoticeTemplateVO detail(String id) {
        NoticeTemplate template = templateMapper.selectById(id);
        if (template == null) {
            return null;
        }
        return convertToVO(template);
    }

    @Override
    public NoticeTemplate getByCode(String templateCode) {
        if (StringUtils.isEmpty(templateCode)) {
            return null;
        }
        return templateMapper.selectByCode(templateCode);
    }

    @Override
    public String create(NoticeTemplateCreateRequest request, String userId) {
        NoticeTemplate template = new NoticeTemplate();
        template.setId(UUID.randomUUID().toString().replace("-", ""));
        template.setTemplateCode(request.getTemplateCode());
        template.setTemplateName(request.getTemplateName());
        template.setTemplateType(request.getTemplateType());
        template.setNoticeType(request.getNoticeType());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setAttachmentConfig(request.getAttachmentConfig());
        template.setThirdPartyConfig(request.getThirdPartyConfig());
        template.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        template.setRemark(request.getRemark());
        template.setCreateUserId(userId);
        template.setUpdateUserId(userId);
        template.setCreateTime(new Date());
        template.setUpdateTime(new Date());

        templateMapper.insert(template);
        return template.getId();
    }

    @Override
    public int update(NoticeTemplateUpdateRequest request, String userId) {
        NoticeTemplate template = templateMapper.selectById(request.getId());
        if (template == null) {
            return 0;
        }

        template.setTemplateName(request.getTemplateName());
        template.setTemplateType(request.getTemplateType());
        template.setNoticeType(request.getNoticeType());
        template.setSubject(request.getSubject());
        template.setContent(request.getContent());
        template.setAttachmentConfig(request.getAttachmentConfig());
        template.setThirdPartyConfig(request.getThirdPartyConfig());
        template.setStatus(request.getStatus());
        template.setRemark(request.getRemark());
        template.setUpdateUserId(userId);
        template.setUpdateTime(new Date());

        return templateMapper.updateById(template);
    }

    @Override
    public int delete(String id) {
        return templateMapper.deleteById(id);
    }

    @Override
    public int batchDelete(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return templateMapper.deleteByIds(ids);
    }

    @Override
    public int batchEnable(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return templateMapper.batchUpdateStatus(ids, 1);
    }

    @Override
    public int batchDisable(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return templateMapper.batchUpdateStatus(ids, 0);
    }

    /**
     * 转换为VO
     */
    private NoticeTemplateVO convertToVO(NoticeTemplate template) {
        NoticeTemplateVO vo = new NoticeTemplateVO();
        vo.setId(template.getId());
        vo.setTemplateCode(template.getTemplateCode());
        vo.setTemplateName(template.getTemplateName());
        vo.setTemplateType(template.getTemplateType());
        vo.setNoticeType(template.getNoticeType());
        vo.setSubject(template.getSubject());
        vo.setContent(template.getContent());
        vo.setAttachmentConfig(template.getAttachmentConfig());
        vo.setThirdPartyConfig(template.getThirdPartyConfig());
        vo.setStatus(template.getStatus());
        vo.setRemark(template.getRemark());
        vo.setCreateUserId(template.getCreateUserId());
        vo.setUpdateUserId(template.getUpdateUserId());
        vo.setCreateTime(template.getCreateTime());
        vo.setUpdateTime(template.getUpdateTime());
        return vo;
    }
}
