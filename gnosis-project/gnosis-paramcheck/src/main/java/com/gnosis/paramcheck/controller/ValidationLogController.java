package com.gnosis.paramcheck.controller;

import com.gnosis.common.dto.CommonResponse;
import com.gnosis.paramcheck.domain.ValidationLog;
import com.gnosis.paramcheck.dto.PageResult;
import com.gnosis.paramcheck.service.ValidationLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/paramcheck/logs")
public class ValidationLogController {

    private static final Logger log = LoggerFactory.getLogger(ValidationLogController.class);

    @Autowired
    private ValidationLogService validationLogService;

    @GetMapping("/page")
    public CommonResponse<PageResult<ValidationLog>> pageQuery(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String flowId,
            @RequestParam(required = false) String requestId,
            @RequestParam(required = false) String modeType,
            @RequestParam(required = false) Boolean isActive) {
        com.gnosis.paramcheck.dto.PageRequest pageRequest = new com.gnosis.paramcheck.dto.PageRequest();
        pageRequest.setPage(page);
        pageRequest.setPageSize(pageSize);
        PageResult<ValidationLog> result = validationLogService.findPage(flowId, requestId, modeType, isActive, pageRequest);
        return CommonResponse.success(result);
    }

    @GetMapping
    public CommonResponse<List<ValidationLog>> getAllLogs() {
        return CommonResponse.success(validationLogService.findAll());
    }

    @GetMapping("/{logId}")
    public CommonResponse<ValidationLog> getLog(@PathVariable Long logId) {
        ValidationLog logEntry = validationLogService.findById(logId);
        if (logEntry == null) {
            return CommonResponse.error(404, "Log not found: " + logId);
        }
        return CommonResponse.success(logEntry);
    }

    @DeleteMapping("/{logId}")
    public CommonResponse<Void> deleteLog(@PathVariable Long logId) {
        try {
            validationLogService.delete(logId);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("[ValidationLogController] failed to delete log: {}", logId, e);
            return CommonResponse.error("删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/delete")
    public CommonResponse<Void> batchDelete(@RequestBody List<Long> logIds) {
        try {
            validationLogService.batchDelete(logIds);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("[ValidationLogController] failed to batch delete", e);
            return CommonResponse.error("批量删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/activate")
    public CommonResponse<Void> batchActivate(@RequestBody List<Long> logIds) {
        try {
            validationLogService.batchActivate(logIds);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("[ValidationLogController] failed to batch activate", e);
            return CommonResponse.error("批量启用失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/deactivate")
    public CommonResponse<Void> batchDeactivate(@RequestBody List<Long> logIds) {
        try {
            validationLogService.batchDeactivate(logIds);
            return CommonResponse.success();
        } catch (Exception e) {
            log.error("[ValidationLogController] failed to batch deactivate", e);
            return CommonResponse.error("批量禁用失败: " + e.getMessage());
        }
    }

    @GetMapping("/export")
    public void exportLogs(HttpServletResponse response,
                           @RequestParam(required = false) String flowId,
                           @RequestParam(required = false) String modeType) {
        try {
            List<ValidationLog> list = validationLogService.findAll();
            String json = com.alibaba.fastjson.JSON.toJSONString(list);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=validation_logs.json");
            response.getWriter().write(json);
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("[ValidationLogController] export failed", e);
        }
    }
}
