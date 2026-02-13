package gnosis.sample.dynamic.liteflow.web;

import gnosis.sample.dynamic.liteflow.entity.MethodInterceptConfig;
import gnosis.sample.dynamic.liteflow.service.MethodInterceptConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 方法拦截配置管理控制器
 */
@RestController
public class MethodInterceptConfigController {
    
    @Autowired
    private MethodInterceptConfigService methodInterceptConfigService;
    
    /**
     * 获取所有启用的拦截配置
     */
    @GetMapping("/liteflow/business-aspect/intercepts")
    public List<MethodInterceptConfig> getAllEnabledConfigs() {
        return methodInterceptConfigService.getAllEnabledConfigs();
    }
    
    /**
     * 保存或更新拦截配置
     */
    @PostMapping("/liteflow/business-aspect/intercepts")
    public String saveConfig(@RequestBody MethodInterceptConfig config) {
        methodInterceptConfigService.saveConfig(config);
        return "Intercept config saved successfully";
    }
    
    /**
     * 删除拦截配置
     */
    @DeleteMapping("/liteflow/business-aspect/intercepts/{id}")
    public String deleteConfig(@PathVariable Long id) {
        methodInterceptConfigService.deleteConfig(id);
        return "Intercept config deleted successfully";
    }
    
    /**
     * 启用/禁用拦截配置
     */
    @PutMapping("/liteflow/business-aspect/intercepts/{id}/status")
    public String toggleConfigStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        methodInterceptConfigService.toggleConfigStatus(id, enabled);
        return "Intercept config status updated successfully";
    }
    
    /**
     * 刷新缓存
     */
    @PostMapping("/liteflow/business-aspect/cache/refresh")
    public String refreshCache() {
        methodInterceptConfigService.refreshCache();
        return "Cache refreshed successfully";
    }
}