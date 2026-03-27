package lifecycle.controller;

import lifecycle.dto.*;
import lifecycle.service.GovernanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 配置治理控制器
 */
@RestController
@RequestMapping("/api/lifecycle/governance")
public class GovernanceController {
    
    private static final Logger log = LoggerFactory.getLogger(GovernanceController.class);
    
    @Autowired
    private GovernanceService governanceService;
    
    /**
     * 创建元数据
     */
    @PostMapping("/metadata")
    public Map<String, Object> createMetadata(@RequestBody MetadataCreateRequest request,
                                               @RequestHeader(value = "X-Operator-Id", required = false) String operatorId) {
        log.info("[GovernanceController] 创建元数据请求：businessTypeId={}, fieldName={}", 
                request.getBusinessTypeId(), request.getFieldName());
        
        if (operatorId == null || operatorId.isEmpty()) {
            operatorId = "system";
        }
        
        return governanceService.createMetadata(request, operatorId);
    }
    
    /**
     * 获取元数据列表
     */
    @GetMapping("/metadata/list")
    public Map<String, Object> listMetadata(@RequestParam(value = "businessTypeId", required = false) String businessTypeId) {
        log.info("[GovernanceController] 查询元数据列表：businessTypeId={}", businessTypeId);
        
        Map<String, Object> response = new HashMap<>();
        List<MetadataVO> list = governanceService.listMetadata(businessTypeId);
        response.put("success", true);
        response.put("data", list);
        response.put("total", list.size());
        
        return response;
    }
    
    /**
     * 获取规则版本历史
     */
    @GetMapping("/rule-version/{ruleId}")
    public Map<String, Object> getRuleVersionHistory(@PathVariable String ruleId) {
        log.info("[GovernanceController] 查询规则版本历史：ruleId={}", ruleId);
        
        Map<String, Object> response = new HashMap<>();
        List<RuleVersionHistoryVO> list = governanceService.getRuleVersionHistory(ruleId);
        response.put("success", true);
        response.put("data", list);
        response.put("total", list.size());
        
        return response;
    }
    
    /**
     * 回滚规则版本
     */
    @PostMapping("/rule-version/{id}/rollback")
    public Map<String, Object> rollbackRuleVersion(@PathVariable String id,
                                                    @RequestHeader(value = "X-Operator-Id", required = false) String operatorId) {
        log.info("[GovernanceController] 回滚规则版本请求：versionId={}", id);
        
        if (operatorId == null || operatorId.isEmpty()) {
            operatorId = "system";
        }
        
        return governanceService.rollbackRuleVersion(id, operatorId);
    }
    
    /**
     * 获取实例追踪
     */
    @GetMapping("/trace/{businessInstanceId}")
    public Map<String, Object> getInstanceTrace(@PathVariable String businessInstanceId) {
        log.info("[GovernanceController] 查询实例追踪：businessInstanceId={}", businessInstanceId);
        
        Map<String, Object> response = new HashMap<>();
        List<InstanceTraceVO> list = governanceService.getInstanceTrace(businessInstanceId);
        response.put("success", true);
        response.put("data", list);
        response.put("total", list.size());
        
        return response;
    }
    
    /**
     * 获取追踪看板数据
     */
    @GetMapping("/trace/dashboard")
    public Map<String, Object> getTraceDashboard() {
        log.info("[GovernanceController] 查询追踪看板数据");
        
        Map<String, Object> response = new HashMap<>();
        TraceDashboardVO dashboard = governanceService.getTraceDashboard();
        response.put("success", true);
        response.put("data", dashboard);
        
        return response;
    }
}
