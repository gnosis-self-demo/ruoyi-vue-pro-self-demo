package com.gnosis.lifecycle.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.gnosis.lifecycle.dto.*;
import com.gnosis.lifecycle.service.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件管理 REST API 控制器
 */
@RestController
@RequestMapping("/api/lifecycle/event")
@Api(tags = "事件管理", description = "事件提交、查询和路由测试 API")
public class EventController {

    private static final Logger log = LoggerFactory.getLogger(EventController.class);

    @Autowired
    private EventService eventService;

    /**
     * 提交事件
     */
    @PostMapping
    @ApiOperation(value = "提交事件", notes = "接收业务事件并触发路由匹配")
    public ResponseEntity<EventVO> submitEvent(
            @ApiParam(value = "事件提交请求", required = true)
            @RequestBody EventSubmitRequest request) {
        log.info("[EventController] 提交事件：businessTypeId={}, eventType={}", 
                request.getBusinessTypeId(), request.getEventType());

        try {
            EventVO result = eventService.submitEvent(request);
            log.info("[EventController] 事件提交成功：eventId={}", result.getId());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[EventController] 提交失败：{}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[EventController] 提交异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取事件详情
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "获取事件详情", notes = "根据 ID 获取事件详细信息")
    public ResponseEntity<EventVO> getById(
            @ApiParam(value = "事件 ID", required = true)
            @PathVariable String id) {
        log.debug("[EventController] 获取事件详情：id={}", id);

        try {
            EventVO result = eventService.getById(id);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[EventController] 获取详情失败：{}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("[EventController] 获取详情异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 获取事件列表
     */
    @GetMapping("/list")
    @ApiOperation(value = "获取事件列表", notes = "分页查询事件列表")
    public ResponseEntity<List<EventVO>> list(
            @ApiParam(value = "业务类型 ID")
            @RequestParam(required = false) String businessTypeId,
            @ApiParam(value = "事件类型")
            @RequestParam(required = false) String eventType,
            @ApiParam(value = "页码", defaultValue = "1")
            @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam(value = "每页大小", defaultValue = "10")
            @RequestParam(defaultValue = "10") int pageSize) {
        log.debug("[EventController] 获取事件列表：pageNum={}, pageSize={}", pageNum, pageSize);

        try {
            List<EventVO> result = eventService.list(businessTypeId, eventType, pageNum, pageSize);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[EventController] 获取列表异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 测试路由规则
     */
    @PostMapping("/route/test")
    @ApiOperation(value = "测试路由规则", notes = "模拟测试路由规则匹配")
    public ResponseEntity<RouteTestResultVO> testRoute(
            @ApiParam(value = "路由测试请求", required = true)
            @RequestBody RouteTestRequest request) {
        log.info("[EventController] 测试路由规则：businessTypeId={}, eventType={}", 
                request.getBusinessTypeId(), request.getEventType());

        try {
            RouteTestResultVO result = eventService.testRoute(request);
            log.info("[EventController] 路由测试完成：matched={}", result.isMatched());
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.warn("[EventController] 测试失败：{}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("[EventController] 测试异常", e);
            return ResponseEntity.status(500).build();
        }
    }

    /**
     * 执行状态流转
     */
    @PostMapping("/transition/execute")
    @ApiOperation(value = "执行状态流转", notes = "执行状态流转操作")
    public ResponseEntity<Map<String, Object>> executeTransition(
            @ApiParam(value = "流转执行请求", required = true)
            @RequestBody TransitionExecutionRequest request) {
        log.info("[EventController] 执行状态流转：businessInstanceId={}, transitionCode={}", 
                request.getBusinessInstanceId(), request.getTransitionCode());

        try {
            Map<String, Object> result = eventService.executeTransition(request);
            log.info("[EventController] 状态流转完成：success={}", result.get("success"));
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[EventController] 流转异常", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("message", "流转异常：" + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * 获取事件元数据字典
     */
    @GetMapping("/metadata/dictionary")
    @ApiOperation(value = "获取事件元数据字典", notes = "获取支持的元数据字段说明")
    public ResponseEntity<Map<String, Object>> getMetadataDictionary() {
        log.debug("[EventController] 获取事件元数据字典");

        try {
            Map<String, Object> result = eventService.getMetadataDictionary();
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("[EventController] 获取元数据字典异常", e);
            return ResponseEntity.status(500).build();
        }
    }
}
