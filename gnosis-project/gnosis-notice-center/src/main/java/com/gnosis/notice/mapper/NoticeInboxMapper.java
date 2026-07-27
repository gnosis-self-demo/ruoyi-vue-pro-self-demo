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

    int insert(NoticeInbox record);
    int batchInsert(@Param("list") List<NoticeInbox> list);
    int updateById(NoticeInbox record);
    NoticeInbox selectById(@Param("id") String id);
    List<NoticeInbox> selectByCondition(NoticeInboxQueryRequest query);
    Long countByCondition(NoticeInboxQueryRequest query);
    Long countUnread(@Param("userId") String userId);
    int markRead(@Param("id") String id);
    int batchMarkRead(@Param("ids") List<String> ids);
    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);
    int deleteById(@Param("id") String id);
    int deleteByIds(@Param("ids") List<String> ids);
}
