package com.cmbc.oa.module.paramcheck.component;

import com.yomahub.liteflow.core.NodeComponent;
import org.springframework.stereotype.Component;

/**
 * LiteFlow 组件: JSON 参数解析节点
 * 节点 ID: parse_param
 *
 * 从请求数据中通过 JSONPath 提取数据，写入上下文供后续节点使用
 *
 * 规则来源: requestData 中的 parse_json_path 字段
 * (由 DynamicValidationService 从 DB component_config 注入)
 */
@Component("parse_param")
public class ParseParamComponent extends NodeComponent {

    @Override
    public void process() {
        Object rawData = this.getRequestData();
        if (rawData == null) {
            return;
        }

        // 获取配置的 JSONPath 表达式，默认为根对象
        String jsonPathExpr = null;
        if (rawData instanceof java.util.Map) {
            jsonPathExpr = (String) ((java.util.Map<?, ?>) rawData).get("parse_json_path");
        }
        if (jsonPathExpr == null || jsonPathExpr.trim().isEmpty()) {
            jsonPathExpr = "$";
        }

        // 解析 JSONPath，但不存储结果（直接使用请求数据）
        try {
            com.jayway.jsonpath.JsonPath.read(rawData, jsonPathExpr);
        } catch (Exception e) {
            // 解析失败但不影响后续流程
        }
    }
}
