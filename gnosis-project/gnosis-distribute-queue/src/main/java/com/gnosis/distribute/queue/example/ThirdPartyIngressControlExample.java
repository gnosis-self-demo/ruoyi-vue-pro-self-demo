package com.gnosis.distribute.queue.example;

import com.gnosis.distribute.queue.config.RuntimeQueueConfig;
import com.gnosis.distribute.queue.dto.request.TaskSubmitRequest;
import com.gnosis.distribute.queue.dto.response.TaskSubmitResponse;
import com.gnosis.distribute.queue.exception.QueueFullException;
import com.gnosis.distribute.queue.exception.QueueNotFoundException;
import com.gnosis.distribute.queue.factory.DistributedQueueFactory;
import com.gnosis.distribute.queue.model.QueueInstance;
import com.gnosis.distribute.queue.sdk.DistributedQueueSdk;
import com.gnosis.distribute.queue.sdk.TaskManagementService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 第三方系统接入限流示例
 * 演示如何创建队列来控制外部系统调用本系统的最大QPS
 */
@Component
public class ThirdPartyIngressControlExample {

    private static final Logger log = LoggerFactory.getLogger(ThirdPartyIngressControlExample.class);
    
    @Autowired
    private DistributedQueueFactory queueFactory;
    
    @Autowired
    private DistributedQueueSdk distributedQueueSdk;
    
    private static final String THIRD_PARTY_INGRESS_QUEUE = "thirdparty-ingress-control";
    private static final int MAX_INGRESS_QPS = 200;  // 控制第三方系统接入的最大QPS为200
    private static final int QUEUE_MAX_LENGTH = 5000; // 队列最大长度
    
    /**
     * 初始化第三方接入限流队列
     */
    public void initializeThirdPartyIngressQueue() {
        try {
            // 创建队列配置
            RuntimeQueueConfig config = new RuntimeQueueConfig();
            config.setMaxQueueLength(THIRD_PARTY_INGRESS_QUEUE, QUEUE_MAX_LENGTH);
            config.setMaxQps(THIRD_PARTY_INGRESS_QUEUE, MAX_INGRESS_QPS);
            config.setEmptyPollIntervalMs(200L);
            
            // 创建队列实例
            QueueInstance instance = queueFactory.createQueueInstance(THIRD_PARTY_INGRESS_QUEUE, config);
            log.info("第三方接入限流队列创建成功: {}, 最大QPS: {}, 队列长度: {}", 
                    THIRD_PARTY_INGRESS_QUEUE, MAX_INGRESS_QPS, QUEUE_MAX_LENGTH);
                    
        } catch (IllegalArgumentException e) {
            log.warn("队列已存在: {}", e.getMessage());
        } catch (Exception e) {
            log.error("创建第三方接入限流队列失败", e);
        }
    }
    
    /**
     * 处理来自电商平台的订单同步请求
     */
    public void handleEcommerceOrderSync(String partnerId, String batchId, int orderCount) {
        try {
            // 构造订单同步数据
            Map<String, Object> orderData = new HashMap<String, Object>();
            orderData.put("partnerId", partnerId);
            orderData.put("batchId", batchId);
            orderData.put("orderCount", orderCount);
            orderData.put("requestType", "ORDER_SYNC");
            orderData.put("timestamp", System.currentTimeMillis());
            orderData.put("priority", "HIGH");
            
            // 提交到限流队列
            TaskSubmitRequest request = new TaskSubmitRequest(
                THIRD_PARTY_INGRESS_QUEUE, 
                orderData.toString()
            );
            
            TaskSubmitResponse response = distributedQueueSdk.getTaskManagementService()
                .submitTask(request);
                
            log.info("电商平台订单同步请求已提交到限流队列: partner={}, batch={}, orders={}, queue={}", 
                    partnerId, batchId, orderCount, THIRD_PARTY_INGRESS_QUEUE);
                    
        } catch (QueueNotFoundException e) {
            log.error("队列不存在: {}", THIRD_PARTY_INGRESS_QUEUE);
        } catch (QueueFullException e) {
            log.warn("队列已满，请求被拒绝: partner={}, currentSize={}, maxSize={}", 
                    partnerId, e.getCurrentSize(), e.getMaxSize());
        } catch (Exception e) {
            log.error("处理订单同步请求失败: partner=" + partnerId, e);
        }
    }
    
