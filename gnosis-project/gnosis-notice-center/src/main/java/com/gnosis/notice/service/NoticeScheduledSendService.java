package com.gnosis.notice.service;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendCreateRequest;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendQueryRequest;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendUpdateRequest;
import com.gnosis.notice.dto.scheduledsend.NoticeScheduledSendVO;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * 定时发送服务接口
 */
public interface NoticeScheduledSendService {

    /**
     * 分页查询定时发送列表
     */
    PageResult<NoticeScheduledSendVO> pageList(NoticeScheduledSendQueryRequest request);

    /**
     * 根据ID查询详情
     */
    NoticeScheduledSendVO detail(String id);

    /**
     * 创建定时发送任务
     */
    String create(NoticeScheduledSendCreateRequest request, String userId);

    /**
     * 更新定时发送任务
     */
    int update(NoticeScheduledSendUpdateRequest request, String userId);

    /**
     * 取消定时发送任务
     */
    int cancel(String id, String userId);

    /**
     * 删除定时发送任务
     */
    int delete(String id);

    /**
     * 批量删除
     */
    int batchDelete(List<String> ids);

    /**
     * 导出定时发送
     */
    void export(NoticeScheduledSendQueryRequest request, HttpServletResponse response) throws Exception;

    /**
     * 导入定时发送
     */
    int importExcel(InputStream inputStream, String userId) throws Exception;
}
