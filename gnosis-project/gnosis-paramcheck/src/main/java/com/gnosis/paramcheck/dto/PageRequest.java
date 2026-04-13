package com.gnosis.paramcheck.dto;

import java.io.Serializable;
import java.util.List;

public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private int page = 1;
    private int pageSize = 10;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getOffset() {
        return (page - 1) * pageSize;
    }
}
