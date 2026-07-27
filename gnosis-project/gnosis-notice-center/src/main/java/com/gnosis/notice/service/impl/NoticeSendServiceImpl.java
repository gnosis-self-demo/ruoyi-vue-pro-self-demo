package com.gnosis.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.gnosis.notice.config.NoticeContextUtil;
import com.gnosis.notice.domain.NoticeLog;
import com.gnosis.notice.domain.NoticeTemplate;
import com.gnosis.notice.dto.send.NoticeSendRequest;
import com.gnosis.notice.dto.send.NoticeSendResponse;
import com.gnosis.notice.mapper.NoticeLogMapper;
import com.gnosis.notice.sender.MessageSender;
import com.gnosis.notice.service.NoticeBlacklistService;
import com.gnosis.notice.service.NoticeFrequencyService;
import com.gnosis.notice.service.NoticeSendService;
import com.gnosis.notice.service.NoticeTemplateService;
import com.gnosis.notice.util.TemplateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 消息发送服务实现 - 修复版
 * 修复项: 异步发送、黑名单检查、频率限制、快速发送日志记录、发送器Map注册
 */
@Service
public class NoticeSendServiceImpl implements NoticeSendService {

    private static final Logger log = LoggerFactory.getLogger(NoticeSendServiceImpl.class);

    @Autowired
    private NoticeTemplateService templateService;

    @Autowired
    private NoticeLogMapper logMapper;

    @Autowired
    private NoticeFrequencyService frequencyService;

    @Autowired
    private NoticeBlacklistService blacklistService;

    @Autowired
    private NoticeContextUtil contextUtil;

    /** 发送器Map注册 - O(1)查找，替代原来的List遍历 */
    private Map<String, MessageSender> senderMap;

    @Autowired
    public void setSenders(List<MessageSender> senders) {
        this.senderMap = new java.util.HashMap<>();
        for (MessageSender sender : senders) {
            senderMap.put(sender.getNoticeType(), sender);
        }
    }

    @Override
    public NoticeSendResponse send(NoticeSendRequest request, String userId) {
        // 黑名单检查
        if (blacklistService.isBlacklisted(request.getReceiver(), request.getNoticeType(), request.getTemplateCode())) {
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg("接收人已退订该类型通知");
            return response;
        }

        // 频率限制检查
        if (!frequencyService.checkFrequency(request.getReceiver(), request.getNoticeType())) {
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg("发送频率超限，请稍后再试");
            return response;
        }

        // 1. 查询模板
        NoticeTemplate template = templateService.getByCode(request.getTemplateCode());
        if (template == null) {
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg("模板不存在: " + request.getTemplateCode());
            return response;
        }

        // 2. 解析模板
        String noticeType = StringUtils.isEmpty(request.getNoticeType()) ? template.getNoticeType() : request.getNoticeType();
        String content = TemplateParser.parse(template.getContent(), request.getParams());
        String subject = TemplateParser.parse(template.getSubject(), request.getParams());
        Integer priority = request.getPriority() != null ? request.getPriority() : template.getPriority();

        // 3. 创建发送日志
        NoticeLog logRecord = createLogRecord(template, noticeType, request.getReceiver(),
                subject, content, priority, request.getGroupId(), request.getAttachmentPaths(), userId);

        // 4. 获取发送器(Map查找)
        MessageSender sender = senderMap.get(noticeType);
        if (sender == null) {
            logRecord.setSendStatus(3);
            logRecord.setErrorMsg("不支持的通知类型: " + noticeType);
            logRecord.setUpdateTime(new Date());
            logMapper.updateById(logRecord);
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg("不支持的通知类型: " + noticeType);
            return response;
        }

        // 5. 异步或同步发送
        if (Boolean.TRUE.equals(request.getAsync())) {
            asyncSend(logRecord, sender, request.getReceiver(), subject, content, request.getParams(), request.getAttachmentPaths());
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(true);
            response.setLogId(logRecord.getId());
            response.setErrorMsg("异步发送已提交");
            return response;
        }

        return doSend(logRecord, sender, request.getReceiver(), subject, content, request.getParams(), request.getAttachmentPaths());
    }

    @Async
    public void asyncSend(NoticeLog logRecord, MessageSender sender, String receiver,
                          String subject, String content, Map<String, Object> params, List<String> attachmentPaths) {
        doSend(logRecord, sender, receiver, subject, content, params, attachmentPaths);
    }

    private NoticeSendResponse doSend(NoticeLog logRecord, MessageSender sender, String receiver,
                                      String subject, String content, Map<String, Object> params, List<String> attachmentPaths) {
        try {
            logRecord.setSendStatus(1);
            logRecord.setUpdateTime(new Date());
            logMapper.updateById(logRecord);

            NoticeSendResponse response = sender.send(receiver, subject, content, params, attachmentPaths);

            if (response.getSuccess()) {
                frequencyService.incrementFrequency(receiver, logRecord.getNoticeType());
                logRecord.setSendStatus(2);
                logRecord.setSendTime(new Date());
                logRecord.setRequestId(response.getRequestId());
            } else {
                logRecord.setSendStatus(3);
                logRecord.setErrorMsg(response.getErrorMsg());
            }
            logRecord.setUpdateTime(new Date());
            logMapper.updateById(logRecord);

            response.setLogId(logRecord.getId());
            return response;
        } catch (Exception e) {
            log.error("发送消息失败", e);
            logRecord.setSendStatus(3);
            logRecord.setErrorMsg(e.getMessage());
            logRecord.setUpdateTime(new Date());
            logMapper.updateById(logRecord);

            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg(e.getMessage());
            response.setLogId(logRecord.getId());
            return response;
        }
    }

