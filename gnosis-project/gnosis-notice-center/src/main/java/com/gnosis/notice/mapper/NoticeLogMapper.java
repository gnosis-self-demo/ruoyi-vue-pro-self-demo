package com.gnosis.notice.mapper;

import com.gnosis.notice.domain.NoticeLog;
import com.gnosis.notice.dto.log.NoticeLogQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息日志Mapper
 */
@Mapper
public interface NoticeLogMapper {

    /**
     * 插入日志
     */
    int insert(NoticeLog record);

    /**
     * 根据ID更新日志
     */
    int updateById(NoticeLog record);

    /**
     * 根据ID查询日志
     */
    NoticeLog selectById(@Param("id") String id);

    /**
     * 条件查询列表
     */
    List<NoticeLog> selectByCondition(NoticeLogQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(NoticeLogQueryRequest query);

    /**
     * 查询需要重试的日志
     */
    List<NoticeLog> selectRetryList(@Param("maxRetry") Integer maxRetry);

    /**
     * 更新重试次数
     */
    int updateRetryCount(@Param("id") String id, @Param("retryCount") Integer retryCount);
}
