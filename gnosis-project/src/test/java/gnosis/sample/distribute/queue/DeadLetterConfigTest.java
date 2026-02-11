package gnosis.sample.distribute.queue;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.service.DeadLetterQueueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 死信队列配置功能测试
 */
@SpringBootTest
public class DeadLetterConfigTest {

    @Autowired
    private RuntimeQueueConfig runtimeConfig;
    
    @Autowired
    private DeadLetterQueueService deadLetterService;

    @Test
    public void testDeadLetterConfigManagement() {
        System.out.println("=== 死信队列配置管理测试 ===");
        
        String testQueue = "config_test_queue";
        
        // 1. 测试默认配置
        testDefaultConfig(testQueue);
        
        // 2. 测试配置更新
        testConfigUpdate(testQueue);
        
        // 3. 测试配置查询
        testConfigQuery(testQueue);
        
        // 4. 测试配置重置
        testConfigReset(testQueue);
        
        System.out.println("=== 配置管理测试完成 ===");
    }

    private void testDefaultConfig(String queueName) {
        System.out.println("1. 测试默认配置...");
        
        assertEquals(3, runtimeConfig.getMaxRetryAttempts(queueName), "默认重试次数应该是3");
        assertEquals(1800000L, runtimeConfig.getProcessingTimeoutMs(queueName), "默认超时时间应该是30分钟");
        assertEquals(300000L, runtimeConfig.getDeadLetterCheckIntervalMs(queueName), "默认检查间隔应该是5分钟");
        
        System.out.println("   ✓ 默认配置验证通过");
    }

    private void testConfigUpdate(String queueName) {
        System.out.println("2. 测试配置更新...");
        
        // 更新配置
        runtimeConfig.setMaxRetryAttempts(queueName, 5);
        runtimeConfig.setProcessingTimeoutMs(queueName, 3600000L); // 1小时
        runtimeConfig.setDeadLetterCheckIntervalMs(queueName, 600000L); // 10分钟
        
        // 验证更新结果
        assertEquals(5, runtimeConfig.getMaxRetryAttempts(queueName), "重试次数应该更新为5");
        assertEquals(3600000L, runtimeConfig.getProcessingTimeoutMs(queueName), "超时时间应该更新为1小时");
        assertEquals(600000L, runtimeConfig.getDeadLetterCheckIntervalMs(queueName), "检查间隔应该更新为10分钟");
        
        System.out.println("   ✓ 配置更新验证通过");
    }

    private void testConfigQuery(String queueName) {
        System.out.println("3. 测试配置查询...");
        
        try {
            // 通过服务查询配置
            Map<String, Object> config = deadLetterService.getDeadLetterConfig(queueName);
            
            assertNotNull(config, "配置不应该为null");
            assertEquals(5, config.get("maxRetryAttempts"), "查询的重试次数应该为5");
            assertEquals(3600000L, config.get("processingTimeoutMs"), "查询的超时时间应该为1小时");
            assertEquals(600000L, config.get("checkIntervalMs"), "查询的检查间隔应该为10分钟");
            
            System.out.println("   ✓ 配置查询验证通过");
            System.out.println("   当前配置: " + config);
            
        } catch (Exception e) {
            System.err.println("   ✗ 配置查询失败: " + e.getMessage());
        }
    }

    private void testConfigReset(String queueName) {
        System.out.println("4. 测试配置重置...");
        
        // 重置配置
        runtimeConfig.resetQueueConfig(queueName);
        
        // 验证重置后的默认值
        assertEquals(3, runtimeConfig.getMaxRetryAttempts(queueName), "重置后重试次数应回到默认值3");
        assertEquals(1800000L, runtimeConfig.getProcessingTimeoutMs(queueName), "重置后超时时间应回到默认值30分钟");
        assertEquals(300000L, runtimeConfig.getDeadLetterCheckIntervalMs(queueName), "重置后检查间隔应回到默认值5分钟");
        
        System.out.println("   ✓ 配置重置验证通过");
    }

    @Test
    public void testMultipleQueueConfigs() {
        System.out.println("=== 多队列配置测试 ===");
        
        String[] queueNames = {"queue_a", "queue_b", "queue_c"};
        int[] retryAttempts = {1, 3, 5};
        long[] timeouts = {600000L, 1800000L, 3600000L};
        
        // 为不同队列设置不同配置
        for (int i = 0; i < queueNames.length; i++) {
            runtimeConfig.setMaxRetryAttempts(queueNames[i], retryAttempts[i]);
            runtimeConfig.setProcessingTimeoutMs(queueNames[i], timeouts[i]);
            
            System.out.println("队列 " + queueNames[i] + ": 重试=" + retryAttempts[i] + 
                             ", 超时=" + timeouts[i] + "ms");
        }
        
        // 验证各队列配置独立性
        for (int i = 0; i < queueNames.length; i++) {
            assertEquals(retryAttempts[i], runtimeConfig.getMaxRetryAttempts(queueNames[i]), 
                        "队列 " + queueNames[i] + " 重试次数配置错误");
            assertEquals(timeouts[i], runtimeConfig.getProcessingTimeoutMs(queueNames[i]), 
                        "队列 " + queueNames[i] + " 超时时间配置错误");
        }
        
        System.out.println("   ✓ 多队列配置独立性验证通过");
        System.out.println("=== 多队列配置测试完成 ===");
    }

    @Test
    public void testInvalidConfigValues() {
        System.out.println("=== 无效配置值测试 ===");
        
        String testQueue = "invalid_config_queue";
        
        // 测试负数值处理
        runtimeConfig.setMaxRetryAttempts(testQueue, -1);
        runtimeConfig.setProcessingTimeoutMs(testQueue, -1000L);
        runtimeConfig.setDeadLetterCheckIntervalMs(testQueue, 0L);
        
        // 验证是否使用默认值
        assertEquals(3, runtimeConfig.getMaxRetryAttempts(testQueue), "负数重试次数应该使用默认值");
        assertEquals(1800000L, runtimeConfig.getProcessingTimeoutMs(testQueue), "负数超时时间应该使用默认值");
        assertEquals(300000L, runtimeConfig.getDeadLetterCheckIntervalMs(testQueue), "零值检查间隔应该使用默认值");
        
        System.out.println("   ✓ 无效配置值处理验证通过");
        System.out.println("=== 无效配置值测试完成 ===");
    }
}