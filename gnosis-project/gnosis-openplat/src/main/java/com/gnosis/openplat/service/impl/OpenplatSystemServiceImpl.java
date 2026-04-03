package com.gnosis.openplat.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.domain.OpenplatSystem;
import com.gnosis.openplat.mapper.OpenplatSystemMapper;
import com.gnosis.openplat.service.OpenplatApiSystemRelationService;
import com.gnosis.openplat.service.OpenplatSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 对接系统服务实现
 */
@Slf4j
@Service
public class OpenplatSystemServiceImpl implements OpenplatSystemService {
    
    @Autowired
    private OpenplatSystemMapper systemMapper;
    
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
        return apiSystemRelationService.getApisBySystemId(systemId);
    }
}
