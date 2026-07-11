package com.gnosis.notice.dto.template;

import java.io.Serializable;

/**
 * 消息模板创建请求
 */
public class NoticeTemplateCreateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板类型：TEXT/RICH_TEXT/HTML
     */
    private String templateType;

    /**
     * 通知类型：SMS/EMAIL/INBOX/WECHAT
     */
    private String noticeType;

    /**
     * 消息主题
     */
    private String subject;

    /**
     * 模板内容
     */
    private String content;

    /**
     * 附件配置JSON
     */
    private String attachmentConfig;

    /**
     * 第三方配置JSON
     */
    private String thirdPartyConfig;

    /**
     * 状态：1启用 0禁用
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getTemplateType() {
        return templateType;
    }

    public void setTemplateType(String templateType) {
        this.templateType = templateType;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttachmentConfig() {
        return attachmentConfig;
    }

    public void setAttachmentConfig(String attachmentConfig) {
        this.attachmentConfig = attachmentConfig;
    }

    public String getThirdPartyConfig() {
        return thirdPartyConfig;
    }

    public void setThirdPartyConfig(String thirdPartyConfig) {
        this.thirdPartyConfig = thirdPartyConfig;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
