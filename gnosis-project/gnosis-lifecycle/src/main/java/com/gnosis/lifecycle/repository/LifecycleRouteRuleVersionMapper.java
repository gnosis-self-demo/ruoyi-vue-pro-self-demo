package com.gnosis.lifecycle.repository;

import com.gnosis.lifecycle.domain.LifecycleRouteRuleVersion;

import java.util.List;

/**
 * 规则版本历史 Mapper 接口
 */
public interface LifecycleRouteRuleVersionMapper {
    
    /**
     * 根据 ID 查询规则版本历史
     */
    LifecycleRouteRuleVersion selectById(String id);
    
    /**
     * 根据规则 ID 查询版本历史列表
     */
    List<LifecycleRouteRuleVersion> selectByRuleId(String ruleId);
    
    /**
     * 查询规则版本历史列表
     */
    List<LifecycleRouteRuleVersion> selectList(LifecycleRouteRuleVersion version);
    
    /**
     * 插入规则版本历史
     */
    int insert(LifecycleRouteRuleVersion version);
    
    /**
     * 更新规则版本历史
     */
    int updateById(LifecycleRouteRuleVersion version);
    
    /**
     * 根据 ID 删除规则版本历史
     */
    int deleteById(String id);
    
    /**
     * 根据规则 ID 删除所有版本
     */
    int deleteByRuleId(String ruleId);
}
