package com.gnosis.lifecycle.repository;

import com.gnosis.lifecycle.domain.LifecycleRouteRule;

import java.util.List;

/**
 * 路由规则 Mapper 接口
 */
public interface LifecycleRouteRuleMapper {
    
    /**
     * 根据 ID 查询路由规则
     */
    LifecycleRouteRule selectById(String id);
    
    /**
     * 查询路由规则列表
     */
    List<LifecycleRouteRule> selectList(LifecycleRouteRule lifecycleRouteRule);
    
    /**
     * 插入路由规则
     */
    int insert(LifecycleRouteRule lifecycleRouteRule);
    
    /**
     * 更新路由规则
     */
    int updateById(LifecycleRouteRule lifecycleRouteRule);
    
    /**
     * 根据 ID 删除路由规则
     */
    int deleteById(String id);
    
    /**
     * 批量删除路由规则
     */
    int deleteByIds(String[] ids);
}
