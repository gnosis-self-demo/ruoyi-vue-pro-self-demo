package com.gnosis.notice.dto.templateversion;

import java.io.Serializable;

/**
 * 模板版本查询请求
 */
public class NoticeTemplateVersionQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 模板ID */
    private String templateId;

    /** 模板编码 */
    private String templateCode;

    /** 版本号 */
    private Integer versionNumber;

    /** 页码 */
    private Integer pageNum;

    /** 每页大小 */
    private Integer pageSize;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public Integer getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(Integer versionNumber) {
        this.versionNumber = versionNumber;
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
