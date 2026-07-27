package com.gnosis.notice.mapper;

import com.gnosis.notice.domain.NoticeBlacklist;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息黑名单Mapper
 */
@Mapper
public interface NoticeBlacklistMapper {

    /**
     * 插入黑名单
     */
    int insert(NoticeBlacklist record);

    /**
     * 根据ID更新黑名单
     */
    int updateById(NoticeBlacklist record);

    /**
     * 根据ID查询黑名单
     */
    NoticeBlacklist selectById(@Param("id") String id);

    /**
     * 条件查询列表
     */
    List<NoticeBlacklist> selectByCondition(NoticeBlacklistQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(NoticeBlacklistQueryRequest query);

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

    /**
     * 检查黑名单（返回匹配数）
     */
    Long checkBlacklist(@Param("userId") String userId, @Param("noticeType") String noticeType, @Param("templateCode") String templateCode);
}
