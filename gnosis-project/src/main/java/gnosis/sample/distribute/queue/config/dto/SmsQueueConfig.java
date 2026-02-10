package gnosis.sample.distribute.queue.config.dto;

import lombok.Data;

/**
 * 短信队列配置
 */
@Data
public class SmsQueueConfig {
    private Integer maxLength = 1000;
    private Integer maxQps = 50;
    private Long pollInterval = 1000L;
}