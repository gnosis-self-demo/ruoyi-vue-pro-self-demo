package com.gnosis.signature;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 签章平台启动类
 * 统一电子签章中台 - 整合多方供应商签章能力
 */
@SpringBootApplication
public class SignaturePlatApplication {

    public static void main(String[] args) {
        SpringApplication.run(SignaturePlatApplication.class, args);
        System.out.println("=================================================");
        System.out.println("  签章平台 (gnosis-signature-plat) 启动成功!");
        System.out.println("  Swagger: http://localhost:8095/swagger-ui.html");
        System.out.println("=================================================");
    }
}
