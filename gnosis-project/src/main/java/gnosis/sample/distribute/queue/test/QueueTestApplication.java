package gnosis.sample.distribute.queue.test;

import gnosis.sample.distribute.queue.factory.DistributedQueueFactory;
import gnosis.sample.distribute.queue.processor.MessageProcessorManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Scanner;

/**
 * 分布式队列测试启动类
 * 提供交互式测试界面
 */
@Slf4j
@SpringBootApplication(scanBasePackages = "gnosis.sample.distribute.queue")
@EnableScheduling
@RequiredArgsConstructor
public class QueueTestApplication {

    private final DistributedQueueFactory queueFactory;
    private final MessageProcessorManager processorManager;

    public static void main(String[] args) {
        SpringApplication.run(QueueTestApplication.class, args);
    }

    @Bean
    public CommandLineRunner testRunner() {
        return args -> {
            log.info("=== 分布式队列系统测试程序 ===");
            log.info("系统已启动，正在初始化...");
            
            // 等待系统初始化完成
            Thread.sleep(2000);
            
            // 初始化处理器管理器
            processorManager.init();
            
            log.info("已注册的处理器: {}", processorManager.getRegisteredProcessors());
            log.info("已创建的队列实例: {}", queueFactory.getAllQueueNames());
            
            // 启动交互式测试
            startInteractiveTest();
        };
    }

    /**
     * 启动交互式测试界面
     */
    private void startInteractiveTest() {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            try {
                showMenu();
                String choice = scanner.nextLine().trim();
                
                switch (choice) {
                    case "1":
                        testAsyncQueue();
                        break;
                    case "2":
                        testSyncQueue();
                        break;
                    case "3":
                        showQueueStatus();
                        break;
                    case "4":
                        showProcessorInfo();
                        break;
                    case "5":
                        createCustomQueue();
                        break;
                    case "6":
                        updateQueueConfig();
                        break;
                    case "0":
                        log.info("退出测试程序");
                        return;
                    default:
                        log.warn("无效选择: {}", choice);
                }
                
                log.info("\n按回车键继续...");
                scanner.nextLine();
                
            } catch (Exception e) {
                log.error("测试过程中发生错误: {}", e.getMessage(), e);
            }
        }
    }

    /**
     * 显示菜单
     */
    private void showMenu() {
        System.out.println("\n=== 分布式队列测试菜单 ===");
        System.out.println("1. 异步队列测试");
        System.out.println("2. 同步队列测试");
        System.out.println("3. 查看队列状态");
        System.out.println("4. 查看处理器信息");
        System.out.println("5. 创建自定义队列");
        System.out.println("6. 更新队列配置");
        System.out.println("0. 退出");
        System.out.print("请选择操作: ");
    }

    /**
     * 异步队列测试
     */
    private void testAsyncQueue() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入队列名称 (默认: sms_queue): ");
        String queueName = scanner.nextLine().trim();
        if (queueName.isEmpty()) {
            queueName = "sms_queue";
        }
        
        System.out.print("请输入消息内容: ");
        String message = scanner.nextLine().trim();
        
        if (message.isEmpty()) {
            message = "测试消息 " + System.currentTimeMillis();
        }
        
        try {
            // 这里需要通过HTTP调用实际的服务接口
            log.info("请使用以下curl命令测试异步队列:");
            log.info("curl -X POST http://localhost:8080/distribute-queue/tasks/{} \\",
                queueName);
            log.info("  -H \"Content-Type: application/json\" \\");
            log.info("  -d '{}'", message.replace("\"", "\\\""));
        } catch (Exception e) {
            log.error("异步队列测试失败: {}", e.getMessage());
        }
    }

    /**
     * 同步队列测试
     */
    private void testSyncQueue() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入队列名称 (默认: sms_queue): ");
        String queueName = scanner.nextLine().trim();
        if (queueName.isEmpty()) {
            queueName = "sms_queue";
        }
        
        System.out.print("请输入消息内容: ");
        String message = scanner.nextLine().trim();
        
        if (message.isEmpty()) {
            message = "同步测试消息 " + System.currentTimeMillis();
        }
        
        try {
            log.info("请使用以下curl命令测试同步队列:");
            log.info("curl -X POST \"http://localhost:8080/distribute-queue/tasks-sync/{}?timeoutMs=5000\" \\",
                queueName);
            log.info("  -H \"Content-Type: application/json\" \\");
            log.info("  -d '{}'", message.replace("\"", "\\\""));
        } catch (Exception e) {
            log.error("同步队列测试失败: {}", e.getMessage());
        }
    }

    /**
     * 显示队列状态
     */
    private void showQueueStatus() {
        try {
            log.info("=== 队列状态信息 ===");
            log.info("已创建的队列实例: {}", queueFactory.getAllQueueNames());
            log.info("队列统计信息: {}", queueFactory.getQueueStatistics());
        } catch (Exception e) {
            log.error("获取队列状态失败: {}", e.getMessage());
        }
    }

    /**
     * 显示处理器信息
     */
    private void showProcessorInfo() {
        try {
            log.info("=== 消息处理器信息 ===");
            log.info("已注册的处理器: {}", processorManager.getRegisteredProcessors());
        } catch (Exception e) {
            log.error("获取处理器信息失败: {}", e.getMessage());
        }
    }

    /**
     * 创建自定义队列
     */
    private void createCustomQueue() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入队列名称: ");
        String queueName = scanner.nextLine().trim();
        
        if (queueName.isEmpty()) {
            log.warn("队列名称不能为空");
            return;
        }
        
        try {
            log.info("请使用以下curl命令创建队列:");
            log.info("curl -X POST http://localhost:8080/distribute-queue/admin/queue-instances/{} \\",
                queueName);
            log.info("  -H \"Content-Type: application/json\" \\");
            log.info("  -d '{\"maxLength\": 1000, \"maxQps\": 50, \"pollInterval\": 1000}'");
        } catch (Exception e) {
            log.error("创建队列失败: {}", e.getMessage());
        }
    }

    /**
     * 更新队列配置
     */
    private void updateQueueConfig() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("请输入队列名称: ");
        String queueName = scanner.nextLine().trim();
        
        if (queueName.isEmpty()) {
            log.warn("队列名称不能为空");
            return;
        }
        
        try {
            log.info("请使用以下curl命令更新队列配置:");
            log.info("curl -X PUT http://localhost:8080/distribute-queue/admin/queue-instances/{}/config \\",
                queueName);
            log.info("  -H \"Content-Type: application/json\" \\");
            log.info("  -d '{\"maxLength\": 2000, \"maxQps\": 100}'");
        } catch (Exception e) {
            log.error("更新队列配置失败: {}", e.getMessage());
        }
    }
}