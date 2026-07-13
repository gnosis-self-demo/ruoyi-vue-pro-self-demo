package com.gnosis.signature.dto.certificate;

import java.util.Date;

/**
 * 证书查询请求
 */
public class SignatureCertificateQueryRequest {

    /** 证书编码 */
    private String certCode;

    /** 证书名称 */
    private String certName;

    /** 证书类型 */
    private String certType;

    /** CA机构 */
    private String caOrg;

    /** 证书序列号 */
    private String certSn;

    /** 过期时间 */
    private Date expireTime;

    /** 状态(0-禁用 1-启用 2-已过期) */
    private Integer status;

    /** 页码 */
    private Integer pageNum;

    /** 每页大小 */
    private Integer pageSize;

    public String getCertCode() {
        return certCode;
    }

    public void setCertCode(String certCode) {
        this.certCode = certCode;
    }

    public String getCertName() {
        return certName;
    }

    public void setCertName(String certName) {
        this.certName = certName;
    }

    public String getCertType() {
        return certType;
    }

    public void setCertType(String certType) {
        this.certType = certType;
    }

    public String getCaOrg() {
        return caOrg;
    }

    public void setCaOrg(String caOrg) {
        this.caOrg = caOrg;
    }

    public String getCertSn() {
        return certSn;
    }

    public void setCertSn(String certSn) {
        this.certSn = certSn;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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
