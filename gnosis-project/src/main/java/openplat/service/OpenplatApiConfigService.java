package openplat.service;

import openplat.domain.OpenplatApiConfig;

import java.util.List;

/**
 * API 配置服务接口
 */
public interface OpenplatApiConfigService {
    
    /**
     * 根据 ID 查询 API 配置信息
     */
    OpenplatApiConfig getById(String id);
    
    /**
     * 根据 API 编码查询 API 配置信息
     */
    OpenplatApiConfig getByApiCode(String apiCode);
    
    /**
     * 根据 API 路径查询 API 配置信息
     */
    OpenplatApiConfig getByApiPath(String apiPath);
    
    /**
     * 查询 API 配置列表
     */
    List<OpenplatApiConfig> list(OpenplatApiConfig apiConfig);
    
    /**
     * 新增 API 配置信息
     */
    int save(OpenplatApiConfig apiConfig);
    
    /**
     * 修改 API 配置信息
     */
    int update(OpenplatApiConfig apiConfig);
    
    /**
     * 删除 API 配置信息
     */
    int delete(String id);
    
    /**
     * 批量删除 API 配置信息
     */
    int batchDelete(String[] ids);
    
    /**
     * 批量启用 API
     */
    int batchEnable(String[] ids);
    
    /**
     * 批量禁用 API
     */
    int batchDisable(String[] ids);
}
