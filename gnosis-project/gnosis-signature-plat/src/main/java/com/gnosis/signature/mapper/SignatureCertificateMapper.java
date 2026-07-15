package com.gnosis.signature.mapper;

import com.gnosis.signature.domain.SignatureCertificate;
import com.gnosis.signature.dto.certificate.SignatureCertificateQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 证书Mapper
 */
@Mapper
public interface SignatureCertificateMapper {

    /**
     * 插入证书记录
     */
    int insert(SignatureCertificate record);

    /**
     * 根据ID更新证书记录
     */
    int updateById(SignatureCertificate record);

    /**
     * 根据ID查询证书记录
     */
    SignatureCertificate selectById(@Param("id") String id);

    /**
     * 条件查询列表
     */
    List<SignatureCertificate> selectByCondition(SignatureCertificateQueryRequest query);

    /**
     * 条件统计总数
     */
    Long countByCondition(SignatureCertificateQueryRequest query);

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
     * 根据ID列表批量查询证书
     */
    List<SignatureCertificate> selectByIds(@Param("ids") List<String> ids);
}
