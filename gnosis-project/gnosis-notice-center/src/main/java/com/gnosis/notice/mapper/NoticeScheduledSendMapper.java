package com.gnosis.notice.mapper;

import com.gnosis.notice.domain.NoticeScheduledSend;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 定时发送Mapper
 */
@Mapper
public interface NoticeScheduledSendMapper {

    /**
     * 插入定时发送记录
     */
    int insert(NoticeScheduledSend record);

    /**
     * 根据ID更新
     */
    int updateById(NoticeScheduledSend record);

    /**
     * 根据ID查询
     */
    NoticeScheduledSend selectById(@Param("id") String id);

    /**
     * 条件查询列表
     */
    List<NoticeScheduledSend> selectByCondition(NoticeScheduledSendQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(NoticeScheduledSendQueryRequest query);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") String id);

    /**
     * 批量删除
     */
    int deleteByIds(@Param("ids") List<String> ids);

    /**
     * 查询待发送列表（sendStatus=0且scheduledTime<=当前时间）
     */
    List<NoticeScheduledSend> selectPendingList();
}
