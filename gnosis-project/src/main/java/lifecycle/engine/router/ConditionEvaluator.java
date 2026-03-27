package lifecycle.engine.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import lifecycle.adapter.StandardEvent;

/**
 * 条件评估器
 * 负责评估单个条件是否满足
 */
@Component
public class ConditionEvaluator {

    private static final Logger log = LoggerFactory.getLogger(ConditionEvaluator.class);

    /**
     * 评估条件
     *
     * @param field 字段名
     * @param operator 操作符
     * @param expectedValue 期望值
     * @param actualValue 实际值
     * @return 是否满足条件
     */
    public boolean evaluate(String field, String operator, Object expectedValue, Object actualValue) {
        log.debug("[ConditionEvaluator] 评估条件：field={}, operator={}, expectedValue={}, actualValue={}", 
                field, operator, expectedValue, actualValue);

        if (actualValue == null) {
            log.debug("[ConditionEvaluator] 实际值为 null，条件不满足");
            return false;
        }

        boolean result;
        switch (operator) {
            case "==":
            case "=":
                result = equals(actualValue, expectedValue);
                break;
            case "!=":
                result = notEquals(actualValue, expectedValue);
                break;
            case ">":
                result = greaterThan(actualValue, expectedValue);
                break;
            case ">=":
                result = greaterThanOrEqual(actualValue, expectedValue);
                break;
            case "<":
                result = lessThan(actualValue, expectedValue);
                break;
            case "<=":
                result = lessThanOrEqual(actualValue, expectedValue);
                break;
            case "contains":
                result = contains(actualValue, expectedValue);
                break;
            case "in":
                result = in(actualValue, expectedValue);
                break;
            case "notin":
                result = notIn(actualValue, expectedValue);
                break;
            default:
                log.warn("[ConditionEvaluator] 不支持的操作符：{}", operator);
                result = false;
        }

        log.debug("[ConditionEvaluator] 条件评估结果：{}", result);
        return result;
    }

    /**
     * 从事件数据中获取字段值
     */
    public Object getFieldValue(StandardEvent event, String field) {
        // 优先从 metadata 中查找
        if (event.getMetadata() != null && event.getMetadata().containsKey(field)) {
            return event.getMetadata().get(field);
        }

        // 然后从 eventData 中查找
        if (event.getEventData() != null && event.getEventData().containsKey(field)) {
            return event.getEventData().get(field);
        }

        // 从标准字段中查找
        switch (field) {
            case "businessTypeId":
                return event.getBusinessTypeId();
            case "eventType":
                return event.getEventType();
            case "businessInstanceId":
                return event.getBusinessInstanceId();
            case "currentStateCode":
                return event.getCurrentStateCode();
            case "operatorId":
                return event.getOperatorId();
            default:
                return null;
        }
    }

    private boolean equals(Object actual, Object expected) {
        if (actual == null) {
            return expected == null;
        }
        return actual.toString().equals(expected != null ? expected.toString() : null);
    }

    private boolean notEquals(Object actual, Object expected) {
        return !equals(actual, expected);
    }

    private boolean greaterThan(Object actual, Object expected) {
        try {
            return compare(actual, expected) > 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean greaterThanOrEqual(Object actual, Object expected) {
        try {
            return compare(actual, expected) >= 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean lessThan(Object actual, Object expected) {
        try {
            return compare(actual, expected) < 0;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean lessThanOrEqual(Object actual, Object expected) {
        try {
            return compare(actual, expected) <= 0;
        } catch (Exception e) {
            return false;
        }
    }

    private int compare(Object actual, Object expected) {
        if (actual instanceof Number && expected instanceof Number) {
            double actualNum = ((Number) actual).doubleValue();
            double expectedNum = ((Number) expected).doubleValue();
            return Double.compare(actualNum, expectedNum);
        }

        // 尝试转换为数字比较
        try {
            double actualNum = Double.parseDouble(actual.toString());
            double expectedNum = Double.parseDouble(expected.toString());
            return Double.compare(actualNum, expectedNum);
        } catch (NumberFormatException e) {
            // 字符串比较
            return actual.toString().compareTo(expected.toString());
        }
    }

    private boolean contains(Object actual, Object expected) {
        if (actual instanceof String) {
            return ((String) actual).contains(expected != null ? expected.toString() : "");
        }
        return false;
    }

    private boolean in(Object actual, Object expected) {
        if (expected instanceof String) {
            String[] values = expected.toString().split(",");
            for (String value : values) {
                if (equals(actual, value.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean notIn(Object actual, Object expected) {
        return !in(actual, expected);
    }
}
