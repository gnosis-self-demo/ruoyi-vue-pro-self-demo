package com.gnosis.paramcheck.aspect;

import com.gnosis.paramcheck.annotation.ParamCheck;
import com.gnosis.paramcheck.domain.ValidationResult;
import com.gnosis.paramcheck.exception.ValidationException;
import com.gnosis.paramcheck.service.DynamicValidationService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 参数校验 AOP 切面
 * 拦截标注了 @ParamCheck 的方法，自动执行流程校验
 */
@Aspect
@Component
@Order(1)
public class ParamCheckAspect {

    private static final Logger log = LoggerFactory.getLogger(ParamCheckAspect.class);

    @Autowired
    private DynamicValidationService dynamicValidationService;

    /**
     * 切入点: 所有标注了 @ParamCheck 注解的方法
     */
    @Around("@annotation(paramCheck)")
    public Object around(ProceedingJoinPoint pjp, ParamCheck paramCheck) throws Throwable {
        if (!paramCheck.enabled()) {
            return pjp.proceed();
        }

        String flowId = paramCheck.flowId();
        Object requestData = extractRequestData(pjp);

        log.debug("[ParamCheckAspect] before method, flowId={}, param={}", flowId, requestData);

        ValidationResult result = dynamicValidationService.execute(flowId, requestData);

        if (!result.isSuccess()) {
            if (paramCheck.throwOnFail()) {
                throw new ValidationException(result.getErrorCode(), result.getErrorMsg(),
                        result.getFailedNode());
            }
            // 如果不抛异常，返回包含错误信息的 Map
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("success", false);
            errorResult.put("errorCode", result.getErrorCode());
            errorResult.put("errorMsg", result.getErrorMsg());
            return errorResult;
        }

        return pjp.proceed();
    }

    /**
     * 提取方法参数作为校验数据
     * 优先取第一个参数，其次取所有参数合并为 Map
     */
    private Object extractRequestData(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();
        if (args == null || args.length == 0) {
            return null;
        }
        if (args.length == 1) {
            return args[0];
        }
        // 多参数: 合并为 Map
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        String[] paramNames = signature.getParameterNames();
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            String name = (paramNames != null && i < paramNames.length)
                    ? paramNames[i] : "arg" + i;
            map.put(name, args[i]);
        }
        return map;
    }
}
