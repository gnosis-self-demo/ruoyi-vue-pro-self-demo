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
}