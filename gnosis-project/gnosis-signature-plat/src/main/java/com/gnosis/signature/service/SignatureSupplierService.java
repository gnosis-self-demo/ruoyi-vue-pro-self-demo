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
     * 导出供应商数据
     */
    public void exportData(SignatureSupplierIdsRequest request, HttpServletResponse response) throws IOException {
        List<SignatureSupplier> list;
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            list = mapper.selectByIds(request.getIds());
        } else {
            list = mapper.selectByCondition(new SignatureSupplierQueryRequest());
        }
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("supplierCode", "供应商编码");
        headers.put("supplierName", "供应商名称");
        headers.put("supplierType", "供应商类型");
        headers.put("apiUrl", "API地址");
        headers.put("apiKey", "API密钥");
        headers.put("apiSecret", "API密钥Secret");
        headers.put("contactName", "联系人");
        headers.put("contactPhone", "联系电话");
        headers.put("contactEmail", "联系邮箱");
        headers.put("description", "描述");
        headers.put("status", "状态");
        headers.put("createUserId", "创建人ID");
        headers.put("updateUserId", "更新人ID");
        headers.put("createTime", "创建时间");
        headers.put("updateTime", "更新时间");

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SignatureSupplier entity : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", entity.getId());
            row.put("supplierCode", entity.getSupplierCode());
            row.put("supplierName", entity.getSupplierName());
            row.put("supplierType", entity.getSupplierType());
            row.put("apiUrl", entity.getApiUrl());
            row.put("apiKey", entity.getApiKey());
            row.put("apiSecret", entity.getApiSecret());
            row.put("contactName", entity.getContactName());
            row.put("contactPhone", entity.getContactPhone());
            row.put("contactEmail", entity.getContactEmail());
            row.put("description", entity.getDescription());
            row.put("status", entity.getStatus());
            row.put("createUserId", entity.getCreateUserId());
            row.put("updateUserId", entity.getUpdateUserId());
            row.put("createTime", entity.getCreateTime());
            row.put("updateTime", entity.getUpdateTime());
            dataList.add(row);
        }
        ExcelExportImportUtil.exportExcel(response, "supplier_export", headers, dataList);
    }

    /**
     * 导入供应商数据
     */
    public int importData(MultipartFile file) throws IOException {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("supplierCode", "供应商编码");
        headers.put("supplierName", "供应商名称");
        headers.put("supplierType", "供应商类型");
        headers.put("apiUrl", "API地址");
        headers.put("apiKey", "API密钥");
        headers.put("apiSecret", "API密钥Secret");
        headers.put("contactName", "联系人");
        headers.put("contactPhone", "联系电话");
        headers.put("contactEmail", "联系邮箱");
        headers.put("description", "描述");
        headers.put("status", "状态");

        List<Map<String, Object>> dataList = ExcelExportImportUtil.importExcel(file.getInputStream(), headers);
        int count = 0;
        for (Map<String, Object> row : dataList) {
            SignatureSupplier entity = new SignatureSupplier();
            entity.setId(UUID.randomUUID().toString());
            entity.setSupplierCode(getStringValue(row, "supplierCode"));
            entity.setSupplierName(getStringValue(row, "supplierName"));
            entity.setSupplierType(getStringValue(row, "supplierType"));
            entity.setApiUrl(getStringValue(row, "apiUrl"));
            entity.setApiKey(getStringValue(row, "apiKey"));
            entity.setApiSecret(getStringValue(row, "apiSecret"));
            entity.setContactName(getStringValue(row, "contactName"));
            entity.setContactPhone(getStringValue(row, "contactPhone"));
            entity.setContactEmail(getStringValue(row, "contactEmail"));
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
