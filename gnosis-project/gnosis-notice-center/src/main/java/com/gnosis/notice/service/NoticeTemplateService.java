package com.gnosis.notice.service;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.domain.NoticeTemplate;
import com.gnosis.notice.dto.template.NoticeTemplateCreateRequest;
import com.gnosis.notice.dto.template.NoticeTemplateQueryRequest;
import com.gnosis.notice.dto.template.NoticeTemplateUpdateRequest;
import com.gnosis.notice.dto.template.NoticeTemplateVO;

import java.util.List;

/**
 * 消息模板服务接口
 */
public interface NoticeTemplateService {

    /**
     * 分页查询模板列表
     */
    PageResult<NoticeTemplateVO> pageList(NoticeTemplateQueryRequest request);

    /**
     * 根据ID查询模板详情
     */
    NoticeTemplateVO detail(String id);

    /**
     * 根据模板编码查询
     */
    NoticeTemplate getByCode(String templateCode);

    /**
     * 创建模板
     */
    String create(NoticeTemplateCreateRequest request, String userId);

    /**
     * 更新模板
     */
    int update(NoticeTemplateUpdateRequest request, String userId);

    /**
     * 删除模板
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
}
