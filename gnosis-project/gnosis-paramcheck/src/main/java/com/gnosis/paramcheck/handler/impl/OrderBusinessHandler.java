package com.gnosis.paramcheck.handler.impl;

import com.gnosis.paramcheck.domain.ValidationContext;
import com.gnosis.paramcheck.domain.ValidationResult;
import com.gnosis.paramcheck.handler.IValidationHandler;
import com.gnosis.paramcheck.handler.ValidationHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * 订单业务 Handler (HYBRID 模式示例)
 * 负责复杂的库存预占检查、防重校验等纯 Java 代码逻辑
 */
@Component
@ValidationHandler("OrderBusinessHandler")
public class OrderBusinessHandler implements IValidationHandler {

    private static final Logger log = LoggerFactory.getLogger(OrderBusinessHandler.class);

    @Autowired
    @Qualifier("gnosisJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    @Override
    public ValidationResult validate(ValidationContext context) {
        Object rawData = context.getData("rawRequest");
        log.info("[OrderBusinessHandler] start validation, requestId={}", context.getRequestId());

        // 1) 库存校验
        Object skuIdObj = extractJsonPath(rawData, "$.items[0].skuId");
        Object qtyObj = extractJsonPath(rawData, "$.items[0].qty");
        if (skuIdObj != null && qtyObj != null) {
            String skuId = String.valueOf(skuIdObj);
            Integer requestedQty = Integer.parseInt(String.valueOf(qtyObj));
            Integer stockQty = jdbcTemplate.queryForObject(
                    "SELECT stock_qty FROM gnosis_sample.product_stock WHERE sku_id = ?",
                    Integer.class, skuId);
            if (stockQty == null || stockQty < requestedQty) {
                log.warn("[OrderBusinessHandler] stock not enough, sku={}, requested={}, available={}",
                        skuId, requestedQty, stockQty);
                return ValidationResult.fail("STOCK_NOT_ENOUGH",
                        "商品库存不足 (SKU: " + skuId + ")，请求: " + requestedQty + "，可用: " + stockQty);
            }
        }

        // 2) 防重复提交 (openGauss 兼容语法)
        Object orderNoObj = extractJsonPath(rawData, "$.orderNo");
        if (orderNoObj != null) {
            String orderNo = String.valueOf(orderNoObj);
            Integer exists = jdbcTemplate.queryForObject(
                    "SELECT COUNT(1) FROM gnosis_sample.order_record " +
                    "WHERE order_no = ? AND created_time > CURRENT_TIMESTAMP - INTERVAL '5 minute' " +
                    "LIMIT 1",
                    Integer.class, orderNo);
            if (exists != null && exists > 0) {
                log.warn("[OrderBusinessHandler] duplicate order, orderNo={}", orderNo);
                return ValidationResult.fail("DUPLICATE_ORDER", "订单号重复提交: " + orderNo);
            }
        }

        log.info("[OrderBusinessHandler] validation passed");
        return ValidationResult.success();
    }

    private Object extractJsonPath(Object data, String path) {
        if (data == null) return null;
        try {
            return com.jayway.jsonpath.JsonPath.read(data, path);
        } catch (Exception e) {
            return null;
        }
    }
}
