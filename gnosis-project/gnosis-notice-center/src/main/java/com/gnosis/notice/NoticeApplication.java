package com.gnosis.notice;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 统一消息通知中心启动类
 */
@SpringBootApplication
@MapperScan("com.gnosis.notice.mapper")
@EnableAsync
@EnableScheduling
public class NoticeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoticeApplication.class, args);
        System.out.println("==========================================");
        System.out.println("  统一消息通知中心启动成功！");
        System.out.println("==========================================");
    }
}
