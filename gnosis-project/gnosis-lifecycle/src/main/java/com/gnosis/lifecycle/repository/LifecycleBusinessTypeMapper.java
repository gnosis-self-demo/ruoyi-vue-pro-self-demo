package com.gnosis.lifecycle.repository;

import com.gnosis.lifecycle.domain.LifecycleBusinessType;

import java.util.List;

/**
 * 业务类型 Mapper 接口
 */
public interface LifecycleBusinessTypeMapper {
    
    /**
     * 根据 ID 查询业务类型
     */
    LifecycleBusinessType selectById(String id);
    
    /**
     * 根据编码查询业务类型
     */
    LifecycleBusinessType selectByCode(String code);
    
    /**
     * 查询业务类型列表
     */
    List<LifecycleBusinessType> selectList(LifecycleBusinessType lifecycleBusinessType);
    
    /**
     * 插入业务类型
     */
    int insert(LifecycleBusinessType lifecycleBusinessType);
    
    /**
     * 更新业务类型
     */
    int updateById(LifecycleBusinessType lifecycleBusinessType);
    
    /**
     * 根据 ID 删除业务类型
     */
    int deleteById(String id);
    
    /**
     * 批量删除业务类型
     */
    int deleteByIds(String[] ids);
}
