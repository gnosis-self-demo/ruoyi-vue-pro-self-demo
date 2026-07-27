package com.gnosis.notice.service.impl;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.domain.NoticeTemplate;
import com.gnosis.notice.domain.NoticeTemplateVersion;
import com.gnosis.notice.dto.templateversion.NoticeTemplateVersionQueryRequest;
import com.gnosis.notice.dto.templateversion.NoticeTemplateVersionVO;
import com.gnosis.notice.mapper.NoticeTemplateVersionMapper;
import com.gnosis.notice.service.NoticeTemplateService;
import com.gnosis.notice.service.NoticeTemplateVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 模板版本服务实现
 */
@Service
public class NoticeTemplateVersionServiceImpl implements NoticeTemplateVersionService {

    private static final Logger log = LoggerFactory.getLogger(NoticeTemplateVersionServiceImpl.class);

    @Autowired
    private NoticeTemplateVersionMapper templateVersionMapper;

    @Autowired
    private NoticeTemplateService templateService;

    @Override
    public PageResult<NoticeTemplateVersionVO> pageList(NoticeTemplateVersionQueryRequest request) {
        if (request == null) {
            request = new NoticeTemplateVersionQueryRequest();
        }
        if (request.getPageNum() == null) {
            request.setPageNum(0);
        }
        if (request.getPageSize() == null) {
            request.setPageSize(10);
        }

        Long total = templateVersionMapper.countByCondition(request);
        if (total == 0) {
            PageResult<NoticeTemplateVersionVO> result = new PageResult<NoticeTemplateVersionVO>();
            result.setTotal(0L);
            result.setList(new ArrayList<NoticeTemplateVersionVO>());
            return result;
        }

        List<NoticeTemplateVersion> versions = templateVersionMapper.selectByCondition(request);
        List<NoticeTemplateVersionVO> voList = new ArrayList<NoticeTemplateVersionVO>();
        for (NoticeTemplateVersion version : versions) {
            voList.add(convertToVO(version));
        }

        PageResult<NoticeTemplateVersionVO> result = new PageResult<NoticeTemplateVersionVO>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    @Override
    public NoticeTemplateVersionVO detail(String id) {
        NoticeTemplateVersion version = templateVersionMapper.selectById(id);
        if (version == null) {
            return null;
        }
        return convertToVO(version);
    }

    @Override
    public String createVersion(String templateId, String changeRemark, String userId) {
        // 1. 根据模板ID获取模板
        NoticeTemplate template = null;
        // 先尝试通过ID查询
        List<NoticeTemplateVersion> existingVersions = templateVersionMapper.selectByTemplateId(templateId);
        if (existingVersions != null && !existingVersions.isEmpty()) {
            String templateCode = existingVersions.get(0).getTemplateCode();
            template = templateService.getByCode(templateCode);
        }

        if (template == null) {
            // 尝试将templateId作为templateCode查询
            template = templateService.getByCode(templateId);
        }

        if (template == null) {
            log.warn("模板不存在，无法创建版本: {}", templateId);
            return null;
        }

        // 2. 创建版本实体，从模板复制所有字段
        NoticeTemplateVersion version = new NoticeTemplateVersion();
        version.setId(UUID.randomUUID().toString().replace("-", ""));
        version.setTemplateId(template.getId());
        version.setTemplateCode(template.getTemplateCode());
        version.setTemplateName(template.getTemplateName());
        version.setTemplateType(template.getTemplateType());
        version.setNoticeType(template.getNoticeType());
        version.setSubject(template.getSubject());
        version.setContent(template.getContent());
        version.setAttachmentConfig(template.getAttachmentConfig());
        version.setThirdPartyConfig(template.getThirdPartyConfig());
        version.setPriority(template.getPriority());
        version.setChangeRemark(changeRemark);
        version.setCreateUserId(userId);
        version.setUpdateUserId(userId);
        version.setCreateTime(new Date());
        version.setUpdateTime(new Date());

        // 3. 计算版本号：查询当前模板最大版本号+1
        List<NoticeTemplateVersion> versions = templateVersionMapper.selectByTemplateId(template.getId());
        Integer maxVersion = 0;
        if (versions != null) {
            for (NoticeTemplateVersion v : versions) {
                if (v.getVersionNumber() != null && v.getVersionNumber() > maxVersion) {
                    maxVersion = v.getVersionNumber();
                }
            }
        }
        version.setVersionNumber(maxVersion + 1);

        // 4. 插入
        templateVersionMapper.insert(version);
        return version.getId();
    }

    @Override
    public List<NoticeTemplateVersionVO> listByTemplateId(String templateId) {
        if (StringUtils.isEmpty(templateId)) {
            return Collections.emptyList();
        }
        List<NoticeTemplateVersion> versions = templateVersionMapper.selectByTemplateId(templateId);
        if (versions == null || versions.isEmpty()) {
            return Collections.emptyList();
        }
        List<NoticeTemplateVersionVO> voList = new ArrayList<NoticeTemplateVersionVO>();
        for (NoticeTemplateVersion version : versions) {
            voList.add(convertToVO(version));
        }
        return voList;
    }

    @Override
    public int delete(String id) {
        return templateVersionMapper.deleteById(id);
    }

    /**
     * 转换为VO
     */
    private NoticeTemplateVersionVO convertToVO(NoticeTemplateVersion version) {
        NoticeTemplateVersionVO vo = new NoticeTemplateVersionVO();
        vo.setId(version.getId());
        vo.setTemplateId(version.getTemplateId());
        vo.setTemplateCode(version.getTemplateCode());
        vo.setVersionNumber(version.getVersionNumber());
        vo.setTemplateName(version.getTemplateName());
        vo.setTemplateType(version.getTemplateType());
        vo.setNoticeType(version.getNoticeType());
        vo.setSubject(version.getSubject());
        vo.setContent(version.getContent());
        vo.setAttachmentConfig(version.getAttachmentConfig());
        vo.setThirdPartyConfig(version.getThirdPartyConfig());
        vo.setPriority(version.getPriority());
        vo.setChangeRemark(version.getChangeRemark());
        vo.setCreateUserId(version.getCreateUserId());
        vo.setUpdateUserId(version.getUpdateUserId());
        vo.setCreateTime(version.getCreateTime());
        vo.setUpdateTime(version.getUpdateTime());
        return vo;
    }
}
