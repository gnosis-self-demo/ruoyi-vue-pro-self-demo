package gnosis.sample.dynamic.liteflow.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.yomahub.liteflow.core.FlowExecutor;
import com.yomahub.liteflow.slot.DefaultContext;
import gnosis.sample.dynamic.liteflow.manager.MethodInterceptConfigManager;
import gnosis.sample.dynamic.liteflow.entity.MethodInterceptConfig;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * LiteFlow动态切面
 */
@Aspect
@Component
public class LiteFlowDynamicAspect {

    @Autowired
    private FlowExecutor flowExecutor;

    @Autowired
    private MethodInterceptConfigManager methodInterceptConfigManager;

    @Pointcut("execution(* gnosis.sample..*(..)) && !execution(* gnosis.sample.dynamic.liteflow..*(..))")
    public void businessMethod() {}

    @Around("businessMethod()")
    public Object aroundBusinessMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        // 生成包含参数类型的方法签名作为缓存键
        String cacheKey = generateCacheKey(className, methodName, args);
        
        // 查找精确匹配的配置
        MethodInterceptConfig config = methodInterceptConfigManager.getConfigByCacheKey(cacheKey);
        
        // 如果没有精确匹配，尝试方法名匹配
        if (config == null) {
            config = methodInterceptConfigManager.getConfigByMethodName(className, methodName);
        }
        
        // 如果找到配置且启用，则执行LiteFlow流程
        if (config != null && config.getEnabled()) {
            DefaultContext context = new DefaultContext();
            context.setData("originalArgs", args);
            context.setData("className", className);
            context.setData("methodName", methodName);
            
            // 将参数放入上下文
            for (int i = 0; i < args.length; i++) {
                context.setData("arg" + i, args[i]);
            }
            
            flowExecutor.execute2Resp(config.getChainName(), context);
            
            // 从上下文中获取结果
            Object result = context.getData("result");
            if (result != null) {
                return result;
            }
        }
        
        // 如果没有配置或配置禁用，执行原始方法
        return joinPoint.proceed();
    }

    /**
     * 生成包含参数类型的方法签名作为缓存键
     * 格式: className#methodName(paramType1,paramType2,...)
     */
    private String generateCacheKey(String className, String methodName, Object[] args) {
        StringBuilder key = new StringBuilder(className).append("#").append(methodName).append("(");
        if (args != null && args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                if (i > 0) {
                    key.append(",");
                }
                if (args[i] != null) {
                    key.append(args[i].getClass().getSimpleName());
                } else {
                    key.append("null");
                }
            }
        }
        key.append(")");
        return key.toString();
    }

    /**
     * 刷新缓存（供外部调用）
     */
    public void refreshCache() {
        methodInterceptConfigManager.refreshCache();
    }
}