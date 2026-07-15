package com.gnosis.signature.service;

import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.signature.domain.SignatureFile;
import com.gnosis.signature.dto.file.SignatureFileCreateRequest;
import com.gnosis.signature.dto.file.SignatureFileIdsRequest;
import com.gnosis.signature.dto.file.SignatureFileQueryRequest;
import com.gnosis.signature.dto.file.SignatureFileUpdateRequest;
import com.gnosis.signature.dto.file.SignatureFileVO;
import com.gnosis.signature.mapper.SignatureFileMapper;
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
 * 签章文件服务
 */
@Service
public class SignatureFileService {

    @Autowired
    private SignatureFileMapper fileMapper;

    /**
     * 分页查询文件列表
     */
    public PageResult<SignatureFileVO> pageList(PageRequest<SignatureFileQueryRequest> request) {
        SignatureFileQueryRequest query = request.getQuery();
        if (query == null) {
            query = new SignatureFileQueryRequest();
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

        Long total = fileMapper.countByCondition(query);
        if (total == 0) {
            PageResult<SignatureFileVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<SignatureFileVO>());
            return result;
        }

        List<SignatureFile> files = fileMapper.selectByCondition(query);
        List<SignatureFileVO> voList = new ArrayList<SignatureFileVO>();
        for (SignatureFile file : files) {
            voList.add(convertToVO(file));
        }

        PageResult<SignatureFileVO> result = new PageResult<SignatureFileVO>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    /**
     * 根据ID查询文件详情
     */
    public SignatureFileVO detail(String id) {
        SignatureFile file = fileMapper.selectById(id);
        if (file == null) {
            return null;
        }
        return convertToVO(file);
    }

    /**
     * 创建文件
     */
    public int create(SignatureFileCreateRequest request) {
        SignatureFile file = new SignatureFile();
        file.setId(UUID.randomUUID().toString().replace("-", ""));
        file.setFileName(request.getFileName());
        file.setFileType(request.getFileType());
        file.setFileSize(request.getFileSize());
        file.setFilePath(request.getFilePath());
        file.setFileHash(request.getFileHash());
        file.setTemplateId(request.getTemplateId());
        file.setProcessId(request.getProcessId());
        file.setSupplierId(request.getSupplierId());
        file.setSignStatus(request.getSignStatus());
        file.setDescription(request.getDescription());
        file.setStatus(request.getStatus());
        file.setCreateUserId(request.getCreateUserId());
        file.setUpdateUserId(request.getUpdateUserId());
        file.setCreateTime(new Date());
        file.setUpdateTime(new Date());
        return fileMapper.insert(file);
    }

    /**
     * 更新文件
     */
    public int update(SignatureFileUpdateRequest request) {
        SignatureFile file = fileMapper.selectById(request.getId());
        if (file == null) {
            return 0;
        }
        file.setFileName(request.getFileName());
        file.setFileType(request.getFileType());
        file.setFileSize(request.getFileSize());
        file.setFilePath(request.getFilePath());
        file.setFileHash(request.getFileHash());
        file.setTemplateId(request.getTemplateId());
        file.setProcessId(request.getProcessId());
        file.setSupplierId(request.getSupplierId());
        file.setSignStatus(request.getSignStatus());
        file.setDescription(request.getDescription());
        file.setStatus(request.getStatus());
        file.setUpdateUserId(request.getUpdateUserId());
        file.setUpdateTime(new Date());
        return fileMapper.updateById(file);
    }

    /**
     * 删除文件
     */
    public int delete(String id) {
        return fileMapper.deleteById(id);
    }

    /**
     * 批量删除文件
     */
    public int batchDelete(SignatureFileIdsRequest request) {
        List<String> ids = request.getIds();
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return fileMapper.deleteByIds(ids);
    }

    /**
     * 批量启用文件
     */
    public int batchEnable(SignatureFileIdsRequest request) {
        List<String> ids = request.getIds();
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return fileMapper.batchUpdateStatus(ids, 1);
    }

    /**
     * 批量禁用文件
     */
    public int batchDisable(SignatureFileIdsRequest request) {
        List<String> ids = request.getIds();
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return fileMapper.batchUpdateStatus(ids, 0);
    }

    /**
     * 导出文件数据
     */
    public void exportData(SignatureFileIdsRequest request, HttpServletResponse response) throws IOException {
        List<SignatureFile> list;
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            list = fileMapper.selectByIds(request.getIds());
        } else {
            list = fileMapper.selectByCondition(new SignatureFileQueryRequest());
        }
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("fileName", "文件名称");
        headers.put("fileType", "文件类型");
        headers.put("fileSize", "文件大小");
        headers.put("filePath", "文件路径");
        headers.put("fileHash", "文件哈希值");
        headers.put("templateId", "模板ID");
        headers.put("processId", "流程ID");
        headers.put("supplierId", "供应商ID");
        headers.put("signStatus", "签章状态");
        headers.put("description", "描述");
        headers.put("status", "状态");
        headers.put("createUserId", "创建人ID");
        headers.put("updateUserId", "更新人ID");
        headers.put("createTime", "创建时间");
        headers.put("updateTime", "更新时间");

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SignatureFile entity : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", entity.getId());
            row.put("fileName", entity.getFileName());
            row.put("fileType", entity.getFileType());
            row.put("fileSize", entity.getFileSize());
            row.put("filePath", entity.getFilePath());
            row.put("fileHash", entity.getFileHash());
            row.put("templateId", entity.getTemplateId());
            row.put("processId", entity.getProcessId());
            row.put("supplierId", entity.getSupplierId());
            row.put("signStatus", entity.getSignStatus());
            row.put("description", entity.getDescription());
            row.put("status", entity.getStatus());
            row.put("createUserId", entity.getCreateUserId());
            row.put("updateUserId", entity.getUpdateUserId());
            row.put("createTime", entity.getCreateTime());
            row.put("updateTime", entity.getUpdateTime());
            dataList.add(row);
        }
        ExcelExportImportUtil.exportExcel(response, "file_export", headers, dataList);
    }

    /**
     * 导入文件数据
     */
    public int importData(MultipartFile file) throws IOException {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("fileName", "文件名称");
        headers.put("fileType", "文件类型");
        headers.put("fileSize", "文件大小");
        headers.put("filePath", "文件路径");
        headers.put("fileHash", "文件哈希值");
        headers.put("templateId", "模板ID");
        headers.put("processId", "流程ID");
        headers.put("supplierId", "供应商ID");
        headers.put("signStatus", "签章状态");
        headers.put("description", "描述");
        headers.put("status", "状态");

        List<Map<String, Object>> dataList = ExcelExportImportUtil.importExcel(file.getInputStream(), headers);
        int count = 0;
        for (Map<String, Object> row : dataList) {
            SignatureFile entity = new SignatureFile();
            entity.setId(UUID.randomUUID().toString());
            entity.setFileName(getStringValue(row, "fileName"));
            entity.setFileType(getStringValue(row, "fileType"));
            entity.setFileSize(getLongValue(row, "fileSize"));
            entity.setFilePath(getStringValue(row, "filePath"));
            entity.setFileHash(getStringValue(row, "fileHash"));
            entity.setTemplateId(getStringValue(row, "templateId"));
            entity.setProcessId(getStringValue(row, "processId"));
            entity.setSupplierId(getStringValue(row, "supplierId"));
            entity.setSignStatus(getIntegerValue(row, "signStatus"));
            entity.setDescription(getStringValue(row, "description"));
            entity.setStatus(getIntegerValue(row, "status"));
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            fileMapper.insert(entity);
            count++;
        }
        return count;
    }

    /**
     * 转换为VO
     */
    private SignatureFileVO convertToVO(SignatureFile file) {
        SignatureFileVO vo = new SignatureFileVO();
        vo.setId(file.getId());
        vo.setFileName(file.getFileName());
        vo.setFileType(file.getFileType());
        vo.setFileSize(file.getFileSize());
        vo.setFilePath(file.getFilePath());
        vo.setFileHash(file.getFileHash());
        vo.setTemplateId(file.getTemplateId());
        vo.setProcessId(file.getProcessId());
        vo.setSupplierId(file.getSupplierId());
        vo.setSignStatus(file.getSignStatus());
        vo.setDescription(file.getDescription());
        vo.setStatus(file.getStatus());
        vo.setCreateUserId(file.getCreateUserId());
        vo.setUpdateUserId(file.getUpdateUserId());
        vo.setCreateTime(file.getCreateTime());
        vo.setUpdateTime(file.getUpdateTime());
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

    private Long getLongValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        if (value == null || String.valueOf(value).isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(String.valueOf(value));
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
