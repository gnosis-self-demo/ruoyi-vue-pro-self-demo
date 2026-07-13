package com.gnosis.signature.mapper;

import com.gnosis.signature.domain.SignatureTemplate;
import com.gnosis.signature.dto.template.SignatureTemplateQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 签章模板Mapper
 */
@Mapper
public interface SignatureTemplateMapper {

    /**
     * 插入模板记录
     */
    int insert(SignatureTemplate record);

    /**
     * 根据ID更新模板记录
     */
    int updateById(SignatureTemplate record);

    /**
     * 根据ID查询模板记录
     */
    SignatureTemplate selectById(@Param("id") String id);

    /**
     * 条件查询列表
     */
    List<SignatureTemplate> selectByCondition(SignatureTemplateQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(SignatureTemplateQueryRequest query);

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
