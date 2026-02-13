package gnosis.sample.dynamic.liteflow.web;

import gnosis.sample.dynamic.liteflow.entity.LiteFlowRule;
import gnosis.sample.dynamic.liteflow.service.LiteFlowRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * LiteFlow规则管理控制器
 */
@RestController
public class LiteFlowRuleController {
    
    @Autowired
    private LiteFlowRuleService liteFlowRuleService;
    
    /**
     * 获取所有启用的规则
     */
    @GetMapping("/liteflow/business-aspect/rules")
    public List<LiteFlowRule> getAllEnabledRules() {
        return liteFlowRuleService.getAllEnabledRules();
    }
    
    /**
     * 保存或更新规则
     */
    @PostMapping("/liteflow/business-aspect/rules")
    public String saveRule(@RequestBody LiteFlowRule rule) {
        liteFlowRuleService.saveRule(rule);
        return "Rule saved successfully";
    }
    
    /**
     * 删除规则
     */
    @DeleteMapping("/liteflow/business-aspect/rules/{id}")
    public String deleteRule(@PathVariable Long id) {
        liteFlowRuleService.deleteRule(id);
        return "Rule deleted successfully";
    }
    
    /**
     * 启用/禁用规则
     */
    @PutMapping("/liteflow/business-aspect/rules/{id}/status")
    public String toggleRuleStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        liteFlowRuleService.toggleRuleStatus(id, enabled);
        return "Rule status updated successfully";
    }
}