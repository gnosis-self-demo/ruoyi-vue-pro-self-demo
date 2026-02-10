package gnosis.sample.distribute.queue.factory;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.consumer.QueueConsumer;
import gnosis.sample.distribute.queue.service.DistributedQueueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 分布式队列工厂
 * 支持创建和管理多个队列实例
 */
@Component
public class DistributedQueueFactory {

    @Autowired
    private DataSource dataSource;

    // 存储队列实例的映射
    private final ConcurrentHashMap<String, QueueInstance> queueInstances = new ConcurrentHashMap<>();

    /**
     * 队列实例包装类
     */
    public static class QueueInstance {
        private final String queueName;
        private final RuntimeQueueConfig config;
        private final DistributedQueueService service;
        private final QueueConsumer consumer;
        private final PlatformTransactionManager transactionManager;

        public QueueInstance(String queueName, RuntimeQueueConfig config, 
                           DistributedQueueService service, QueueConsumer consumer,
                           PlatformTransactionManager transactionManager) {
            this.queueName = queueName;
            this.config = config;
            this.service = service;
            this.consumer = consumer;
            this.transactionManager = transactionManager;
        }

        // getter方法
        public String getQueueName() { return queueName; }
        public RuntimeQueueConfig getConfig() { return config; }
        public DistributedQueueService getService() { return service; }
        public QueueConsumer getConsumer() { return consumer; }
        public PlatformTransactionManager getTransactionManager() { return transactionManager; }
    }

    /**
     * 创建新的队列实例
     * @param queueName 队列名称
     * @param config 队列配置
     * @return 队列实例
     */
    public QueueInstance createQueueInstance(String queueName, RuntimeQueueConfig config) {
        if (queueInstances.containsKey(queueName)) {
            throw new IllegalArgumentException("Queue instance '" + queueName + "' already exists");
        }

        // 创建事务管理器
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
        
        // 创建队列服务
        DistributedQueueService service = new DistributedQueueService();
        service.setDataSource(dataSource);
        service.setRuntimeConfig(config);
        
        // 创建消费者
        QueueConsumer consumer = new QueueConsumer();
        consumer.setQueueService(service);
        consumer.setRuntimeConfig(config);
        
        // 启动消费者
        consumer.startConsumer(queueName);
        
        // 创建实例
        QueueInstance instance = new QueueInstance(queueName, config, service, consumer, transactionManager);
        queueInstances.put(queueName, instance);
        
        System.out.println("Created queue instance: " + queueName);
        return instance;
    }

    /**
     * 获取已存在的队列实例
     * @param queueName 队列名称
     * @return 队列实例，不存在则返回null
     */
    public QueueInstance getQueueInstance(String queueName) {
        return queueInstances.get(queueName);
    }

    /**
     * 获取所有队列实例名称
     * @return 队列名称集合
     */
    public java.util.Set<String> getAllQueueNames() {
        return queueInstances.keySet();
    }

    /**
     * 删除队列实例
     * @param queueName 队列名称
     * @return 是否删除成功
     */
    public boolean removeQueueInstance(String queueName) {
        QueueInstance instance = queueInstances.remove(queueName);
        if (instance != null) {
            // 停止消费者线程
            instance.getConsumer().stopConsumer();
            System.out.println("Removed queue instance: " + queueName);
            return true;
        }
        return false;
    }

    /**
     * 创建默认配置的队列实例
     * @param queueName 队列名称
     * @return 队列实例
     */
    public QueueInstance createDefaultQueueInstance(String queueName) {
        RuntimeQueueConfig config = new RuntimeQueueConfig();
        // 设置默认配置
        config.setMaxQueueLength(queueName, 1000);  // 默认最大长度1000
        config.setMaxQps(queueName, 100);           // 默认QPS 100
        config.setEmptyPollIntervalMs(1000);        // 默认轮询间隔1秒
        
        return createQueueInstance(queueName, config);
    }

    /**
     * 批量创建队列实例
     * @param queueConfigs 队列配置映射
     */
    public void createMultipleQueueInstances(java.util.Map<String, RuntimeQueueConfig> queueConfigs) {
        for (java.util.Map.Entry<String, RuntimeQueueConfig> entry : queueConfigs.entrySet()) {
            createQueueInstance(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 获取队列实例统计信息
     * @return 统计信息
     */
    public java.util.Map<String, Object> getQueueStatistics() {
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalInstances", queueInstances.size());
        stats.put("queueNames", getAllQueueNames());
        
        java.util.Map<String, Object> configDetails = new java.util.HashMap<>();
        for (java.util.Map.Entry<String, QueueInstance> entry : queueInstances.entrySet()) {
            QueueInstance instance = entry.getValue();
            java.util.Map<String, Object> instanceConfig = new java.util.HashMap<>();
            instanceConfig.put("maxLength", instance.getConfig().getMaxQueueLength(entry.getKey()));
            instanceConfig.put("maxQps", instance.getConfig().getMaxQps(entry.getKey()));
            instanceConfig.put("pollInterval", instance.getConfig().getEmptyPollIntervalMs());
            configDetails.put(entry.getKey(), instanceConfig);
        }
        stats.put("configurations", configDetails);
        
        return stats;
    }
}