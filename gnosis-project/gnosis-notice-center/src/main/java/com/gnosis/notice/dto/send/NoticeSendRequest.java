package com.gnosis.notice.dto.send;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 消息发送请求
 */
public class NoticeSendRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 通知类型：SMS/EMAIL/INBOX/WECHAT（不指定则使用模板类型）
     */
    private String noticeType;

    /**
     * 接收人（多个用逗号分隔）
     */
    private String receiver;

    /**
     * 接收人列表
     */
    private List<String> receivers;

    /**
     * 模板变量参数
     */
    private Map<String, Object> params;

    /**
     * 消息主题（用于快速发送）
     */
    private String subject;

    /**
     * 消息内容（用于快速发送）
     */
    private String content;

    /**
     * 附件路径列表
     */
    private List<String> attachmentPaths;

    /**
     * 是否异步发送
     */
    private Boolean async;

    public String getTemplateCode() {
        return templateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.templateCode = templateCode;
    }

    public String getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(String noticeType) {
        this.noticeType = noticeType;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<String> getAttachmentPaths() {
        return attachmentPaths;
    }

    public void setAttachmentPaths(List<String> attachmentPaths) {
        this.attachmentPaths = attachmentPaths;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
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
}
