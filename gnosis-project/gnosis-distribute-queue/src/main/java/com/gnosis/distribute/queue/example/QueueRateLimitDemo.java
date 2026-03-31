package com.gnosis.distribute.queue.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 队列限流演示主类
 * 展示如何使用队列控制外部接口调用和第三方系统接入的QPS
 */
@Component
@Order(Integer.MIN_VALUE)
@Lazy
public class QueueRateLimitDemo implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(QueueRateLimitDemo.class);
    
    @Autowired
    private ExternalApiRateLimitExample externalApiExample;
    
    @Autowired
    private ThirdPartyIngressControlExample thirdPartyExample;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("=== 开始队列限流演示 ===");
        
        // 1. 初始化两个限流队列
        log.info("\n1. 初始化限流队列...");
        externalApiExample.initializeExternalApiQueue();
        thirdPartyExample.initializeThirdPartyIngressQueue();
        
        Thread.sleep(2000); // 等待队列初始化完成
        
        // 2. 演示外部API调用限流
        log.info("\n2. 演示外部API调用限流...");
        demonstrateExternalApiLimiting();
        
        Thread.sleep(3000);
//
//        // 3. 演示第三方系统接入限流
//        log.info("\n3. 演示第三方系统接入限流...");
//        demonstrateThirdPartyIngressLimiting();
//
//        Thread.sleep(3000);
//
//        // 4. 演示动态调整限流策略
//        log.info("\n4. 演示动态调整限流策略...");
//        demonstrateDynamicRateAdjustment();
        
        log.info("\n=== 队列限流演示完成 ===");
    }
    
    /**
     * 演示外部API调用限流
     */
    private void demonstrateExternalApiLimiting() {
        log.info("开始外部API调用限流演示...");
        
        // 显示初始状态
        externalApiExample.printQueueStatus();
        
        // 批量测试限流效果
        externalApiExample.batchTestExternalApiLimit();
        
        // 显示测试后状态
        externalApiExample.printQueueStatus();
        
        log.info("外部API调用限流演示完成");
    }
    
    /**
     * 演示第三方系统接入限流
     */
    private void demonstrateThirdPartyIngressLimiting() {
        log.info("开始第三方系统接入限流演示...");
        
        // 显示初始状态
        thirdPartyExample.printIngressQueueStatus();
        
        // 批量测试限流效果
        thirdPartyExample.batchTestThirdPartyIngress();
        
        // 显示测试后状态
        thirdPartyExample.printIngressQueueStatus();
        
        log.info("第三方系统接入限流演示完成");
    }
    
    /**
     * 演示动态调整限流策略
     */
    private void demonstrateDynamicRateAdjustment() {
        log.info("开始动态调整限流策略演示...");
        
        // 1. 正常负载下的限流
        log.info("1. 正常负载限流策略:");
        externalApiExample.adjustExternalApiRate(50);
        thirdPartyExample.adjustThirdPartyIngressRate(200);
        
        // 2. 高负载时的限流调整
        log.info("2. 高负载限流调整:");
        externalApiExample.adjustExternalApiRate(30);
        thirdPartyExample.adjustThirdPartyIngressRate(100);
        
        // 3. 紧急降级处理
        log.info("3. 紧急降级处理:");
        thirdPartyExample.emergencyDegradation();
        
        // 4. 系统恢复后的处理
        log.info("4. 系统恢复处理:");
        thirdPartyExample.recoveryToNormal();
        
        log.info("动态调整限流策略演示完成");
    }
}