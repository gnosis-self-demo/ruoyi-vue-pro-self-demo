package com.cmbc.oa.module.paramcheck.domain;

import lombok.Data;
import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.Map;

/**
 * 校验流程配置实体 (映射 sys_validation_flows)
 */
@Data
public class ValidationFlow implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 流程标识 */
    private String flowId;

    /** 流程名称 */
    private String flowName;

    /** 模式类型: FLOW / HANDLER / HYBRID */
    private String modeType;

    /** LiteFlow EL 表达式 (FLOW/HYBRID 必填) */
    private String elExpression;

    /** 自定义处理器编码 (HANDLER/HYBRID 必填) */
    private String handlerCode;

    /** 组件配置 (JSONPath/正则/SQL 等) */
    private Map<String, Object> componentConfig;

    /** 是否激活 */
    private Boolean isActive;

    /** 版本号 */
    private Integer version;

    /** 更新时间 */
    private OffsetDateTime updatedTime;
}
