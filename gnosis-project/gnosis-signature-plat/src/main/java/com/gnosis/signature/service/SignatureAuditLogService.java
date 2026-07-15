package com.gnosis.signature.service;

import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.signature.domain.SignatureAuditLog;
import com.gnosis.signature.dto.audit.SignatureAuditLogCreateRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogIdsRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogQueryRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogUpdateRequest;
import com.gnosis.signature.dto.audit.SignatureAuditLogVO;
import com.gnosis.signature.mapper.SignatureAuditLogMapper;
import com.gnosis.signature.util.ExcelExportImportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 审计日志服务
 */
@Service
public class SignatureAuditLogService {

    @Autowired
    private SignatureAuditLogMapper mapper;

    /**
     * 分页查询审计日志列表
     */
    public PageResult<SignatureAuditLogVO> pageList(PageRequest<SignatureAuditLogQueryRequest> pageRequest) {
        if (pageRequest == null) {
            pageRequest = new PageRequest<>();
        }
        SignatureAuditLogQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new SignatureAuditLogQueryRequest();
        }
        if (query.getPageNum() == null) {
            query.setPageNum(pageRequest.getPageNum() != null ? pageRequest.getPageNum() : 0);
        }
        if (query.getPageSize() == null) {
            query.setPageSize(pageRequest.getPageSize() != null ? pageRequest.getPageSize() : 10);
        }

