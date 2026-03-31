package com.gnosis.openplat.mapper;

import com.gnosis.openplat.domain.OpenplatNonceCache;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 防重放缓存 Mapper 接口
 */
@Mapper
public interface OpenplatNonceCacheMapper {
    
    /**
     * 根据 ID 查询防重放缓存信息
     */
    OpenplatNonceCache selectById(@Param("id") String id);
    
    /**
     * 根据 app_id 和 nonce 查询防重放缓存信息
     */
    OpenplatNonceCache selectByAppIdAndNonce(@Param("appId") String appId, @Param("nonce") String nonce);
    
    /**
     * 查询防重放缓存列表
     */
    List<OpenplatNonceCache> selectList(OpenplatNonceCache nonceCache);
    
    /**
     * 新增防重放缓存信息
     */
    int insert(OpenplatNonceCache nonceCache);
    
    /**
     * 删除防重放缓存信息
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 批量删除防重放缓存信息
     */
    int deleteByIds(@Param("ids") String[] ids);
    
    /**
     * 清理过期缓存
     */
    int deleteExpired(@Param("expireTime") java.util.Date expireTime);
}