    /**
     * 处理库存更新请求
     */
    public void handleInventoryUpdate(String productId, int newStock, String sourceSystem) {
        try {
            // 构造库存更新数据
            Map<String, Object> inventoryData = new HashMap<String, Object>();
            inventoryData.put("productId", productId);
            inventoryData.put("newStock", newStock);
            inventoryData.put("sourceSystem", sourceSystem);
            inventoryData.put("requestType", "INVENTORY_UPDATE");
            inventoryData.put("timestamp", System.currentTimeMillis());
            inventoryData.put("criticalLevel", newStock < 100 ? "HIGH" : "NORMAL");
            
            // 提交到限流队列（同步处理重要库存更新）
            TaskSubmitRequest request = new TaskSubmitRequest(
                THIRD_PARTY_INGRESS_QUEUE, 
                inventoryData.toString(),
                10000L  // 10秒超时
            );
            
            TaskSubmitResponse response = distributedQueueSdk.getTaskManagementService()
                .submitTaskSync(request);
                
            log.info("库存更新请求已同步处理: product={}, stock={}, source={}, result={}", 
                    productId, newStock, sourceSystem, response != null ? "SUCCESS" : "FAILED");
                    
        } catch (Exception e) {
            log.error("处理库存更新请求失败: product=" + productId, e);
        }
    }
    
    /**
     * 处理用户数据同步请求
     */
    public void handleUserDataSync(String partnerId, int userCount) {
        try {
            // 构造用户数据同步请求
            Map<String, Object> userData = new HashMap<String, Object>();
            userData.put("partnerId", partnerId);
            userData.put("userCount", userCount);
            userData.put("requestType", "USER_DATA_SYNC");
            userData.put("syncType", "INCREMENTAL");
            userData.put("timestamp", System.currentTimeMillis());
            
            // 提交到限流队列
            TaskSubmitRequest request = new TaskSubmitRequest(
                THIRD_PARTY_INGRESS_QUEUE, 
                userData.toString()
            );
            
            TaskSubmitResponse response = distributedQueueSdk.getTaskManagementService()
                .submitTask(request);
                
            log.info("用户数据同步请求已提交到限流队列: partner={}, users={}, queue={}", 
                    partnerId, userCount, THIRD_PARTY_INGRESS_QUEUE);
                    
        } catch (Exception e) {
            log.error("处理用户数据同步请求失败: partner=" + partnerId, e);
        }
    }
    
    /**
     * 批量测试第三方系统接入限流效果
     */
    public void batchTestThirdPartyIngress() {
        log.info("开始批量测试第三方系统接入限流...");
        
        ExecutorService executor = Executors.newFixedThreadPool(50);
        
        // 模拟电商平台批量订单同步
        for (int i = 1; i <= 100; i++) {
            final int batchId = i;
            executor.submit(new Runnable() {
                public void run() {
                    handleEcommerceOrderSync("TAOBAO_PARTNER", "TB_BATCH_" + batchId, 50 + (batchId % 200));
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // 模拟库存管理系统更新请求
        for (int i = 1; i <= 200; i++) {
            final int productId = i;
            executor.submit(new Runnable() {
                public void run() {
                    handleInventoryUpdate("PRODUCT_" + productId, 1000 - (productId % 500), "WMS_SYSTEM");
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // 模拟用户数据同步
        for (int i = 1; i <= 50; i++) {
            final int partnerId = i;
            executor.submit(new Runnable() {
                public void run() {
                    handleUserDataSync("PARTNER_" + partnerId, 1000 + (partnerId * 50));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("第三方系统接入批量测试完成");
    }
    
    /**
     * 动态调整第三方接入限流策略
     */
    public void adjustThirdPartyIngressRate(int newMaxQps) {
        try {
            RuntimeQueueConfig config = queueFactory.getQueueInstance(THIRD_PARTY_INGRESS_QUEUE).getConfig();
            config.setMaxQps(THIRD_PARTY_INGRESS_QUEUE, newMaxQps);
            log.info("第三方接入限流队列QPS调整完成: {} -> {}", MAX_INGRESS_QPS, newMaxQps);
        } catch (Exception e) {
            log.error("调整第三方接入限流参数失败", e);
        }
    }
    
    /**
     * 系统压力过大时的紧急降级处理
     */
    public void emergencyDegradation() {
        log.warn("系统检测到高负载，执行紧急降级策略");
        adjustThirdPartyIngressRate(50); // 降低到50 QPS
        log.info("紧急降级完成，第三方接入QPS已降至50");
    }
    
    /**
     * 系统恢复正常后的恢复处理
     */
    public void recoveryToNormal() {
        log.info("系统负载恢复正常，恢复标准限流策略");
        adjustThirdPartyIngressRate(MAX_INGRESS_QPS); // 恢复到正常水平
        log.info("限流策略已恢复至正常水平: {} QPS", MAX_INGRESS_QPS);
    }
    
    /**
     * 获取队列状态和监控信息
     */
    public void printIngressQueueStatus() {
        try {
            TaskManagementService taskService = distributedQueueSdk.getTaskManagementService();
            // 这里可以添加获取队列状态的具体实现
            log.info("第三方接入队列状态信息获取完成");
        } catch (Exception e) {
            log.error("获取第三方接入队列状态失败", e);
        }
    }
}