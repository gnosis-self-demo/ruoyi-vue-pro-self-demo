package openplat.mapper;

import openplat.domain.OpenplatApiDoc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * API 文档 Mapper 接口
 */
@Mapper
public interface OpenplatApiDocMapper {
    
    /**
     * 根据 ID 查询 API 文档信息
     */
    OpenplatApiDoc selectById(@Param("id") String id);
    
    /**
     * 根据 API ID 查询 API 文档信息
     */
    OpenplatApiDoc selectByApiId(@Param("apiId") String apiId);
    
    /**
     * 查询 API 文档列表
     */
    List<OpenplatApiDoc> selectList(OpenplatApiDoc apiDoc);
    
    /**
     * 新增 API 文档信息
     */
    int insert(OpenplatApiDoc apiDoc);
    
    /**
     * 修改 API 文档信息
     */
    int update(OpenplatApiDoc apiDoc);
    
    /**
     * 删除 API 文档信息
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 批量删除 API 文档信息
     */
    int deleteByIds(@Param("ids") String[] ids);
}
