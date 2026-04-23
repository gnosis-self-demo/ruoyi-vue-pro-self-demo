package com.gnosis.process.mapper;

import com.gnosis.process.domain.BusinessResourceBinding;
import com.gnosis.process.dto.BusinessResourceBindingQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BusinessResourceBindingMapper {

    BusinessResourceBinding selectById(@Param("id") String id);

    List<BusinessResourceBinding> selectList(BusinessResourceBindingQueryRequest request);

    Long selectCount(BusinessResourceBindingQueryRequest request);

    List<BusinessResourceBinding> selectAllForExport(BusinessResourceBindingQueryRequest request);

    List<BusinessResourceBinding> selectByBusinessConfigId(@Param("businessConfigId") String businessConfigId);

    List<BusinessResourceBinding> selectByBusinessCodeAndType(@Param("businessCode") String businessCode, @Param("resourceType") String resourceType);

    int insert(BusinessResourceBinding binding);

    int updateById(BusinessResourceBinding binding);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int batchUpdateActive(@Param("ids") List<String> ids, @Param("isActive") Boolean isActive, @Param("updateUserId") String updateUserId);
}
