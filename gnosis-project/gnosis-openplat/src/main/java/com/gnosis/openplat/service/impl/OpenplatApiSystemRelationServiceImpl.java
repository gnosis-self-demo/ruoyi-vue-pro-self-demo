package com.gnosis.openplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.domain.OpenplatApiSystemRelation;
import com.gnosis.openplat.mapper.OpenplatApiSystemRelationMapper;
import com.gnosis.openplat.service.OpenplatApiSystemRelationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * API与系统关系配置服务实现类
 */
@Service
public class OpenplatApiSystemRelationServiceImpl extends ServiceImpl<OpenplatApiSystemRelationMapper, OpenplatApiSystemRelation> implements OpenplatApiSystemRelationService {
    
    @Override
    public OpenplatApiSystemRelation getById(String relationId) {
        return baseMapper.getById(relationId);
    }
    
    @Override
    public List<OpenplatApiSystemRelation> getByApiId(String apiId) {
        return baseMapper.getByApiId(apiId);
    }
    
    @Override
    public List<OpenplatApiSystemRelation> getBySystemId(String systemId) {
        return baseMapper.getBySystemId(systemId);
    }
    
    @Override
    public OpenplatApiSystemRelation getByApiPathAndSystemId(String apiPath, String systemId) {
        return baseMapper.getByApiPathAndSystemId(apiPath, systemId);
    }
    
    @Override
    public List<OpenplatApiSystemRelation> list(OpenplatApiSystemRelation relation) {
        return baseMapper.list(relation);
    }
    
    @Override
    public boolean save(OpenplatApiSystemRelation relation) {
        return baseMapper.save(relation) > 0;
    }
    
    @Override
    public boolean update(OpenplatApiSystemRelation relation) {
        return baseMapper.update(relation) > 0;
    }
    
    @Override
    public boolean delete(String relationId) {
        return baseMapper.delete(relationId) > 0;
    }
    
    @Override
    public boolean batchDelete(String[] ids) {
        return baseMapper.batchDelete(ids) > 0;
    }
    
    @Override
    public boolean batchEnable(String[] ids) {
        return baseMapper.batchEnable(ids) > 0;
    }
    
    @Override
    public boolean batchDisable(String[] ids) {
        return baseMapper.batchDisable(ids) > 0;
    }
    
    @Override
    public boolean batchRelateSystems(String apiId, String[] systemIds) {
        // 先删除已有的关联关系
        baseMapper.deleteByApiIdAndSystemIds(apiId, systemIds);
        
        // 批量添加新的关联关系
        for (String systemId : systemIds) {
            OpenplatApiSystemRelation relation = new OpenplatApiSystemRelation();
            relation.setRelationId("rel_" + System.currentTimeMillis() + "_" + systemId);
            relation.setApiId(apiId);
            relation.setSystemId(systemId);
            relation.setStatus("ENABLED");
            relation.setCreateUserId("admin");
            relation.setCreateTime(new java.util.Date());
            relation.setUpdateUserId("admin");
            relation.setUpdateTime(new java.util.Date());
            baseMapper.insert(relation);
        }
        
        return true;
    }
    
    @Override
    public boolean batchUnrelateSystems(String apiId, String[] systemIds) {
        return baseMapper.deleteByApiIdAndSystemIds(apiId, systemIds) > 0;
    }
    
    @Override
    public List<OpenplatApiConfig> getApisBySystemId(String systemId) {
        return baseMapper.selectApisBySystemId(systemId);
    }
}