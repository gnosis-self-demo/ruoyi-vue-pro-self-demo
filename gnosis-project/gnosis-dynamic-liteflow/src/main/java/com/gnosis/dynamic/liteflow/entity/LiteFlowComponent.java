package com.gnosis.dynamic.liteflow.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * LiteFlow组件配置实体
 */
@Data
@TableName("liteflow_sys_component")
public class LiteFlowComponent {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    @TableField("component_id")
    private String componentId;
    
    @TableField("component_class")
    private String componentClass;
    
    @TableField("component_name")
    private String componentName;
    
    @TableField("enabled")
    private Boolean enabled;
    
    @TableField("description")
    private String description;
    
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}