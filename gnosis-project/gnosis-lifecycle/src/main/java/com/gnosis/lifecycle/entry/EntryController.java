package com.gnosis.lifecycle.entry;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.gnosis.lifecycle.domain.LifecycleBusinessType;
import com.gnosis.lifecycle.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 入口控制 REST API 控制器
 */
@RestController
@RequestMapping("/api/lifecycle/entry")
@Api(tags = "业务生命周期入口管理", description = "业务入口注册、校验和管理 API")
public class EntryController {

    private static final Logger log = LoggerFactory.getLogger(EntryController.class);

    @Autowired
    private EntryService entryService;

    @Autowired
    private EntryValidator entryValidator;

    /**
     * 注册业务入口
     */
    @PostMapping("/register")
    @ApiOperation(value = "注册业务入口", notes = "创建新的业务入口")
    public ResponseEntity<BusinessTypeVO> register(
            @ApiParam(value = "业务类型创建请求", required = true) 
            @RequestBody BusinessTypeCreateRequest request) {
        log.info("[EntryController] 注册业务入口：{}", request.getCode());

        try {
            BusinessTypeVO result = entryService.register(request);
            log.info("[EntryController] 业务入口注册成功：{}", result.getId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[EntryController] 注册失败：{}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[EntryController] 注册异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取业务入口列表（分页）
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取业务入口列表", notes = "分页查询业务入口列表")
    public ResponseEntity<List<BusinessTypeVO>> list(
            @ApiParam(value = "业务类型名称") 
            @RequestParam(required = false) String name,
            @ApiParam(value = "业务类型编码") 
            @RequestParam(required = false) String code,
            @ApiParam(value = "页码", defaultValue = "1") 
            @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam(value = "每页大小", defaultValue = "10") 
            @RequestParam(defaultValue = "10") int pageSize) {
        log.debug("[EntryController] 获取业务入口列表：pageNum={}, pageSize={}", pageNum, pageSize);

        try {
            LifecycleBusinessType query = new LifecycleBusinessType();
            query.setName(name);
            query.setCode(code);

            List<BusinessTypeVO> result = entryService.list(query, pageNum, pageSize);
            log.debug("[EntryController] 业务入口列表获取成功，数量：{}", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[EntryController] 获取列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取业务入口详情
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "获取业务入口详情", notes = "根据 ID 获取业务入口详细信息")
    public ResponseEntity<BusinessTypeVO> getById(
            @ApiParam(value = "业务类型 ID", required = true) 
            @PathVariable String id) {
        log.debug("[EntryController] 获取业务入口详情：{}", id);

        try {
            BusinessTypeVO result = entryService.getById(id);
            if (result == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[EntryController] 获取详情失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("[EntryController] 获取详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新业务入口
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "更新业务入口", notes = "更新指定 ID 的业务入口信息")
    public ResponseEntity<BusinessTypeVO> update(
            @ApiParam(value = "业务类型 ID", required = true) 
            @PathVariable String id,
            @ApiParam(value = "业务类型更新请求", required = true) 
            @RequestBody BusinessTypeUpdateRequest request) {
        log.info("[EntryController] 更新业务入口：{}", id);

        try {
            // 确保 ID 一致
            request.setId(id);
            BusinessTypeVO result = entryService.update(request);
            log.info("[EntryController] 业务入口更新成功：{}", result.getId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[EntryController] 更新失败：{}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[EntryController] 更新异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除业务入口
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除业务入口", notes = "删除指定 ID 的业务入口")
    public ResponseEntity<Void> delete(
            @ApiParam(value = "业务类型 ID", required = true) 
            @PathVariable String id) {
        log.info("[EntryController] 删除业务入口：{}", id);

        try {
            entryService.delete(id);
            log.info("[EntryController] 业务入口删除成功：{}", id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("[EntryController] 删除失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("[EntryController] 删除异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 批量删除业务入口
     */
    @DeleteMapping("/batch")
    @ApiOperation(value = "批量删除业务入口", notes = "批量删除多个业务入口")
    public ResponseEntity<Map<String, Object>> batchDelete(
            @ApiParam(value = "业务类型 ID 数组", required = true) 
            @RequestBody String[] ids) {
        log.info("[EntryController] 批量删除业务入口，数量：{}", ids.length);

        Map<String, Object> response = new HashMap<>();
        try {
            entryService.batchDelete(ids);
            response.put("success", true);
            response.put("message", "批量删除成功");
            response.put("count", ids.length);
            log.info("[EntryController] 批量删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[EntryController] 批量删除异常", e);
            response.put("success", false);
            response.put("message", "批量删除失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 批量启用业务入口
     */
    @PostMapping("/batch/enable")
    @ApiOperation(value = "批量启用业务入口", notes = "批量启用多个业务入口")
    public ResponseEntity<Map<String, Object>> batchEnable(
            @ApiParam(value = "业务类型 ID 数组", required = true) 
            @RequestBody String[] ids) {
        log.info("[EntryController] 批量启用业务入口，数量：{}", ids.length);

        Map<String, Object> response = new HashMap<>();
        try {
            entryService.batchEnable(ids);
            response.put("success", true);
            response.put("message", "批量启用成功");
            response.put("count", ids.length);
            log.info("[EntryController] 批量启用成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[EntryController] 批量启用异常", e);
            response.put("success", false);
            response.put("message", "批量启用失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 批量禁用业务入口
     */
    @PostMapping("/batch/disable")
    @ApiOperation(value = "批量禁用业务入口", notes = "批量禁用多个业务入口")
    public ResponseEntity<Map<String, Object>> batchDisable(
            @ApiParam(value = "业务类型 ID 数组", required = true) 
            @RequestBody String[] ids) {
        log.info("[EntryController] 批量禁用业务入口，数量：{}", ids.length);

        Map<String, Object> response = new HashMap<>();
        try {
            entryService.batchDisable(ids);
            response.put("success", true);
            response.put("message", "批量禁用成功");
            response.put("count", ids.length);
            log.info("[EntryController] 批量禁用成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[EntryController] 批量禁用异常", e);
            response.put("success", false);
            response.put("message", "批量禁用失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 校验入口数据
     */
    @PostMapping("/{id}/validate")
    @ApiOperation(value = "校验入口数据", notes = "校验用户是否有权限访问该业务入口")
    public ResponseEntity<EntryValidationResult> validate(
            @ApiParam(value = "业务类型 ID", required = true) 
            @PathVariable String id,
            @ApiParam(value = "入口校验请求", required = true) 
            @RequestBody EntryValidationRequest request) {
        log.info("[EntryController] 校验入口数据：businessTypeId={}, userId={}", 
                request.getBusinessTypeId(), request.getUserId());

        try {
            // 确保 ID 一致
            request.setBusinessTypeId(id);
            EntryValidationResult result = entryService.validate(request);
            log.info("[EntryController] 入口校验完成：passed={}", result.isPassed());
            
            if (result.isPassed()) {
                return ResponseEntity.ok(result);
            } else {
                return ResponseEntity.badRequest().body(result);
            }
        } catch (Exception e) {
            log.error("[EntryController] 校验异常", e);
            EntryValidationResult errorResult = EntryValidationResult.fail(
                    "VALIDATION_ERROR", "校验异常：" + e.getMessage());
            return ResponseEntity.status(500).body(errorResult);
        }
    }
}
