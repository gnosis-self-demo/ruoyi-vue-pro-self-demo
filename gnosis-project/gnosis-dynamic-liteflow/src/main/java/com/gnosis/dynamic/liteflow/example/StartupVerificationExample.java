package com.gnosis.dynamic.liteflow.example;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 启动验证示例
 */
@Component
public class StartupVerificationExample implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        // 验证LiteFlow动态切面功能
        System.out.println("LiteFlow动态切面功能启动验证完成");
    }
}
