package com.gnosis.signature.dto.seal;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * 印章查询请求 DTO
 */
@ApiModel(value = "SignatureSealQueryRequest", description = "印章查询请求")
public class SignatureSealQueryRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "印章编码", example = "SEAL_001")
    private String sealCode;

    @ApiModelProperty(value = "印章名称", example = "公司公章")
    private String sealName;

    @ApiModelProperty(value = "印章类型(公章/合同章/法人章等)", example = "公章")
    private String sealType;

    @ApiModelProperty(value = "授权人ID", example = "user001")
    private String authorizeUserId;

    @ApiModelProperty(value = "授权时间-起始", example = "2026-01-01 00:00:00")
    private Date authorizeTimeStart;

    @ApiModelProperty(value = "授权时间-结束", example = "2026-12-31 23:59:59")
    private Date authorizeTimeEnd;

    @ApiModelProperty(value = "过期时间-起始", example = "2027-01-01 00:00:00")
    private Date expireTimeStart;

    @ApiModelProperty(value = "过期时间-结束", example = "2027-12-31 23:59:59")
    private Date expireTimeEnd;

    @ApiModelProperty(value = "状态(0-禁用 1-启用 2-已销毁)", example = "1")
    private Integer status;

    @ApiModelProperty(value = "页码", example = "1")
    private Integer pageNum;

    @ApiModelProperty(value = "每页大小", example = "10")
    private Integer pageSize;

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

    public String getAuthorizeUserId() {
        return authorizeUserId;
    }

    public void setAuthorizeUserId(String authorizeUserId) {
        this.authorizeUserId = authorizeUserId;
    }

    public Date getAuthorizeTimeStart() {
        return authorizeTimeStart;
    }

    public void setAuthorizeTimeStart(Date authorizeTimeStart) {
        this.authorizeTimeStart = authorizeTimeStart;
    }

    public Date getAuthorizeTimeEnd() {
        return authorizeTimeEnd;
    }

    public void setAuthorizeTimeEnd(Date authorizeTimeEnd) {
        this.authorizeTimeEnd = authorizeTimeEnd;
    }

    public Date getExpireTimeStart() {
        return expireTimeStart;
    }

    public void setExpireTimeStart(Date expireTimeStart) {
        this.expireTimeStart = expireTimeStart;
    }

    public Date getExpireTimeEnd() {
        return expireTimeEnd;
    }

    public void setExpireTimeEnd(Date expireTimeEnd) {
        this.expireTimeEnd = expireTimeEnd;
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
