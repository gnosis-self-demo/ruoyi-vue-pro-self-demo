package gnosis.sample.config;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 运行时队列配置管理器
 * 支持动态调整队列最大长度和QPS限制
 */
@Component
public class RuntimeQueueConfig {

    // 队列最大长度配置映射
    private final ConcurrentHashMap<String, Integer> maxLengthMap = new ConcurrentHashMap<>();
    
    // 队列最大QPS配置映射
    private final ConcurrentHashMap<String, Integer> maxQpsMap = new ConcurrentHashMap<>();
    
    // 空队列轮询间隔（毫秒）
    private volatile long emptyPollIntervalMs = 1000L;

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
        return configs;
    }

    @Override
    public String toString() {
        return "RuntimeQueueConfig{" +
                "maxLengthMap=" + maxLengthMap +
                ", maxQpsMap=" + maxQpsMap +
                ", emptyPollIntervalMs=" + emptyPollIntervalMs +
                '}';
    }
}