package gnosis.sample.dynamic.liteflow.service;

import gnosis.sample.dynamic.liteflow.aspect.LiteFlowDynamicAspect;
import gnosis.sample.dynamic.liteflow.entity.MethodInterceptConfig;
import gnosis.sample.dynamic.liteflow.manager.MethodInterceptConfigManager;
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
    
    /**
     * 获取所有启用的拦截配置
     */
    public List<MethodInterceptConfig> getAllEnabledConfigs() {
        return methodInterceptConfigManager.getAllEnabledConfigs();
    }
    
    /**
     * 保存或更新配置
     */
    public void saveConfig(MethodInterceptConfig config) {
        methodInterceptConfigManager.saveConfig(config);
        // 刷新切面缓存
        liteFlowDynamicAspect.refreshConfigCache();
    }
    
    /**
     * 删除配置
     */
    public void deleteConfig(Long id) {
        methodInterceptConfigManager.deleteConfig(id);
        // 刷新切面缓存
        liteFlowDynamicAspect.refreshConfigCache();
    }
    
    /**
     * 启用/禁用配置
     */
    public void toggleConfigStatus(Long id, Boolean enabled) {
        methodInterceptConfigManager.toggleConfigStatus(id, enabled);
        // 刷新切面缓存
        liteFlowDynamicAspect.refreshConfigCache();
    }
    
    /**
     * 刷新缓存
     */
    public void refreshCache() {
        liteFlowDynamicAspect.refreshConfigCache();
    }
}