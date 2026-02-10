package gnosis.sample.distribute.queue.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息处理器管理器
 * 负责管理和路由不同的消息处理器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MessageProcessorManager {
    
    private final List<MessageProcessor> processors;
    private final Map<String, MessageProcessor> processorMap = new ConcurrentHashMap<>();
    
    /**
     * 初始化处理器映射
     */
    public void init() {
        for (MessageProcessor processor : processors) {
            processorMap.put(processor.getSupportedQueueType(), processor);
            log.info("注册消息处理器: {} -> {}", 
                processor.getSupportedQueueType(), processor.getName());
        }
    }
    
    /**
     * 根据队列类型获取对应的处理器
     * @param queueType 队列类型
     * @return 消息处理器
     */
    public MessageProcessor getProcessor(String queueType) {
        return processorMap.get(queueType);
    }
    
    /**
     * 处理消息
     * @param queueType 队列类型
     * @param message 消息内容
     * @return 处理结果
     * @throws Exception 处理异常
     */
    public String processMessage(String queueType, String message) throws Exception {
        MessageProcessor processor = getProcessor(queueType);
        if (processor == null) {
            log.warn("未找到队列类型 {} 对应的处理器，使用默认处理", queueType);
            return defaultMessageHandler(message);
        }
        
        return processor.process(message);
    }
    
    /**
     * 默认消息处理方法
     */
    private String defaultMessageHandler(String message) {
        log.info("默认处理消息: {}", message);
        return "DEFAULT_PROCESSED: " + message;
    }
    
    /**
     * 获取所有已注册的处理器信息
     */
    public Map<String, String> getRegisteredProcessors() {
        Map<String, String> info = new ConcurrentHashMap<>();
        processorMap.forEach((queueType, processor) -> 
            info.put(queueType, processor.getName()));
        return info;
    }
}