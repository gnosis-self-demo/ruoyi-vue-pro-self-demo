package lifecycle.repository;

import lifecycle.domain.LifecycleEventPublishRecord;

import java.util.List;

/**
 * 事件发布记录 Mapper 接口
 */
public interface LifecycleEventPublishRecordMapper {
    
    /**
     * 根据 ID 查询事件发布记录
     */
    LifecycleEventPublishRecord selectById(String id);
    
    /**
     * 根据事件 ID 查询事件发布记录
     */
    LifecycleEventPublishRecord selectByEventId(String eventId);
    
    /**
     * 查询事件发布记录列表
     */
    List<LifecycleEventPublishRecord> selectList(LifecycleEventPublishRecord record);
    
    /**
     * 插入事件发布记录
     */
    int insert(LifecycleEventPublishRecord record);
    
    /**
     * 更新事件发布记录
     */
    int updateById(LifecycleEventPublishRecord record);
    
    /**
     * 根据 ID 删除事件发布记录
     */
    int deleteById(String id);
}
