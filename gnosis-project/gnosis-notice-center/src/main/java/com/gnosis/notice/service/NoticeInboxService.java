package com.gnosis.notice.service;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.dto.inbox.NoticeInboxCreateRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxQueryRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxUpdateRequest;
import com.gnosis.notice.dto.inbox.NoticeInboxVO;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * 站内信服务接口 - 修复版: 添加编辑修改/导入导出/批量启用禁用
 */
public interface NoticeInboxService {

    PageResult<NoticeInboxVO> pageList(NoticeInboxQueryRequest request);
    NoticeInboxVO detail(String id);
    Long countUnread(String userId);
    int create(NoticeInboxCreateRequest request, String userId);
    /** 编辑修改站内信 */
    int update(NoticeInboxUpdateRequest request, String userId);
    int markRead(String id);
    int batchMarkRead(List<String> ids);
    int delete(String id);
    int batchDelete(List<String> ids);
    /** 批量启用(恢复)站内信 */
    int batchEnable(List<String> ids);
    /** 批量禁用(归档)站内信 */
    int batchDisable(List<String> ids);
    /** 导出站内信Excel */
    void export(NoticeInboxQueryRequest request, HttpServletResponse response) throws Exception;
    /** 导入站内信Excel */
    int importExcel(InputStream inputStream, String userId) throws Exception;
}
