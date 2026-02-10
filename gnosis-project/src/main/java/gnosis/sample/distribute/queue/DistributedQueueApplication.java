package gnosis.sample.distribute.queue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 分布式队列服务启动类
 * 基于openGauss的分布式队列系统
 */
@SpringBootApplication
@EnableScheduling
public class DistributedQueueApplication {

    public static void main(String[] args) {
        SpringApplication.run(DistributedQueueApplication.class, args);
    }
}