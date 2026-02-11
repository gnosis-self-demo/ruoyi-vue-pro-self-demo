package gnosis.sample.distribute.queue.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 运行时队列配置管理器
 * 支持动态调整队列最大长度和QPS限制
 */
@Component
@Getter
@Setter
public class RuntimeQueueConfig {

    // 队列最大长度配置映射
    private final ConcurrentHashMap<String, Integer> maxLengthMap = new ConcurrentHashMap<>();
    
    // 队列最大QPS配置映射
    private final ConcurrentHashMap<String, Integer> maxQpsMap = new ConcurrentHashMap<>();
    
    // 空队列轮询间隔（毫秒）
    private volatile long emptyPollIntervalMs = 1000L;

    // 死信队列相关配置
    private final ConcurrentHashMap<String, Integer> maxRetryAttemptsMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> processingTimeoutMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Long> deadLetterCheckIntervalMap = new ConcurrentHashMap<>();

    /**
     * 获取指定队列的最大长度
     * @param queueName 队列名称
     * @return 最大长度，-1表示无限制
     */
    public int getMaxQueueLength(String queueName) {
        Integer max = maxLengthMap.get(queueName);
        return max != null ? max : -1;
    }

    /**
     * 设置队列最大长度
     * @param queueName 队列名称
     * @param maxLength 最大长度，<=0表示无限制
     */
    public void setMaxQueueLength(String queueName, int maxLength) {
        if (maxLength <= 0) {
            maxLengthMap.remove(queueName);
        } else {
            maxLengthMap.put(queueName, maxLength);
        }
    }

    /**
     * 获取所有队列长度配置
     * @return 配置映射副本
     */
    public Map<String, Integer> getMaxLengthMap() {
        return new ConcurrentHashMap<>(maxLengthMap);
    }

    /**
     * 获取指定队列的最大QPS
     * @param queueName 队列名称
     * @return 最大QPS，-1表示无限制
     */
    public int getMaxQps(String queueName) {
        Integer qps = maxQpsMap.get(queueName);
        return qps != null ? qps : -1;
    }

    /**
     * 设置队列最大QPS
     * @param queueName 队列名称
     * @param maxQps 最大QPS，<=0表示无限制
     */
    public void setMaxQps(String queueName, int maxQps) {
        if (maxQps <= 0) {
            maxQpsMap.remove(queueName);
        } else {
            maxQpsMap.put(queueName, maxQps);
        }
    }

    /**
     * 获取所有队列QPS配置
     * @return 配置映射副本
     */
    public Map<String, Integer> getMaxQpsMap() {
        return new ConcurrentHashMap<>(maxQpsMap);
    }

    /**
     * 根据QPS计算处理完一个消息后需要休眠的时间
     * @param queueName 队列名称
     * @return 休眠毫秒数
     */
    public long getSleepMsAfterProcess(String queueName) {
        int qps = getMaxQps(queueName);
        if (qps <= 0) {
            return 0L;
        }
        return (long) Math.ceil(1000.0 / qps);
    }

    /**
     * 获取空队列轮询间隔
     * @return 轮询间隔毫秒数
     */
    public long getEmptyPollIntervalMs() {
        return emptyPollIntervalMs;
    }

    /**
     * 设置空队列轮询间隔
     * @param intervalMs 轮询间隔毫秒数
     */
    public void setEmptyPollIntervalMs(long intervalMs) {
        if (intervalMs > 0) {
            this.emptyPollIntervalMs = intervalMs;
        }
    }

    /**
     * 重置指定队列的所有配置
     * @param queueName 队列名称
     */
    public void resetQueueConfig(String queueName) {
        maxLengthMap.remove(queueName);
        maxQpsMap.remove(queueName);
        maxRetryAttemptsMap.remove(queueName);
        processingTimeoutMap.remove(queueName);
        deadLetterCheckIntervalMap.remove(queueName);
    }

