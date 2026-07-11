package com.gnosis.notice.mapper;

import com.gnosis.notice.domain.NoticeTemplate;
import com.gnosis.notice.dto.template.NoticeTemplateQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 消息模板Mapper
 */
@Mapper
public interface NoticeTemplateMapper {

    /**
     * 插入模板
     */
    int insert(NoticeTemplate record);

    /**
     * 根据ID更新模板
     */
    int updateById(NoticeTemplate record);

    /**
     * 根据ID查询模板
     */
    NoticeTemplate selectById(@Param("id") String id);

    /**
     * 根据模板编码查询
     */
    NoticeTemplate selectByCode(@Param("templateCode") String templateCode);

    /**
     * 条件查询列表
     */
    List<NoticeTemplate> selectByCondition(NoticeTemplateQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(NoticeTemplateQueryRequest query);

    /**
     * 根据ID删除
     */
    int deleteById(@Param("id") String id);

    /**
     * 批量删除
     */
    int deleteByIds(@Param("ids") List<String> ids);

    /**
     * 批量更新状态
     */
    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);
}
