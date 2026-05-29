package com.gnosis.accounts.dto.transfer;

import java.io.Serializable;
import java.util.List;

public class AccBankTransferIdsRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> ids;

    public List<String> getIds() { return ids; }
    public void setIds(List<String> ids) { this.ids = ids; }
}
