package openplat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 开放平台独立启动类
 * 仅启动 openplat 模块，避免 lifecycle 模块的包路径冲突问题
 */
@SpringBootApplication
@EnableSwagger2
@EnableScheduling
@MapperScan("openplat.mapper")
@ComponentScan(basePackages = {
    "openplat"
})
public class OpenplatApplication {

    public static void main(String[] args) {
        SpringApplication.run(OpenplatApplication.class, args);
        System.out.println("========================================");
        System.out.println("开放平台启动成功！");
        System.out.println("Swagger 文档：http://localhost:8080/swagger-ui.html");
        System.out.println("前端地址：http://localhost:3000");
        System.out.println("========================================");
    }
}
