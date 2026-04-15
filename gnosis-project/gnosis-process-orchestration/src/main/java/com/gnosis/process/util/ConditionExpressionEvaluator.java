package com.gnosis.process.util;

import com.googlecode.aviator.AviatorEvaluator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ConditionExpressionEvaluator {

    private static final Logger log = LoggerFactory.getLogger(ConditionExpressionEvaluator.class);

    public static boolean evaluate(String expression, Map<String, Object> context) {
        if (expression == null || expression.trim().isEmpty()) {
            return true;
        }
        try {
            Object result = AviatorEvaluator.execute(expression, context, true);
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
            log.warn("Condition expression did not return Boolean: expression={}, result={}", expression, result);
            return false;
        } catch (Exception e) {
            log.error("Failed to evaluate condition expression: expression={}, context={}", expression, context, e);
            return false;
        }
    }
}
