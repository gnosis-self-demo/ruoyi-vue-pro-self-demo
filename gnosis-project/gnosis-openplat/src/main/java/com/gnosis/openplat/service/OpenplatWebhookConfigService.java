package com.gnosis.openplat.service;

import com.gnosis.openplat.domain.OpenplatWebhookConfig;

import java.util.List;

/**
 * Webhook 配置服务接口
 */
public interface OpenplatWebhookConfigService {
    
    /**
     * 根据 ID 查询 Webhook 配置信息
     */
    OpenplatWebhookConfig getById(String id);
    
    /**
     * 查询 Webhook 配置列表
     */
    List<OpenplatWebhookConfig> list(OpenplatWebhookConfig webhookConfig);
    
    /**
     * 根据事件类型查询 Webhook 配置列表
     */
    List<OpenplatWebhookConfig> getByEventType(String eventType);
    
    /**
     * 新增 Webhook 配置信息
     */
    int save(OpenplatWebhookConfig webhookConfig);
    
    /**
     * 修改 Webhook 配置信息
     */
    int update(OpenplatWebhookConfig webhookConfig);
    
    /**
     * 删除 Webhook 配置信息
     */
    int delete(String id);
    
    /**
     * 批量删除 Webhook 配置信息
     */
    int batchDelete(String[] ids);
    
    /**
     * 批量启用 Webhook
     */
    int batchEnable(String[] ids);
    
    /**
     * 批量禁用 Webhook
     */
    int batchDisable(String[] ids);
}
