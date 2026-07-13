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
import com.gnosis.notice.util.ExcelUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 消息模板服务实现
 */
@Service
public class NoticeTemplateServiceImpl implements NoticeTemplateService {

    private static final Logger log = LoggerFactory.getLogger(NoticeTemplateServiceImpl.class);

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

    @Override
    public void export(NoticeTemplateQueryRequest request, HttpServletResponse response) throws Exception {
        List<NoticeTemplate> templates = templateMapper.selectByCondition(request);
        List<String[]> data = new ArrayList<>();
        for (NoticeTemplate template : templates) {
            String[] row = new String[]{
                template.getTemplateCode(),
                template.getTemplateName(),
                template.getTemplateType(),
                template.getNoticeType(),
                template.getSubject(),
                template.getContent(),
                template.getStatus() != null && template.getStatus() == 1 ? "启用" : "禁用",
                template.getRemark(),
                template.getCreateUserId(),
                template.getCreateTime() != null ? template.getCreateTime().toString() : "",
                template.getUpdateUserId(),
                template.getUpdateTime() != null ? template.getUpdateTime().toString() : ""
            };
            data.add(row);
        }
        String[] headers = {"模板编码", "模板名称", "模板类型", "通知类型", "消息主题", "模板内容",
            "状态", "备注", "创建人", "创建时间", "更新人", "更新时间"};
        ExcelUtils.export(response, "消息模板", "模板列表", headers, data);
    }

    @Override
    public int importExcel(InputStream inputStream, String userId) throws Exception {
        List<String[]> data = ExcelUtils.read(inputStream, true);
        int count = 0;
        for (String[] row : data) {
            if (row.length < 6 || StringUtils.isEmpty(row[0])) {
                continue;
            }
            NoticeTemplate template = new NoticeTemplate();
            template.setId(UUID.randomUUID().toString().replace("-", ""));
            template.setTemplateCode(row[0]);
            template.setTemplateName(row[1]);
            template.setTemplateType(row[2]);
            template.setNoticeType(row[3]);
            template.setSubject(row[4]);
            template.setContent(row[5]);
            template.setRemark(row.length > 6 ? row[6] : "");

            // 检查模板编码是否已存在
            NoticeTemplate exist = templateMapper.selectByCode(row[0]);
            if (exist != null) {
                // 更新已存在的模板
                exist.setTemplateName(template.getTemplateName());
                exist.setTemplateType(template.getTemplateType());
                exist.setNoticeType(template.getNoticeType());
                exist.setSubject(template.getSubject());
                exist.setContent(template.getContent());
                exist.setRemark(template.getRemark());
                exist.setUpdateUserId(userId);
                exist.setUpdateTime(new Date());
                templateMapper.updateById(exist);
            } else {
                // 新增模板
                template.setCreateUserId(userId);
                template.setUpdateUserId(userId);
                template.setCreateTime(new Date());
                template.setUpdateTime(new Date());
                template.setStatus(1); // 默认启用
                templateMapper.insert(template);
            }
            count++;
        }
        return count;
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
