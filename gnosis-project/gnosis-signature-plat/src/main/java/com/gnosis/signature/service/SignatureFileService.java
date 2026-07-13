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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
}
