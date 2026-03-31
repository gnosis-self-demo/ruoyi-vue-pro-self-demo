package com.gnosis.lifecycle.repository;

import com.gnosis.lifecycle.domain.LifecycleEventMetadata;

import java.util.List;

/**
 * 事件元数据 Mapper 接口
 */
public interface LifecycleEventMetadataMapper {
    
    /**
     * 根据 ID 查询事件元数据
     */
    LifecycleEventMetadata selectById(String id);
    
    /**
     * 根据事件类型查询元数据列表
     */
    List<LifecycleEventMetadata> selectByEventType(String eventType);
    
    /**
     * 查询事件元数据列表
     */
    List<LifecycleEventMetadata> selectList(LifecycleEventMetadata lifecycleEventMetadata);
    
    /**
     * 插入事件元数据
     */
    int insert(LifecycleEventMetadata lifecycleEventMetadata);
    
    /**
     * 更新事件元数据
     */
    int updateById(LifecycleEventMetadata lifecycleEventMetadata);
    
    /**
     * 根据 ID 删除事件元数据
     */
    int deleteById(String id);
    
    /**
     * 批量删除事件元数据
     */
    int deleteByIds(String[] ids);
}
