package com.gnosis.notice.mapper;

import com.gnosis.notice.domain.NoticeGroup;
import com.gnosis.notice.dto.group.NoticeGroupQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息分组Mapper
 */
@Mapper
public interface NoticeGroupMapper {

    /**
     * 插入分组
     */
    int insert(NoticeGroup record);

    /**
     * 根据ID更新分组
     */
    int updateById(NoticeGroup record);

    /**
     * 根据ID查询分组
     */
    NoticeGroup selectById(@Param("id") String id);

    /**
     * 根据分组编码查询
     */
    NoticeGroup selectByCode(@Param("groupCode") String groupCode);

    /**
     * 条件查询列表
     */
    List<NoticeGroup> selectByCondition(NoticeGroupQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(NoticeGroupQueryRequest query);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") String id);

    /**
     * 批量删除
     */
    int deleteByIds(@Param("ids") List<String> ids);

    /**
     * 批量更新状态
     */
    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);
}
