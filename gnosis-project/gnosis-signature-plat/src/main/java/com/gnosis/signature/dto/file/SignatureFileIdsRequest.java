package com.gnosis.signature.dto.file;

import java.io.Serializable;
import java.util.List;

/**
 * 签章文件批量删除/批量操作请求
 */
public class SignatureFileIdsRequest implements Serializable {

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
