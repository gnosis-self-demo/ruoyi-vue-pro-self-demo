package com.gnosis.signature.domain;

import com.gnosis.common.domain.BaseEntity;

import java.util.Date;

/**
 * 证书实体
 */
public class SignatureCertificate extends BaseEntity {

    private static final long serialVersionUID = 1L;

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

    /** 证书内容 */
    private String certContent;

    /** 过期时间 */
    private Date expireTime;

    /** 描述 */
    private String description;

    /** 状态(0-禁用 1-启用 2-已过期) */
    private Integer status;

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

    public String getCertContent() {
        return certContent;
    }

    public void setCertContent(String certContent) {
        this.certContent = certContent;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
