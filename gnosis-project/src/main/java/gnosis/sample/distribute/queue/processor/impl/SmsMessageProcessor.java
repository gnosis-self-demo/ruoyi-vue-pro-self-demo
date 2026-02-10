package gnosis.sample.distribute.queue.processor.impl;

import gnosis.sample.distribute.queue.dto.BusinessProcessRequest;
import gnosis.sample.distribute.queue.dto.BusinessProcessResult;
import gnosis.sample.distribute.queue.processor.AbstractDistributeQueueBusinessProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 短信消息处理器
 */
@Slf4j
@Component
public class SmsMessageProcessor extends AbstractDistributeQueueBusinessProcessor {
    
    @Override
    public BusinessProcessResult process(BusinessProcessRequest request) throws Exception {
        log.info("处理短信消息: {}", request.getBusinessData());
        
        // 模拟短信发送耗时
        Thread.sleep(100);
        
        // 这里可以集成真实的短信服务SDK
        String result = "SMS_SENT_SUCCESS: " + request.getBusinessData();
        log.info("短信发送成功: {}", result);
        
        return BusinessProcessResult.success(request.getRequestId(), result);
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