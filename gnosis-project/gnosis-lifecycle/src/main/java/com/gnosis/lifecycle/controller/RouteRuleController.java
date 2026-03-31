package com.gnosis.lifecycle.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.gnosis.lifecycle.dto.RouteRuleCreateRequest;
import com.gnosis.lifecycle.dto.RouteRuleVO;
import com.gnosis.lifecycle.service.RouteRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 路由规则 REST API 控制器
 */
@RestController
@RequestMapping("/api/lifecycle/route/rule")
@Api(tags = "路由规则管理", description = "路由规则 CRUD 操作 API")
public class RouteRuleController {

    private static final Logger log = LoggerFactory.getLogger(RouteRuleController.class);

    @Autowired
    private RouteRuleService routeRuleService;

    /**
     * 创建路由规则
     */
    @PostMapping
    @ApiOperation(value = "创建路由规则", notes = "创建新的路由规则")
    public ResponseEntity<RouteRuleVO> create(
            @ApiParam(value = "路由规则创建请求", required = true)
            @RequestBody RouteRuleCreateRequest request) {
        log.info("[RouteRuleController] 创建路由规则：businessTypeId={}, eventType={}, ruleName={}", 
                request.getBusinessTypeId(), request.getEventType(), request.getRuleName());

        try {
            RouteRuleVO result = routeRuleService.create(request);
            log.info("[RouteRuleController] 路由规则创建成功：ruleId={}", result.getId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[RouteRuleController] 创建失败：{}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[RouteRuleController] 创建异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 更新路由规则
     */
    @PutMapping("/{id}")
    @ApiOperation(value = "更新路由规则", notes = "更新指定 ID 的路由规则")
    public ResponseEntity<RouteRuleVO> update(
            @ApiParam(value = "路由规则 ID", required = true)
            @PathVariable String id,
            @ApiParam(value = "路由规则创建请求", required = true)
            @RequestBody RouteRuleCreateRequest request) {
        log.info("[RouteRuleController] 更新路由规则：id={}", id);

        try {
            RouteRuleVO result = routeRuleService.update(id, request);
            log.info("[RouteRuleController] 路由规则更新成功：id={}", id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[RouteRuleController] 更新失败：{}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[RouteRuleController] 更新异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取路由规则详情
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "获取路由规则详情", notes = "根据 ID 获取路由规则详细信息")
    public ResponseEntity<RouteRuleVO> getById(
            @ApiParam(value = "路由规则 ID", required = true)
            @PathVariable String id) {
        log.debug("[RouteRuleController] 获取路由规则详情：id={}", id);

        try {
            RouteRuleVO result = routeRuleService.getById(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[RouteRuleController] 获取详情失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("[RouteRuleController] 获取详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取路由规则列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取路由规则列表", notes = "查询路由规则列表")
    public ResponseEntity<List<RouteRuleVO>> list(
            @ApiParam(value = "业务类型 ID")
            @RequestParam(required = false) String businessTypeId,
            @ApiParam(value = "事件类型")
            @RequestParam(required = false) String eventType) {
        log.debug("[RouteRuleController] 获取路由规则列表：businessTypeId={}, eventType={}", 
                businessTypeId, eventType);

        try {
            List<RouteRuleVO> result = routeRuleService.list(businessTypeId, eventType);
            log.debug("[RouteRuleController] 路由规则列表获取成功，数量：{}", result.size());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[RouteRuleController] 获取列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 删除路由规则
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value = "删除路由规则", notes = "删除指定 ID 的路由规则")
    public ResponseEntity<Void> delete(
            @ApiParam(value = "路由规则 ID", required = true)
            @PathVariable String id) {
        log.info("[RouteRuleController] 删除路由规则：id={}", id);

        try {
            routeRuleService.delete(id);
            log.info("[RouteRuleController] 路由规则删除成功：id={}", id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.warn("[RouteRuleController] 删除失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("[RouteRuleController] 删除异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 批量删除路由规则
     */
    @DeleteMapping("/batch")
    @ApiOperation(value = "批量删除路由规则", notes = "批量删除多个路由规则")
    public ResponseEntity<Map<String, Object>> batchDelete(
            @ApiParam(value = "路由规则 ID 数组", required = true)
            @RequestBody String[] ids) {
        log.info("[RouteRuleController] 批量删除路由规则，数量：{}", ids.length);

        Map<String, Object> response = new HashMap<>();
        try {
            routeRuleService.batchDelete(ids);
            response.put("success", true);
            response.put("message", "批量删除成功");
            response.put("count", ids.length);
            log.info("[RouteRuleController] 批量删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[RouteRuleController] 批量删除异常", e);
            response.put("success", false);
            response.put("message", "批量删除失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 批量启用路由规则
     */
    @PostMapping("/batch/enable")
    @ApiOperation(value = "批量启用路由规则", notes = "批量启用多个路由规则")
    public ResponseEntity<Map<String, Object>> batchEnable(
            @ApiParam(value = "路由规则 ID 数组", required = true)
            @RequestBody String[] ids) {
        log.info("[RouteRuleController] 批量启用路由规则，数量：{}", ids.length);

        Map<String, Object> response = new HashMap<>();
        try {
            routeRuleService.batchEnable(ids);
            response.put("success", true);
            response.put("message", "批量启用成功");
            response.put("count", ids.length);
            log.info("[RouteRuleController] 批量启用成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[RouteRuleController] 批量启用异常", e);
            response.put("success", false);
            response.put("message", "批量启用失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    /**
     * 批量禁用路由规则
     */
    @PostMapping("/batch/disable")
    @ApiOperation(value = "批量禁用路由规则", notes = "批量禁用多个路由规则")
    public ResponseEntity<Map<String, Object>> batchDisable(
            @ApiParam(value = "路由规则 ID 数组", required = true)
            @RequestBody String[] ids) {
        log.info("[RouteRuleController] 批量禁用路由规则，数量：{}", ids.length);

        Map<String, Object> response = new HashMap<>();
        try {
            routeRuleService.batchDisable(ids);
            response.put("success", true);
            response.put("message", "批量禁用成功");
            response.put("count", ids.length);
            log.info("[RouteRuleController] 批量禁用成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("[RouteRuleController] 批量禁用异常", e);
            response.put("success", false);
            response.put("message", "批量禁用失败：" + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
