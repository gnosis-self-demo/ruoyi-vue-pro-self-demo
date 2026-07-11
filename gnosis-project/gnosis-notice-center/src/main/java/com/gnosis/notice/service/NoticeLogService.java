package com.gnosis.notice.service;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.dto.log.NoticeLogQueryRequest;
import com.gnosis.notice.dto.log.NoticeLogVO;

/**
 * 消息日志服务接口
 */
public interface NoticeLogService {

    /**
     * 分页查询日志列表
     */
    PageResult<NoticeLogVO> pageList(NoticeLogQueryRequest request);

    /**
     * 根据ID查询日志详情
     */
    NoticeLogVO detail(String id);

    /**
     * 重试发送
     */
    int retry(String id);
}
