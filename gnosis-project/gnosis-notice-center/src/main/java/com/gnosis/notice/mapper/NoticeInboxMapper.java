package com.gnosis.notice.mapper;

import com.gnosis.notice.domain.NoticeInbox;
import com.gnosis.notice.dto.inbox.NoticeInboxQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 站内信Mapper
 */
@Mapper
public interface NoticeInboxMapper {

    /**
     * 插入站内信
     */
    int insert(NoticeInbox record);

    /**
     * 批量插入站内信
     */
    int batchInsert(@Param("list") List<NoticeInbox> list);

    /**
     * 根据ID查询
     */
    NoticeInbox selectById(@Param("id") String id);

    /**
     * 条件查询列表
     */
    List<NoticeInbox> selectByCondition(NoticeInboxQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(NoticeInboxQueryRequest query);

    /**
     * 统计未读数量
     */
    Long countUnread(@Param("userId") String userId);

    /**
     * 标记已读
     */
    int markRead(@Param("id") String id);

    /**
     * 批量标记已读
     */
    int batchMarkRead(@Param("ids") List<String> ids);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") String id);

    /**
     * 批量删除
     */
    int deleteByIds(@Param("ids") List<String> ids);
}
