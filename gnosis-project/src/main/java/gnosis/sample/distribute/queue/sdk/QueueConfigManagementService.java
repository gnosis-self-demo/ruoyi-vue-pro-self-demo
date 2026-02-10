package gnosis.sample.distribute.queue.sdk;

import java.util.Map;

/**
 * 队列配置管理服务接口
 * 支持运行时动态调整队列配置
 */
public interface QueueConfigManagementService {

    /**
     * 获取所有队列配置
     * 
     * @return 所有队列的配置信息
     */
    Map<String, Object> getAllConfigs();

    /**
     * 获取指定队列的配置
     * 
     * @param queueName 队列名称
     * @return 队列配置信息
     */
    Map<String, Object> getQueueConfig(String queueName);

    /**
     * 设置队列最大长度
     * 
     * @param queueName 队列名称
     * @param maxLength 最大长度
     * @return 设置结果
     * @throws IllegalArgumentException 参数无效异常
     * @throws ConfigurationException 配置失败异常
     */
    Map<String, Object> setMaxLength(String queueName, int maxLength) 
        throws IllegalArgumentException, ConfigurationException;

    /**
     * 设置队列最大QPS
     * 
     * @param queueName 队列名称
     * @param maxQps 最大QPS
     * @return 设置结果
     * @throws IllegalArgumentException 参数无效异常
     * @throws ConfigurationException 配置失败异常
     */
    Map<String, Object> setMaxQps(String queueName, int maxQps) 
        throws IllegalArgumentException, ConfigurationException;

    /**
     * 设置空队列轮询间隔
     * 
     * @param intervalMs 轮询间隔(毫秒)
     * @return 设置结果
     * @throws IllegalArgumentException 参数无效异常
     * @throws ConfigurationException 配置失败异常
     */
    Map<String, Object> setEmptyPollInterval(long intervalMs) 
        throws IllegalArgumentException, ConfigurationException;

    /**
     * 重置指定队列的所有配置为默认值
     * 
     * @param queueName 队列名称
     * @return 重置结果
     * @throws ConfigurationException 配置失败异常
     */
    Map<String, Object> resetQueueConfig(String queueName) 
        throws ConfigurationException;

    /**
     * 批量设置多个队列配置
     * 
     * @param configs 批量配置，格式为 {queueName: {maxLength: value, maxQps: value}}
     * @return 批量设置结果
     * @throws ConfigurationException 配置失败异常
     */
    Map<String, Object> batchSetConfig(Map<String, Map<String, Integer>> configs) 
        throws ConfigurationException;

    // 自定义异常类
    class ConfigurationException extends Exception {
        public ConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}