package com.gnosis.signature.dto.seal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 印章视图对象 DTO
 */
@ApiModel(value = "SignatureSealVO", description = "印章视图对象")
public class SignatureSealVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private String id;

    @ApiModelProperty(value = "印章编码")
    private String sealCode;

    @ApiModelProperty(value = "印章名称")
    private String sealName;

    @ApiModelProperty(value = "印章类型(公章/合同章/法人章等)")
    private String sealType;

    @ApiModelProperty(value = "印章图片路径")
    private String sealImagePath;

    @ApiModelProperty(value = "印章图片数据(Base64)")
    private String sealImageData;

    @ApiModelProperty(value = "授权人ID")
    private String authorizeUserId;

    @ApiModelProperty(value = "授权时间")
    private Date authorizeTime;

    @ApiModelProperty(value = "过期时间")
    private Date expireTime;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "状态(0-禁用 1-启用 2-已销毁)")
    private Integer status;

    @ApiModelProperty(value = "创建人ID")
    private String createUserId;

    @ApiModelProperty(value = "更新人ID")
    private String updateUserId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
