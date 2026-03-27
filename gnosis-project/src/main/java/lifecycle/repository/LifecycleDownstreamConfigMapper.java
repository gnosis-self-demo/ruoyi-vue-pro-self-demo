package lifecycle.repository;

import lifecycle.domain.LifecycleDownstreamConfig;

import java.util.List;

/**
 * 下游配置 Mapper 接口
 */
public interface LifecycleDownstreamConfigMapper {
    
    /**
     * 根据 ID 查询下游配置
     */
    LifecycleDownstreamConfig selectById(String id);
    
    /**
     * 根据终态 ID 查询下游配置列表
     */
    List<LifecycleDownstreamConfig> selectByFinalStateId(String finalStateId);
    
    /**
     * 查询下游配置列表
     */
    List<LifecycleDownstreamConfig> selectList(LifecycleDownstreamConfig lifecycleDownstreamConfig);
    
    /**
     * 插入下游配置
     */
    int insert(LifecycleDownstreamConfig lifecycleDownstreamConfig);
    
    /**
     * 更新下游配置
     */
    int updateById(LifecycleDownstreamConfig lifecycleDownstreamConfig);
    
    /**
     * 根据 ID 删除下游配置
     */
    int deleteById(String id);
    
    /**
     * 批量删除下游配置
     */
    int deleteByIds(String[] ids);
}
