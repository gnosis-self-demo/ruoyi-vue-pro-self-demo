package com.gnosis.paramcheck.controller;

import com.gnosis.common.dto.BaseResponse;
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
    public BaseResponse<PageResult<ValidationLog>> pageQuery(
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
        return BaseResponse.success(result);
    }

    @GetMapping
    public BaseResponse<List<ValidationLog>> getAllLogs() {
        return BaseResponse.success(validationLogService.findAll());
    }

    @GetMapping("/{logId}")
    public BaseResponse<ValidationLog> getLog(@PathVariable Long logId) {
        ValidationLog logEntry = validationLogService.findById(logId);
        if (logEntry == null) {
            return BaseResponse.error(404, "Log not found: " + logId);
        }
        return BaseResponse.success(logEntry);
    }

    @DeleteMapping("/{logId}")
    public BaseResponse<Void> deleteLog(@PathVariable Long logId) {
        try {
            validationLogService.delete(logId);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ValidationLogController] failed to delete log: {}", logId, e);
            return BaseResponse.error("删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/delete")
    public BaseResponse<Void> batchDelete(@RequestBody List<Long> logIds) {
        try {
            validationLogService.batchDelete(logIds);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ValidationLogController] failed to batch delete", e);
            return BaseResponse.error("批量删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/activate")
    public BaseResponse<Void> batchActivate(@RequestBody List<Long> logIds) {
        try {
            validationLogService.batchActivate(logIds);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ValidationLogController] failed to batch activate", e);
            return BaseResponse.error("批量启用失败: " + e.getMessage());
        }
    }

    @PostMapping("/batch/deactivate")
    public BaseResponse<Void> batchDeactivate(@RequestBody List<Long> logIds) {
        try {
            validationLogService.batchDeactivate(logIds);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ValidationLogController] failed to batch deactivate", e);
            return BaseResponse.error("批量禁用失败: " + e.getMessage());
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
