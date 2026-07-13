package com.gnosis.signature.domain;

import com.gnosis.common.domain.BaseEntity;

import java.util.Date;

/**
 * 印章实体
 */
public class SignatureSeal extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 印章编码 */
    private String sealCode;

    /** 印章名称 */
    private String sealName;

    /** 印章类型(公章/合同章/法人章等) */
    private String sealType;

    /** 印章图片路径 */
    private String sealImagePath;

    /** 印章图片数据(Base64) */
    private String sealImageData;

    /** 授权人ID */
    private String authorizeUserId;

    /** 授权时间 */
    private Date authorizeTime;

    /** 过期时间 */
    private Date expireTime;

    /** 描述 */
    private String description;

    /** 状态(0-禁用 1-启用 2-已销毁) */
    private Integer status;

    public String getSealCode() {
        return sealCode;
    }

    public void setSealCode(String sealCode) {
        this.sealCode = sealCode;
    }

    public String getSealName() {
        return sealName;
    }

    public void setSealName(String sealName) {
        this.sealName = sealName;
    }

    public String getSealType() {
        return sealType;
    }

    public void setSealType(String sealType) {
        this.sealType = sealType;
    }

    public String getSealImagePath() {
        return sealImagePath;
    }

    public void setSealImagePath(String sealImagePath) {
        this.sealImagePath = sealImagePath;
    }

    public String getSealImageData() {
        return sealImageData;
    }

    public void setSealImageData(String sealImageData) {
        this.sealImageData = sealImageData;
    }

    public String getAuthorizeUserId() {
        return authorizeUserId;
    }

    public void setAuthorizeUserId(String authorizeUserId) {
        this.authorizeUserId = authorizeUserId;
    }

    public Date getAuthorizeTime() {
        return authorizeTime;
    }

    public void setAuthorizeTime(Date authorizeTime) {
        this.authorizeTime = authorizeTime;
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
