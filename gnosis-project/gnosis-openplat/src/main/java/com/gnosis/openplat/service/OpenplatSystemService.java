package com.gnosis.openplat.service;

import com.gnosis.openplat.domain.OpenplatApiConfig;
import com.gnosis.openplat.domain.OpenplatSystem;

import java.util.List;

/**
 * 对接系统服务接口
 */
public interface OpenplatSystemService {
    
    /**
     * 根据 ID 查询系统信息
     */
    OpenplatSystem getById(String id);
    
    /**
     * 根据系统编码查询系统信息
     */
    OpenplatSystem getBySystemCode(String systemCode);

    /**
     * 根据应用ID查询系统信息
     */
    OpenplatSystem getByAppId(String appId);
    
    /**
     * 查询系统列表
     */
    List<OpenplatSystem> list(OpenplatSystem system);
    
    /**
     * 新增系统信息
     */
    int save(OpenplatSystem system);
    
    /**
     * 修改系统信息
     */
    int update(OpenplatSystem system);
    
    /**
     * 删除系统信息
     */
    int delete(String id);
    
    /**
     * 批量删除系统信息
     */
    int batchDelete(String[] ids);
    
    /**
     * 批量启用系统
     */
    int batchEnable(String[] ids);
    
    /**
     * 批量禁用系统
     */
    int batchDisable(String[] ids);
    
    /**
     * 查询系统关联的API列表
     */
    List<OpenplatApiConfig> getSystemApis(String systemId);
}
