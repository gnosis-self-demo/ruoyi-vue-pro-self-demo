package lifecycle.repository;

import lifecycle.domain.LifecycleEvent;

import java.util.List;

/**
 * 事件记录 Mapper 接口
 */
public interface LifecycleEventMapper {
    
    /**
     * 根据 ID 查询事件记录
     */
    LifecycleEvent selectById(String id);
    
    /**
     * 查询事件记录列表
     */
    List<LifecycleEvent> selectList(LifecycleEvent lifecycleEvent);
    
    /**
     * 插入事件记录
     */
    int insert(LifecycleEvent lifecycleEvent);
    
    /**
     * 更新事件记录
     */
    int updateById(LifecycleEvent lifecycleEvent);
    
    /**
     * 根据 ID 删除事件记录
     */
    int deleteById(String id);
    
    /**
     * 批量删除事件记录
     */
    int deleteByIds(String[] ids);
}
