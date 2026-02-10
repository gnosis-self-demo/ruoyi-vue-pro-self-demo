package gnosis.sample.distribute.queue.sdk.example;

import gnosis.sample.distribute.queue.sdk.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 分布式队列SDK使用示例
 * 展示如何通过SDK接口调用队列服务功能
 */
@Component
public class DistributedQueueSdkExample {

    @Autowired
    private DistributedQueueSdk distributedQueueSdk;

    /**
     * 示例：创建队列实例并提交任务
     */
    public void demonstrateQueueUsage() {
        try {
            // 1. 创建队列实例
            Map<String, Object> configParams = new HashMap<>();
            configParams.put("maxLength", 1000);
            configParams.put("maxQps", 100);
            
            Map<String, Object> createResult = distributedQueueSdk
                .getQueueInstanceManagementService()
                .createQueueInstance("test-queue", configParams);
            
            System.out.println("队列创建结果: " + createResult);

            // 2. 异步提交任务
            Map<String, Object> asyncResult = distributedQueueSdk
                .getTaskManagementService()
                .submitTask("test-queue", "Hello World!");
            
            System.out.println("异步任务提交结果: " + asyncResult);

            // 3. 同步提交任务并等待结果
            Map<String, Object> syncResult = distributedQueueSdk
                .getTaskManagementService()
                .submitTaskSync("test-queue", "Hello Sync!", 5000);
            
            System.out.println("同步任务执行结果: " + syncResult);

            // 4. 获取队列状态
            Map<String, Object> status = distributedQueueSdk
                .getTaskManagementService()
                .getQueueStatus("test-queue");
            
            System.out.println("队列状态: " + status);

            // 5. 更新队列配置
            Map<String, Object> configUpdates = new HashMap<>();
            configUpdates.put("maxLength", 2000);
            configUpdates.put("maxQps", 200);
            
            Map<String, Object> updateResult = distributedQueueSdk
                .getQueueInstanceManagementService()
                .updateQueueConfig("test-queue", configUpdates);
            
            System.out.println("配置更新结果: " + updateResult);

        } catch (Exception e) {
            System.err.println("SDK调用出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 示例：批量操作
     */
    public void demonstrateBatchOperations() {
        try {
            // 批量创建队列
            Map<String, Map<String, Object>> batchConfig = new HashMap<>();
            
            Map<String, Object> queue1Config = new HashMap<>();
            queue1Config.put("maxLength", 500);
            queue1Config.put("maxQps", 50);
            batchConfig.put("batch-queue-1", queue1Config);
            
            Map<String, Object> queue2Config = new HashMap<>();
            queue2Config.put("maxLength", 1000);
            queue2Config.put("maxQps", 100);
            batchConfig.put("batch-queue-2", queue2Config);
            
            Map<String, Object> batchCreateResult = distributedQueueSdk
                .getQueueInstanceManagementService()
                .createMultipleInstances(batchConfig);
            
            System.out.println("批量创建结果: " + batchCreateResult);

            // 批量配置更新
            Map<String, Map<String, Integer>> batchConfigs = new HashMap<>();
            
            Map<String, Integer> queue1BatchConfig = new HashMap<>();
            queue1BatchConfig.put("maxLength", 800);
            queue1BatchConfig.put("maxQps", 80);
            batchConfigs.put("batch-queue-1", queue1BatchConfig);
            
            Map<String, Integer> queue2BatchConfig = new HashMap<>();
            queue2BatchConfig.put("maxLength", 1200);
            queue2BatchConfig.put("maxQps", 120);
            batchConfigs.put("batch-queue-2", queue2BatchConfig);
            
            Map<String, Object> batchConfigResult = distributedQueueSdk
                .getQueueConfigManagementService()
                .batchSetConfig(batchConfigs);
            
            System.out.println("批量配置更新结果: " + batchConfigResult);

        } catch (Exception e) {
            System.err.println("批量操作出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 示例：配置管理
     */
    public void demonstrateConfigManagement() {
        try {
            // 获取所有配置
            Map<String, Object> allConfigs = distributedQueueSdk
                .getQueueConfigManagementService()
                .getAllConfigs();
            
            System.out.println("所有队列配置: " + allConfigs);

            // 单独设置某个队列的配置
            Map<String, Object> singleConfigResult = distributedQueueSdk
                .getQueueConfigManagementService()
                .setMaxLength("test-queue", 1500);
            
            System.out.println("单个配置设置结果: " + singleConfigResult);

            // 重置配置
            Map<String, Object> resetResult = distributedQueueSdk
                .getQueueConfigManagementService()
                .resetQueueConfig("test-queue");
            
            System.out.println("配置重置结果: " + resetResult);

        } catch (Exception e) {
            System.err.println("配置管理出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 示例：健康检查
     */
    public void demonstrateHealthCheck() {
        try {
            Map<String, Object> healthStatus = distributedQueueSdk
                .getTaskManagementService()
                .healthCheck();
            
            System.out.println("服务健康状态: " + healthStatus);
        } catch (Exception e) {
            System.err.println("健康检查出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
}