package gnosis.sample.dynamic.liteflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * LiteFlow规则配置实体
 */
@Data
@TableName("liteflow_sys_rule")
public class LiteFlowRule {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("chain_name")
    private String chainName;
    
    @TableField("rule_content")
    private String ruleContent;
    
    @TableField("rule_type")
    private String ruleType;
    
    @TableField("enabled")
    private Boolean enabled;
    
    @TableField("version")
    private Integer version;
    
    @TableField("description")
    private String description;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}