package com.gnosis.signature.dto.seal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 印章创建请求 DTO
 */
@ApiModel(value = "SignatureSealCreateRequest", description = "印章创建请求")
public class SignatureSealCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "印章编码", required = true, example = "SEAL_001")
    private String sealCode;

    @ApiModelProperty(value = "印章名称", required = true, example = "公司公章")
    private String sealName;

    @ApiModelProperty(value = "印章类型(公章/合同章/法人章等)", required = true, example = "公章")
    private String sealType;

    @ApiModelProperty(value = "印章图片路径", example = "/images/seal_001.png")
    private String sealImagePath;

    @ApiModelProperty(value = "印章图片数据(Base64)", example = "base64encodeddata")
    private String sealImageData;

    @ApiModelProperty(value = "授权人ID", example = "user001")
    private String authorizeUserId;

    @ApiModelProperty(value = "授权时间", example = "2026-01-01 00:00:00")
    private Date authorizeTime;

    @ApiModelProperty(value = "过期时间", example = "2027-01-01 00:00:00")
    private Date expireTime;

    @ApiModelProperty(value = "描述", example = "公司公章描述")
    private String description;

    @ApiModelProperty(value = "状态(0-禁用 1-启用 2-已销毁)", example = "1")
    private Integer status;

    @ApiModelProperty(value = "创建人ID", example = "user001")
    private String createUserId;

    @ApiModelProperty(value = "更新人ID", example = "user001")
    private String updateUserId;

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

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getUpdateUserId() {
        return updateUserId;
    }

    public void setUpdateUserId(String updateUserId) {
        this.updateUserId = updateUserId;
    }
}
