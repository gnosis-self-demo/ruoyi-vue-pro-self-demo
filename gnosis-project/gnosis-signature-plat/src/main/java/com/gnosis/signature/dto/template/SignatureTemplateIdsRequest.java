package com.gnosis.signature.dto.template;

import java.io.Serializable;
import java.util.List;

/**
 * 签章模板批量删除/批量操作请求
 */
public class SignatureTemplateIdsRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID列表
     */
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
