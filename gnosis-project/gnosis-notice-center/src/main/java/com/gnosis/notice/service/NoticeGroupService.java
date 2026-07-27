package com.gnosis.notice.service;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.dto.group.NoticeGroupCreateRequest;
import com.gnosis.notice.dto.group.NoticeGroupQueryRequest;
import com.gnosis.notice.dto.group.NoticeGroupUpdateRequest;
import com.gnosis.notice.dto.group.NoticeGroupVO;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.util.List;

/**
 * 消息分组服务接口
 */
public interface NoticeGroupService {

    /**
     * 分页查询分组列表
     */
    PageResult<NoticeGroupVO> pageList(NoticeGroupQueryRequest request);

    /**
     * 根据ID查询分组详情
     */
    NoticeGroupVO detail(String id);

    /**
     * 根据分组编码查询
     */
    NoticeGroupVO getByCode(String groupCode);

    /**
     * 创建分组
     */
    String create(NoticeGroupCreateRequest request, String userId);

    /**
     * 更新分组
     */
    int update(NoticeGroupUpdateRequest request, String userId);

    /**
     * 删除分组
     */
    int delete(String id);

    /**
     * 批量删除
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
     * 导出分组
     */
    void export(NoticeGroupQueryRequest request, HttpServletResponse response) throws Exception;

    /**
     * 导入分组
     */
    int importExcel(InputStream inputStream, String userId) throws Exception;
}
