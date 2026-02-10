package gnosis.sample.distribute.queue.config.dto;

import lombok.Data;

/**
 * 通知队列配置
 */
@Data
public class NotificationQueueConfig {
    private Integer maxLength = 2000;
    private Integer maxQps = 100;
    private Long pollInterval = 500L;
}