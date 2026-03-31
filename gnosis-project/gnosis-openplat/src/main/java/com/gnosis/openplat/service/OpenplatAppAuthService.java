package com.gnosis.openplat.service;

import com.gnosis.openplat.domain.OpenplatAppAuth;

import java.util.List;

/**
 * 应用认证服务接口
 */
public interface OpenplatAppAuthService {
    
    /**
     * 根据 ID 查询应用认证信息
     */
    OpenplatAppAuth getById(String id);
    
    /**
     * 根据 app_id 查询应用认证信息
     */
    OpenplatAppAuth getByAppId(String appId);
    
    /**
     * 查询应用认证列表
     */
    List<OpenplatAppAuth> list(OpenplatAppAuth appAuth);
    
    /**
     * 新增应用认证信息
     */
    int save(OpenplatAppAuth appAuth);
    
    /**
     * 修改应用认证信息
     */
    int update(OpenplatAppAuth appAuth);
    
    /**
     * 删除应用认证信息
     */
    int delete(String id);
    
    /**
     * 批量删除应用认证信息
     */
    int batchDelete(String[] ids);
    
    /**
     * 批量启用应用
     */
    int batchEnable(String[] ids);
    
    /**
     * 批量禁用应用
     */
    int batchDisable(String[] ids);
    
    /**
     * 验证应用认证
     */
    boolean validateAppAuth(String appId, String appSecret);
}
