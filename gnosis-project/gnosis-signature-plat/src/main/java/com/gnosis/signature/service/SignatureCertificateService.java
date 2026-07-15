package com.gnosis.signature.service;

import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.signature.domain.SignatureCertificate;
import com.gnosis.signature.dto.certificate.SignatureCertificateCreateRequest;
import com.gnosis.signature.dto.certificate.SignatureCertificateIdsRequest;
import com.gnosis.signature.dto.certificate.SignatureCertificateQueryRequest;
import com.gnosis.signature.dto.certificate.SignatureCertificateUpdateRequest;
import com.gnosis.signature.dto.certificate.SignatureCertificateVO;
import com.gnosis.signature.mapper.SignatureCertificateMapper;
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
 * 证书服务
 */
@Service
public class SignatureCertificateService {

    @Autowired
    private SignatureCertificateMapper mapper;

    /**
     * 分页查询证书列表
     */
    public PageResult<SignatureCertificateVO> pageList(PageRequest<SignatureCertificateQueryRequest> pageRequest) {
        if (pageRequest == null) {
            pageRequest = new PageRequest<>();
        }
        SignatureCertificateQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new SignatureCertificateQueryRequest();
        }
        if (query.getPageNum() == null) {
            query.setPageNum(pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 0);
        }
        if (query.getPageSize() == null) {
            query.setPageSize(pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10);
        }

