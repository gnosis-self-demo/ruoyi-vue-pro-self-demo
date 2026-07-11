package com.gnosis.notice.service;

import com.gnosis.notice.domain.NoticeLog;
import com.gnosis.notice.dto.send.NoticeSendRequest;
import com.gnosis.notice.dto.send.NoticeSendResponse;

import java.util.List;

/**
 * 消息发送服务接口
 */
public interface NoticeSendService {

    /**
     * 发送消息
     */
    NoticeSendResponse send(NoticeSendRequest request, String userId);

    /**
     * 批量发送消息
     */
    List<NoticeSendResponse> batchSend(NoticeSendRequest request, String userId);

    /**
     * 发送短信
     */
    NoticeSendResponse sendSms(String receiver, String content, String userId);

    /**
     * 发送邮件
     */
    NoticeSendResponse sendEmail(String receiver, String subject, String content, String userId);

    /**
     * 发送站内信
     */
    List<NoticeSendResponse> sendInbox(List<String> userIds, String subject, String content, String userId);

    /**
     * 发送微信通知
     */
    NoticeSendResponse sendWechat(String receiver, String content, String userId);

    /**
     * 创建发送日志
     */
    NoticeLog createLog(NoticeSendRequest request, String userId);
}
