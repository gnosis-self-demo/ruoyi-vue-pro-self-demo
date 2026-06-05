package com.gnosis.paramcheck.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.paramcheck.domain.ValidationResult;
import com.gnosis.paramcheck.service.DynamicValidationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ValidationDemoController {

    private static final Logger log = LoggerFactory.getLogger(ValidationDemoController.class);

    @Autowired
    private DynamicValidationService validationService;

    @PostMapping("/validate")
    public BaseResponse<ValidationResult> validate(
            @RequestParam String flowId,
            @RequestBody Map<String, Object> requestData) {
        try {
            log.info("[ValidationDemo] validate request, flowId={}, data={}", flowId, requestData);
            ValidationResult result = validationService.execute(flowId, requestData);
            return BaseResponse.success(result);
        } catch (IllegalArgumentException e) {
            log.warn("[ValidationDemo] validation argument error: {}", e.getMessage());
            return BaseResponse.error(e.getMessage());
        } catch (Exception e) {
            log.error("[ValidationDemo] validation error", e);
            return BaseResponse.error("校验执行异常: " + e.getMessage());
        }
    }
}
