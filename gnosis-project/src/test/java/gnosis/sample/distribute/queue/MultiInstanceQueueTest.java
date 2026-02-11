package gnosis.sample.distribute.queue;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.factory.DistributedQueueFactory;
import gnosis.sample.distribute.queue.model.QueueInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 多实例队列功能测试
 */
@SpringBootTest
public class MultiInstanceQueueTest {

    @Autowired
    private DistributedQueueFactory queueFactory;

    @Test
    public void testCreateMultipleQueueInstances() {
        // 创建动态配置
        RuntimeQueueConfig smsConfig = new RuntimeQueueConfig();
        smsConfig.setMaxQueueLength("test_sms_queue", 500);
        smsConfig.setMaxQps("test_sms_queue", 50);
        
        RuntimeQueueConfig emailConfig = new RuntimeQueueConfig();
        emailConfig.setMaxQueueLength("test_email_queue", 200);
        emailConfig.setMaxQps("test_email_queue", 20);
        
        // 使用唯一的队列名称，避免与预定义队列冲突
        QueueInstance smsInstance = 
            queueFactory.createQueueInstance("test_sms_queue", smsConfig);
        QueueInstance emailInstance = 
            queueFactory.createQueueInstance("test_email_queue", emailConfig);
        
        // 验证实例创建成功
        assertNotNull(smsInstance, "SMS队列实例不应为null");
        assertNotNull(emailInstance, "Email队列实例不应为null");
        
        // 验证配置正确应用
        assertEquals(500, smsInstance.getConfig().getMaxQueueLength("test_sms_queue"));
        assertEquals(50, smsInstance.getConfig().getMaxQps("test_sms_queue"));
        assertEquals(200, emailInstance.getConfig().getMaxQueueLength("test_email_queue"));
        assertEquals(20, emailInstance.getConfig().getMaxQps("test_email_queue"));
        
        // 验证队列名称正确
        assertEquals("test_sms_queue", smsInstance.getQueueName());
        assertEquals("test_email_queue", emailInstance.getQueueName());
    }
    
    @Test
    public void testQueueInstanceUniqueness() {
        RuntimeQueueConfig config = new RuntimeQueueConfig();
        String uniqueQueueName = "unique_queue_" + System.currentTimeMillis();
        config.setMaxQueueLength(uniqueQueueName, 100);
        config.setMaxQps(uniqueQueueName, 10);
        
        // 创建唯一队列
        QueueInstance instance1 = queueFactory.createQueueInstance(uniqueQueueName, config);
        assertNotNull(instance1, "队列实例不应为null");
        
        // 尝试创建同名队列应该失败
        assertThrows(IllegalArgumentException.class, () -> {
            queueFactory.createQueueInstance(uniqueQueueName, config);
        });
    }
    
    @Test
    public void testRemoveQueueInstance() {
        RuntimeQueueConfig config = new RuntimeQueueConfig();
        String tempQueueName = "temp_queue_" + System.currentTimeMillis();
        config.setMaxQueueLength(tempQueueName, 100);
        config.setMaxQps(tempQueueName, 10);
        
        // 创建临时队列
        QueueInstance instance = queueFactory.createQueueInstance(tempQueueName, config);
        assertNotNull(instance, "队列实例不应为null");
        
        // 删除队列实例
        boolean removed = queueFactory.removeQueueInstance(tempQueueName);
        assertTrue(removed, "队列实例应被成功删除");
        
        // 验证队列已不存在
        assertNull(queueFactory.getQueueInstance(tempQueueName), "队列实例应为null");
    }
}