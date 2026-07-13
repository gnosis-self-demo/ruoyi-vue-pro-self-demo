package com.gnosis.signature.dto.process;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 签章流程批量操作请求 DTO
 */
@ApiModel(value = "SignatureProcessIdsRequest", description = "签章流程批量操作请求")
public class SignatureProcessIdsRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID列表", required = true)
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
