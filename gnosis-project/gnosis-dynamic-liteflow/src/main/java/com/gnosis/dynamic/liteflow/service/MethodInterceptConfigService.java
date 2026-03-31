package com.gnosis.dynamic.liteflow.service;

import com.gnosis.dynamic.liteflow.aspect.LiteFlowDynamicAspect;
import com.gnosis.dynamic.liteflow.entity.MethodInterceptConfig;
import com.gnosis.dynamic.liteflow.manager.MethodInterceptConfigManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 方法拦截配置服务
 */
@Service
public class MethodInterceptConfigService {
    
    @Autowired
    private MethodInterceptConfigManager methodInterceptConfigManager;
    
    @Autowired
    private LiteFlowDynamicAspect liteFlowDynamicAspect;
    
    public MethodInterceptConfig createConfig(MethodInterceptConfig config) {
        MethodInterceptConfig created = methodInterceptConfigManager.saveConfig(config);
        return created;
    }
    
    public MethodInterceptConfig getConfigById(Long id) {
        return methodInterceptConfigManager.getConfigById(id);
    }
    
    public MethodInterceptConfig updateConfig(MethodInterceptConfig config) {
        MethodInterceptConfig updated = methodInterceptConfigManager.updateConfig(config);
        return updated;
    }
    
    public void deleteConfig(Long id) {
        methodInterceptConfigManager.deleteConfig(id);
    }
    
    public List<MethodInterceptConfig> getAllConfigs() {
        return methodInterceptConfigManager.getAllConfigs();
    }
    
    public void refreshCache() {
        methodInterceptConfigManager.refreshCache();
    }
}