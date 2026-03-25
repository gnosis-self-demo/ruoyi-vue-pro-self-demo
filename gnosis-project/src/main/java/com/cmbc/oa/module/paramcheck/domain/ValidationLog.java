package com.cmbc.oa.module.paramcheck.domain;

import lombok.Data;
import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * 校验日志实体 (映射 sys_validation_logs)
 */
@Data
public class ValidationLog implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long logId;
    private String flowId;
    private String requestId;
    private String modeType;
    private Object inputSnapshot;
    private String failedNode;
    private String errorMsg;
    private OffsetDateTime createdTime;
}
