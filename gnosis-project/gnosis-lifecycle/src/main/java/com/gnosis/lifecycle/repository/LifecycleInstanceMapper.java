package com.gnosis.lifecycle.repository;

import com.gnosis.lifecycle.domain.LifecycleInstance;

import java.util.List;

/**
 * 生命周期实例 Mapper 接口
 */
public interface LifecycleInstanceMapper {
    
    /**
     * 根据 ID 查询生命周期实例
     */
    LifecycleInstance selectById(String id);
    
    /**
     * 根据业务类型 ID 和业务 ID 查询实例
     */
    LifecycleInstance selectByBusinessId(String businessTypeId, String businessId);
    
    /**
     * 查询生命周期实例列表
     */
    List<LifecycleInstance> selectList(LifecycleInstance lifecycleInstance);
    
    /**
     * 插入生命周期实例
     */
    int insert(LifecycleInstance lifecycleInstance);
    
    /**
     * 更新生命周期实例
     */
    int updateById(LifecycleInstance lifecycleInstance);
    
    /**
     * 根据 ID 删除生命周期实例
     */
    int deleteById(String id);
    
    /**
     * 批量删除生命周期实例
     */
    int deleteByIds(String[] ids);
}
