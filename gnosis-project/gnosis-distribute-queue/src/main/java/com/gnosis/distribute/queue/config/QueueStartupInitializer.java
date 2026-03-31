package com.gnosis.distribute.queue.config;

import com.gnosis.distribute.queue.config.dto.QueueConfig;
import com.gnosis.distribute.queue.factory.DistributedQueueFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 队列启动配置类
 * 在应用启动时自动创建配置的队列实例
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class QueueStartupInitializer implements ApplicationListener<ApplicationReadyEvent> {
    
    private final DistributedQueueFactory queueFactory;
    private final QueueStartupConfig startupConfig;
    
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("开始初始化队列实例...");
        
        // 创建配置文件中定义的队列
        createConfiguredQueues();
        
        log.info("队列实例初始化完成");
    }
    
    /**
     * 创建配置文件中定义的队列实例
     */
    private void createConfiguredQueues() {
        Map<String, QueueConfig> configuredQueues = startupConfig.getQueues();
        
        for (Map.Entry<String, QueueConfig> entry : configuredQueues.entrySet()) {
            String queueName = entry.getKey();
            QueueConfig config = entry.getValue();
            
            if (Boolean.TRUE.equals(config.getEnabled())) {
                try {
                    if (queueFactory.getQueueInstance(queueName) == null) {
                        createQueueFromConfig(queueName, config);
                        log.info("创建配置队列实例: {}", queueName);
                    }
                } catch (Exception e) {
                    log.error("创建配置队列 {} 失败: {}", queueName, e.getMessage());
                }
            }
        }
    }
    
    /**
     * 根据配置创建队列实例
     */
    private void createQueueFromConfig(String queueName, QueueConfig config) {
        RuntimeQueueConfig runtimeConfig = new RuntimeQueueConfig();
            
        // 使用配置值
        int maxLength = config.getMaxLength() != null ? 
            config.getMaxLength() : 1000;
        int maxQps = config.getMaxQps() != null ? 
            config.getMaxQps() : 50;
        long pollInterval = config.getPollInterval() != null ? 
            config.getPollInterval() : 1000L;
            
        runtimeConfig.setMaxQueueLength(queueName, maxLength);
        runtimeConfig.setMaxQps(queueName, maxQps);
        runtimeConfig.setEmptyPollIntervalMs(pollInterval);
        
        // 设置死信队列配置
        int maxRetryAttempts = config.getMaxRetryAttempts() != null ? 
            config.getMaxRetryAttempts() : 3;
        long processingTimeoutMs = config.getProcessingTimeoutMs() != null ? 
            config.getProcessingTimeoutMs() : 1800000L; // 30分钟
        long checkIntervalMs = config.getDeadLetterCheckIntervalMs() != null ? 
            config.getDeadLetterCheckIntervalMs() : 300000L; // 5分钟
            
        runtimeConfig.setMaxRetryAttempts(queueName, maxRetryAttempts);
        runtimeConfig.setProcessingTimeoutMs(queueName, processingTimeoutMs);
        runtimeConfig.setDeadLetterCheckIntervalMs(queueName, checkIntervalMs);
        
        queueFactory.createQueueInstance(queueName, runtimeConfig);
    }
}