package com.gnosis.signature.mapper;

import com.gnosis.signature.domain.SignatureSeal;
import com.gnosis.signature.dto.seal.SignatureSealQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 印章Mapper
 */
@Mapper
public interface SignatureSealMapper {

    /**
     * 插入印章记录
     */
    int insert(SignatureSeal record);

    /**
     * 根据ID更新印章记录
     */
    int updateById(SignatureSeal record);

    /**
     * 根据ID查询印章记录
     */
    SignatureSeal selectById(@Param("id") String id);

    /**
     * 条件查询列表
     */
    List<SignatureSeal> selectByCondition(SignatureSealQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(SignatureSealQueryRequest query);

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
     * 根据ID列表批量查询印章
     */
    List<SignatureSeal> selectByIds(@Param("ids") List<String> ids);
}
