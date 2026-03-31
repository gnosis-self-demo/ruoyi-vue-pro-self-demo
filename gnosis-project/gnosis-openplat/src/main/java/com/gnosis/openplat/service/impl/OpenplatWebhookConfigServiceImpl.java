package com.gnosis.openplat.service.impl;

import cn.hutool.core.util.IdUtil;
import lombok.extern.slf4j.Slf4j;
import com.gnosis.openplat.domain.OpenplatWebhookConfig;
import com.gnosis.openplat.mapper.OpenplatWebhookConfigMapper;
import com.gnosis.openplat.service.OpenplatWebhookConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Webhook 配置服务实现
 */
@Slf4j
@Service
public class OpenplatWebhookConfigServiceImpl implements OpenplatWebhookConfigService {
    
    @Autowired
    private OpenplatWebhookConfigMapper webhookConfigMapper;
    
    @Override
    public OpenplatWebhookConfig getById(String id) {
        return webhookConfigMapper.selectById(id);
    }
    
    @Override
    public List<OpenplatWebhookConfig> list(OpenplatWebhookConfig webhookConfig) {
        return webhookConfigMapper.selectList(webhookConfig);
    }
    
    @Override
    public List<OpenplatWebhookConfig> getByEventType(String eventType) {
        return webhookConfigMapper.selectByEventType(eventType);
    }
    
    @Override
    public int save(OpenplatWebhookConfig webhookConfig) {
        webhookConfig.setId(IdUtil.getSnowflakeNextIdStr());
        webhookConfig.setCreateTime(new Date());
        webhookConfig.setUpdateTime(new Date());
        return webhookConfigMapper.insert(webhookConfig);
    }
    
    @Override
    public int update(OpenplatWebhookConfig webhookConfig) {
        webhookConfig.setUpdateTime(new Date());
        return webhookConfigMapper.update(webhookConfig);
    }
    
    @Override
    public int delete(String id) {
        return webhookConfigMapper.deleteById(id);
    }
    
    @Override
    public int batchDelete(String[] ids) {
        return webhookConfigMapper.deleteByIds(ids);
    }
    
    @Override
    public int batchEnable(String[] ids) {
        int count = 0;
        for (String id : ids) {
            OpenplatWebhookConfig webhookConfig = new OpenplatWebhookConfig();
            webhookConfig.setId(id);
            webhookConfig.setStatus("ENABLED");
            webhookConfig.setUpdateTime(new Date());
            count += webhookConfigMapper.update(webhookConfig);
        }
        return count;
    }
    
    @Override
    public int batchDisable(String[] ids) {
        int count = 0;
        for (String id : ids) {
            OpenplatWebhookConfig webhookConfig = new OpenplatWebhookConfig();
            webhookConfig.setId(id);
            webhookConfig.setStatus("DISABLED");
            webhookConfig.setUpdateTime(new Date());
            count += webhookConfigMapper.update(webhookConfig);
        }
        return count;
    }
}
