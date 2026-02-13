package gnosis.sample.dynamic.liteflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 方法拦截配置实体
 */
@Data
@TableName("liteflow_sys_method_intercept")
public class MethodInterceptConfig {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("target_class")
    private String targetClass;
    
    @TableField("target_method")
    private String targetMethod;
    
    @TableField("chain_name")
    private String chainName;
    
    @TableField("enabled")
    private Boolean enabled;
    
    @TableField("description")
    private String description;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
    
    @TableField("method_signature")
    private String methodSignature;
}