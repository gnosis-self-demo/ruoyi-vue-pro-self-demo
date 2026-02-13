package gnosis.sample.distribute.queue.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 简单测试控制器
 */
@RestController
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "Application is working!";
    }
}