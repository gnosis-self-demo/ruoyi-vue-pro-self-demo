package gnosis.sample.distribute.queue.processor.impl;

import gnosis.sample.distribute.queue.processor.MessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 短信消息处理器
 */
@Slf4j
@Component
public class SmsMessageProcessor implements MessageProcessor {
    
    @Override
    public String process(String message) throws Exception {
        log.info("处理短信消息: {}", message);
        
        // 模拟短信发送耗时
        Thread.sleep(100);
        
        // 这里可以集成真实的短信服务SDK
        String result = "SMS_SENT_SUCCESS: " + message;
        log.info("短信发送成功: {}", result);
        
        return result;
    }
    
    @Override
    public String getSupportedQueueType() {
        return "sms_queue";
    }
    
    @Override
    public String getName() {
        return "短信处理器";
    }
}