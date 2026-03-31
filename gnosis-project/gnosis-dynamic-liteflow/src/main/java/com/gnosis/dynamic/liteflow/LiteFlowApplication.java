package com.gnosis.dynamic.liteflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.gnosis.dynamic.liteflow")
public class LiteFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(LiteFlowApplication.class, args);
    }
}
