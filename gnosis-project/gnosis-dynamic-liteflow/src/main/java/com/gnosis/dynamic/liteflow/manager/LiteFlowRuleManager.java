package com.gnosis.dynamic.liteflow.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.gnosis.dynamic.liteflow.entity.LiteFlowRule;
import com.gnosis.dynamic.liteflow.mapper.LiteFlowRuleMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**
 * LiteFlow规则Manager
 */
@Service
public class LiteFlowRuleManager {
    
    @Resource
    private LiteFlowRuleMapper liteFlowRuleMapper;
    
    /**
     * 获取所有启用的规则
     */
    public List<LiteFlowRule> getAllEnabledRules() {
        LambdaQueryWrapper<LiteFlowRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LiteFlowRule::getEnabled, true)
                   .orderByAsc(LiteFlowRule::getChainName);
        return liteFlowRuleMapper.selectList(queryWrapper);
    }
    
    /**
     * 根据链名称获取规则
     */
    public LiteFlowRule getRuleByChainName(String chainName) {
        LambdaQueryWrapper<LiteFlowRule> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LiteFlowRule::getChainName, chainName)
                   .eq(LiteFlowRule::getEnabled, true);
        List<LiteFlowRule> rules = liteFlowRuleMapper.selectList(queryWrapper);
        return rules.isEmpty() ? null : rules.get(0);
    }
    
    /**
     * 保存规则
     */
    @Transactional
    public void saveRule(LiteFlowRule rule) {
        LocalDateTime now = LocalDateTime.now();
        if (rule.getId() == null) {
            // 新增
            rule.setCreatedAt(now);
            rule.setUpdatedAt(now);
            if (rule.getVersion() == null) {
                rule.setVersion(1);
            }
            if (rule.getEnabled() == null) {
                rule.setEnabled(true);
            }
            if (rule.getRuleType() == null || rule.getRuleType().isEmpty()) {
                rule.setRuleType("script");
            }
            liteFlowRuleMapper.insert(rule);
        } else {
            // 更新
            rule.setUpdatedAt(now);
            liteFlowRuleMapper.updateById(rule);
        }
    }
    
    /**
     * 删除规则
     */
    @Transactional
    public void deleteRule(Long id) {
        liteFlowRuleMapper.deleteById(id);
    }
    
    /**
     * 启用/禁用规则
     */
    @Transactional
    public void toggleRuleStatus(Long id, Boolean enabled) {
        LambdaUpdateWrapper<LiteFlowRule> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(LiteFlowRule::getId, id)
                    .set(LiteFlowRule::getEnabled, enabled)
                    .set(LiteFlowRule::getUpdatedAt, LocalDateTime.now());
        liteFlowRuleMapper.update(null, updateWrapper);
    }
}