    @Override
    public List<NoticeSendResponse> batchSend(NoticeSendRequest request, String userId) {
        List<NoticeSendResponse> responses = new ArrayList<>();
        if (request.getReceivers() != null && !request.getReceivers().isEmpty()) {
            for (String receiver : request.getReceivers()) {
                NoticeSendRequest singleRequest = cloneRequest(request);
                singleRequest.setReceiver(receiver);
                responses.add(send(singleRequest, userId));
            }
        } else if (!StringUtils.isEmpty(request.getReceiver())) {
            String[] receiverArray = request.getReceiver().split(",");
            for (String receiver : receiverArray) {
                NoticeSendRequest singleRequest = cloneRequest(request);
                singleRequest.setReceiver(receiver.trim());
                responses.add(send(singleRequest, userId));
            }
        }
        return responses;
    }

    @Override
    public NoticeSendResponse sendSms(String receiver, String content, String userId) {
        return sendQuick("SMS", receiver, "", content, null, userId);
    }

    @Override
    public NoticeSendResponse sendEmail(String receiver, String subject, String content, String userId) {
        return sendQuick("EMAIL", receiver, subject, content, null, userId);
    }

    @Override
    public List<NoticeSendResponse> sendInbox(List<String> userIds, String subject, String content, String userId) {
        List<NoticeSendResponse> responses = new ArrayList<>();
        for (String receiver : userIds) {
            responses.add(sendQuick("INBOX", receiver, subject, content, null, userId));
        }
        return responses;
    }

    @Override
    public NoticeSendResponse sendWechat(String receiver, String content, String userId) {
        return sendQuick("WECHAT", receiver, "", content, null, userId);
    }

    @Override
    public NoticeLog createLog(NoticeSendRequest request, String userId) {
        NoticeTemplate template = templateService.getByCode(request.getTemplateCode());
        if (template == null) {
            return null;
        }
        String content = TemplateParser.parse(template.getContent(), request.getParams());
        String subject = TemplateParser.parse(template.getSubject(), request.getParams());
        Integer priority = request.getPriority() != null ? request.getPriority() : template.getPriority();
        return createLogRecord(template, template.getNoticeType(), request.getReceiver(),
                subject, content, priority, request.getGroupId(), request.getAttachmentPaths(), userId);
    }

    /**
     * 快速发送 - 修复版: 现在也创建日志记录
     */
    private NoticeSendResponse sendQuick(String noticeType, String receiver, String subject,
                                         String content, List<String> attachmentPaths, String userId) {
        // 黑名单检查
        if (blacklistService.isBlacklisted(receiver, noticeType, null)) {
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg("接收人已退订该类型通知");
            return response;
        }

        // 频率限制检查
        if (!frequencyService.checkFrequency(receiver, noticeType)) {
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg("发送频率超限");
            return response;
        }

        // 创建日志记录(修复: 快速发送也创建日志)
        NoticeLog logRecord = new NoticeLog();
        logRecord.setId(UUID.randomUUID().toString().replace("-", ""));
        logRecord.setNoticeType(noticeType);
        logRecord.setReceiver(receiver);
        logRecord.setSubject(subject);
        logRecord.setContent(content);
        logRecord.setAttachmentPaths(attachmentPaths != null ? JSON.toJSONString(attachmentPaths) : null);
        logRecord.setPriority(0);
        logRecord.setSendStatus(0);
        logRecord.setRetryCount(0);
        logRecord.setMaxRetry(3);
        logRecord.setCreateUserId(userId);
        logRecord.setCreateTime(new Date());
        logRecord.setUpdateTime(new Date());
        logMapper.insert(logRecord);

        MessageSender sender = senderMap.get(noticeType);
        if (sender == null) {
            logRecord.setSendStatus(3);
            logRecord.setErrorMsg("不支持的通知类型: " + noticeType);
            logRecord.setUpdateTime(new Date());
            logMapper.updateById(logRecord);
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg("不支持的通知类型: " + noticeType);
            return response;
        }

        return doSend(logRecord, sender, receiver, subject, content, null, attachmentPaths);
    }

    private NoticeLog createLogRecord(NoticeTemplate template, String noticeType, String receiver,
                                      String subject, String content, Integer priority, String groupId,
                                      List<String> attachmentPaths, String userId) {
        NoticeLog logRecord = new NoticeLog();
        logRecord.setId(UUID.randomUUID().toString().replace("-", ""));
        logRecord.setTemplateId(template.getId());
        logRecord.setTemplateCode(template.getTemplateCode());
        logRecord.setNoticeType(noticeType);
        logRecord.setReceiver(receiver);
        logRecord.setSubject(subject);
        logRecord.setContent(content);
        logRecord.setAttachmentPaths(attachmentPaths != null ? JSON.toJSONString(attachmentPaths) : null);
        logRecord.setPriority(priority != null ? priority : 0);
        logRecord.setGroupId(groupId);
        logRecord.setSendStatus(0);
        logRecord.setRetryCount(0);
        logRecord.setMaxRetry(3);
        logRecord.setCreateUserId(userId);
        logRecord.setCreateTime(new Date());
        logRecord.setUpdateTime(new Date());
        logMapper.insert(logRecord);
        return logRecord;
    }

    private NoticeSendRequest cloneRequest(NoticeSendRequest request) {
        NoticeSendRequest clone = new NoticeSendRequest();
        clone.setTemplateCode(request.getTemplateCode());
        clone.setNoticeType(request.getNoticeType());
        clone.setReceiver(request.getReceiver());
        clone.setReceivers(request.getReceivers());
        clone.setParams(request.getParams());
        clone.setAttachmentPaths(request.getAttachmentPaths());
        clone.setAsync(request.getAsync());
        clone.setPriority(request.getPriority());
        clone.setGroupId(request.getGroupId());
        return clone;
    }
}
