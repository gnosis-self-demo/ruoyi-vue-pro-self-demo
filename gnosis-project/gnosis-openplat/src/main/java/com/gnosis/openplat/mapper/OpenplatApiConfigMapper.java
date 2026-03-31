package com.gnosis.openplat.mapper;

import com.gnosis.openplat.domain.OpenplatApiConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * API 配置 Mapper 接口
 */
@Mapper
public interface OpenplatApiConfigMapper {
    
    /**
     * 根据 ID 查询 API 配置信息
     */
    OpenplatApiConfig selectById(@Param("id") String id);
    
    /**
     * 根据 API 编码查询 API 配置信息
     */
    OpenplatApiConfig selectByApiCode(@Param("apiCode") String apiCode);
    
    /**
     * 根据 API 路径查询 API 配置信息
     */
    OpenplatApiConfig selectByApiPath(@Param("apiPath") String apiPath);
    
    /**
     * 查询 API 配置列表
     */
    List<OpenplatApiConfig> selectList(OpenplatApiConfig apiConfig);
    
    /**
     * 新增 API 配置信息
     */
    int insert(OpenplatApiConfig apiConfig);
    
    /**
     * 修改 API 配置信息
     */
    int update(OpenplatApiConfig apiConfig);
    
    /**
     * 删除 API 配置信息
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 批量删除 API 配置信息
     */
    int deleteByIds(@Param("ids") String[] ids);
}
