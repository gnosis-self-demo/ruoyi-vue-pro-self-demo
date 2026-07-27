package com.gnosis.notice.dto.templateversion;

import com.gnosis.common.dto.BaseVO;

/**
 * 模板版本视图对象
 */
public class NoticeTemplateVersionVO extends BaseVO {

    private static final long serialVersionUID = 1L;

    /** 模板ID */
    private String templateId;

    /** 模板编码 */
    private String templateCode;

    /** 版本号 */
    private Integer versionNumber;

    /** 模板名称 */
    private String templateName;

    /** 模板类型 */
    private String templateType;

    /** 通知类型 */
    private String noticeType;

    /** 消息主题 */
    private String subject;

    /** 模板内容 */
    private String content;

    /** 附件配置JSON */
    private String attachmentConfig;

    /** 第三方配置JSON */
    private String thirdPartyConfig;

    /** 消息优先级 */
    private Integer priority;

    /** 变更备注 */
    private String changeRemark;

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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getChangeRemark() {
        return changeRemark;
    }

    public void setChangeRemark(String changeRemark) {
        this.changeRemark = changeRemark;
    }
}
