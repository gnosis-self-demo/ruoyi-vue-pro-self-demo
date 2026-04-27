package com.gnosis.common.dto;

public class PageRequest<T> {

    private T query;
    private Integer pageNum;
    private Integer pageSize;

    public PageRequest() {
        this.pageNum = 0;
        this.pageSize = 10;
    }

    public T getQuery() {
        return query;
    }

    public void setQuery(T query) {
        this.query = query;
    }

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
}
