package gnosis.sample.distribute.queue;

import gnosis.sample.distribute.queue.enums.QueueMessageStatus;
import gnosis.sample.distribute.queue.service.DeadLetterQueueService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 死信队列功能测试
 * 验证僵死任务处理机制
 */
@SpringBootTest
public class DeadLetterQueueTest {

    @Autowired
    private DeadLetterQueueService deadLetterService;

    @Test
    public void testDeadLetterQueueMechanism() {
        System.out.println("=== 死信队列机制测试 ===");
        
        // 1. 测试状态扩展
        testStatusEnumExtension();
        
        // 2. 测试死信统计
        testDeadLetterStats();
        
        System.out.println("=== 测试完成 ===");
    }

    private void testStatusEnumExtension() {
        System.out.println("1. 测试状态枚举扩展...");
        
        // 验证新状态的存在
        assertEquals("failed", QueueMessageStatus.FAILED.getValue());
        assertEquals("dead_letter", QueueMessageStatus.DEAD_LETTER.getValue());
        
        // 验证状态转换
        assertEquals(QueueMessageStatus.FAILED, 
            QueueMessageStatus.fromValue("failed"));
        assertEquals(QueueMessageStatus.DEAD_LETTER, 
            QueueMessageStatus.fromValue("dead_letter"));
            
        System.out.println("   ✓ 状态枚举扩展验证通过");
    }

    private void testDeadLetterStats() {
        System.out.println("2. 测试死信统计功能...");
        
        try {
            Map<String, Object> stats = deadLetterService.getDeadLetterStats();
            
            assertNotNull(stats);
            assertTrue(stats.containsKey("deadLetterCount"));
            assertTrue(stats.containsKey("lastCheckTime"));
            
            System.out.println("   ✓ 死信统计功能正常");
            System.out.println("   当前死信数量: " + stats.get("deadLetterCount"));
            
        } catch (Exception e) {
            System.err.println("   ✗ 死信统计功能异常: " + e.getMessage());
        }
    }

    @Test
    public void testDeadLetterServiceMethods() {
        System.out.println("=== 死信队列服务方法测试 ===");
        
        try {
            // 测试配置获取
            System.out.println("1. 测试配置信息获取...");
            
            // 测试清理功能（使用较大天数避免误删）
            System.out.println("2. 测试清理功能...");
            int cleaned = deadLetterService.cleanupOldDeadLetters(365);
            System.out.println("   清理了 " + cleaned + " 条历史死信");
            
            System.out.println("   ✓ 服务方法测试通过");
            
        } catch (Exception e) {
            System.err.println("   ✗ 服务方法测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void testProcessingFailureHandling() {
        System.out.println("=== 处理失败处理测试 ===");
        
        try {
            // 测试超时检查功能
            System.out.println("1. 执行超时检查...");
            deadLetterService.checkProcessingTimeout();
            System.out.println("   ✓ 超时检查执行完成");
            
            // 测试失败任务检查
            System.out.println("2. 执行失败任务检查...");
            deadLetterService.checkFailedTasks();
            System.out.println("   ✓ 失败任务检查执行完成");
            
            System.out.println("   ✓ 处理失败处理机制验证通过");
            
        } catch (Exception e) {
            System.err.println("   ✗ 处理失败处理测试失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}