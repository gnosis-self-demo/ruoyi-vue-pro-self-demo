package gnosis.sample.distribute.queue.config.dto;

import lombok.Data;

/**
 * 队列配置
 */
@Data
public class QueueConfig {
    private Integer maxLength;
    private Integer maxQps;
    private Long pollInterval;
    private Boolean enabled = true;
    
    // 死信队列相关配置
    private Integer maxRetryAttempts = 3;           // 最大重试次数
    private Long processingTimeoutMs = 1800000L;    // 处理超时时间(毫秒，默认30分钟)
    private Long deadLetterCheckIntervalMs = 300000L; // 死信检查间隔(毫秒，默认5分钟)
}