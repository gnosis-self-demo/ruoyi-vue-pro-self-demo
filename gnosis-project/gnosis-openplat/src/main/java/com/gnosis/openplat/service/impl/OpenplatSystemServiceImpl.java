package com.gnosis.openplat.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.domain.OpenplatSystem;
import com.gnosis.openplat.mapper.OpenplatApiConfigMapper;
import com.gnosis.openplat.mapper.OpenplatSystemMapper;
import com.gnosis.openplat.service.OpenplatApiSystemRelationService;
import com.gnosis.openplat.service.OpenplatSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 对接系统服务实现
 */
@Slf4j
@Service
public class OpenplatSystemServiceImpl implements OpenplatSystemService {
    
    @Autowired
    private OpenplatSystemMapper systemMapper;
    
    @Autowired
    private OpenplatApiConfigMapper apiConfigMapper;
    
    @Autowired
    private OpenplatApiSystemRelationService apiSystemRelationService;

    @Override
    public OpenplatSystem getById(String id) {
        return systemMapper.selectById(id);
    }
    
    @Override
    public OpenplatSystem getBySystemCode(String systemCode) {
        return systemMapper.selectBySystemCode(systemCode);
    }
    
    @Override
    public List<OpenplatSystem> list(OpenplatSystem system) {
        return systemMapper.selectList(system);
    }
    
    @Override
    public int save(OpenplatSystem system) {
        system.setId(IdUtil.getSnowflakeNextIdStr());
        system.setCreateTime(new Date());
        system.setUpdateTime(new Date());
        return systemMapper.insert(system);
    }
    
    @Override
    public int update(OpenplatSystem system) {
        system.setUpdateTime(new Date());
        return systemMapper.update(system);
    }
    
    @Override
    public int delete(String id) {
        return systemMapper.deleteById(id);
    }
    
    @Override
    public int batchDelete(String[] ids) {
        return systemMapper.deleteByIds(ids);
    }
    
    @Override
    public int batchEnable(String[] ids) {
        int count = 0;
        for (String id : ids) {
            OpenplatSystem system = new OpenplatSystem();
            system.setId(id);
            system.setStatus("ENABLED");
            system.setUpdateTime(new Date());
            count += systemMapper.update(system);
        }
        return count;
    }
    
    @Override
    public int batchDisable(String[] ids) {
        int count = 0;
        for (String id : ids) {
            OpenplatSystem system = new OpenplatSystem();
            system.setId(id);
            system.setStatus("DISABLED");
            system.setUpdateTime(new Date());
            count += systemMapper.update(system);
        }
        return count;
    }
    
    @Override
    public List<OpenplatApiConfig> getSystemApis(String systemId) {
        Set<String> apiIdSet = new HashSet<>();
        List<OpenplatApiConfig> result = new ArrayList<>();
        
        List<OpenplatApiConfig> relationApis = apiSystemRelationService.getApisBySystemId(systemId);
        if (relationApis != null) {
            for (OpenplatApiConfig api : relationApis) {
                if (apiIdSet.add(api.getId())) {
                    result.add(api);
                }
            }
        }
        
        List<OpenplatApiConfig> directApis = apiConfigMapper.selectBySystemId(systemId);
        if (directApis != null) {
            for (OpenplatApiConfig api : directApis) {
                if (apiIdSet.add(api.getId())) {
                    result.add(api);
                }
            }
        }
        
        return result;
    }
}
