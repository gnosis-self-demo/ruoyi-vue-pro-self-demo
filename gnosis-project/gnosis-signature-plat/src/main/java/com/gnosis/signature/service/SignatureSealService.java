package com.gnosis.signature.service;

import com.gnosis.common.dto.PageRequest;
import com.gnosis.common.dto.PageResult;
import com.gnosis.signature.domain.SignatureSeal;
import com.gnosis.signature.dto.seal.SignatureSealCreateRequest;
import com.gnosis.signature.dto.seal.SignatureSealIdsRequest;
import com.gnosis.signature.dto.seal.SignatureSealQueryRequest;
import com.gnosis.signature.dto.seal.SignatureSealUpdateRequest;
import com.gnosis.signature.dto.seal.SignatureSealVO;
import com.gnosis.signature.mapper.SignatureSealMapper;
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
 * 印章服务
 */
@Service
public class SignatureSealService {

    private static final Logger log = LoggerFactory.getLogger(SignatureSealService.class);

    @Autowired
    private SignatureSealMapper sealMapper;

    /**
     * 分页查询印章列表
     */
    public PageResult<SignatureSealVO> pageList(PageRequest<SignatureSealQueryRequest> pageRequest) {
        if (pageRequest == null) {
            pageRequest = new PageRequest<SignatureSealQueryRequest>();
            pageRequest.setQuery(new SignatureSealQueryRequest());
        }
        SignatureSealQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new SignatureSealQueryRequest();
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
        Long total = sealMapper.countByCondition(query);
        if (total == 0) {
            PageResult<SignatureSealVO> result = new PageResult<SignatureSealVO>();
            result.setTotal(0L);
            result.setList(new ArrayList<SignatureSealVO>());
            return result;
        }

        // 查询列表
        List<SignatureSeal> seals = sealMapper.selectByCondition(query);
        List<SignatureSealVO> voList = new ArrayList<SignatureSealVO>();
        for (SignatureSeal seal : seals) {
            voList.add(convertToVO(seal));
        }

        PageResult<SignatureSealVO> result = new PageResult<SignatureSealVO>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    /**
     * 根据ID查询印章详情
     */
    public SignatureSealVO detail(String id) {
        SignatureSeal seal = sealMapper.selectById(id);
        if (seal == null) {
            return null;
        }
        return convertToVO(seal);
    }

    /**
     * 创建印章
     */
    public int create(SignatureSealCreateRequest request) {
        SignatureSeal seal = new SignatureSeal();
        seal.setId(UUID.randomUUID().toString());
        seal.setSealCode(request.getSealCode());
        seal.setSealName(request.getSealName());
        seal.setSealType(request.getSealType());
        seal.setSealImagePath(request.getSealImagePath());
        seal.setSealImageData(request.getSealImageData());
        seal.setAuthorizeUserId(request.getAuthorizeUserId());
        seal.setAuthorizeTime(request.getAuthorizeTime());
        seal.setExpireTime(request.getExpireTime());
        seal.setDescription(request.getDescription());
        seal.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        seal.setCreateUserId(request.getCreateUserId());
        seal.setUpdateUserId(request.getUpdateUserId());
        seal.setCreateTime(new Date());
        seal.setUpdateTime(new Date());

        return sealMapper.insert(seal);
    }

    /**
     * 更新印章
     */
    public int update(SignatureSealUpdateRequest request) {
        SignatureSeal seal = sealMapper.selectById(request.getId());
        if (seal == null) {
            return 0;
        }

        seal.setSealCode(request.getSealCode());
        seal.setSealName(request.getSealName());
        seal.setSealType(request.getSealType());
        seal.setSealImagePath(request.getSealImagePath());
        seal.setSealImageData(request.getSealImageData());
        seal.setAuthorizeUserId(request.getAuthorizeUserId());
        seal.setAuthorizeTime(request.getAuthorizeTime());
        seal.setExpireTime(request.getExpireTime());
        seal.setDescription(request.getDescription());
        seal.setStatus(request.getStatus());
        seal.setUpdateUserId(request.getUpdateUserId());
        seal.setUpdateTime(new Date());

        return sealMapper.updateById(seal);
    }

    /**
     * 删除印章
     */
    public int delete(String id) {
        return sealMapper.deleteById(id);
    }

    /**
     * 批量删除印章
     */
    public int batchDelete(SignatureSealIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return sealMapper.deleteByIds(request.getIds());
    }

    /**
     * 批量启用印章
     */
    public int batchEnable(SignatureSealIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return sealMapper.batchUpdateStatus(request.getIds(), 1);
    }

    /**
     * 批量禁用印章
     */
    public int batchDisable(SignatureSealIdsRequest request) {
        if (request == null || request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return sealMapper.batchUpdateStatus(request.getIds(), 0);
    }

    /**
     * 导出印章数据
     */
    public void exportData(SignatureSealIdsRequest request, HttpServletResponse response) throws IOException {
        List<SignatureSeal> list;
        if (request != null && request.getIds() != null && !request.getIds().isEmpty()) {
            list = sealMapper.selectByIds(request.getIds());
        } else {
            list = sealMapper.selectByCondition(new SignatureSealQueryRequest());
        }
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("id", "ID");
        headers.put("sealCode", "印章编码");
        headers.put("sealName", "印章名称");
        headers.put("sealType", "印章类型");
        headers.put("sealImagePath", "印章图片路径");
        headers.put("sealImageData", "印章图片数据");
        headers.put("authorizeUserId", "授权人ID");
        headers.put("authorizeTime", "授权时间");
        headers.put("expireTime", "过期时间");
        headers.put("description", "描述");
        headers.put("status", "状态");
        headers.put("createUserId", "创建人ID");
        headers.put("updateUserId", "更新人ID");
        headers.put("createTime", "创建时间");
        headers.put("updateTime", "更新时间");

        List<Map<String, Object>> dataList = new ArrayList<>();
        for (SignatureSeal entity : list) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("id", entity.getId());
            row.put("sealCode", entity.getSealCode());
            row.put("sealName", entity.getSealName());
            row.put("sealType", entity.getSealType());
            row.put("sealImagePath", entity.getSealImagePath());
            row.put("sealImageData", entity.getSealImageData());
            row.put("authorizeUserId", entity.getAuthorizeUserId());
            row.put("authorizeTime", entity.getAuthorizeTime());
            row.put("expireTime", entity.getExpireTime());
            row.put("description", entity.getDescription());
            row.put("status", entity.getStatus());
            row.put("createUserId", entity.getCreateUserId());
            row.put("updateUserId", entity.getUpdateUserId());
            row.put("createTime", entity.getCreateTime());
            row.put("updateTime", entity.getUpdateTime());
            dataList.add(row);
        }
        ExcelExportImportUtil.exportExcel(response, "seal_export", headers, dataList);
    }

    /**
     * 导入印章数据
     */
    public int importData(MultipartFile file) throws IOException {
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        headers.put("sealCode", "印章编码");
        headers.put("sealName", "印章名称");
        headers.put("sealType", "印章类型");
        headers.put("sealImagePath", "印章图片路径");
        headers.put("sealImageData", "印章图片数据");
        headers.put("authorizeUserId", "授权人ID");
        headers.put("description", "描述");
        headers.put("status", "状态");

        List<Map<String, Object>> dataList = ExcelExportImportUtil.importExcel(file.getInputStream(), headers);
        int count = 0;
        for (Map<String, Object> row : dataList) {
            SignatureSeal entity = new SignatureSeal();
            entity.setId(UUID.randomUUID().toString());
            entity.setSealCode(getStringValue(row, "sealCode"));
            entity.setSealName(getStringValue(row, "sealName"));
            entity.setSealType(getStringValue(row, "sealType"));
            entity.setSealImagePath(getStringValue(row, "sealImagePath"));
            entity.setSealImageData(getStringValue(row, "sealImageData"));
            entity.setAuthorizeUserId(getStringValue(row, "authorizeUserId"));
            entity.setDescription(getStringValue(row, "description"));
            entity.setStatus(getIntegerValue(row, "status"));
            entity.setCreateTime(new Date());
            entity.setUpdateTime(new Date());
            sealMapper.insert(entity);
            count++;
        }
        return count;
    }

    /**
     * 转换为VO
     */
    private SignatureSealVO convertToVO(SignatureSeal seal) {
        SignatureSealVO vo = new SignatureSealVO();
        vo.setId(seal.getId());
        vo.setSealCode(seal.getSealCode());
        vo.setSealName(seal.getSealName());
        vo.setSealType(seal.getSealType());
        vo.setSealImagePath(seal.getSealImagePath());
        vo.setSealImageData(seal.getSealImageData());
        vo.setAuthorizeUserId(seal.getAuthorizeUserId());
        vo.setAuthorizeTime(seal.getAuthorizeTime());
        vo.setExpireTime(seal.getExpireTime());
        vo.setDescription(seal.getDescription());
        vo.setStatus(seal.getStatus());
        vo.setCreateUserId(seal.getCreateUserId());
        vo.setUpdateUserId(seal.getUpdateUserId());
        vo.setCreateTime(seal.getCreateTime());
        vo.setUpdateTime(seal.getUpdateTime());
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
