package com.gnosis.paramcheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ComponentScan(basePackages = {
    "com.gnosis.paramcheck",
    "com.gnosis.common"
}, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.openapi.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.lifecycle.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.distribute.*")
})
public class ParamCheckApplication {

    public static void main(String[] args) {
        SpringApplication.run(ParamCheckApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  ParamCheck 模块已启动");
        System.out.println("  三种校验模式: FLOW / HANDLER / HYBRID");
        System.out.println("========================================\n");
    }
}
