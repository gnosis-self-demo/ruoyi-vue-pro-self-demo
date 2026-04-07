package com.gnosis.openplat.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.domain.OpenplatApiSystemRelation;
import com.gnosis.openplat.domain.OpenplatSystem;
import com.gnosis.openplat.mapper.OpenplatApiConfigMapper;
import com.gnosis.openplat.service.OpenplatApiConfigService;
import com.gnosis.openplat.service.OpenplatApiSystemRelationService;
import com.gnosis.openplat.service.OpenplatSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    
    @Autowired
    private OpenplatApiSystemRelationService apiSystemRelationService;
    
    @Autowired
    private OpenplatSystemService systemService;

    @Override
    public OpenplatApiConfig getById(String id) {
        OpenplatApiConfig apiConfig = apiConfigMapper.selectById(id);
        if (apiConfig != null) {
            List<OpenplatApiSystemRelation> relations = apiSystemRelationService.getByApiId(apiConfig.getId());
            if (relations != null && !relations.isEmpty()) {
                List<OpenplatSystem> systems = new ArrayList<>();
                for (OpenplatApiSystemRelation relation : relations) {
                    OpenplatSystem system = systemService.getById(relation.getSystemId());
                    if (system != null) {
                        systems.add(system);
                    }
                }
                apiConfig.setSystems(systems);
            }
        }
        return apiConfig;
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
        List<OpenplatApiConfig> apiConfigs = apiConfigMapper.selectList(apiConfig);
        // 为每个API查询关联的系统列表
        for (OpenplatApiConfig config : apiConfigs) {
            List<OpenplatApiSystemRelation> relations = apiSystemRelationService.getByApiId(config.getId());
            if (relations != null && !relations.isEmpty()) {
                List<OpenplatSystem> systems = new ArrayList<>();
                for (OpenplatApiSystemRelation relation : relations) {
                    // 通过systemService查询系统详情
                    OpenplatSystem system = systemService.getById(relation.getSystemId());
                    if (system != null) {
                        systems.add(system);
                    }
                }
                config.setSystems(systems);
            }
        }
        return apiConfigs;
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
    
    @Override
    public boolean relateSystems(String apiId, String[] systemIds) {
        return apiSystemRelationService.batchRelateSystems(apiId, systemIds);
    }
    
    @Override
    public boolean unrelateSystems(String apiId, String[] systemIds) {
        return apiSystemRelationService.batchUnrelateSystems(apiId, systemIds);
    }
}
