package com.gnosis.notice.service;

import com.gnosis.common.dto.PageResult;
import com.gnosis.notice.dto.templateversion.NoticeTemplateVersionQueryRequest;
import com.gnosis.notice.dto.templateversion.NoticeTemplateVersionVO;

import java.util.List;

/**
 * 模板版本服务接口
 */
public interface NoticeTemplateVersionService {

    /**
     * 分页查询模板版本列表
     */
    PageResult<NoticeTemplateVersionVO> pageList(NoticeTemplateVersionQueryRequest request);

    /**
     * 根据ID查询版本详情
     */
    NoticeTemplateVersionVO detail(String id);

    /**
     * 从模板表读取当前模板内容创建版本记录
     */
    String createVersion(String templateId, String changeRemark, String userId);

    /**
     * 查询某模板的所有版本
     */
    List<NoticeTemplateVersionVO> listByTemplateId(String templateId);

    /**
     * 删除版本
     */
    int delete(String id);
}
