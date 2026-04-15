package com.gnosis.process.dto;

import java.io.Serializable;

public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer pageNum = 1;
    private Integer pageSize = 10;

    public Integer getPageNum() {
        return pageNum;
    }

    public void setPageNum(Integer pageNum) {
        this.pageNum = pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getOffset() {
        if (pageNum == null || pageSize == null) {
            return 0;
        }
        return (pageNum - 1) * pageSize;
    }
}
