package com.gnosis.signature.dto.certificate;

import java.util.List;

/**
 * 证书批量操作请求
 */
public class SignatureCertificateIdsRequest {

    /** ID列表 */
    private List<String> ids;

    public List<String> getIds() {
        return ids;
    }

    public void setIds(List<String> ids) {
        this.ids = ids;
    }
}
