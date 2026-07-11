package com.gnosis.notice.service;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.dto.inbox.NoticeInboxCreateRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxQueryRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxVO;

import java.util.List;

/**
 * 站内信服务接口
 */
public interface NoticeInboxService {

    /**
     * 分页查询站内信列表
     */
    PageResult<NoticeInboxVO> pageList(NoticeInboxQueryRequest request);

    /**
     * 根据ID查询详情
     */
    NoticeInboxVO detail(String id);

    /**
     * 统计未读数量
     */
    Long countUnread(String userId);

    /**
     * 创建站内信
     */
    int create(NoticeInboxCreateRequest request, String userId);

    /**
     * 标记已读
     */
    int markRead(String id);

    /**
     * 批量标记已读
     */
    int batchMarkRead(List<String> ids);

    /**
     * 删除站内信
     */
    int delete(String id);

    /**
     * 批量删除
     */
    int batchDelete(List<String> ids);
}
