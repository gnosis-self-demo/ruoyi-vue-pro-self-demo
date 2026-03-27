package openplat.mapper;

import openplat.domain.OpenplatAccessLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 调用日志 Mapper 接口
 */
@Mapper
public interface OpenplatAccessLogMapper {
    
    /**
     * 根据 ID 查询调用日志信息
     */
    OpenplatAccessLog selectById(@Param("id") String id);
    
    /**
     * 查询调用日志列表
     */
    List<OpenplatAccessLog> selectList(OpenplatAccessLog accessLog);
    
    /**
     * 新增调用日志信息
     */
    int insert(OpenplatAccessLog accessLog);
    
    /**
     * 删除调用日志信息
     */
    int deleteById(@Param("id") String id);
    
    /**
     * 批量删除调用日志信息
     */
    int deleteByIds(@Param("ids") String[] ids);
    
    /**
     * 清理过期日志
     */
    int deleteExpired(@Param("expireDate") java.util.Date expireDate);
}
