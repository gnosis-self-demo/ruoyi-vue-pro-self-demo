package com.gnosis.signature.domain;

import com.gnosis.common.domain.BaseEntity;

/**
 * 签章文件实体
 */
public class SignatureFile extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 文件名称 */
    private String fileName;

    /** 文件类型 */
    private String fileType;

    /** 文件大小(字节) */
    private Long fileSize;

    /** 文件存储路径 */
    private String filePath;

    /** 文件哈希值 */
    private String fileHash;

    /** 模板ID */
    private String templateId;

    /** 流程ID */
    private String processId;

    /** 供应商ID */
    private String supplierId;

    /** 签章状态(0-未签章 1-签章中 2-已签章 3-签章失败) */
    private Integer signStatus;

    /** 描述 */
    private String description;

    /** 状态(0-禁用 1-启用) */
    private Integer status;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileHash() {
        return fileHash;
    }

    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getProcessId() {
        return processId;
    }

    public void setProcessId(String processId) {
        this.processId = processId;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public Integer getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(Integer signStatus) {
        this.signStatus = signStatus;
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
