package com.gnosis.lifecycle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 业务生命周期管理组件启动类
 */
@SpringBootApplication
@EnableSwagger2
@EnableScheduling
@MapperScan("com.gnosis.lifecycle.repository")
@ComponentScan(basePackages = {
    "com.gnosis.lifecycle"
}, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.openapi.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.paramcheck.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.gnosis.distribute.*")
})
public class LifecycleApplication {

    public static void main(String[] args) {
        SpringApplication.run(LifecycleApplication.class, args);
        System.out.println("=================================================");
        System.out.println("    业务生命周期管理组件启动成功！");
        System.out.println("=================================================");
        System.out.println("Swagger UI: http://localhost:8080/swagger-ui.html");
        System.out.println("前端地址：http://localhost:3000 (需单独启动)");
        System.out.println("=================================================");
    }
}
