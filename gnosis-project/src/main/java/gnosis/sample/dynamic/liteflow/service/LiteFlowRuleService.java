package gnosis.sample.dynamic.liteflow.service;

import gnosis.sample.dynamic.liteflow.entity.LiteFlowRule;
import gnosis.sample.dynamic.liteflow.manager.LiteFlowRuleManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * LiteFlow规则服务
 */
@Service
public class LiteFlowRuleService {
    
    @Autowired
    private LiteFlowRuleManager liteFlowRuleManager;
    
    /**
     * 获取所有启用的规则
     */
    public List<LiteFlowRule> getAllEnabledRules() {
        return liteFlowRuleManager.getAllEnabledRules();
    }
    
    /**
     * 保存或更新规则
     */
    public void saveRule(LiteFlowRule rule) {
        liteFlowRuleManager.saveRule(rule);
    }
    
    /**
     * 删除规则
     */
    public void deleteRule(Long id) {
        liteFlowRuleManager.deleteRule(id);
    }
    
    /**
     * 启用/禁用规则
     */
    public void toggleRuleStatus(Long id, Boolean enabled) {
        liteFlowRuleManager.toggleRuleStatus(id, enabled);
    }
}