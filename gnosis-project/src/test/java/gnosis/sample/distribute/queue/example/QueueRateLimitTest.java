package gnosis.sample.distribute.queue.example;

import gnosis.sample.distribute.queue.example.ExternalApiRateLimitExample;
import gnosis.sample.distribute.queue.example.ThirdPartyIngressControlExample;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 队列限流功能测试类
 */
@SpringBootTest
public class QueueRateLimitTest {

    @Autowired
    private ExternalApiRateLimitExample externalApiExample;
    
    @Autowired
    private ThirdPartyIngressControlExample thirdPartyExample;
    
    @Test
    public void testExternalApiRateLimitInitialization() {
        // 测试外部API限流队列初始化
        externalApiExample.initializeExternalApiQueue();
        
        // 测试发送单个支付请求
        externalApiExample.sendPaymentRequest("TEST_ORDER_001", 99.99);
        
        // 测试发送单个短信请求
        externalApiExample.sendSmsRequest("13800138000", "测试验证码: 123456");
        
        System.out.println("外部API限流测试完成");
    }
    
    @Test
    public void testThirdPartyIngressInitialization() {
        // 测试第三方接入限流队列初始化
        thirdPartyExample.initializeThirdPartyIngressQueue();
        
        // 测试处理订单同步请求
        thirdPartyExample.handleEcommerceOrderSync("TEST_PARTNER", "TEST_BATCH_001", 10);
        
        // 测试处理库存更新请求
        thirdPartyExample.handleInventoryUpdate("TEST_PRODUCT_001", 999, "TEST_WMS");
        
        // 测试处理用户数据同步请求
        thirdPartyExample.handleUserDataSync("TEST_PARTNER_001", 1000);
        
        System.out.println("第三方接入限流测试完成");
    }
    
    @Test
    public void testRateAdjustment() {
        // 测试动态调整限流参数
        externalApiExample.adjustExternalApiRate(30);
        thirdPartyExample.adjustThirdPartyIngressRate(150);
        
        // 测试紧急降级
        thirdPartyExample.emergencyDegradation();
        
        // 测试恢复正常
        thirdPartyExample.recoveryToNormal();
        
        System.out.println("限流参数调整测试完成");
    }
}