        Long total = mapper.countByCondition(query);
        if (total == 0) {
            PageResult<SignatureAuditLogVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<>());
            return result;
        }

        List<SignatureAuditLog> list = mapper.selectByCondition(query);
        List<SignatureAuditLogVO> voList = new ArrayList<>();
        for (SignatureAuditLog entity : list) {
            voList.add(convertToVO(entity));
        }

        PageResult<SignatureAuditLogVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    /**
     * 根据ID查询审计日志详情
     */
    public SignatureAuditLogVO detail(String id) {
        SignatureAuditLog entity = mapper.selectById(id);
        if (entity == null) {
            return null;
        }
        return convertToVO(entity);
    }

    /**
     * 创建审计日志
     */
    public int create(SignatureAuditLogCreateRequest request) {
        SignatureAuditLog entity = new SignatureAuditLog();
        entity.setId(UUID.randomUUID().toString());
        entity.setOperationType(request.getOperationType());
        entity.setOperationModule(request.getOperationModule());
        entity.setOperationDesc(request.getOperationDesc());
        entity.setRequestParams(request.getRequestParams());
        entity.setResponseResult(request.getResponseResult());
        entity.setOperationIp(request.getOperationIp());
        entity.setOperationResult(request.getOperationResult());
        entity.setErrorMsg(request.getErrorMsg());
        entity.setStatus(request.getStatus());
        entity.setCreateUserId(request.getCreateUserId());
        entity.setUpdateUserId(request.getUpdateUserId());
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        return mapper.insert(entity);
    }

    /**
     * 更新审计日志
     */
    public int update(SignatureAuditLogUpdateRequest request) {
        SignatureAuditLog entity = mapper.selectById(request.getId());
        if (entity == null) {
            return 0;
        }
        entity.setOperationType(request.getOperationType());
        entity.setOperationModule(request.getOperationModule());
        entity.setOperationDesc(request.getOperationDesc());
        entity.setRequestParams(request.getRequestParams());
        entity.setResponseResult(request.getResponseResult());
        entity.setOperationIp(request.getOperationIp());
        entity.setOperationResult(request.getOperationResult());
        entity.setErrorMsg(request.getErrorMsg());
        entity.setStatus(request.getStatus());
        entity.setUpdateUserId(request.getUpdateUserId());
        entity.setUpdateTime(new Date());
        return mapper.updateById(entity);
    }

    /**
     * 删除审计日志
     */
    public int delete(String id) {
        return mapper.deleteById(id);
    }

    /**
     * 批量删除审计日志
     */
    public int batchDelete(SignatureAuditLogIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return mapper.deleteByIds(request.getIds());
    }

    /**
     * 批量启用审计日志
     */
    public int batchEnable(SignatureAuditLogIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return mapper.batchUpdateStatus(request.getIds(), 1);
    }

    /**
     * 批量禁用审计日志
     */
    public int batchDisable(SignatureAuditLogIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return mapper.batchUpdateStatus(request.getIds(), 0);
    }

    /**
     * 导出审计日志数据
     */
    public void exportData(SignatureAuditLogIdsRequest request, HttpServletResponse response) throws IOException {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("operationType", "操作类型");
        headers.put("operationModule", "操作模块");
        headers.put("operationDesc", "操作描述");
        headers.put("requestParams", "请求参数");
        headers.put("responseResult", "响应结果");
        headers.put("operationIp", "操作IP");
        headers.put("operationResult", "操作结果");
        headers.put("errorMsg", "错误信息");
        headers.put("status", "状态");
        headers.put("createUserId", "创建人ID");
        headers.put("updateUserId", "更新人ID");
        headers.put("createTime", "创建时间");
        headers.put("updateTime", "更新时间");

        List<SignatureAuditLog> list;
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            list = mapper.selectByIds(request.getIds());
        } else {
            list = mapper.selectByCondition(new SignatureAuditLogQueryRequest());
        }

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SignatureAuditLog entity : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", entity.getId());
            row.put("operationType", entity.getOperationType());
            row.put("operationModule", entity.getOperationModule());
            row.put("operationDesc", entity.getOperationDesc());
            row.put("requestParams", entity.getRequestParams());
            row.put("responseResult", entity.getResponseResult());
            row.put("operationIp", entity.getOperationIp());
            row.put("operationResult", entity.getOperationResult());
            row.put("errorMsg", entity.getErrorMsg());
            row.put("status", entity.getStatus());
            row.put("createUserId", entity.getCreateUserId());
            row.put("updateUserId", entity.getUpdateUserId());
            row.put("createTime", entity.getCreateTime());
            row.put("updateTime", entity.getUpdateTime());
            dataList.add(row);
        }

        ExcelExportImportUtil.exportExcel(response, "审计日志", headers, dataList);
    }

    /**
     * 导入审计日志数据
     */
    public int importData(MultipartFile file) throws IOException {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("operationType", "操作类型");
        headers.put("operationModule", "操作模块");
        headers.put("operationDesc", "操作描述");
        headers.put("requestParams", "请求参数");
        headers.put("responseResult", "响应结果");
        headers.put("operationIp", "操作IP");
        headers.put("operationResult", "操作结果");
        headers.put("errorMsg", "错误信息");
        headers.put("status", "状态");
        headers.put("createUserId", "创建人ID");
        headers.put("updateUserId", "更新人ID");

        InputStream inputStream = file.getInputStream();
        List<Map<String, Object>> dataList = ExcelExportImportUtil.importExcel(inputStream, headers);

        int count = 0;
        for (Map<String, Object> row : dataList) {
            SignatureAuditLog entity = new SignatureAuditLog();
            entity.setId(UUID.randomUUID().toString());
            entity.setOperationType(getStringValue(row, "operationType"));
            entity.setOperationModule(getStringValue(row, "operationModule"));
            entity.setOperationDesc(getStringValue(row, "operationDesc"));
            entity.setRequestParams(getStringValue(row, "requestParams"));
            entity.setResponseResult(getStringValue(row, "responseResult"));
            entity.setOperationIp(getStringValue(row, "operationIp"));
            entity.setOperationResult(getIntegerValue(row, "operationResult"));
            entity.setErrorMsg(getStringValue(row, "errorMsg"));
            entity.setStatus(getIntegerValue(row, "status"));
            entity.setCreateUserId(getStringValue(row, "createUserId"));
            entity.setUpdateUserId(getStringValue(row, "updateUserId"));
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
    private SignatureAuditLogVO convertToVO(SignatureAuditLog entity) {
        SignatureAuditLogVO vo = new SignatureAuditLogVO();
        vo.setId(entity.getId());
        vo.setOperationType(entity.getOperationType());
        vo.setOperationModule(entity.getOperationModule());
        vo.setOperationDesc(entity.getOperationDesc());
        vo.setRequestParams(entity.getRequestParams());
        vo.setResponseResult(entity.getResponseResult());
        vo.setOperationIp(entity.getOperationIp());
        vo.setOperationResult(entity.getOperationResult());
        vo.setErrorMsg(entity.getErrorMsg());
        vo.setStatus(entity.getStatus());
        vo.setCreateUserId(entity.getCreateUserId());
        vo.setUpdateUserId(entity.getUpdateUserId());
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }

    /**
     * 从Map中获取字符串值
     */
    private String getStringValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value != null ? String.valueOf(value) : null;
    }

    /**
     * 从Map中获取整数值
     */
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
