package com.gnosis.process;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
@EnableScheduling
@MapperScan("com.gnosis.process.mapper")
@ComponentScan(basePackages = {
    "com.gnosis.process"
}, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.openapi.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.paramcheck.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.lifecycle.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.distribute.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.dynamic.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.openplat.*")
})
public class ProcessOrchestrationApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProcessOrchestrationApplication.class, args);
        System.out.println("=================================================");
        System.out.println("    流程编排组件启动成功！");
        System.out.println("=================================================");
        System.out.println("Swagger UI: http://localhost:8085/swagger-ui.html");
        System.out.println("前端地址：http://localhost:3004 (需单独启动)");
        System.out.println("=================================================");
    }
}
