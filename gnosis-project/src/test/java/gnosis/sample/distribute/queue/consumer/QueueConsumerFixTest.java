package gnosis.sample.distribute.queue.consumer;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.factory.DistributedQueueFactory;
import gnosis.sample.distribute.queue.model.QueueInstance;
import gnosis.sample.distribute.queue.processor.BusinessProcessorManager;
import gnosis.sample.distribute.queue.service.DistributedQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * QueueConsumer修复测试类
 * 验证NullPointerException问题是否已解决
 */
@SpringBootTest
public class QueueConsumerFixTest {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private BusinessProcessorManager businessProcessorManager;

    @Test
    public void testQueueConsumerCreation() throws Exception {
        // 创建工厂实例
        DistributedQueueFactory factory = new DistributedQueueFactory();
        
        // 使用反射设置私有字段
        Field dataSourceField = DistributedQueueFactory.class.getDeclaredField("dataSource");
        dataSourceField.setAccessible(true);
        dataSourceField.set(factory, dataSource);
        
        Field processorManagerField = DistributedQueueFactory.class.getDeclaredField("businessProcessorManager");
        processorManagerField.setAccessible(true);
        processorManagerField.set(factory, businessProcessorManager);
        
        // 创建队列配置
        RuntimeQueueConfig config = new RuntimeQueueConfig();
        config.setMaxQueueLength("test-queue", 100);
        config.setMaxQps("test-queue", 10);
        config.setEmptyPollIntervalMs(1000);
        
        // 创建队列实例（这会触发QueueConsumer的创建）
        QueueInstance instance = factory.createQueueInstance("test-queue", config);
        
        // 验证实例创建成功
        assertNotNull(instance, "队列实例不应为null");
        assertNotNull(instance.getService(), "队列服务不应为null");
        assertNotNull(instance.getConsumer(), "消费者不应为null");
        
        // 验证消费者字段已正确设置
        QueueConsumer consumer = instance.getConsumer();
        assertNotNull(consumer, "消费者不应为null");
        
        System.out.println("QueueConsumer创建测试通过！");
        
        // 清理资源
        factory.removeQueueInstance("test-queue");
    }
}