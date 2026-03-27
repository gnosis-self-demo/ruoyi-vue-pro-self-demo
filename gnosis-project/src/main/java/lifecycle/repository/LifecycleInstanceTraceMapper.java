package lifecycle.repository;

import lifecycle.domain.LifecycleInstanceTrace;

import java.util.List;

/**
 * 实例追踪 Mapper 接口
 */
public interface LifecycleInstanceTraceMapper {
    
    /**
     * 根据 ID 查询实例追踪
     */
    LifecycleInstanceTrace selectById(String id);
    
    /**
     * 根据业务实例 ID 查询实例追踪列表
     */
    List<LifecycleInstanceTrace> selectByBusinessInstanceId(String businessInstanceId);
    
    /**
     * 查询实例追踪列表
     */
    List<LifecycleInstanceTrace> selectList(LifecycleInstanceTrace trace);
    
    /**
     * 插入实例追踪
     */
    int insert(LifecycleInstanceTrace trace);
    
    /**
     * 更新实例追踪
     */
    int updateById(LifecycleInstanceTrace trace);
    
    /**
     * 根据 ID 删除实例追踪
     */
    int deleteById(String id);
    
    /**
     * 批量删除实例追踪
     */
    int deleteByIds(String[] ids);
}
