package paramcheck.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handler 注册中心
 * 启动时扫描所有 @ValidationHandler 注解的 IValidationHandler 实现类并注册
 */
@Service
public class HandlerRegistry {

    private static final Logger log = LoggerFactory.getLogger(HandlerRegistry.class);

    private final Map<String, IValidationHandler> handlerMap = new ConcurrentHashMap<>(16);

    @Autowired
    private ApplicationContext applicationContext;

    @PostConstruct
    public void init() {
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(ValidationHandler.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            Object bean = entry.getValue();
            if (bean instanceof IValidationHandler) {
                IValidationHandler handler = (IValidationHandler) bean;
                ValidationHandler annotation = bean.getClass().getAnnotation(ValidationHandler.class);
                String code = annotation.value();
                handlerMap.put(code, handler);
                log.info("[ParamCheck] Registered IValidationHandler: code={}, class={}", code, bean.getClass().getName());
            }
        }
        log.info("[ParamCheck] HandlerRegistry initialized, total handlers: {}", handlerMap.size());
    }

    /**
     * 根据编码获取处理器
     *
     * @param code handler_code (与 DB 配置一致)
     * @return 处理器实例
     * @throws IllegalArgumentException 未找到对应 Handler 时抛出
     */
    public IValidationHandler getHandler(String code) {
        IValidationHandler handler = handlerMap.get(code);
        if (handler == null) {
            throw new IllegalArgumentException("[ParamCheck] Handler not found, code: " + code);
        }
        return handler;
    }

    /**
     * 判断指定编码的 Handler 是否已注册
     */
    public boolean hasHandler(String code) {
        return handlerMap.containsKey(code);
    }

    /**
     * 获取已注册 Handler 总数 (用于测试)
     */
    public int handlerCount() {
        return handlerMap.size();
    }
}
