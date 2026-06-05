package com.gnosis.paramcheck.controller;

import com.gnosis.common.dto.BaseResponse;
import com.gnosis.paramcheck.domain.ValidationFlow;
import com.gnosis.paramcheck.dto.PageResult;
import com.gnosis.paramcheck.service.FlowConfigService;
import com.gnosis.paramcheck.service.FlowRefreshService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/api/paramcheck/config")
public class ConfigManagementController {

    private static final Logger log = LoggerFactory.getLogger(ConfigManagementController.class);

    @Autowired
    private FlowConfigService flowConfigService;

    @Autowired
    private FlowRefreshService flowRefreshService;

    @GetMapping("/flows/page")
    public BaseResponse<PageResult<ValidationFlow>> pageQuery(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String flowId,
            @RequestParam(required = false) String flowName,
            @RequestParam(required = false) String businessType,
            @RequestParam(required = false) String modeType,
            @RequestParam(required = false) Boolean isActive) {
        com.gnosis.paramcheck.dto.PageRequest pageRequest = new com.gnosis.paramcheck.dto.PageRequest();
        pageRequest.setPage(page);
        pageRequest.setPageSize(pageSize);
        PageResult<ValidationFlow> result = flowConfigService.findPage(flowId, flowName, businessType, modeType, isActive, pageRequest);
        return BaseResponse.success(result);
    }

    @GetMapping("/flows")
    public BaseResponse<List<ValidationFlow>> getActiveFlows() {
        return BaseResponse.success(flowConfigService.findAllActive());
    }

    @GetMapping("/flows/all")
    public BaseResponse<List<ValidationFlow>> getAllFlows() {
        return BaseResponse.success(flowConfigService.findAll());
    }

    @GetMapping("/flows/{flowId}")
    public BaseResponse<ValidationFlow> getFlow(@PathVariable String flowId) {
        ValidationFlow flow = flowConfigService.findById(flowId);
        if (flow == null) {
            return BaseResponse.error(404, "Flow not found: " + flowId);
        }
        return BaseResponse.success(flow);
    }

    @PostMapping("/flows")
    public BaseResponse<Void> saveFlow(@RequestBody ValidationFlow flow) {
        try {
            flowConfigService.save(flow);
            flowRefreshService.refreshFlow(flow.getFlowId());
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to save flow: {}", flow.getFlowId(), e);
            return BaseResponse.error("保存失败: " + e.getMessage());
        }
    }

    @PutMapping("/flows")
    public BaseResponse<Void> updateFlow(@RequestBody ValidationFlow flow) {
        try {
            flowConfigService.save(flow);
            flowRefreshService.refreshFlow(flow.getFlowId());
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to update flow: {}", flow.getFlowId(), e);
            return BaseResponse.error("更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/flows/{flowId}")
    public BaseResponse<Void> deleteFlow(@PathVariable String flowId) {
        try {
            flowConfigService.delete(flowId);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to delete flow: {}", flowId, e);
            return BaseResponse.error("删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/flows/batch/delete")
    public BaseResponse<Void> batchDeleteFlows(@RequestBody List<String> flowIds) {
        try {
            flowConfigService.batchDelete(flowIds);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to batch delete flows", e);
            return BaseResponse.error("批量删除失败: " + e.getMessage());
        }
    }

    @PostMapping("/flows/batch/activate")
    public BaseResponse<Void> batchActivateFlows(@RequestBody List<String> flowIds) {
        try {
            flowConfigService.batchActivate(flowIds);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to batch activate flows", e);
            return BaseResponse.error("批量启用失败: " + e.getMessage());
        }
    }

    @PostMapping("/flows/batch/deactivate")
    public BaseResponse<Void> batchDeactivateFlows(@RequestBody List<String> flowIds) {
        try {
            flowConfigService.batchDeactivate(flowIds);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to batch deactivate flows", e);
            return BaseResponse.error("批量禁用失败: " + e.getMessage());
        }
    }

    @PostMapping("/flows/{flowId}/refresh")
    public BaseResponse<Void> refreshFlow(@PathVariable String flowId) {
        try {
            flowRefreshService.refreshFlow(flowId);
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to refresh flow: {}", flowId, e);
            return BaseResponse.error("刷新失败: " + e.getMessage());
        }
    }

    @PostMapping("/flows/refresh-all")
    public BaseResponse<Void> refreshAllFlows() {
        try {
            flowRefreshService.refreshAllFlows();
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ConfigManagement] failed to refresh all flows", e);
            return BaseResponse.error("刷新失败: " + e.getMessage());
        }
    }

    @GetMapping("/flows/export")
    public void exportFlows(HttpServletResponse response,
                            @RequestParam(required = false) String flowId,
                            @RequestParam(required = false) String flowName,
                            @RequestParam(required = false) String businessType,
                            @RequestParam(required = false) String modeType,
                            @RequestParam(required = false) Boolean isActive) {
        try {
            List<ValidationFlow> flows = flowConfigService.findAll();
            String json = com.alibaba.fastjson.JSON.toJSONString(flows);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment;filename=validation_flows.json");
            response.getWriter().write(json);
            response.getWriter().flush();
        } catch (Exception e) {
            log.error("[ConfigManagement] export failed", e);
        }
    }

    @PostMapping("/flows/import")
    public BaseResponse<Void> importFlows(@RequestBody List<ValidationFlow> flows) {
        try {
            for (ValidationFlow flow : flows) {
                flowConfigService.save(flow);
            }
            return BaseResponse.success();
        } catch (Exception e) {
            log.error("[ConfigManagement] import failed", e);
            return BaseResponse.error("导入失败: " + e.getMessage());
        }
    }
}
