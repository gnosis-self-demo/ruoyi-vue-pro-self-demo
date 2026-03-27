package lifecycle.repository;

import lifecycle.domain.LifecycleDownstreamTriggerConfig;

import java.util.List;

/**
 * 下游触发配置 Mapper 接口
 */
public interface LifecycleDownstreamTriggerConfigMapper {
    
    /**
     * 根据 ID 查询下游触发配置
     */
    LifecycleDownstreamTriggerConfig selectById(String id);
    
    /**
     * 根据终态 ID 查询下游触发配置列表
     */
    List<LifecycleDownstreamTriggerConfig> selectByFinalStateId(String finalStateId);
    
    /**
     * 查询下游触发配置列表
     */
    List<LifecycleDownstreamTriggerConfig> selectList(LifecycleDownstreamTriggerConfig config);
    
    /**
     * 插入下游触发配置
     */
    int insert(LifecycleDownstreamTriggerConfig config);
    
    /**
     * 更新下游触发配置
     */
    int updateById(LifecycleDownstreamTriggerConfig config);
    
    /**
     * 根据 ID 删除下游触发配置
     */
    int deleteById(String id);
    
    /**
     * 批量删除下游触发配置
     */
    int deleteByIds(String[] ids);
}
