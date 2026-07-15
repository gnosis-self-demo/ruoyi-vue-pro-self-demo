package com.gnosis.signature.service;

import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.signature.domain.SignatureProcess;
import com.gnosis.signature.dto.process.SignatureProcessCreateRequest;
import com.gnosis.signature.dto.process.SignatureProcessIdsRequest;
import com.gnosis.signature.dto.process.SignatureProcessQueryRequest;
import com.gnosis.signature.dto.process.SignatureProcessUpdateRequest;
import com.gnosis.signature.dto.process.SignatureProcessVO;
import com.gnosis.signature.mapper.SignatureProcessMapper;
import com.gnosis.signature.util.ExcelExportImportUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * 签章流程服务
 */
@Service
public class SignatureProcessService {

    private static final Logger log = LoggerFactory.getLogger(SignatureProcessService.class);

    @Autowired
    private SignatureProcessMapper processMapper;

    /**
     * 分页查询流程列表
     */
    public PageResult<SignatureProcessVO> pageList(PageRequest<SignatureProcessQueryRequest> pageRequest) {
        if (pageRequest == null) {
            pageRequest = new PageRequest<SignatureProcessQueryRequest>();
            pageRequest.setQuery(new SignatureProcessQueryRequest());
        }
        SignatureProcessQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new SignatureProcessQueryRequest();
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
        Long total = processMapper.countByCondition(query);
        if (total == 0) {
            PageResult<SignatureProcessVO> result = new PageResult<SignatureProcessVO>();
            result.setTotal(0L);
            result.setList(new ArrayList<SignatureProcessVO>());
            return result;
        }

        // 查询列表
        List<SignatureProcess> processes = processMapper.selectByCondition(query);
        List<SignatureProcessVO> voList = new ArrayList<SignatureProcessVO>();
        for (SignatureProcess process : processes) {
            voList.add(convertToVO(process));
        }

        PageResult<SignatureProcessVO> result = new PageResult<SignatureProcessVO>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    /**
     * 根据ID查询流程详情
     */
    public SignatureProcessVO detail(String id) {
        SignatureProcess process = processMapper.selectById(id);
        if (process == null) {
            return null;
        }
        return convertToVO(process);
    }

    /**
     * 创建流程
     */
    public int create(SignatureProcessCreateRequest request) {
        SignatureProcess process = new SignatureProcess();
        process.setId(UUID.randomUUID().toString());
        process.setProcessCode(request.getProcessCode());
        process.setProcessName(request.getProcessName());
        process.setProcessType(request.getProcessType());
        process.setProcessConfig(request.getProcessConfig());
        process.setSignOrder(request.getSignOrder());
        process.setDescription(request.getDescription());
        process.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        process.setCreateUserId(request.getCreateUserId());
        process.setUpdateUserId(request.getUpdateUserId());
        process.setCreateTime(new Date());
        process.setUpdateTime(new Date());

        return processMapper.insert(process);
    }

    /**
     * 更新流程
     */
    public int update(SignatureProcessUpdateRequest request) {
        SignatureProcess process = processMapper.selectById(request.getId());
        if (process == null) {
            return 0;
        }

        process.setProcessCode(request.getProcessCode());
        process.setProcessName(request.getProcessName());
        process.setProcessType(request.getProcessType());
        process.setProcessConfig(request.getProcessConfig());
        process.setSignOrder(request.getSignOrder());
        process.setDescription(request.getDescription());
        process.setStatus(request.getStatus());
        process.setUpdateUserId(request.getUpdateUserId());
        process.setUpdateTime(new Date());

        return processMapper.updateById(process);
    }

    /**
     * 删除流程
     */
    public int delete(String id) {
        return processMapper.deleteById(id);
    }

    /**
     * 批量删除流程
     */
    public int batchDelete(SignatureProcessIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return processMapper.deleteByIds(request.getIds());
    }

    /**
     * 批量启用流程
     */
    public int batchEnable(SignatureProcessIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return processMapper.batchUpdateStatus(request.getIds(), 1);
    }

    /**
     * 批量禁用流程
     */
    public int batchDisable(SignatureProcessIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return processMapper.batchUpdateStatus(request.getIds(), 0);
    }

    /**
     * 导出流程数据
     */
    public void exportData(SignatureProcessIdsRequest request, HttpServletResponse response) throws IOException {
        List<SignatureProcess> list;
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            list = processMapper.selectByIds(request.getIds());
        } else {
            list = processMapper.selectByCondition(new SignatureProcessQueryRequest());
        }
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("processCode", "流程编码");
        headers.put("processName", "流程名称");
        headers.put("processType", "流程类型");
        headers.put("processConfig", "流程配置");
        headers.put("signOrder", "签署顺序");
        headers.put("description", "描述");
        headers.put("status", "状态");
        headers.put("createUserId", "创建人ID");
        headers.put("updateUserId", "更新人ID");
        headers.put("createTime", "创建时间");
        headers.put("updateTime", "更新时间");

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SignatureProcess entity : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", entity.getId());
            row.put("processCode", entity.getProcessCode());
            row.put("processName", entity.getProcessName());
            row.put("processType", entity.getProcessType());
            row.put("processConfig", entity.getProcessConfig());
            row.put("signOrder", entity.getSignOrder());
            row.put("description", entity.getDescription());
            row.put("status", entity.getStatus());
            row.put("createUserId", entity.getCreateUserId());
            row.put("updateUserId", entity.getUpdateUserId());
            row.put("createTime", entity.getCreateTime());
            row.put("updateTime", entity.getUpdateTime());
            dataList.add(row);
        }
        ExcelExportImportUtil.exportExcel(response, "process_export", headers, dataList);
    }

    /**
     * 导入流程数据
     */
    public int importData(MultipartFile file) throws IOException {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("processCode", "流程编码");
        headers.put("processName", "流程名称");
        headers.put("processType", "流程类型");
        headers.put("processConfig", "流程配置");
        headers.put("signOrder", "签署顺序");
        headers.put("description", "描述");
        headers.put("status", "状态");

        List<Map<String, Object>> dataList = ExcelExportImportUtil.importExcel(file.getInputStream(), headers);
        int count = 0;
        for (Map<String, Object> row : dataList) {
            SignatureProcess entity = new SignatureProcess();
            entity.setId(UUID.randomUUID().toString());
            entity.setProcessCode(getStringValue(row, "processCode"));
            entity.setProcessName(getStringValue(row, "processName"));
            entity.setProcessType(getStringValue(row, "processType"));
            entity.setProcessConfig(getStringValue(row, "processConfig"));
            entity.setSignOrder(getIntegerValue(row, "signOrder"));
            entity.setDescription(getStringValue(row, "description"));
            entity.setStatus(getIntegerValue(row, "status"));
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            processMapper.insert(entity);
            count++;
        }
        return count;
    }

    /**
     * 转换为VO
     */
    private SignatureProcessVO convertToVO(SignatureProcess process) {
        SignatureProcessVO vo = new SignatureProcessVO();
        vo.setId(process.getId());
        vo.setProcessCode(process.getProcessCode());
        vo.setProcessName(process.getProcessName());
        vo.setProcessType(process.getProcessType());
        vo.setProcessConfig(process.getProcessConfig());
        vo.setSignOrder(process.getSignOrder());
        vo.setDescription(process.getDescription());
        vo.setStatus(process.getStatus());
        vo.setCreateUserId(process.getCreateUserId());
        vo.setUpdateUserId(process.getUpdateUserId());
        vo.setCreateTime(process.getCreateTime());
        vo.setUpdateTime(process.getUpdateTime());
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
