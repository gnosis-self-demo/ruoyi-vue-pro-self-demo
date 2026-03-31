package com.gnosis.distribute.queue.model;

import com.gnosis.distribute.queue.enums.QueueMessageStatus;
import lombok.Data;

/**
 * 队列消息模型
 */
@Data
public class QueueMessage {
    private final long id;
    private final String queueName;
    private final String messageBody;
    private final int attemptCount;
    private final QueueMessageStatus status;

    public QueueMessage(long id, String queueName, String messageBody, int attemptCount) {
        this(id, queueName, messageBody, attemptCount, null);
    }

    public QueueMessage(long id, String queueName, String messageBody, int attemptCount, QueueMessageStatus status) {
        this.id = id;
        this.queueName = queueName;
        this.messageBody = messageBody;
        this.attemptCount = attemptCount;
        this.status = status;
    }
}