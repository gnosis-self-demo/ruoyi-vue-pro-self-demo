package com.gnosis.lifecycle.entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.gnosis.lifecycle.domain.LifecycleBusinessType;
import com.gnosis.lifecycle.dto.EntryValidationRequest;
import com.gnosis.lifecycle.dto.EntryValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 入口校验器
 * 负责业务入口的数据校验
 */
@Component
public class EntryValidator {

    private static final Logger log = LoggerFactory.getLogger(EntryValidator.class);

    @Autowired
    private PermissionChecker permissionChecker;

    /**
     * 校验入口
     *
     * @param request 校验请求
     * @param businessType 业务类型
     * @return 校验结果
     */
    public EntryValidationResult validate(EntryValidationRequest request, LifecycleBusinessType businessType) {
        log.info("[EntryValidator] 开始校验入口：businessTypeId={}, userId={}", 
                request.getBusinessTypeId(), request.getUserId());

        // 1. 权限校验
        EntryValidationResult permissionResult = permissionChecker.checkPermission(
                request.getUserId(), 
                request.getUserRoles(), 
                businessType
        );

        if (!permissionResult.isPassed()) {
            log.warn("[EntryValidator] 权限校验失败：userId={}, businessType={}", 
                    request.getUserId(), businessType.getCode());
            return permissionResult;
        }

        // 2. 数据校验
        EntryValidationResult dataResult = validateData(request.getValidationData(), businessType);
        if (!dataResult.isPassed()) {
            log.warn("[EntryValidator] 数据校验失败：userId={}, businessType={}, message={}", 
                    request.getUserId(), businessType.getCode(), dataResult.getMessage());
            return dataResult;
        }

        // 3. 校验通过
        EntryValidationResult result = EntryValidationResult.success("入口校验通过");
        result.setBusinessTypeName(businessType.getName());
        result.setBusinessTypeCode(businessType.getCode());

        log.info("[EntryValidator] 入口校验通过：userId={}, businessType={}", 
                request.getUserId(), businessType.getCode());

        return result;
    }

    /**
     * 数据校验
     *
     * @param validationData 校验数据
     * @param businessType 业务类型
     * @return 校验结果
     */
    private EntryValidationResult validateData(Map<String, Object> validationData, LifecycleBusinessType businessType) {
        log.debug("[EntryValidator] 执行数据校验：businessType={}", businessType.getCode());

        // 基础校验：数据不能为空
        if (validationData == null || validationData.isEmpty()) {
            return EntryValidationResult.fail("VALIDATION_DATA_EMPTY", "校验数据不能为空");
        }

        // TODO: 根据业务类型配置的具体校验规则进行校验
        // 这里可以扩展为：
        // 1. 从配置中加载校验规则
        // 2. 执行规则校验
        // 3. 返回校验结果

        // 示例：简单的字段非空校验
        for (Map.Entry<String, Object> entry : validationData.entrySet()) {
            if (entry.getValue() == null || "".equals(entry.getValue().toString().trim())) {
                return EntryValidationResult.fail("FIELD_EMPTY", "字段不能为空：" + entry.getKey());
            }
        }

        return EntryValidationResult.success("数据校验通过");
    }

    /**
     * 解析权限配置
     *
     * @param permissionConfig 权限配置 JSON 字符串
     * @return 权限配置对象
     */
    public JSONObject parsePermissionConfig(String permissionConfig) {
        if (permissionConfig == null || permissionConfig.trim().isEmpty()) {
            return new JSONObject();
        }

        try {
            return JSON.parseObject(permissionConfig);
        } catch (Exception e) {
            log.error("[EntryValidator] 解析权限配置失败：{}", permissionConfig, e);
            return new JSONObject();
        }
    }
}
