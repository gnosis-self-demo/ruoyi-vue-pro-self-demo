package com.gnosis.signature.dto.seal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * 印章批量操作请求 DTO
 */
@ApiModel(value = "SignatureSealIdsRequest", description = "印章批量操作请求")
public class SignatureSealIdsRequest implements Serializable {

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
