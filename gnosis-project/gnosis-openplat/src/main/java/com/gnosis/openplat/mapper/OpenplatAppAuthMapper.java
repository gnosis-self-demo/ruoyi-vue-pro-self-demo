package com.gnosis.openplat.mapper;

import com.gnosis.openplat.domain.OpenplatAppAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 应用认证 Mapper 接口
 */
@Mapper
public interface OpenplatAppAuthMapper {
    
    /**
     * 根据 ID 查询应用认证信息
     */
    OpenplatAppAuth selectById(@Param("id") String id);
    
    /**
     * 根据 app_id 查询应用认证信息
     */
    OpenplatAppAuth selectByAppId(@Param("appId") String appId);
    
    /**
     * 查询应用认证列表
     */
    List<OpenplatAppAuth> selectList(OpenplatAppAuth appAuth);
    
    /**
     * 新增应用认证信息
     */
    int insert(OpenplatAppAuth appAuth);
    
    /**
     * 修改应用认证信息
     */
    int update(OpenplatAppAuth appAuth);
    
    /**
     * 删除应用认证信息
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 批量删除应用认证信息
     */
    int deleteByIds(@Param("ids") String[] ids);
}
