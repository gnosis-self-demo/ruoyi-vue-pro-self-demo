package com.gnosis.openplat.mapper;

import com.gnosis.openplat.domain.OpenplatSystem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 对接系统 Mapper 接口
 */
@Mapper
public interface OpenplatSystemMapper {
    
    /**
     * 根据 ID 查询系统信息
     */
    OpenplatSystem selectById(@Param("id") String id);
    
    /**
     * 根据系统编码查询系统信息
     */
    OpenplatSystem selectBySystemCode(@Param("systemCode") String systemCode);
    
    /**
     * 查询系统列表
     */
    List<OpenplatSystem> selectList(OpenplatSystem system);
    
    /**
     * 新增系统信息
     */
    int insert(OpenplatSystem system);
    
    /**
     * 修改系统信息
     */
    int update(OpenplatSystem system);
    
    /**
     * 删除系统信息
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 批量删除系统信息
     */
    int deleteByIds(@Param("ids") String[] ids);
}
