package com.gnosis.openplat.service;

import com.gnosis.openplat.domain.OpenplatApiSystemRelation;

import java.util.List;

/**
 * API与系统关系配置服务接口
 */
public interface OpenplatApiSystemRelationService {
    
    /**
     * 根据ID查询关系配置
     */
    OpenplatApiSystemRelation getById(String relationId);
    
    /**
     * 根据API ID查询关系配置列表
     */
    List<OpenplatApiSystemRelation> getByApiId(String apiId);
    
    /**
     * 根据系统ID查询关系配置列表
     */
    List<OpenplatApiSystemRelation> getBySystemId(String systemId);
    
    /**
     * 根据API路径和系统ID查询关系配置
     */
    OpenplatApiSystemRelation getByApiPathAndSystemId(String apiPath, String systemId);
    
    /**
     * 查询关系配置列表
     */
    List<OpenplatApiSystemRelation> list(OpenplatApiSystemRelation relation);
    
    /**
     * 新增关系配置
     */
    boolean save(OpenplatApiSystemRelation relation);
    
    /**
     * 修改关系配置
     */
    boolean update(OpenplatApiSystemRelation relation);
    
    /**
     * 删除关系配置
     */
    boolean delete(String relationId);
    
    /**
     * 批量删除关系配置
     */
    boolean batchDelete(String[] relationIds);
    
    /**
     * 批量启用关系配置
     */
    boolean batchEnable(String[] relationIds);
    
    /**
     * 批量禁用关系配置
     */
    boolean batchDisable(String[] relationIds);
}