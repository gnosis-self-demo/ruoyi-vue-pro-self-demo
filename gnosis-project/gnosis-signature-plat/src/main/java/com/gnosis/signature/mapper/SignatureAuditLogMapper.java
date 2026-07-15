package com.gnosis.signature.mapper;

import com.gnosis.signature.domain.SignatureAuditLog;
import com.gnosis.signature.dto.audit.SignatureAuditLogQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 审计日志Mapper
 */
@Mapper
public interface SignatureAuditLogMapper {

    /**
     * 插入审计日志记录
     */
    int insert(SignatureAuditLog record);

    /**
     * 根据ID更新审计日志记录
     */
    int updateById(SignatureAuditLog record);

    /**
     * 根据ID查询审计日志记录
     */
    SignatureAuditLog selectById(@Param("id") String id);

    /**
     * 根据ID批量查询审计日志记录
     */
    List<SignatureAuditLog> selectByIds(@Param("ids") List<String> ids);

    /**
     * 条件查询列表
     */
    List<SignatureAuditLog> selectByCondition(SignatureAuditLogQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(SignatureAuditLogQueryRequest query);

    /**
     * 根据ID删除审计日志记录
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
