package gnosis.sample.distribute.queue.model;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.consumer.QueueConsumer;
import gnosis.sample.distribute.queue.service.DistributedQueueService;
import lombok.Data;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 队列实例模型
 */
@Data
public class QueueInstance {
    private final String queueName;
    private final RuntimeQueueConfig config;
    private final DistributedQueueService service;
    private final QueueConsumer consumer;
    private final PlatformTransactionManager transactionManager;

    public QueueInstance(String queueName, RuntimeQueueConfig config, 
                       DistributedQueueService service, QueueConsumer consumer,
                       PlatformTransactionManager transactionManager) {
        this.queueName = queueName;
        this.config = config;
        this.service = service;
        this.consumer = consumer;
        this.transactionManager = transactionManager;
    }
}