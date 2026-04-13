package com.gnosis.paramcheck.mapper;

import com.gnosis.paramcheck.domain.BusinessType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BusinessTypeMapper {

    BusinessType selectByCode(@Param("code") String code);

    List<BusinessType> selectAll();

    List<BusinessType> selectActive();

    List<BusinessType> selectByCondition(@Param("code") String code,
                                         @Param("name") String name,
                                         @Param("isActive") Boolean isActive);

    int insert(BusinessType businessType);

    int updateByCode(BusinessType businessType);

    int deleteByCode(@Param("code") String code);

    int batchDelete(@Param("codes") List<String> codes);

    int activate(@Param("code") String code);

    int deactivate(@Param("code") String code);

    int batchActivate(@Param("codes") List<String> codes);

    int batchDeactivate(@Param("codes") List<String> codes);

    int countByCondition(@Param("code") String code,
                         @Param("name") String name,
                         @Param("isActive") Boolean isActive);

    List<BusinessType> selectByConditionWithPaging(@Param("code") String code,
                                                    @Param("name") String name,
                                                    @Param("isActive") Boolean isActive,
                                                    @Param("offset") int offset,
                                                    @Param("pageSize") int pageSize);
}
