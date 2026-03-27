package lifecycle.controller;

import lifecycle.dto.DownstreamConfigRequest;
import lifecycle.dto.DownstreamConfigVO;
import lifecycle.dto.EventPublishRequest;
import lifecycle.dto.FinalStateConfigRequest;
import lifecycle.service.ExitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 出口管理控制器
 */
@RestController
@RequestMapping("/api/lifecycle/exit")
public class ExitController {
    
    private static final Logger log = LoggerFactory.getLogger(ExitController.class);
    
    @Autowired
    private ExitService exitService;
    
    /**
     * 配置终态
     */
    @PostMapping("/final-state")
    public Map<String, Object> configureFinalState(@RequestBody FinalStateConfigRequest request,
                                                    @RequestHeader(value = "X-Operator-Id", required = false) String operatorId) {
        log.info("[ExitController] 配置终态请求：businessTypeId={}, stateId={}", 
                request.getBusinessTypeId(), request.getStateId());
        
        if (operatorId == null || operatorId.isEmpty()) {
            operatorId = "system";
        }
        
        return exitService.configureFinalState(request, operatorId);
    }
    
    /**
     * 获取终态配置列表
     */
    @GetMapping("/final-state/list")
    public Map<String, Object> listFinalStates(@RequestParam(value = "businessTypeId", required = false) String businessTypeId) {
        log.info("[ExitController] 查询终态配置列表：businessTypeId={}", businessTypeId);
        
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> list = exitService.listFinalStates(businessTypeId);
        response.put("success", true);
        response.put("data", list);
        response.put("total", list.size());
        
        return response;
    }
    
    /**
     * 配置下游触发
     */
    @PostMapping("/downstream")
    public Map<String, Object> configureDownstream(@RequestBody DownstreamConfigRequest request,
                                                    @RequestHeader(value = "X-Operator-Id", required = false) String operatorId) {
        log.info("[ExitController] 配置下游触发请求：finalStateId={}, triggerType={}", 
                request.getFinalStateId(), request.getTriggerType());
        
        if (operatorId == null || operatorId.isEmpty()) {
            operatorId = "system";
        }
        
        return exitService.configureDownstream(request, operatorId);
    }
    
    /**
     * 获取下游配置
     */
    @GetMapping("/downstream/{id}")
    public Map<String, Object> getDownstreamConfig(@PathVariable String id) {
        log.info("[ExitController] 查询下游配置详情：id={}", id);
        
        Map<String, Object> response = new HashMap<>();
        try {
            DownstreamConfigVO vo = exitService.getDownstreamConfig(id);
            response.put("success", true);
            response.put("data", vo);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    /**
     * 更新下游配置
     */
    @PutMapping("/downstream/{id}")
    public Map<String, Object> updateDownstream(@PathVariable String id,
                                                 @RequestBody DownstreamConfigRequest request,
                                                 @RequestHeader(value = "X-Operator-Id", required = false) String operatorId) {
        log.info("[ExitController] 更新下游配置请求：id={}", id);
        
        request.setId(id);
        
        if (operatorId == null || operatorId.isEmpty()) {
            operatorId = "system";
        }
        
        return exitService.updateDownstream(request, operatorId);
    }
    
    /**
     * 删除下游配置
     */
    @DeleteMapping("/downstream/{id}")
    public Map<String, Object> deleteDownstream(@PathVariable String id,
                                                 @RequestHeader(value = "X-Operator-Id", required = false) String operatorId) {
        log.info("[ExitController] 删除下游配置请求：id={}", id);
        
        if (operatorId == null || operatorId.isEmpty()) {
            operatorId = "system";
        }
        
        return exitService.deleteDownstream(id, operatorId);
    }
    
    /**
     * 批量删除下游配置
     */
    @DeleteMapping("/downstream/batch")
    public Map<String, Object> batchDeleteDownstream(@RequestParam String[] ids,
                                                      @RequestHeader(value = "X-Operator-Id", required = false) String operatorId) {
        log.info("[ExitController] 批量删除下游配置请求：ids={}", (Object[]) ids);
        
        if (operatorId == null || operatorId.isEmpty()) {
            operatorId = "system";
        }
        
        return exitService.batchDeleteDownstream(ids, operatorId);
    }
    
    /**
     * 发布事件
     */
    @PostMapping("/event/publish")
    public Map<String, Object> publishEvent(@RequestBody EventPublishRequest request,
                                             @RequestHeader(value = "X-Operator-Id", required = false) String operatorId) {
        log.info("[ExitController] 发布事件请求：eventId={}", request.getEventId());
        
        if (operatorId == null || operatorId.isEmpty()) {
            operatorId = "system";
        }
        
        return exitService.publishEvent(request, operatorId);
    }
    
    /**
     * 获取事件发布记录
     */
    @GetMapping("/event/publish/{eventId}")
    public Map<String, Object> getPublishRecord(@PathVariable String eventId) {
        log.info("[ExitController] 查询事件发布记录：eventId={}", eventId);
        
        return exitService.getPublishRecord(eventId);
    }
}
