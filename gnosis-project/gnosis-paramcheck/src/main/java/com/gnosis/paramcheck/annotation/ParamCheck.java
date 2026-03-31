package com.gnosis.paramcheck.annotation;

import java.lang.annotation.*;

/**
 * 方法级参数校验注解
 * 标注在 Controller 方法上，指定要执行的校验流程 ID
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ParamCheck {

    /** 校验流程 ID (对应 sys_validation_flows.flow_id) */
    String flowId();

    /** 是否启用，默认为 true */
    boolean enabled() default true;

    /** 失败时是否抛出异常，默认为 true (false 则返回带错误信息的 Result) */
    boolean throwOnFail() default true;
}
