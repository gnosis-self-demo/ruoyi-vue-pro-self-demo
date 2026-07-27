package com.gnosis.notice.mapper;

import com.gnosis.notice.domain.NoticeTemplateVersion;
import com.gnosis.notice.dto.templateversion.NoticeTemplateVersionQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 模板版本Mapper
 */
@Mapper
public interface NoticeTemplateVersionMapper {

    /**
     * 插入模板版本
     */
    int insert(NoticeTemplateVersion record);

    /**
     * 根据ID更新模板版本
     */
    int updateById(NoticeTemplateVersion record);

    /**
     * 根据ID查询模板版本
     */
    NoticeTemplateVersion selectById(@Param("id") String id);

    /**
     * 条件查询列表
     */
    List<NoticeTemplateVersion> selectByCondition(NoticeTemplateVersionQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(NoticeTemplateVersionQueryRequest query);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") String id);

    /**
     * 根据模板ID查询所有版本（按版本号降序）
     */
    List<NoticeTemplateVersion> selectByTemplateId(@Param("templateId") String templateId);
}
