package lifecycle.repository;

import lifecycle.domain.LifecycleFinalStateConfig;

import java.util.List;

/**
 * 终态配置 Mapper 接口
 */
public interface LifecycleFinalStateConfigMapper {
    
    /**
     * 根据 ID 查询终态配置
     */
    LifecycleFinalStateConfig selectById(String id);
    
    /**
     * 根据业务类型 ID 查询终态配置列表
     */
    List<LifecycleFinalStateConfig> selectByBusinessTypeId(String businessTypeId);
    
    /**
     * 查询终态配置列表
     */
    List<LifecycleFinalStateConfig> selectList(LifecycleFinalStateConfig config);
    
    /**
     * 插入终态配置
     */
    int insert(LifecycleFinalStateConfig config);
    
    /**
     * 更新终态配置
     */
    int updateById(LifecycleFinalStateConfig config);
    
    /**
     * 根据 ID 删除终态配置
     */
    int deleteById(String id);
}
