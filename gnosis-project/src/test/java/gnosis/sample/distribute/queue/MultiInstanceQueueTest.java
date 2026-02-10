package gnosis.sample.distribute.queue;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.factory.DistributedQueueFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

/**
 * 多实例队列功能测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class MultiInstanceQueueTest {

    @Autowired
    private DistributedQueueFactory queueFactory;

    @Test
    public void testCreateMultipleQueueInstances() {
        // 创建不同配置的队列实例
        RuntimeQueueConfig smsConfig = new RuntimeQueueConfig();
        smsConfig.setMaxQueueLength("sms_queue", 1000);
        smsConfig.setMaxQps("sms_queue", 50);
        
        RuntimeQueueConfig emailConfig = new RuntimeQueueConfig();
        emailConfig.setMaxQueueLength("email_queue", 500);
        emailConfig.setMaxQps("email_queue", 30);
        
        // 创建队列实例
        DistributedQueueFactory.QueueInstance smsInstance = 
            queueFactory.createQueueInstance("sms_queue", smsConfig);
        DistributedQueueFactory.QueueInstance emailInstance = 
            queueFactory.createQueueInstance("email_queue", emailConfig);
        
        // 验证实例创建成功
        assertNotNull("SMS队列实例不应为null", smsInstance);
        assertNotNull("Email队列实例不应为null", emailInstance);
        
        // 验证配置正确
        assertEquals("SMS队列最大长度应为1000", 
            1000, smsInstance.getConfig().getMaxQueueLength("sms_queue"));
        assertEquals("Email队列最大QPS应为30", 
            30, emailInstance.getConfig().getMaxQps("email_queue"));
        
        // 验证可以通过工厂获取实例
        assertEquals("应该能通过工厂获取SMS实例", 
            smsInstance, queueFactory.getQueueInstance("sms_queue"));
        assertEquals("应该能通过工厂获取Email实例", 
            emailInstance, queueFactory.getQueueInstance("email_queue"));
        
        System.out.println("多实例队列创建测试通过！");
    }

    @Test
    public void testQueueInstanceUniqueness() {
        RuntimeQueueConfig config = new RuntimeQueueConfig();
        
        // 第一次创建应该成功
        DistributedQueueFactory.QueueInstance instance1 = 
            queueFactory.createQueueInstance("unique_queue", config);
        assertNotNull("第一次创建应该成功", instance1);
        
        // 第二次创建相同名称的队列应该失败
        try {
            queueFactory.createQueueInstance("unique_queue", config);
            fail("重复创建相同名称的队列应该抛出异常");
        } catch (IllegalArgumentException e) {
            assertTrue("应该抛出队列已存在的异常", 
                e.getMessage().contains("already exists"));
        }
        
        System.out.println("队列实例唯一性测试通过！");
    }

    @Test
    public void testRemoveQueueInstance() {
        RuntimeQueueConfig config = new RuntimeQueueConfig();
        
        // 创建队列实例
        DistributedQueueFactory.QueueInstance instance = 
            queueFactory.createQueueInstance("temp_queue", config);
        assertNotNull("临时队列实例不应为null", instance);
        
        // 验证实例存在
        assertNotNull("临时队列实例应该存在", 
            queueFactory.getQueueInstance("temp_queue"));
        
        // 删除队列实例
        boolean removed = queueFactory.removeQueueInstance("temp_queue");
        assertTrue("应该成功删除队列实例", removed);
        
        // 验证实例已被删除
        assertNull("临时队列实例应该已被删除", 
            queueFactory.getQueueInstance("temp_queue"));
        
        // 再次删除应该返回false
        assertFalse("删除不存在的队列实例应该返回false", 
            queueFactory.removeQueueInstance("temp_queue"));
        
        System.out.println("队列实例删除测试通过！");
    }
}