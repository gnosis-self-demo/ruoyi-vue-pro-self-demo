package openplat;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.net.InetAddress;
import java.net.UnknownHostException;

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
}, excludeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "paramcheck.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "lifecycle.*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "gnosis.sample.*")
})
public class OpenplatApplication {

    public static void main(String[] args) throws UnknownHostException {
        Environment env = new SpringApplication(OpenplatApplication.class)
            .run(args)
            .getEnvironment();
        
        String serverPort = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        
        System.out.println("========================================");
        System.out.println("开放平台启动成功！");
        System.out.println("访问地址:");
        System.out.println("  本地：http://localhost:" + serverPort + contextPath);
        System.out.println("  外部：http://" + hostAddress + ":" + serverPort + contextPath);
        System.out.println("  Swagger: http://localhost:" + serverPort + contextPath + "/swagger-ui.html");
        System.out.println("  前端：http://localhost:3000");
        System.out.println("========================================");
    }
}
