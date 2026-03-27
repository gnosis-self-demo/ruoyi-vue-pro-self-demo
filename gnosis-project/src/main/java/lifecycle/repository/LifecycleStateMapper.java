package lifecycle.repository;

import lifecycle.domain.LifecycleState;

import java.util.List;

/**
 * 状态节点 Mapper 接口
 */
public interface LifecycleStateMapper {
    
    /**
     * 根据 ID 查询状态节点
     */
    LifecycleState selectById(String id);
    
    /**
     * 根据业务类型 ID 查询状态列表
     */
    List<LifecycleState> selectByBusinessTypeId(String businessTypeId);
    
    /**
     * 查询状态节点列表
     */
    List<LifecycleState> selectList(LifecycleState lifecycleState);
    
    /**
     * 插入状态节点
     */
    int insert(LifecycleState lifecycleState);
    
    /**
     * 更新状态节点
     */
    int updateById(LifecycleState lifecycleState);
    
    /**
     * 根据 ID 删除状态节点
     */
    int deleteById(String id);
    
    /**
     * 批量删除状态节点
     */
    int deleteByIds(String[] ids);
    
    /**
     * 根据业务类型 ID 删除状态节点
     */
    int deleteByBusinessTypeId(String businessTypeId);
    
    /**
     * 根据状态编码和业务类型 ID 查询状态节点
     */
    LifecycleState selectByCodeAndBusinessTypeId(String stateCode, String businessTypeId);
}
