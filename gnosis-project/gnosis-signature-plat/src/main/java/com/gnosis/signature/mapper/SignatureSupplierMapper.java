package com.gnosis.signature.mapper;

import com.gnosis.signature.domain.SignatureSupplier;
import com.gnosis.signature.dto.supplier.SignatureSupplierQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 供应商 Mapper 接口
 */
@Mapper
public interface SignatureSupplierMapper {

    /**
     * 新增供应商
     */
    int insert(SignatureSupplier supplier);

    /**
     * 根据ID更新供应商
     */
    int updateById(SignatureSupplier supplier);

    /**
     * 根据ID查询供应商
     */
    SignatureSupplier selectById(@Param("id") String id);

    /**
     * 根据条件查询供应商列表
     */
    List<SignatureSupplier> selectByCondition(SignatureSupplierQueryRequest query);

    /**
     * 根据条件统计供应商数量
     */
    Long countByCondition(SignatureSupplierQueryRequest query);

    /**
     * 根据ID删除供应商
     */
    int deleteById(@Param("id") String id);

    /**
     * 批量删除供应商
     */
    int deleteByIds(@Param("ids") List<String> ids);

    /**
     * 批量更新供应商状态
     */
    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);
}
