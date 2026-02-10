package gnosis.sample.distribute.queue.processor;

import gnosis.sample.distribute.queue.dto.BusinessProcessRequest;
import gnosis.sample.distribute.queue.dto.BusinessProcessResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 业务处理器管理器
 * 负责管理和路由不同的业务处理器
 */
@Slf4j
@Component
public class BusinessProcessorManager {
    
    @Autowired
    private Map<String, AbstractDistributeQueueBusinessProcessor> processorMap;
    
    private final Map<String, AbstractDistributeQueueBusinessProcessor> initializedProcessorMap = new ConcurrentHashMap<>();
    
    /**
     * 初始化处理器映射
     */
    @PostConstruct
    public void init() {
        for (Map.Entry<String, AbstractDistributeQueueBusinessProcessor> entry : processorMap.entrySet()) {
            AbstractDistributeQueueBusinessProcessor processor = entry.getValue();
            initializedProcessorMap.put(processor.getSupportedQueueType(), processor);
            log.info("注册业务处理器: {} -> {}", 
                processor.getSupportedQueueType(), processor.getName());
        }
    }
    
    /**
     * 根据队列类型获取对应的处理器
     * @param queueType 队列类型
     * @return 业务处理器
     */
    public AbstractDistributeQueueBusinessProcessor getProcessor(String queueType) {
        return initializedProcessorMap.get(queueType);
    }
    
    /**
     * 处理业务请求
     * @param queueType 队列类型
     * @param request 处理请求
     * @return 处理结果
     * @throws Exception 处理异常
     */
    public BusinessProcessResult processRequest(
            String queueType, BusinessProcessRequest request) throws Exception {
        AbstractDistributeQueueBusinessProcessor processor = getProcessor(queueType);
        if (processor == null) {
            log.warn("未找到队列类型 {} 对应的处理器，使用默认处理", queueType);
            return defaultProcessHandler(request);
        }
        
        return processor.process(request);
    }
    
    /**
     * 默认业务处理方法
     */
    private BusinessProcessResult defaultProcessHandler(BusinessProcessRequest request) {
        log.info("默认处理业务请求: {}", request.getBusinessData());
        return BusinessProcessResult.success(
            request.getRequestId(), "DEFAULT_PROCESSED: " + request.getBusinessData());
    }
    
    /**
     * 获取所有已注册的处理器信息
     */
    public Map<String, String> getRegisteredProcessors() {
        Map<String, String> info = new ConcurrentHashMap<>();
        initializedProcessorMap.forEach((queueType, processor) -> 
            info.put(queueType, processor.getName()));
        return info;
    }
}