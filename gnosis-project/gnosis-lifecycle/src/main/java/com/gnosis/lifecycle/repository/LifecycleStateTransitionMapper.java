package com.gnosis.lifecycle.repository;

import com.gnosis.lifecycle.domain.LifecycleStateTransition;

import java.util.List;

/**
 * 状态流转 Mapper 接口
 */
public interface LifecycleStateTransitionMapper {
    
    /**
     * 根据 ID 查询状态流转
     */
    LifecycleStateTransition selectById(String id);
    
    /**
     * 根据业务类型 ID 查询状态流转列表
     */
    List<LifecycleStateTransition> selectByBusinessTypeId(String businessTypeId);
    
    /**
     * 查询状态流转列表
     */
    List<LifecycleStateTransition> selectList(LifecycleStateTransition lifecycleStateTransition);
    
    /**
     * 插入状态流转
     */
    int insert(LifecycleStateTransition lifecycleStateTransition);
    
    /**
     * 更新状态流转
     */
    int updateById(LifecycleStateTransition lifecycleStateTransition);
    
    /**
     * 根据 ID 删除状态流转
     */
    int deleteById(String id);
    
    /**
     * 批量删除状态流转
     */
    int deleteByIds(String[] ids);
    
    /**
     * 根据业务类型 ID 删除状态流转
     */
    int deleteByBusinessTypeId(String businessTypeId);
}
