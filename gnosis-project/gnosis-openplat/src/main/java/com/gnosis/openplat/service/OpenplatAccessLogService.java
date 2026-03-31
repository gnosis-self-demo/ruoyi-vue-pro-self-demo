package com.gnosis.openplat.service;

import com.gnosis.openplat.domain.OpenplatAccessLog;

import java.util.List;

/**
 * 调用日志服务接口
 */
public interface OpenplatAccessLogService {
    
    /**
     * 根据 ID 查询调用日志信息
     */
    OpenplatAccessLog getById(String id);
    
    /**
     * 查询调用日志列表
     */
    List<OpenplatAccessLog> list(OpenplatAccessLog accessLog);
    
    /**
     * 新增调用日志信息
     */
    int save(OpenplatAccessLog accessLog);
    
    /**
     * 删除调用日志信息
     */
    int delete(String id);
    
    /**
     * 批量删除调用日志信息
     */
    int batchDelete(String[] ids);
    
    /**
     * 清理过期日志
     */
    int cleanExpiredLogs(java.util.Date expireDate);
}
