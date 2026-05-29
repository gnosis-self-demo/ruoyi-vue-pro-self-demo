package com.gnosis.accounts.api;

import java.io.Serializable;
import java.math.BigDecimal;

public class EntryItem implements Serializable {

    private static final long serialVersionUID = 1L;

    private String accountId;
    private BigDecimal amount;
    private String description;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
