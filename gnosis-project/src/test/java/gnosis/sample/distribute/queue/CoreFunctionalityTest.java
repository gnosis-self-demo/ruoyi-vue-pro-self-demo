package gnosis.sample.distribute.queue;

import gnosis.sample.distribute.queue.enums.QueueMessageStatus;
import gnosis.sample.distribute.queue.util.IdGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 核心功能单元测试（不依赖Spring上下文）
 */
public class CoreFunctionalityTest {

    @Test
    public void testIdGeneration() {
        // 测试ID生成器
        long id1 = IdGenerator.nextId();
        long id2 = IdGenerator.nextId();
        
        // 验证ID唯一性
        assertNotEquals(id1, id2, "Generated IDs should be unique");
        
        // 验证ID递增性
        assertTrue(id2 > id1, "Second ID should be greater than first");
        
        System.out.println("Generated IDs: " + id1 + ", " + id2);
    }

    @Test
    public void testIdTimestampParsing() {
        long id = IdGenerator.nextId();
        long timestamp = IdGenerator.parseTimestamp(id);
        long currentTimestamp = IdGenerator.getCurrentTimestamp();
        
        // 验证时间戳解析合理性
        assertTrue(timestamp > 1700000000000L && timestamp <= currentTimestamp, 
                  "Parsed timestamp should be reasonable");
        
        System.out.println("Original ID: " + id);
        System.out.println("Parsed timestamp: " + timestamp);
        System.out.println("Current timestamp: " + currentTimestamp);
    }

    @Test
    public void testQueueMessageStatusEnum() {
        // 测试枚举值
        assertEquals("pending", QueueMessageStatus.PENDING.getValue());
        assertEquals("processing", QueueMessageStatus.PROCESSING.getValue());
        assertEquals("done", QueueMessageStatus.DONE.getValue());
        assertEquals("failed", QueueMessageStatus.FAILED.getValue());
        assertEquals("dead_letter", QueueMessageStatus.DEAD_LETTER.getValue());
        
        // 测试从值获取枚举
        assertEquals(QueueMessageStatus.PENDING, QueueMessageStatus.fromValue("pending"));
        assertEquals(QueueMessageStatus.PROCESSING, QueueMessageStatus.fromValue("processing"));
        assertEquals(QueueMessageStatus.DONE, QueueMessageStatus.fromValue("done"));
        assertEquals(QueueMessageStatus.FAILED, QueueMessageStatus.fromValue("failed"));
        assertEquals(QueueMessageStatus.DEAD_LETTER, QueueMessageStatus.fromValue("dead_letter"));
        
        // 测试无效值处理
        assertThrows(IllegalArgumentException.class, () -> {
            QueueMessageStatus.fromValue("invalid");
        }, "Should throw exception for invalid value");
    }

    @Test
    public void testIdGeneratorEdgeCases() {
        // 测试序列号循环 - 通过快速生成多个ID来测试
        long[] ids = new long[20];
        for (int i = 0; i < 20; i++) {
            ids[i] = IdGenerator.nextId();
        }
        
        // 验证所有ID都是唯一的
        for (int i = 0; i < ids.length - 1; i++) {
            for (int j = i + 1; j < ids.length; j++) {
                assertNotEquals(ids[i], ids[j], "All IDs should be unique");
            }
        }
        
        // 验证时间戳解析的一致性
        for (long id : ids) {
            long timestamp = IdGenerator.parseTimestamp(id);
            assertTrue(timestamp >= 1700000000000L, "Parsed timestamp should be valid");
        }
        
        System.out.println("ID generator edge cases test passed");
    }
}