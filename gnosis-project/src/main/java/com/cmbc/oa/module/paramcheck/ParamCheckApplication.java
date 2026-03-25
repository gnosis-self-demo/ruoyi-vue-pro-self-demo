package com.cmbc.oa.module.paramcheck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 参数动态校验组件启动类
 * 
 * 启动后访问:
 * - 接口文档: http://localhost:8080/swagger-ui.html (如已配置 Swagger)
 * - 健康检查: http://localhost:8080/actuator/health
 * 
 * 验证方式:
 * 1. POST /api/order/create (HYBRID 模式示例)
 * 2. POST /api/user/register (HANDLER 模式示例)
 * 3. POST /api/login (FLOW 模式示例)
 */
@SpringBootApplication
@EnableScheduling       // 启用定时任务 (FlowRefreshService 热更新)
@EnableAsync          // 启用异步 (LogRepository 异步写日志)
@ComponentScan(basePackages = {
    "com.cmbc.oa.module.paramcheck",  // 本模块
    "com.gnosis"                       // 主项目包 (根据实际调整)
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
