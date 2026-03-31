package com.gnosis.openplat.mapper;

import com.gnosis.openplat.domain.OpenplatWebhookConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Webhook 配置 Mapper 接口
 */
@Mapper
public interface OpenplatWebhookConfigMapper {
    
    /**
     * 根据 ID 查询 Webhook 配置信息
     */
    OpenplatWebhookConfig selectById(@Param("id") String id);
    
    /**
     * 查询 Webhook 配置列表
     */
    List<OpenplatWebhookConfig> selectList(OpenplatWebhookConfig webhookConfig);
    
    /**
     * 根据事件类型查询 Webhook 配置列表
     */
    List<OpenplatWebhookConfig> selectByEventType(@Param("eventType") String eventType);
    
    /**
     * 新增 Webhook 配置信息
     */
    int insert(OpenplatWebhookConfig webhookConfig);
    
    /**
     * 修改 Webhook 配置信息
     */
    int update(OpenplatWebhookConfig webhookConfig);
    
    /**
     * 删除 Webhook 配置信息
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 批量删除 Webhook 配置信息
     */
    int deleteByIds(@Param("ids") String[] ids);
}
