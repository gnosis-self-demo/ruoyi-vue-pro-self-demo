package com.gnosis.paramcheck.handler;

import org.springframework.stereotype.Component;
import java.lang.annotation.*;

/**
 * 自定义处理器注解
 * 标注在实现 IValidationHandler 的类上，对应 DB 配置中的 handler_code
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface ValidationHandler {

    /**
     * 处理器唯一编码
     * 必须与 sys_validation_flows.handler_code 字段值一致
     */
    String value();
}
