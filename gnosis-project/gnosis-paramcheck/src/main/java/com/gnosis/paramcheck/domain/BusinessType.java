package com.gnosis.paramcheck.domain;

import lombok.Data;
import java.io.Serializable;
import java.time.OffsetDateTime;

/**
 * 业务类型实体类
 */
@Data
public class BusinessType implements Serializable {
    private String code; // 业务类型编码
    private String name; // 业务类型名称
    private String description; // 描述
    private String createUserId; // 创建人ID
    private String updateUserId; // 更新人ID
    private OffsetDateTime createTime; // 创建时间
    private OffsetDateTime updateTime; // 更新时间
}
