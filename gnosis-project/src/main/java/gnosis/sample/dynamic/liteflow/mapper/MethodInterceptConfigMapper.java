package gnosis.sample.dynamic.liteflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import gnosis.sample.dynamic.liteflow.entity.MethodInterceptConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 方法拦截配置Mapper
 */
@Mapper
public interface MethodInterceptConfigMapper extends BaseMapper<MethodInterceptConfig> {
}