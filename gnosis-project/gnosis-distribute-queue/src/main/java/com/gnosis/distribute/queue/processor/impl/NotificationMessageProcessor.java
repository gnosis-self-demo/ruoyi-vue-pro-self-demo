package com.gnosis.distribute.queue.processor.impl;

import com.gnosis.distribute.queue.dto.BusinessProcessRequest;
import com.gnosis.distribute.queue.dto.BusinessProcessResult;
import com.gnosis.distribute.queue.processor.AbstractDistributeQueueBusinessProcessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 通知消息处理器
 */
@Slf4j
@Component
public class NotificationMessageProcessor extends AbstractDistributeQueueBusinessProcessor {
    
    @Override
    public BusinessProcessResult process(BusinessProcessRequest request) throws Exception {
        log.info("处理通知消息: {}", request.getBusinessData());
        
        // 模拟通知处理耗时
        Thread.sleep(50);
        
        String result = "NOTIFICATION_PROCESSED: " + request.getBusinessData();
        log.info("通知处理完成: {}", result);
        
        return BusinessProcessResult.success(request.getRequestId(), result);
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