package com.gnosis.accounts.dto.account;

import java.io.Serializable;
import java.util.List;

public class AccAccountIdsRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> ids;

    public List<String> getIds() { return ids; }
    public void setIds(List<String> ids) { this.ids = ids; }
}
