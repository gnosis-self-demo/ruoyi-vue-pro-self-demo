package gnosis.sample.distribute.queue;

import gnosis.sample.distribute.queue.service.DeadLetterQueueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 配置验证测试
 * 验证修复后的配置是否正常工作
 */
@SpringBootTest
public class ConfigValidationTest {

    @Autowired
    private DeadLetterQueueService deadLetterQueueService;

    @Test
    public void testScheduledAnnotationFix() {
        // 验证@Scheduled注解修复后服务能够正常启动
        assertNotNull(deadLetterQueueService, "DeadLetterQueueService应该能够正常注入");
        
        // 验证配置方法可用
        Map<String, Object> config = deadLetterQueueService.getDeadLetterConfig("test_queue");
        assertNotNull(config, "应该能够获取队列配置");
        
        System.out.println("配置验证测试通过");
    }

    @Test
    public void testDefaultConfigValues() {
        // 测试默认配置值
        Map<String, Object> config = deadLetterQueueService.getDeadLetterConfig("nonexistent_queue");
        
        // 验证默认值
        assertEquals(3, config.get("maxRetryAttempts"), "默认最大重试次数应该是3");
        assertEquals(1800000L, config.get("processingTimeoutMs"), "默认处理超时时间应该是30分钟");
        assertEquals(300000L, config.get("checkIntervalMs"), "默认检查间隔应该是5分钟");
        
        System.out.println("默认配置值测试通过: " + config);
    }
}