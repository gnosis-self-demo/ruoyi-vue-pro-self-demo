package com.example.demo;

import com.example.demo.util.IdGenerator;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * 基础功能测试
 */
public class BasicFunctionalityTest {

    @Test
    public void testIdGenerator() {
        // 测试ID生成器
        long id1 = IdGenerator.nextId();
        long id2 = IdGenerator.nextId();
        
        // 验证ID唯一性
        assertNotEquals("Generated IDs should be unique", id1, id2);
        
        // 验证ID递增性
        assertTrue("Second ID should be greater than first", id2 > id1);
        
        System.out.println("Generated IDs: " + id1 + ", " + id2);
    }

    @Test
    public void testIdTimestampParsing() {
        long id = IdGenerator.nextId();
        long timestamp = IdGenerator.parseTimestamp(id);
        long currentTimestamp = IdGenerator.getCurrentTimestamp();
        
        // 验证时间戳解析合理性
        assertTrue("Parsed timestamp should be reasonable", 
                  timestamp > 1700000000000L && timestamp <= currentTimestamp);
        
        System.out.println("Original ID: " + id);
        System.out.println("Parsed timestamp: " + timestamp);
        System.out.println("Current timestamp: " + currentTimestamp);
    }
}