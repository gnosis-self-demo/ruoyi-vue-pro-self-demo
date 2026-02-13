package gnosis.sample.dynamic.liteflow.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import gnosis.sample.dynamic.liteflow.entity.MethodInterceptConfig;
import gnosis.sample.dynamic.liteflow.mapper.MethodInterceptConfigMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 方法拦截配置Manager
 */
@Service
public class MethodInterceptConfigManager {
    
    @Resource
    private MethodInterceptConfigMapper methodInterceptConfigMapper;
    
    // 缓存：cacheKey -> config
    private final Map<String, MethodInterceptConfig> cache = new ConcurrentHashMap<>();
    // 缓存：className#methodName -> config（用于方法名匹配）
    private final Map<String, MethodInterceptConfig> methodNameCache = new ConcurrentHashMap<>();
    
    /**
     * 获取所有启用的拦截配置
     */
    public List<MethodInterceptConfig> getAllEnabledConfigs() {
        LambdaQueryWrapper<MethodInterceptConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MethodInterceptConfig::getEnabled, true);
        return methodInterceptConfigMapper.selectList(queryWrapper);
    }
    
    /**
     * 根据目标类和方法获取配置（支持重载方法）
     */
    public MethodInterceptConfig getConfigByTarget(String targetClass, String targetMethod, Class<?>[] parameterTypes) {
        // 首先尝试精确匹配（包含参数类型）
        String methodSignature = generateMethodSignature(targetMethod, parameterTypes);
        LambdaQueryWrapper<MethodInterceptConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MethodInterceptConfig::getTargetClass, targetClass)
                   .eq(MethodInterceptConfig::getTargetMethod, methodSignature)
                   .eq(MethodInterceptConfig::getEnabled, true);
        List<MethodInterceptConfig> configs = methodInterceptConfigMapper.selectList(queryWrapper);
        
        if (!configs.isEmpty()) {
            return configs.get(0);
        }
        
        // 如果没有精确匹配，尝试模糊匹配（不包含参数类型）
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MethodInterceptConfig::getTargetClass, targetClass)
                   .eq(MethodInterceptConfig::getTargetMethod, targetMethod)
                   .eq(MethodInterceptConfig::getEnabled, true);
        configs = methodInterceptConfigMapper.selectList(queryWrapper);
        
        return configs.isEmpty() ? null : configs.get(0);
    }
    
    /**
     * 保存配置
     */
    @Transactional
    public MethodInterceptConfig saveConfig(MethodInterceptConfig config) {
        LocalDateTime now = LocalDateTime.now();
        if (config.getId() == null) {
            config.setCreatedAt(now);
            config.setUpdatedAt(now);
            if (config.getEnabled() == null) {
                config.setEnabled(true);
            }
            methodInterceptConfigMapper.insert(config);
        } else {
            config.setUpdatedAt(now);
            methodInterceptConfigMapper.updateById(config);
        }
        refreshCache();
        return config;
    }
    
    /**
     * 删除配置
     */
    @Transactional
    public void deleteConfig(Long id) {
        methodInterceptConfigMapper.deleteById(id);
        refreshCache();
    }
    
    /**
     * 启用/禁用配置
     */
    @Transactional
    public void toggleConfigStatus(Long id, Boolean enabled) {
        LambdaUpdateWrapper<MethodInterceptConfig> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(MethodInterceptConfig::getId, id)
                    .set(MethodInterceptConfig::getEnabled, enabled)
                    .set(MethodInterceptConfig::getUpdatedAt, LocalDateTime.now());
        methodInterceptConfigMapper.update(null, updateWrapper);
        refreshCache();
    }
    
    /**
     * 根据缓存键获取配置
     */
    public MethodInterceptConfig getConfigByCacheKey(String cacheKey) {
        if (cache.isEmpty()) {
            refreshCache();
        }
        return cache.get(cacheKey);
    }
    
    /**
     * 根据方法名获取配置
     */
    public MethodInterceptConfig getConfigByMethodName(String className, String methodName) {
        if (methodNameCache.isEmpty()) {
            refreshCache();
        }
        return methodNameCache.get(className + "#" + methodName);
    }
    
    /**
     * 获取所有配置（包括禁用的）
     */
    public List<MethodInterceptConfig> getAllConfigs() {
        LambdaQueryWrapper<MethodInterceptConfig> queryWrapper = new LambdaQueryWrapper<>();
        return methodInterceptConfigMapper.selectList(queryWrapper);
    }
    
    /**
     * 刷新缓存
     */
    public void refreshCache() {
        cache.clear();
        methodNameCache.clear();
        
        List<MethodInterceptConfig> allConfigs = getAllConfigs();
        for (MethodInterceptConfig config : allConfigs) {
            if (config.getEnabled()) {
                // 将targetMethod作为cacheKey存储
                cache.put(config.getTargetMethod(), config);
                
                // 解析className#methodName格式（如果targetMethod包含括号，则提取方法名）
                String targetMethod = config.getTargetMethod();
                String methodName = targetMethod;
                if (targetMethod.contains("(")) {
                    methodName = targetMethod.substring(0, targetMethod.indexOf("("));
                }
                methodNameCache.put(config.getTargetClass() + "#" + methodName, config);
            }
        }
    }
    
    /**
     * 根据ID获取配置
     */
    public MethodInterceptConfig getConfigById(Long id) {
        return methodInterceptConfigMapper.selectById(id);
    }
    
    /**
     * 更新配置
     */
    public MethodInterceptConfig updateConfig(MethodInterceptConfig config) {
        config.setUpdatedAt(LocalDateTime.now());
        methodInterceptConfigMapper.updateById(config);
        refreshCache();
        return config;
    }
    
    /**
     * 生成方法签名（包含参数类型）
     */
    private String generateMethodSignature(String methodName, Class<?>[] parameterTypes) {
        if (parameterTypes == null || parameterTypes.length == 0) {
            return methodName;
        }
        
        StringBuilder signature = new StringBuilder(methodName);
        signature.append("(");
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                signature.append(",");
            }
            signature.append(parameterTypes[i].getSimpleName());
        }
        signature.append(")");
        return signature.toString();
    }
}