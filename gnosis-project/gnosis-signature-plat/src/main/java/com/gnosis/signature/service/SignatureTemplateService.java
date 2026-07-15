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
import com.gnosis.signature.util.ExcelExportImportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
     * 导出模板数据
     */
    public void exportData(SignatureTemplateIdsRequest request, HttpServletResponse response) throws IOException {
        List<SignatureTemplate> list;
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            list = templateMapper.selectByIds(request.getIds());
        } else {
            list = templateMapper.selectByCondition(new SignatureTemplateQueryRequest());
        }
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("templateCode", "模板编码");
        headers.put("templateName", "模板名称");
        headers.put("templateType", "模板类型");
        headers.put("templateContent", "模板内容");
        headers.put("signPosition", "签章位置");
        headers.put("description", "描述");
        headers.put("status", "状态");
        headers.put("createUserId", "创建人ID");
        headers.put("updateUserId", "更新人ID");
        headers.put("createTime", "创建时间");
        headers.put("updateTime", "更新时间");

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SignatureTemplate entity : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", entity.getId());
            row.put("templateCode", entity.getTemplateCode());
            row.put("templateName", entity.getTemplateName());
            row.put("templateType", entity.getTemplateType());
            row.put("templateContent", entity.getTemplateContent());
            row.put("signPosition", entity.getSignPosition());
            row.put("description", entity.getDescription());
            row.put("status", entity.getStatus());
            row.put("createUserId", entity.getCreateUserId());
            row.put("updateUserId", entity.getUpdateUserId());
            row.put("createTime", entity.getCreateTime());
            row.put("updateTime", entity.getUpdateTime());
            dataList.add(row);
        }
        ExcelExportImportUtil.exportExcel(response, "template_export", headers, dataList);
    }

    /**
     * 导入模板数据
     */
    public int importData(MultipartFile file) throws IOException {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("templateCode", "模板编码");
        headers.put("templateName", "模板名称");
        headers.put("templateType", "模板类型");
        headers.put("templateContent", "模板内容");
        headers.put("signPosition", "签章位置");
        headers.put("description", "描述");
        headers.put("status", "状态");

        List<Map<String, Object>> dataList = ExcelExportImportUtil.importExcel(file.getInputStream(), headers);
        int count = 0;
        for (Map<String, Object> row : dataList) {
            SignatureTemplate entity = new SignatureTemplate();
            entity.setId(UUID.randomUUID().toString());
            entity.setTemplateCode(getStringValue(row, "templateCode"));
            entity.setTemplateName(getStringValue(row, "templateName"));
            entity.setTemplateType(getStringValue(row, "templateType"));
            entity.setTemplateContent(getStringValue(row, "templateContent"));
            entity.setSignPosition(getStringValue(row, "signPosition"));
            entity.setDescription(getStringValue(row, "description"));
            entity.setStatus(getIntegerValue(row, "status"));
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            templateMapper.insert(entity);
            count++;
        }
        return count;
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

    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? String.valueOf(value) : null;
    }

    private Integer getIntegerValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null || String.valueOf(value).isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
