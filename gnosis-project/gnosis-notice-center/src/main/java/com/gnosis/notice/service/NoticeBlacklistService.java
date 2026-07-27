package com.gnosis.notice.service;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistCreateRequest;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistQueryRequest;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistUpdateRequest;
import com.gnosis.notice.dto.blacklist.NoticeBlacklistVO;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * 消息黑名单服务接口
 */
public interface NoticeBlacklistService {

    /**
     * 分页查询黑名单列表
     */
    PageResult<NoticeBlacklistVO> pageList(NoticeBlacklistQueryRequest request);

    /**
     * 根据ID查询黑名单详情
     */
    NoticeBlacklistVO detail(String id);

    /**
     * 创建黑名单
     */
    int create(NoticeBlacklistCreateRequest request, String userId);

    /**
     * 更新黑名单
     */
    int update(NoticeBlacklistUpdateRequest request, String userId);

    /**
     * 删除黑名单
     */
    int delete(String id);

    /**
     * 批量删除黑名单
     */
    int batchDelete(List<String> ids);

    /**
     * 批量启用
     */
    int batchEnable(List<String> ids);

    /**
     * 批量禁用
     */
    int batchDisable(List<String> ids);

    /**
     * 导出黑名单
     */
    void export(NoticeBlacklistQueryRequest request, HttpServletResponse response) throws Exception;

    /**
     * 导入黑名单
     */
    int importExcel(InputStream inputStream, String userId) throws Exception;

    /**
     * 检查用户是否在黑名单中
     */
    boolean isBlacklisted(String userId, String noticeType, String templateCode);
}
