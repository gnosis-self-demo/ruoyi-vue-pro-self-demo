package gnosis.sample.distribute.queue.processor.impl;

import gnosis.sample.distribute.queue.processor.MessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 邮件消息处理器
 */
@Slf4j
@Component
public class EmailMessageProcessor implements MessageProcessor {
    
    @Override
    public String process(String message) throws Exception {
        log.info("处理邮件消息: {}", message);
        
        // 模拟邮件发送耗时
        Thread.sleep(80);
        
        // 这里可以集成真实的邮件服务SDK
        String result = "EMAIL_SENT_SUCCESS: " + message;
        log.info("邮件发送成功: {}", result);
        
        return result;
    }
    
    @Override
    public String getSupportedQueueType() {
        return "email_queue";
    }
    
    @Override
    public String getName() {
        return "邮件处理器";
    }
}