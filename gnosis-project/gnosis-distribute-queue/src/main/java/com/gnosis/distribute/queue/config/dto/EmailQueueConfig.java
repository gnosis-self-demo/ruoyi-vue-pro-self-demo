package com.gnosis.distribute.queue.config.dto;

import lombok.Data;

/**
 * 邮件队列配置
 */
@Data
public class EmailQueueConfig {
    private Integer maxLength = 1000;
    private Integer maxQps = 30;
    private Long pollInterval = 2000L;
}