package com.gnosis.signature.mapper;

import com.gnosis.signature.domain.SignatureFile;
import com.gnosis.signature.dto.file.SignatureFileQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 签章文件Mapper
 */
@Mapper
public interface SignatureFileMapper {

    /**
     * 插入文件记录
     */
    int insert(SignatureFile record);

    /**
     * 根据ID更新文件记录
     */
    int updateById(SignatureFile record);

    /**
     * 根据ID查询文件记录
     */
    SignatureFile selectById(@Param("id") String id);

    /**
     * 条件查询列表
     */
    List<SignatureFile> selectByCondition(SignatureFileQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(SignatureFileQueryRequest query);

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
     * 根据ID列表批量查询文件
     */
    List<SignatureFile> selectByIds(@Param("ids") List<String> ids);
}
