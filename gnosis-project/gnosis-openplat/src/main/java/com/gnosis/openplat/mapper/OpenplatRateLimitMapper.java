package com.gnosis.openplat.mapper;

import com.gnosis.openplat.domain.OpenplatRateLimit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 限流配置 Mapper 接口
 */
@Mapper
public interface OpenplatRateLimitMapper {
    
    /**
     * 根据 ID 查询限流配置信息
     */
    OpenplatRateLimit selectById(@Param("id") String id);
    
    /**
     * 查询限流配置列表
     */
    List<OpenplatRateLimit> selectList(OpenplatRateLimit rateLimit);
    
    /**
     * 新增限流配置信息
     */
    int insert(OpenplatRateLimit rateLimit);
    
    /**
     * 修改限流配置信息
     */
    int update(OpenplatRateLimit rateLimit);
    
    /**
     * 删除限流配置信息
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 批量删除限流配置信息
     */
    int deleteByIds(@Param("ids") String[] ids);
}
