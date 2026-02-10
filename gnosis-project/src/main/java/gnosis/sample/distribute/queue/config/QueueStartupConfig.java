package gnosis.sample.distribute.queue.config;

import gnosis.sample.distribute.queue.config.dto.EmailQueueConfig;
import gnosis.sample.distribute.queue.config.dto.NotificationQueueConfig;
import gnosis.sample.distribute.queue.config.dto.QueueConfig;
import gnosis.sample.distribute.queue.config.dto.SmsQueueConfig;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 队列启动配置类
 * 通过配置属性绑定方式加载队列初始配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "distributed.queue")
public class QueueStartupConfig {
    
    private SmsQueueConfig sms = new SmsQueueConfig();
    private EmailQueueConfig email = new EmailQueueConfig();
    private NotificationQueueConfig notification = new NotificationQueueConfig();
    
    /**
     * 获取所有队列配置
     * @return 队列配置映射
     */
    public java.util.Map<String, QueueConfig> getQueues() {
        Map<String, QueueConfig> queues = new HashMap<>();
        
        QueueConfig smsConfig = new QueueConfig();
        smsConfig.setMaxLength(sms.getMaxLength());
        smsConfig.setMaxQps(sms.getMaxQps());
        smsConfig.setPollInterval(sms.getPollInterval());
        smsConfig.setEnabled(true);
        queues.put("sms_queue", smsConfig);
        
        QueueConfig emailConfig = new QueueConfig();
        emailConfig.setMaxLength(email.getMaxLength());
        emailConfig.setMaxQps(email.getMaxQps());
        emailConfig.setPollInterval(email.getPollInterval());
        emailConfig.setEnabled(true);
        queues.put("email_queue", emailConfig);
        
        QueueConfig notificationConfig = new QueueConfig();
        notificationConfig.setMaxLength(notification.getMaxLength());
        notificationConfig.setMaxQps(notification.getMaxQps());
        notificationConfig.setPollInterval(notification.getPollInterval());
        notificationConfig.setEnabled(true);
        queues.put("notification_queue", notificationConfig);
        
        return queues;
    }
    

}