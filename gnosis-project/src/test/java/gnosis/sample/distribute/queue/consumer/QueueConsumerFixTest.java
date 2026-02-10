package gnosis.sample.distribute.queue.consumer;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.factory.DistributedQueueFactory;
import gnosis.sample.distribute.queue.model.QueueInstance;
import gnosis.sample.distribute.queue.processor.BusinessProcessorManager;
import gnosis.sample.distribute.queue.service.DistributedQueueService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

import static org.junit.Assert.assertNotNull;

/**
 * QueueConsumer修复测试类
 * 验证NullPointerException问题是否已解决
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class QueueConsumerFixTest {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private BusinessProcessorManager businessProcessorManager;

    @Test
    public void testQueueConsumerCreation() {
        // 创建工厂实例
        DistributedQueueFactory factory = new DistributedQueueFactory();
        
        // 手动注入依赖（在实际Spring环境中这些会自动注入）
        factory.dataSource = dataSource;
        factory.businessProcessorManager = businessProcessorManager;
        
        // 创建队列配置
        RuntimeQueueConfig config = new RuntimeQueueConfig();
        config.setMaxQueueLength("test-queue", 100);
        config.setMaxQps("test-queue", 10);
        config.setEmptyPollIntervalMs(1000);
        
        // 创建队列实例（这会触发QueueConsumer的创建）
        QueueInstance instance = factory.createQueueInstance("test-queue", config);
        
        // 验证实例创建成功
        assertNotNull("队列实例不应为null", instance);
        assertNotNull("队列服务不应为null", instance.getService());
        assertNotNull("消费者不应为null", instance.getConsumer());
        
        // 验证消费者字段已正确设置
        QueueConsumer consumer = instance.getConsumer();
        assertNotNull("queueService不应为null", consumer);
        assertNotNull("runtimeConfig不应为null", consumer.getRuntimeConfig());
        assertNotNull("processorManager不应为null", consumer.getProcessorManager());
        
        System.out.println("QueueConsumer创建测试通过！");
        
        // 清理资源
        factory.removeQueueInstance("test-queue");
    }
}