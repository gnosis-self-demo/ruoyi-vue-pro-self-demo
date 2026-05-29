package com.gnosis.accounts.mapper;

import com.gnosis.accounts.domain.AccSubject;
import com.gnosis.accounts.dto.subject.AccSubjectQueryRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AccSubjectMapper {

    int insert(AccSubject record);

    int updateById(AccSubject record);

    AccSubject selectById(@Param("id") String id);

    AccSubject selectBySubjectCode(@Param("subjectCode") String subjectCode);

    List<AccSubject> selectByCondition(AccSubjectQueryRequest query);

    Long countByCondition(AccSubjectQueryRequest query);

    int deleteById(@Param("id") String id);

    int deleteByIds(@Param("ids") List<String> ids);

    int batchUpdateStatus(@Param("ids") List<String> ids, @Param("status") Integer status);
}
