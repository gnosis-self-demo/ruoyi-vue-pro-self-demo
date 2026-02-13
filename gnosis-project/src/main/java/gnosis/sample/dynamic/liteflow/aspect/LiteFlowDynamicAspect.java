package gnosis.sample.dynamic.liteflow.aspect;

import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.slot.DefaultContext;
import gnosis.sample.dynamic.liteflow.entity.MethodInterceptConfig;
import gnosis.sample.dynamic.liteflow.manager.MethodInterceptConfigManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LiteFlow动态切面实现
 */
@Aspect
@Component
public class LiteFlowDynamicAspect {
    
    private static final Logger log = LoggerFactory.getLogger(LiteFlowDynamicAspect.class);
    
    @Autowired
    private FlowExecutor flowExecutor;
    
    @Autowired
    private MethodInterceptConfigManager methodInterceptConfigManager;
    
    // 缓存配置，避免频繁查询数据库
    private final Map<String, MethodInterceptConfig> configCache = new ConcurrentHashMap<>();
    
    /**
     * 定义切入点：只拦截业务包下的方法，避免影响框架和系统方法
     * 排除Spring框架、JDK内部类等
     */
    @Pointcut("execution(* gnosis.sample..*(..)) && !execution(* gnosis.sample.dynamic.liteflow..*(..))")
    public void businessMethods() {}
    
    /**
     * 环绕通知，实现动态替换逻辑
     */
    @Around("businessMethods()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getName();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        Class<?>[] parameterTypes = signature.getParameterTypes();
        
        // 生成包含参数类型的缓存键，解决重载方法问题
        String cacheKey = generateCacheKey(className, methodName, parameterTypes);
        
        // 检查是否有拦截配置
        MethodInterceptConfig config = configCache.computeIfAbsent(cacheKey, k -> {
            return methodInterceptConfigManager.getConfigByTarget(className, methodName, parameterTypes);
        });
        
        if (config != null && config.getEnabled()) {
            log.info("拦截方法: {}#{}, 使用LiteFlow链: {}", className, methodName, config.getChainName());
            
            // 创建上下文并传递参数
            DefaultContext context = new DefaultContext();
            context.setData("originalArgs", joinPoint.getArgs());
            context.setData("className", className);
            context.setData("methodName", methodName);
            context.setData("parameterTypes", parameterTypes);
            
            try {
                // 执行LiteFlow流程
                flowExecutor.execute2Resp(config.getChainName(), context);
                
                // 获取执行结果
                Object result = context.getData("result");
                if (result != null) {
                    log.info("LiteFlow执行成功，返回结果");
                    return result;
                }
            } catch (Exception e) {
                log.error("LiteFlow执行异常，回退到原方法", e);
            }
            
            // 如果LiteFlow执行失败或没有返回结果，回退到原方法
            log.info("回退到原方法执行");
        }
        
        // 执行原方法
        return joinPoint.proceed();
    }
    
    /**
     * 刷新配置缓存
     */
    public void refreshConfigCache() {
        configCache.clear();
        log.info("LiteFlow动态切面配置缓存已刷新");
    }
    
    /**
     * 生成包含参数类型的缓存键，解决重载方法问题
     */
    private String generateCacheKey(String className, String methodName, Class<?>[] parameterTypes) {
        StringBuilder key = new StringBuilder();
        key.append(className).append("#").append(methodName);
        if (parameterTypes != null && parameterTypes.length > 0) {
            key.append("(");
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i > 0) {
                    key.append(",");
                }
                key.append(parameterTypes[i].getName());
            }
            key.append(")");
        }
        return key.toString();
    }
}