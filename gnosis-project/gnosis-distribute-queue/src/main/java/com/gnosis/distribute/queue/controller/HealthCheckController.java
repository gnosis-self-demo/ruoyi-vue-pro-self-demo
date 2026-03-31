package com.gnosis.distribute.queue.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
public class HealthCheckController {

    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> result = new HashMap<>();
        result.put("status", "UP");
        result.put("message", "Application is running");
        return result;
    }
}