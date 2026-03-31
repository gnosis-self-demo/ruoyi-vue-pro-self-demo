package com.gnosis.openplat.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.mapper.OpenplatApiConfigMapper;
import com.gnosis.openplat.service.OpenplatApiConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * API 配置服务实现
 */
@Slf4j
@Service
public class OpenplatApiConfigServiceImpl implements OpenplatApiConfigService {
    
    @Autowired
    private OpenplatApiConfigMapper apiConfigMapper;
    
    @Override
    public OpenplatApiConfig getById(String id) {
        return apiConfigMapper.selectById(id);
    }
    
    @Override
    public OpenplatApiConfig getByApiCode(String apiCode) {
        return apiConfigMapper.selectByApiCode(apiCode);
    }
    
    @Override
    public OpenplatApiConfig getByApiPath(String apiPath) {
        return apiConfigMapper.selectByApiPath(apiPath);
    }
    
    @Override
    public List<OpenplatApiConfig> list(OpenplatApiConfig apiConfig) {
        return apiConfigMapper.selectList(apiConfig);
    }
    
    @Override
    public int save(OpenplatApiConfig apiConfig) {
        apiConfig.setId(IdUtil.getSnowflakeNextIdStr());
        apiConfig.setCreateTime(new Date());
        apiConfig.setUpdateTime(new Date());
        return apiConfigMapper.insert(apiConfig);
    }
    
    @Override
    public int update(OpenplatApiConfig apiConfig) {
        apiConfig.setUpdateTime(new Date());
        return apiConfigMapper.update(apiConfig);
    }
    
    @Override
    public int delete(String id) {
        return apiConfigMapper.deleteById(id);
    }
    
    @Override
    public int batchDelete(String[] ids) {
        return apiConfigMapper.deleteByIds(ids);
    }
    
    @Override
    public int batchEnable(String[] ids) {
        int count = 0;
        for (String id : ids) {
            OpenplatApiConfig apiConfig = new OpenplatApiConfig();
            apiConfig.setId(id);
            apiConfig.setStatus("ENABLED");
            apiConfig.setUpdateTime(new Date());
            count += apiConfigMapper.update(apiConfig);
        }
        return count;
    }
    
    @Override
    public int batchDisable(String[] ids) {
        int count = 0;
        for (String id : ids) {
            OpenplatApiConfig apiConfig = new OpenplatApiConfig();
            apiConfig.setId(id);
            apiConfig.setStatus("DISABLED");
            apiConfig.setUpdateTime(new Date());
            count += apiConfigMapper.update(apiConfig);
        }
        return count;
    }
}
