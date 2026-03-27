package openplat.service;

import openplat.domain.OpenplatSystem;

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
}