    /**
     * 获取所有配置信息
     * @return 包含所有配置的映射
     */
    public Map<String, Object> getAllConfigs() {
        Map<String, Object> configs = new ConcurrentHashMap<>();
        configs.put("max_length", getMaxLengthMap());
        configs.put("max_qps", getMaxQpsMap());
        configs.put("empty_poll_interval_ms", getEmptyPollIntervalMs());
        
        // 添加死信队列配置
        configs.put("max_retry_attempts", getMaxRetryAttemptsMap());
        configs.put("processing_timeout_ms", getProcessingTimeoutMap());
        configs.put("dead_letter_check_interval_ms", getDeadLetterCheckIntervalMap());
        
        return configs;
    }

    // 死信队列配置管理方法
    
    /**
     * 获取指定队列的最大重试次数
     * @param queueName 队列名称
     * @return 最大重试次数，默认3次
     */
    public int getMaxRetryAttempts(String queueName) {
        Integer attempts = maxRetryAttemptsMap.get(queueName);
        return attempts != null ? attempts : 3;
    }

    /**
     * 设置队列最大重试次数
     * @param queueName 队列名称
     * @param maxAttempts 最大重试次数
     */
    public void setMaxRetryAttempts(String queueName, int maxAttempts) {
        if (maxAttempts <= 0) {
            maxRetryAttemptsMap.remove(queueName);
        } else {
            maxRetryAttemptsMap.put(queueName, maxAttempts);
        }
    }

    /**
     * 获取所有重试次数配置
     * @return 配置映射副本
     */
    public Map<String, Integer> getMaxRetryAttemptsMap() {
        return new ConcurrentHashMap<>(maxRetryAttemptsMap);
    }

    /**
     * 获取指定队列的处理超时时间
     * @param queueName 队列名称
     * @return 处理超时时间(毫秒)，默认30分钟
     */
    public long getProcessingTimeoutMs(String queueName) {
        Long timeout = processingTimeoutMap.get(queueName);
        return timeout != null ? timeout : 1800000L; // 30分钟
    }

    /**
     * 设置队列处理超时时间
     * @param queueName 队列名称
     * @param timeoutMs 超时时间(毫秒)
     */
    public void setProcessingTimeoutMs(String queueName, long timeoutMs) {
        if (timeoutMs <= 0) {
            processingTimeoutMap.remove(queueName);
        } else {
            processingTimeoutMap.put(queueName, timeoutMs);
        }
    }

    /**
     * 获取所有处理超时配置
     * @return 配置映射副本
     */
    public Map<String, Long> getProcessingTimeoutMap() {
        return new ConcurrentHashMap<>(processingTimeoutMap);
    }

    /**
     * 获取指定队列的死信检查间隔
     * @param queueName 队列名称
     * @return 检查间隔(毫秒)，默认5分钟
     */
    public long getDeadLetterCheckIntervalMs(String queueName) {
        Long interval = deadLetterCheckIntervalMap.get(queueName);
        return interval != null ? interval : 300000L; // 5分钟
    }

    /**
     * 设置队列死信检查间隔
     * @param queueName 队列名称
     * @param intervalMs 检查间隔(毫秒)
     */
    public void setDeadLetterCheckIntervalMs(String queueName, long intervalMs) {
        if (intervalMs <= 0) {
            deadLetterCheckIntervalMap.remove(queueName);
        } else {
            deadLetterCheckIntervalMap.put(queueName, intervalMs);
        }
    }

    /**
     * 获取所有死信检查间隔配置
     * @return 配置映射副本
     */
    public Map<String, Long> getDeadLetterCheckIntervalMap() {
        return new ConcurrentHashMap<>(deadLetterCheckIntervalMap);
    }

    @Override
    public String toString() {
        return "RuntimeQueueConfig{" +
                "maxLengthMap=" + maxLengthMap +
                ", maxQpsMap=" + maxQpsMap +
                ", emptyPollIntervalMs=" + emptyPollIntervalMs +
                ", maxRetryAttemptsMap=" + maxRetryAttemptsMap +
                ", processingTimeoutMap=" + processingTimeoutMap +
                ", deadLetterCheckIntervalMap=" + deadLetterCheckIntervalMap +
                '}';
    }
}