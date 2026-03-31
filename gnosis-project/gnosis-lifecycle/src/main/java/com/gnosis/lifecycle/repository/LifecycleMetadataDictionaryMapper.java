package com.gnosis.lifecycle.repository;

import com.gnosis.lifecycle.domain.LifecycleMetadataDictionary;

import java.util.List;

/**
 * 元数据字典 Mapper 接口
 */
public interface LifecycleMetadataDictionaryMapper {
    
    /**
     * 根据 ID 查询元数据字典
     */
    LifecycleMetadataDictionary selectById(String id);
    
    /**
     * 根据业务类型 ID 查询元数据字典列表
     */
    List<LifecycleMetadataDictionary> selectByBusinessTypeId(String businessTypeId);
    
    /**
     * 查询元数据字典列表
     */
    List<LifecycleMetadataDictionary> selectList(LifecycleMetadataDictionary metadata);
    
    /**
     * 插入元数据字典
     */
    int insert(LifecycleMetadataDictionary metadata);
    
    /**
     * 更新元数据字典
     */
    int updateById(LifecycleMetadataDictionary metadata);
    
    /**
     * 根据 ID 删除元数据字典
     */
    int deleteById(String id);
    
    /**
     * 批量删除元数据字典
     */
    int deleteByIds(String[] ids);
}
