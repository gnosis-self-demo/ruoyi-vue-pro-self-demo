package com.gnosis.notice.service;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.dto.log.NoticeLogQueryRequest;
import com.gnosis.notice.dto.log.NoticeLogVO;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;

/**
 * 消息日志服务接口 - 修复版: 添加导入导出
 */
public interface NoticeLogService {

    PageResult<NoticeLogVO> pageList(NoticeLogQueryRequest request);

    NoticeLogVO detail(String id);

    /** 重试发送 - 修复: 递增retry_count */
    int retry(String id);

    /** 导出日志Excel */
    void export(NoticeLogQueryRequest request, HttpServletResponse response) throws Exception;

    /** 导入日志Excel */
    int importExcel(InputStream inputStream, String userId) throws Exception;
}
