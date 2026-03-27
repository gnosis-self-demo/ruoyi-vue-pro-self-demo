package paramcheck.lifecycle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 业务生命周期管理组件启动类
 * 
 * 使用说明：
 * 1. 直接运行此类的 main 方法即可启动业务生命周期管理组件
 * 2. 访问 Swagger UI: http://localhost:8080/swagger-ui.html
 * 3. 访问前端页面：http://localhost:3000 (需先启动前端)
 * 
 * @author gnosis
 * @date 2026-03-27
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan(basePackages = {
    "paramcheck.lifecycle",
    "paramcheck"
})
@EnableSwagger2
@EnableScheduling
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
