package gnosis.sample.distribute.queue.example;

import gnosis.sample.distribute.queue.config.RuntimeQueueConfig;
import gnosis.sample.distribute.queue.dto.request.TaskSubmitRequest;
import gnosis.sample.distribute.queue.dto.response.TaskSubmitResponse;
import gnosis.sample.distribute.queue.exception.QueueFullException;
import gnosis.sample.distribute.queue.exception.QueueNotFoundException;
import gnosis.sample.distribute.queue.factory.DistributedQueueFactory;
import gnosis.sample.distribute.queue.model.QueueInstance;
import gnosis.sample.distribute.queue.sdk.DistributedQueueSdk;
import gnosis.sample.distribute.queue.sdk.TaskManagementService;
import gnosis.sample.distribute.queue.sdk.impl.DistributedQueueSdkImpl;
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
 * 外部API调用限流示例
 * 演示如何创建队列来控制调用外部接口的最大QPS
 */
@Component
public class ExternalApiRateLimitExample {

    private static final Logger log = LoggerFactory.getLogger(ExternalApiRateLimitExample.class);
    
    @Autowired
    private DistributedQueueFactory queueFactory;
    
    @Autowired
    private DistributedQueueSdk distributedQueueSdk;
    
    private static final String EXTERNAL_API_QUEUE = "external-api-rate-limit";
    private static final int MAX_EXTERNAL_QPS = 20;  // 控制对外部API的最大QPS为50
    private static final int QUEUE_MAX_LENGTH = 10; // 队列最大长度
    
    /**
     * 初始化外部API限流队列
     */
    public void initializeExternalApiQueue() {
        try {
            // 创建队列配置
            RuntimeQueueConfig config = new RuntimeQueueConfig();
            config.setMaxQueueLength(EXTERNAL_API_QUEUE, QUEUE_MAX_LENGTH);
            config.setMaxQps(EXTERNAL_API_QUEUE, MAX_EXTERNAL_QPS);
            config.setEmptyPollIntervalMs(1000L);
            
            // 创建队列实例
            QueueInstance instance = queueFactory.createQueueInstance(EXTERNAL_API_QUEUE, config);
            log.info("外部API限流队列创建成功: {}, 最大QPS: {}, 队列长度: {}", 
                    EXTERNAL_API_QUEUE, MAX_EXTERNAL_QPS, QUEUE_MAX_LENGTH);
                    
        } catch (IllegalArgumentException e) {
            log.warn("队列已存在: {}", e.getMessage());
        } catch (Exception e) {
            log.error("创建外部API限流队列失败", e);
        }
    }
    
    /**
     * 模拟向外部支付网关发送请求
     */
    public void sendPaymentRequest(String orderId, double amount) {
        try {
            // 构造支付请求数据
            Map<String, Object> paymentData = new HashMap<String, Object>();
            paymentData.put("orderId", orderId);
            paymentData.put("amount", amount);
            paymentData.put("currency", "CNY");
            paymentData.put("timestamp", System.currentTimeMillis());
            paymentData.put("apiEndpoint", "https://payment-gateway.example.com/api/v1/charge");
            
            // 提交到限流队列
            TaskSubmitRequest request = new TaskSubmitRequest(
                EXTERNAL_API_QUEUE, 
                paymentData.toString()
            );
            
            TaskSubmitResponse response = distributedQueueSdk.getTaskManagementService()
                .submitTaskSync(request);
                
            log.info("支付请求已提交到限流队列: orderId={}, amount={}, queue={}", 
                    orderId, amount, EXTERNAL_API_QUEUE);
                    
        } catch (QueueNotFoundException e) {
            log.error("队列不存在: {}", EXTERNAL_API_QUEUE);
        } catch (QueueFullException e) {
            log.warn("队列已满，请求被拒绝: orderId={}, currentSize={}, maxSize={}", 
                    orderId, e.getCurrentSize(), e.getMaxSize());
        } catch (Exception e) {
            log.error("提交支付请求失败: orderId=" + orderId, e);
        }
    }
    
    /**
     * 模拟向外部短信服务发送请求
     */
    public void sendSmsRequest(String phone, String message) {
        try {
            // 构造短信请求数据
            Map<String, Object> smsData = new HashMap<String, Object>();
            smsData.put("phone", phone);
            smsData.put("message", message);
            smsData.put("templateId", "VERIFY_CODE");
            smsData.put("timestamp", System.currentTimeMillis());
            smsData.put("apiEndpoint", "https://sms-service.example.com/api/v1/send");
            
            // 提交到限流队列
            TaskSubmitRequest request = new TaskSubmitRequest(
                EXTERNAL_API_QUEUE, 
                smsData.toString()
            );
            
            TaskSubmitResponse response = distributedQueueSdk.getTaskManagementService()
                .submitTaskSync(request);
                
            log.info("短信请求已提交到限流队列: phone={}, message={}, queue={}", 
                    phone, message, EXTERNAL_API_QUEUE);
                    
        } catch (Exception e) {
            log.error("提交短信请求失败: phone=" + phone, e);
        }
    }
    
    /**
     * 批量测试外部API限流效果
     */
    public void batchTestExternalApiLimit() {
        log.info("开始批量测试外部API限流...");
        
        ExecutorService executor = Executors.newFixedThreadPool(20);
        
        // 模拟短时间内大量支付请求
        for (int i = 1; i <= 200; i++) {
            final int requestId = i;
            executor.submit(new Runnable() {
                public void run() {
                    sendPaymentRequest("ORDER_" + requestId, 99.99);
                    try {
                        Thread.sleep(50); // 模拟业务处理间隔
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        // 模拟短信发送请求
        for (int i = 1; i <= 100; i++) {
            final int requestId = i;
            executor.submit(new Runnable() {
                public void run() {
                    sendSmsRequest("1380013800" + (requestId % 10), 
                                 "您的验证码是: " + (100000 + requestId));
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        log.info("批量测试完成");
    }
    
    /**
     * 动态调整外部API调用频率
     */
    public void adjustExternalApiRate(int newMaxQps) {
        try {
            RuntimeQueueConfig config = queueFactory.getQueueInstance(EXTERNAL_API_QUEUE).getConfig();
            config.setMaxQps(EXTERNAL_API_QUEUE, newMaxQps);
            log.info("外部API限流队列QPS调整完成: {} -> {}", MAX_EXTERNAL_QPS, newMaxQps);
        } catch (Exception e) {
            log.error("调整外部API限流参数失败", e);
        }
    }
    
    /**
     * 获取队列状态信息
     */
    public void printQueueStatus() {
        try {
            TaskManagementService taskService = distributedQueueSdk.getTaskManagementService();
            // 这里可以添加获取队列状态的具体实现
            log.info("队列状态信息获取完成");
        } catch (Exception e) {
            log.error("获取队列状态失败", e);
        }
    }
}