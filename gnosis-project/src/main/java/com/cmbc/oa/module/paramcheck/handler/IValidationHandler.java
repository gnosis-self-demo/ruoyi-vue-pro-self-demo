package com.cmbc.oa.module.paramcheck.handler;

import com.cmbc.oa.module.paramcheck.domain.ValidationContext;
import com.cmbc.oa.module.paramcheck.domain.ValidationResult;

/**
 * 自定义校验处理器接口
 * 实现此接口编写高度定制化的校验逻辑（复杂业务、特定规则、外部 API 等）
 */
public interface IValidationHandler {

    /**
     * 执行校验逻辑
     *
     * @param context 校验上下文 (含原始请求、已解析数据等)
     * @return 校验结果
     */
    ValidationResult validate(ValidationContext context);

    /**
     * 获取处理器编码
     * 默认返回类名，也可覆写为自定义编码
     */
    default String getCode() {
        return this.getClass().getSimpleName();
    }
}
