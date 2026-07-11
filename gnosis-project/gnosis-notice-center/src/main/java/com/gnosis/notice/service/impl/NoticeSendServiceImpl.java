package com.gnosis.notice.service.impl;

import com.alibaba.fastjson.JSON;
import com.gnosis.notice.domain.NoticeLog;
import com.gnosis.notice.domain.NoticeTemplate;
import com.gnosis.notice.dto.send.NoticeSendRequest;
import com.gnosis.notice.dto.send.NoticeSendResponse;
import com.gnosis.notice.mapper.NoticeLogMapper;
import com.gnosis.notice.sender.MessageSender;
import com.gnosis.notice.service.NoticeSendService;
import com.gnosis.notice.service.NoticeTemplateService;
import com.gnosis.notice.util.TemplateParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 消息发送服务实现
 */
@Service
public class NoticeSendServiceImpl implements NoticeSendService {

    private static final Logger log = LoggerFactory.getLogger(NoticeSendServiceImpl.class);

    @Autowired
    private NoticeTemplateService templateService;

    @Autowired
    private NoticeLogMapper logMapper;

    @Autowired
    private List<MessageSender> senders;

    @Override
    public NoticeSendResponse send(NoticeSendRequest request, String userId) {
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

        // 3. 创建发送日志
        NoticeLog logRecord = createLogRecord(template, noticeType, request.getReceiver(),
                subject, content, request.getAttachmentPaths(), userId);

        // 4. 获取发送器
        MessageSender sender = getSender(noticeType);
        if (sender == null) {
            logRecord.setSendStatus(3);
            logRecord.setErrorMsg("不支持的通知类型: " + noticeType);
            logMapper.updateById(logRecord);
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg("不支持的通知类型: " + noticeType);
            return response;
        }

        // 5. 发送消息
        try {
            logRecord.setSendStatus(1);
            logMapper.updateById(logRecord);

            NoticeSendResponse response = sender.send(request.getReceiver(), subject, content,
                    request.getParams(), request.getAttachmentPaths());

            // 6. 更新日志状态
            if (response.getSuccess()) {
                logRecord.setSendStatus(2);
                logRecord.setSendTime(new Date());
                logRecord.setRequestId(response.getRequestId());
            } else {
                logRecord.setSendStatus(3);
                logRecord.setErrorMsg(response.getErrorMsg());
            }
            logMapper.updateById(logRecord);

            response.setLogId(logRecord.getId());
            return response;
        } catch (Exception e) {
            log.error("发送消息失败", e);
            logRecord.setSendStatus(3);
            logRecord.setErrorMsg(e.getMessage());
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
        NoticeSendRequest request = new NoticeSendRequest();
        request.setNoticeType("SMS");
        request.setReceiver(receiver);
        return sendWithContent(request, content, userId);
    }

    @Override
    public NoticeSendResponse sendEmail(String receiver, String subject, String content, String userId) {
        NoticeSendRequest request = new NoticeSendRequest();
        request.setNoticeType("EMAIL");
        request.setReceiver(receiver);
        return sendWithSubjectContent(request, subject, content, userId);
    }

    @Override
    public List<NoticeSendResponse> sendInbox(List<String> userIds, String subject, String content, String userId) {
        NoticeSendRequest request = new NoticeSendRequest();
        request.setNoticeType("INBOX");
        request.setReceivers(userIds);
        return batchSendWithSubjectContent(request, subject, content, userId);
    }

    @Override
    public NoticeSendResponse sendWechat(String receiver, String content, String userId) {
        NoticeSendRequest request = new NoticeSendRequest();
        request.setNoticeType("WECHAT");
        request.setReceiver(receiver);
        return sendWithContent(request, content, userId);
    }

    @Override
    public NoticeLog createLog(NoticeSendRequest request, String userId) {
        NoticeTemplate template = templateService.getByCode(request.getTemplateCode());
        if (template == null) {
            return null;
        }
        String content = TemplateParser.parse(template.getContent(), request.getParams());
        String subject = TemplateParser.parse(template.getSubject(), request.getParams());
        return createLogRecord(template, template.getNoticeType(), request.getReceiver(),
                subject, content, request.getAttachmentPaths(), userId);
    }

    private NoticeSendResponse sendWithContent(NoticeSendRequest request, String content, String userId) {
        NoticeTemplate template = new NoticeTemplate();
        template.setNoticeType(request.getNoticeType());
        template.setContent(content);
        template.setSubject("");

        NoticeLog logRecord = createLogRecord(template, request.getNoticeType(), request.getReceiver(),
                "", content, null, userId);

        MessageSender sender = getSender(request.getNoticeType());
        if (sender == null) {
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg("不支持的通知类型: " + request.getNoticeType());
            return response;
        }

        return sender.send(request.getReceiver(), "", content, null, null);
    }

    private NoticeSendResponse sendWithSubjectContent(NoticeSendRequest request, String subject, String content, String userId) {
        MessageSender sender = getSender(request.getNoticeType());
        if (sender == null) {
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg("不支持的通知类型: " + request.getNoticeType());
            return response;
        }
        return sender.send(request.getReceiver(), subject, content, null, null);
    }

    private List<NoticeSendResponse> batchSendWithSubjectContent(NoticeSendRequest request, String subject, String content, String userId) {
        MessageSender sender = getSender(request.getNoticeType());
        if (sender == null) {
            List<NoticeSendResponse> responses = new ArrayList<>();
            NoticeSendResponse response = new NoticeSendResponse();
            response.setSuccess(false);
            response.setErrorMsg("不支持的通知类型: " + request.getNoticeType());
            responses.add(response);
            return responses;
        }
        return sender.batchSend(request.getReceivers(), subject, content, null, null);
    }

    private NoticeLog createLogRecord(NoticeTemplate template, String noticeType, String receiver,
                                      String subject, String content, List<String> attachmentPaths, String userId) {
        NoticeLog logRecord = new NoticeLog();
        logRecord.setId(UUID.randomUUID().toString().replace("-", ""));
        logRecord.setTemplateId(template.getId());
        logRecord.setTemplateCode(template.getTemplateCode());
        logRecord.setNoticeType(noticeType);
        logRecord.setReceiver(receiver);
        logRecord.setSubject(subject);
        logRecord.setContent(content);
        logRecord.setAttachmentPaths(attachmentPaths != null ? JSON.toJSONString(attachmentPaths) : null);
        logRecord.setSendStatus(0);
        logRecord.setRetryCount(0);
        logRecord.setMaxRetry(3);
        logRecord.setCreateUserId(userId);
        logRecord.setCreateTime(new Date());
        logMapper.insert(logRecord);
        return logRecord;
    }

    private MessageSender getSender(String noticeType) {
        for (MessageSender sender : senders) {
            if (sender.getNoticeType().equals(noticeType)) {
                return sender;
            }
        }
        return null;
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
        return clone;
    }
}
