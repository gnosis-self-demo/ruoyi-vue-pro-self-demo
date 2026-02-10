package gnosis.sample.distribute.queue.processor.impl;

import gnosis.sample.distribute.queue.processor.MessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 通知消息处理器
 */
@Slf4j
@Component
public class NotificationMessageProcessor implements MessageProcessor {
    
    @Override
    public String process(String message) throws Exception {
        log.info("处理通知消息: {}", message);
        
        // 模拟通知处理耗时
        Thread.sleep(50);
        
        String result = "NOTIFICATION_PROCESSED: " + message;
        log.info("通知处理完成: {}", result);
        
        return result;
    }
    
    @Override
    public String getSupportedQueueType() {
        return "notification_queue";
    }
    
    @Override
    public String getName() {
        return "通知处理器";
    }
}