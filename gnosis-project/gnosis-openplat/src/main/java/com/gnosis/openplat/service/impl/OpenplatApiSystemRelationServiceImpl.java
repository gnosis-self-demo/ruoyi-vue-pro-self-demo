package com.gnosis.openplat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
    public boolean batchDelete(String[] relationIds) {
        return baseMapper.batchDelete(relationIds) > 0;
    }
    
    @Override
    public boolean batchEnable(String[] relationIds) {
        return baseMapper.batchEnable(relationIds) > 0;
    }
    
    @Override
    public boolean batchDisable(String[] relationIds) {
        return baseMapper.batchDisable(relationIds) > 0;
    }
}