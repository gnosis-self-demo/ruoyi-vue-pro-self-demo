package com.gnosis.signature.dto.audit;

import java.util.List;

/**
 * 审计日志批量操作请求
 */
public class SignatureAuditLogIdsRequest {

    /** ID列表 */
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
