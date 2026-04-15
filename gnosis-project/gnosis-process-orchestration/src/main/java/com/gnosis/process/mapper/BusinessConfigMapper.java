package com.gnosis.process.mapper;

import com.gnosis.process.domain.BusinessConfig;
import com.gnosis.process.dto.BusinessConfigQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BusinessConfigMapper {

    BusinessConfig selectById(@Param("id") String id);

    BusinessConfig selectByCode(@Param("code") String code);

    List<BusinessConfig> selectList(BusinessConfigQueryRequest request);

    Long selectCount(BusinessConfigQueryRequest request);

    int insert(BusinessConfig businessConfig);

    int updateById(BusinessConfig businessConfig);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") String status, @Param("updateUserId") String updateUserId);
}
