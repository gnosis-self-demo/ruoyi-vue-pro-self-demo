package lifecycle.util;

import org.springframework.stereotype.Component;

/**
 * ID 生成器 - Snowflake 算法简化版
 * 生成全局唯一的 64 位 ID
 */
@Component
public class IdGenerator {
    // 起始时间戳 (2023-11-15)
    private static final long START_TIME = 1700000000000L;
    
    // 序列号占用的位数
    private static final long SEQUENCE_BITS = 10L;
    
    // 最大序列号
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    // 时间戳左移位数
    private static final long TIMESTAMP_LEFT_SHIFT = SEQUENCE_BITS;
    
    // 上一次生成 ID 的时间戳
    private static volatile long lastTimestamp = -1L;
    
    // 序列号
    private static long sequence = 0L;

    /**
     * 生成下一个唯一 ID（字符串格式）
     * @return 唯一 ID
     */
    public synchronized String generate() {
        return String.valueOf(nextId());
    }

    /**
     * 生成下一个唯一 ID（长整型）
     * @return 唯一 ID
     */
    public static synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        
        // 如果当前时间小于上一次 ID 生成时间，说明系统时钟回退了
        if (timestamp < lastTimestamp) {
            try {
                // 等待时钟追上
                Thread.sleep(lastTimestamp - timestamp);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            timestamp = System.currentTimeMillis();
        }
        
        // 如果是同一毫秒内生成 ID
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号溢出，等待下一毫秒
            if (sequence == 0) {
                while (timestamp <= lastTimestamp) {
                    timestamp = System.currentTimeMillis();
                }
            }
        } else {
            // 新的毫秒开始，序列号重置为 0
            sequence = 0;
        }
        
        lastTimestamp = timestamp;
        
        // 组合 ID：时间戳部分 + 序列号部分
        return ((timestamp - START_TIME) << TIMESTAMP_LEFT_SHIFT) | sequence;
    }
    
    /**
     * 获取当前时间戳
     * @return 当前时间戳
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * 从 ID 中解析出时间戳
     * @param id 生成的 ID
     * @return 时间戳
     */
    public static long parseTimestamp(long id) {
        return (id >> TIMESTAMP_LEFT_SHIFT) + START_TIME;
    }
}
