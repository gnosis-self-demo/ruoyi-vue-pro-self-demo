package com.gnosis.signature.dto.supplier;

import java.util.List;

/**
 * 供应商批量操作请求
 */
public class SignatureSupplierIdsRequest {

    /** ID列表 */
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
