package paramcheck.controller;

import paramcheck.domain.ValidationFlow;
import paramcheck.repository.FlowConfigRepository;
import paramcheck.service.FlowRefreshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 配置管理API
 * 提供流程配置的CRUD操作和版本控制
 */
@RestController
@RequestMapping("/api/paramcheck/config")
public class ConfigManagementController {

    private static final Logger log = LoggerFactory.getLogger(ConfigManagementController.class);

    @Autowired
    private FlowConfigRepository flowConfigRepository;

    @Autowired
    private FlowRefreshService flowRefreshService;

    /**
     * 获取所有激活的流程配置
     */
    @GetMapping("/flows")
    public ResponseEntity<List<ValidationFlow>> getActiveFlows() {
        List<ValidationFlow> flows = flowConfigRepository.findAllActive();
        return ResponseEntity.ok(flows);
    }

    /**
     * 获取所有流程配置，包括禁用的
     */
    @GetMapping("/flows/all")
    public ResponseEntity<Map<String, Object>> getAllFlows(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String flowId,
            @RequestParam(required = false) String flowName,
            @RequestParam(required = false) String businessType,
            @RequestParam(required = false) String modeType,
            @RequestParam(required = false) Boolean isActive) {
        Map<String, Object> result = flowConfigRepository.findAll(page, pageSize, flowId, flowName, businessType, modeType, isActive);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取单个流程配置详情
     */
    @GetMapping("/flows/{flowId}")
    public ResponseEntity<ValidationFlow> getFlow(@PathVariable String flowId) {
        Optional<ValidationFlow> flow = flowConfigRepository.findById(flowId);
        return flow.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * 手动刷新指定流程
     * 用于在配置变更后立即生效
     */
    @PostMapping("/flows/{flowId}/refresh")
    public ResponseEntity<Void> refreshFlow(@PathVariable String flowId) {
        try {
            flowRefreshService.refreshFlow(flowId);
            log.info("[ConfigManagement] refreshed flow: {}", flowId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to refresh flow: {}", flowId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 全量刷新所有流程
     */
    @PostMapping("/flows/refresh-all")
    public ResponseEntity<Void> refreshAllFlows() {
        try {
            flowRefreshService.refreshAllFlows();
            log.info("[ConfigManagement] refreshed all flows");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to refresh all flows", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 禁用流程
     */
    @PostMapping("/flows/{flowId}/deactivate")
    public ResponseEntity<Void> deactivateFlow(@PathVariable String flowId) {
        try {
            flowConfigRepository.deactivate(flowId);
            log.info("[ConfigManagement] deactivated flow: {}", flowId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to deactivate flow: {}", flowId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 启用流程
     */
    @PostMapping("/flows/{flowId}/activate")
    public ResponseEntity<Void> activateFlow(@PathVariable String flowId) {
        try {
            flowConfigRepository.activate(flowId);
            log.info("[ConfigManagement] activated flow: {}", flowId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to activate flow: {}", flowId, e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 保存流程配置（创建或更新）
     */
    @PostMapping("/flows")
    public ResponseEntity<Void> saveFlow(@RequestBody ValidationFlow flow) {
        try {
            // 保存配置
            flowConfigRepository.save(flow);
            // 保存后刷新流程
            flowRefreshService.refreshFlow(flow.getFlowId());
            log.info("[ConfigManagement] saved flow: {}", flow.getFlowId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to save flow: {}", flow.getFlowId(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 批量禁用流程
     */
    @PostMapping("/flows/batch/deactivate")
    public ResponseEntity<Void> batchDeactivateFlows(@RequestBody List<String> flowIds) {
        try {
            flowConfigRepository.batchDeactivate(flowIds);
            log.info("[ConfigManagement] batch deactivated flows: {}", flowIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to batch deactivate flows", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 批量启用流程
     */
    @PostMapping("/flows/batch/activate")
    public ResponseEntity<Void> batchActivateFlows(@RequestBody List<String> flowIds) {
        try {
            flowConfigRepository.batchActivate(flowIds);
            log.info("[ConfigManagement] batch activated flows: {}", flowIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to batch activate flows", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 批量删除流程
     */
    @PostMapping("/flows/batch/delete")
    public ResponseEntity<Void> batchDeleteFlows(@RequestBody List<String> flowIds) {
        try {
            flowConfigRepository.batchDelete(flowIds);
            log.info("[ConfigManagement] batch deleted flows: {}", flowIds);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to batch delete flows", e);
            return ResponseEntity.badRequest().build();
        }
    }
}
