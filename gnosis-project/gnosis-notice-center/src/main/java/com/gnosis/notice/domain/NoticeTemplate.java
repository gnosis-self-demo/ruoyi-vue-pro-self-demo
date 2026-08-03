package com.gnosis.notice.domain;

import com.gnosis.common.domain.BaseEntity;

/**
 * 消息模板实体
 */
public class NoticeTemplate extends BaseEntity {

    private String templateCode;
    private String templateName;
    private String templateType;
    private String noticeType;
    private String subject;
    private String content;
    private String attachmentConfig;
    private String thirdPartyConfig;
    /** 消息优先级：0普通 1重要 2紧急 */
    private Integer priority = 0;
    private Integer status;
    private String remark;

    public String getTemplateCode() { return templateCode; }
    public void setTemplateCode(String templateCode) { this.templateCode = templateCode; }
    public String getTemplateName() { return templateName; }
    public void setTemplateName(String templateName) { this.templateName = templateName; }
    public String getTemplateType() { return templateType; }
    public void setTemplateType(String templateType) { this.templateType = templateType; }
    public String getNoticeType() { return noticeType; }
    public void setNoticeType(String noticeType) { this.noticeType = noticeType; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAttachmentConfig() { return attachmentConfig; }
    public void setAttachmentConfig(String attachmentConfig) { this.attachmentConfig = attachmentConfig; }
    public String getThirdPartyConfig() { return thirdPartyConfig; }
    public void setThirdPartyConfig(String thirdPartyConfig) { this.thirdPartyConfig = thirdPartyConfig; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}
