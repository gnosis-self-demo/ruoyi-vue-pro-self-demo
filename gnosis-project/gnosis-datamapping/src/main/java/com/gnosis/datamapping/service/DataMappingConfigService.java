package com.gnosis.datamapping.service;

import com.gnosis.datamapping.domain.DataMappingConfig;
import com.gnosis.datamapping.dto.*;
import com.gnosis.datamapping.mapper.DataMappingConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DataMappingConfigService {

    @Autowired
    private DataMappingConfigMapper configMapper;

    public PageResult<DataMappingConfigVO> pageList(PageRequest<DataMappingConfigQueryRequest> pageRequest) {
        DataMappingConfigQueryRequest query = pageRequest.getQuery();
        if (query == null) {
            query = new DataMappingConfigQueryRequest();
        }
        query.setPageNum(pageRequest.getPageNum());
        query.setPageSize(pageRequest.getPageSize());

        Long total = configMapper.countByCondition(query);
        if (total == 0) {
            PageResult<DataMappingConfigVO> result = new PageResult<>();
            result.setTotal(0L);
            result.setList(new ArrayList<>());
            return result;
        }

        List<DataMappingConfig> configs = configMapper.selectByCondition(query);
        List<DataMappingConfigVO> voList = new ArrayList<>();
        for (DataMappingConfig config : configs) {
            voList.add(convertToVO(config));
        }

        PageResult<DataMappingConfigVO> result = new PageResult<>();
        result.setTotal(total);
        result.setList(voList);
        return result;
    }

    public DataMappingConfigVO detail(String id) {
        DataMappingConfig config = configMapper.selectById(id);
        return config != null ? convertToVO(config) : null;
    }

    public int create(DataMappingConfigCreateRequest request) {
        DataMappingConfig config = new DataMappingConfig();
        config.setId(UUID.randomUUID().toString().replace("-", ""));
        config.setConfigName(request.getConfigName());
        config.setConfigCode(request.getConfigCode());
        config.setSystemId(request.getSystemId());
        config.setSystemName(request.getSystemName());
        config.setConfigVersion(request.getConfigVersion());
        config.setApiVersion(request.getApiVersion());
        config.setTargetEndpoint(request.getTargetEndpoint());
        config.setJsonRootPath(request.getJsonRootPath());
        config.setConfigJson(request.getConfigJson());
        config.setDescription(request.getDescription());
        config.setStatus(1);
        config.setCreateUserId(request.getCreateUserId());
        config.setUpdateUserId(request.getCreateUserId());
        config.setCreateTime(new Date());
        config.setUpdateTime(new Date());

        return configMapper.insert(config);
    }

    public int update(DataMappingConfigUpdateRequest request) {
        DataMappingConfig config = configMapper.selectById(request.getId());
        if (config == null) {
            return 0;
        }

        config.setConfigName(request.getConfigName());
        config.setConfigCode(request.getConfigCode());
        config.setSystemId(request.getSystemId());
        config.setSystemName(request.getSystemName());
        config.setConfigVersion(request.getConfigVersion());
        config.setApiVersion(request.getApiVersion());
        config.setTargetEndpoint(request.getTargetEndpoint());
        config.setJsonRootPath(request.getJsonRootPath());
        config.setConfigJson(request.getConfigJson());
        config.setDescription(request.getDescription());
        config.setUpdateUserId(request.getUpdateUserId());
        config.setUpdateTime(new Date());

        return configMapper.updateById(config);
    }

    public int delete(String id) {
        return configMapper.deleteById(id);
    }

    public int batchDelete(DataMappingConfigIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return configMapper.deleteByIds(request.getIds());
    }

    public int batchEnable(DataMappingConfigIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return configMapper.batchUpdateStatus(request.getIds(), 1);
    }

    public int batchDisable(DataMappingConfigIdsRequest request) {
        if (request.getIds() == null || request.getIds().isEmpty()) {
            return 0;
        }
        return configMapper.batchUpdateStatus(request.getIds(), 0);
    }

    public DataMappingConfig getByConfigCode(String configCode) {
        return configMapper.selectByConfigCode(configCode);
    }

    private DataMappingConfigVO convertToVO(DataMappingConfig config) {
        DataMappingConfigVO vo = new DataMappingConfigVO();
        vo.setId(config.getId());
        vo.setConfigName(config.getConfigName());
        vo.setConfigCode(config.getConfigCode());
        vo.setSystemId(config.getSystemId());
        vo.setSystemName(config.getSystemName());
        vo.setConfigVersion(config.getConfigVersion());
        vo.setApiVersion(config.getApiVersion());
        vo.setTargetEndpoint(config.getTargetEndpoint());
        vo.setJsonRootPath(config.getJsonRootPath());
        vo.setConfigJson(config.getConfigJson());
        vo.setDescription(config.getDescription());
        vo.setStatus(config.getStatus());
        vo.setCreateUserId(config.getCreateUserId());
        vo.setUpdateUserId(config.getUpdateUserId());
        vo.setCreateTime(config.getCreateTime());
        vo.setUpdateTime(config.getUpdateTime());
        return vo;
    }
}