        Long total = mapper.countByCondition(query);
        if (total == 0) {
            PageResult<SignatureCertificateVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<>());
            return result;
        }

        List<SignatureCertificate> list = mapper.selectByCondition(query);
        List<SignatureCertificateVO> voList = new ArrayList<>();
        for (SignatureCertificate entity : list) {
            voList.add(convertToVO(entity));
        }

        PageResult<SignatureCertificateVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    /**
     * 根据ID查询证书详情
     */
    public SignatureCertificateVO detail(String id) {
        SignatureCertificate entity = mapper.selectById(id);
        if (entity == null) {
            return null;
        }
        return convertToVO(entity);
    }

    /**
     * 创建证书
     */
    public int create(SignatureCertificateCreateRequest request) {
        SignatureCertificate entity = new SignatureCertificate();
        entity.setId(UUID.randomUUID().toString());
        entity.setCertCode(request.getCertCode());
        entity.setCertName(request.getCertName());
        entity.setCertType(request.getCertType());
        entity.setCaOrg(request.getCaOrg());
        entity.setCertSn(request.getCertSn());
        entity.setCertContent(request.getCertContent());
        entity.setExpireTime(request.getExpireTime());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        entity.setCreateUserId(request.getCreateUserId());
        entity.setUpdateUserId(request.getUpdateUserId());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        return mapper.insert(entity);
    }

    /**
     * 更新证书
     */
    public int update(SignatureCertificateUpdateRequest request) {
        SignatureCertificate entity = mapper.selectById(request.getId());
        if (entity == null) {
            return 0;
        }
        entity.setCertCode(request.getCertCode());
        entity.setCertName(request.getCertName());
        entity.setCertType(request.getCertType());
        entity.setCaOrg(request.getCaOrg());
        entity.setCertSn(request.getCertSn());
        entity.setCertContent(request.getCertContent());
        entity.setExpireTime(request.getExpireTime());
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        entity.setUpdateUserId(request.getUpdateUserId());
        entity.setUpdateTime(new Date());
        return mapper.updateById(entity);
    }

    /**
     * 删除证书
     */
    public int delete(String id) {
        return mapper.deleteById(id);
    }

    /**
     * 批量删除证书
     */
    public int batchDelete(SignatureCertificateIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return mapper.deleteByIds(request.getIds());
    }

    /**
     * 批量启用证书
     */
    public int batchEnable(SignatureCertificateIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return mapper.batchUpdateStatus(request.getIds(), 1);
    }

    /**
     * 批量禁用证书
     */
    public int batchDisable(SignatureCertificateIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return mapper.batchUpdateStatus(request.getIds(), 0);
    }

    /**
     * 导出证书数据
     */
    public void exportData(SignatureCertificateIdsRequest request, HttpServletResponse response) throws IOException {
        List<SignatureCertificate> list;
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            list = mapper.selectByIds(request.getIds());
        } else {
            list = mapper.selectByCondition(new SignatureCertificateQueryRequest());
        }
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("certCode", "证书编码");
        headers.put("certName", "证书名称");
        headers.put("certType", "证书类型");
        headers.put("caOrg", "CA机构");
        headers.put("certSn", "证书序列号");
        headers.put("certContent", "证书内容");
        headers.put("expireTime", "过期时间");
        headers.put("description", "描述");
        headers.put("status", "状态");
        headers.put("createUserId", "创建人ID");
        headers.put("updateUserId", "更新人ID");
        headers.put("createTime", "创建时间");
        headers.put("updateTime", "更新时间");

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SignatureCertificate entity : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", entity.getId());
            row.put("certCode", entity.getCertCode());
            row.put("certName", entity.getCertName());
            row.put("certType", entity.getCertType());
            row.put("caOrg", entity.getCaOrg());
            row.put("certSn", entity.getCertSn());
            row.put("certContent", entity.getCertContent());
            row.put("expireTime", entity.getExpireTime());
            row.put("description", entity.getDescription());
            row.put("status", entity.getStatus());
            row.put("createUserId", entity.getCreateUserId());
            row.put("updateUserId", entity.getUpdateUserId());
            row.put("createTime", entity.getCreateTime());
            row.put("updateTime", entity.getUpdateTime());
            dataList.add(row);
        }
        ExcelExportImportUtil.exportExcel(response, "certificate_export", headers, dataList);
    }

    /**
     * 导入证书数据
     */
    public int importData(MultipartFile file) throws IOException {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("certCode", "证书编码");
        headers.put("certName", "证书名称");
        headers.put("certType", "证书类型");
        headers.put("caOrg", "CA机构");
        headers.put("certSn", "证书序列号");
        headers.put("certContent", "证书内容");
        headers.put("description", "描述");
        headers.put("status", "状态");

        List<Map<String, Object>> dataList = ExcelExportImportUtil.importExcel(file.getInputStream(), headers);
        int count = 0;
        for (Map<String, Object> row : dataList) {
            SignatureCertificate entity = new SignatureCertificate();
            entity.setId(UUID.randomUUID().toString());
            entity.setCertCode(getStringValue(row, "certCode"));
            entity.setCertName(getStringValue(row, "certName"));
            entity.setCertType(getStringValue(row, "certType"));
            entity.setCaOrg(getStringValue(row, "caOrg"));
            entity.setCertSn(getStringValue(row, "certSn"));
            entity.setCertContent(getStringValue(row, "certContent"));
            entity.setDescription(getStringValue(row, "description"));
            entity.setStatus(getIntegerValue(row, "status"));
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            mapper.insert(entity);
            count++;
        }
        return count;
    }

    /**
     * 转换为VO
     */
    private SignatureCertificateVO convertToVO(SignatureCertificate entity) {
        SignatureCertificateVO vo = new SignatureCertificateVO();
        vo.setId(entity.getId());
        vo.setCertCode(entity.getCertCode());
        vo.setCertName(entity.getCertName());
        vo.setCertType(entity.getCertType());
        vo.setCaOrg(entity.getCaOrg());
        vo.setCertSn(entity.getCertSn());
        vo.setCertContent(entity.getCertContent());
        vo.setExpireTime(entity.getExpireTime());
        vo.setDescription(entity.getDescription());
        vo.setStatus(entity.getStatus());
        vo.setCreateUserId(entity.getCreateUserId());
        vo.setUpdateUserId(entity.getUpdateUserId());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
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
