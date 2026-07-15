package com.gnosis.signature.mapper;

import com.gnosis.signature.domain.SignatureProcess;
import com.gnosis.signature.dto.process.SignatureProcessQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 签章流程Mapper
 */
@Mapper
public interface SignatureProcessMapper {

    /**
     * 插入流程
     */
    int insert(SignatureProcess record);

    /**
     * 根据ID更新流程
     */
    int updateById(SignatureProcess record);

    /**
     * 根据ID查询流程
     */
    SignatureProcess selectById(@Param("id") String id);

    /**
     * 条件查询流程列表
     */
    List<SignatureProcess> selectByCondition(SignatureProcessQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(SignatureProcessQueryRequest query);

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

    /**
     * 根据ID列表批量查询流程
     */
    List<SignatureProcess> selectByIds(@Param("ids") List<String> ids);
}
