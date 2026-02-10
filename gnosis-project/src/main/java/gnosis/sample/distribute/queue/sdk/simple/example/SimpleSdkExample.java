package gnosis.sample.distribute.queue.sdk.simple.example;

import gnosis.sample.distribute.queue.dto.request.TaskSubmitRequest;
import gnosis.sample.distribute.queue.dto.response.CommonResponse;
import gnosis.sample.distribute.queue.dto.response.TaskSubmitResponse;
import gnosis.sample.distribute.queue.sdk.simple.SimpleDistributedQueueSdk;

import java.util.HashMap;
import java.util.Map;

/**
 * 简化版SDK使用示例
 * 演示如何使用独立的SDK接口
 */
public class SimpleSdkExample {
    
    public static void main(String[] args) {
        // 创建SDK实例
        SimpleDistributedQueueSdk sdk = new SimpleDistributedQueueSdk();
        
        System.out.println("=== 分布式队列SDK使用示例 ===\n");
        
        try {
            // 1. 健康检查
            demonstrateHealthCheck(sdk);
            
            // 2. 队列实例管理
            demonstrateQueueInstanceManagement(sdk);
            
            // 3. 任务管理
            demonstrateTaskManagement(sdk);
            
            // 4. 配置管理
            demonstrateConfigManagement(sdk);
            
            // 5. 批量操作
            demonstrateBatchOperations(sdk);
            
        } catch (Exception e) {
            System.err.println("SDK调用出错: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void demonstrateHealthCheck(SimpleDistributedQueueSdk sdk) {
        System.out.println("--- 健康检查 ---");
        CommonResponse<CommonResponse.CommonData> health = sdk.getTaskManagementService().healthCheck();
        System.out.println("服务状态: " + health);
        System.out.println();
    }
    
    private static void demonstrateQueueInstanceManagement(SimpleDistributedQueueSdk sdk) 
            throws Exception {
        System.out.println("--- 队列实例管理 ---");
        
        // 创建队列实例
        Map<String, Object> config = new HashMap<String, Object>();
        config.put("maxLength", 1000);
        config.put("maxQps", 100);
        
        Map<String, Object> createResult = sdk.getQueueInstanceManagementService()
            .createQueueInstance("test-queue", config);
        System.out.println("创建队列结果: " + createResult);
        
        // 获取队列信息
        Map<String, Object> queueInfo = sdk.getQueueInstanceManagementService()
            .getQueueInstance("test-queue");
        System.out.println("队列信息: " + queueInfo);
        
        // 获取所有队列
        Map<String, Object> allQueues = sdk.getQueueInstanceManagementService()
            .getAllQueueInstances();
        System.out.println("所有队列: " + allQueues);
        
        System.out.println();
    }
    
    private static void demonstrateTaskManagement(SimpleDistributedQueueSdk sdk) 
            throws Exception {
        System.out.println("--- 任务管理 ---");
        
        // 异步提交任务
        TaskSubmitRequest asyncRequest = new TaskSubmitRequest("test-queue", "Hello World Async!");
        TaskSubmitResponse asyncResult = sdk.getTaskManagementService().submitTask(asyncRequest);
        System.out.println("异步任务结果: " + asyncResult);
        
        // 同步提交任务
        TaskSubmitRequest syncRequest = new TaskSubmitRequest("test-queue", "Hello World Sync!", 5000L);
        TaskSubmitResponse syncResult = sdk.getTaskManagementService().submitTaskSync(syncRequest);
        System.out.println("同步任务结果: " + syncResult);
        
        // 查询队列状态
        CommonResponse<CommonResponse.CommonData> status = sdk.getTaskManagementService()
            .getQueueStatus("test-queue");
        System.out.println("队列状态: " + status);
        
        System.out.println();
    }
    
    private static void demonstrateConfigManagement(SimpleDistributedQueueSdk sdk) 
            throws Exception {
        System.out.println("--- 配置管理 ---");
        
        // 获取所有配置
        Map<String, Object> allConfigs = sdk.getQueueConfigManagementService()
            .getAllConfigs();
        System.out.println("所有配置: " + allConfigs);
        
        // 获取特定队列配置
        Map<String, Object> queueConfig = sdk.getQueueConfigManagementService()
            .getQueueConfig("test-queue");
        System.out.println("队列配置: " + queueConfig);
        
        // 设置最大长度
        Map<String, Object> maxLengthResult = sdk.getQueueConfigManagementService()
            .setMaxLength("test-queue", 2000);
        System.out.println("设置最大长度结果: " + maxLengthResult);
        
        // 设置最大QPS
        Map<String, Object> maxQpsResult = sdk.getQueueConfigManagementService()
            .setMaxQps("test-queue", 200);
        System.out.println("设置最大QPS结果: " + maxQpsResult);
        
        System.out.println();
    }
    
    private static void demonstrateBatchOperations(SimpleDistributedQueueSdk sdk) 
            throws Exception {
        System.out.println("--- 批量操作 ---");
        
        // 批量创建队列
        Map<String, Map<String, Object>> batchConfig = new HashMap<String, Map<String, Object>>();
        
        Map<String, Object> queue1Config = new HashMap<String, Object>();
        queue1Config.put("maxLength", 500);
        queue1Config.put("maxQps", 50);
        batchConfig.put("batch-queue-1", queue1Config);
        
        Map<String, Object> queue2Config = new HashMap<String, Object>();
        queue2Config.put("maxLength", 1000);
        queue2Config.put("maxQps", 100);
        batchConfig.put("batch-queue-2", queue2Config);
        
        Map<String, Object> batchCreateResult = sdk.getQueueInstanceManagementService()
            .createMultipleInstances(batchConfig);
        System.out.println("批量创建结果: " + batchCreateResult);
        
        // 批量配置更新
        Map<String, Map<String, Integer>> batchConfigs = new HashMap<String, Map<String, Integer>>();
        
        Map<String, Integer> config1 = new HashMap<String, Integer>();
        config1.put("maxLength", 800);
        config1.put("maxQps", 80);
        batchConfigs.put("batch-queue-1", config1);
        
        Map<String, Integer> config2 = new HashMap<String, Integer>();
        config2.put("maxLength", 1200);
        config2.put("maxQps", 120);
        batchConfigs.put("batch-queue-2", config2);
        
        Map<String, Object> batchConfigResult = sdk.getQueueConfigManagementService()
            .batchSetConfig(batchConfigs);
        System.out.println("批量配置更新结果: " + batchConfigResult);
        
        System.out.println();
    }
}