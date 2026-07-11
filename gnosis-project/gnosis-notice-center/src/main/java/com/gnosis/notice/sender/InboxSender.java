package com.gnosis.notice.sender;

import com.gnosis.notice.domain.NoticeInbox;
import com.gnosis.notice.dto.send.NoticeSendResponse;
import com.gnosis.notice.mapper.NoticeInboxMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 站内信发送器
 */
@Component
public class InboxSender implements MessageSender {

    private static final Logger log = LoggerFactory.getLogger(InboxSender.class);

    @Autowired
    private NoticeInboxMapper inboxMapper;

    @Override
    public String getNoticeType() {
        return "INBOX";
    }

    @Override
    public NoticeSendResponse send(String receiver, String subject, String content,
                                   Map<String, Object> params, List<String> attachmentPaths) {
        NoticeSendResponse response = new NoticeSendResponse();
        try {
            NoticeInbox inbox = new NoticeInbox();
            inbox.setId(UUID.randomUUID().toString().replace("-", ""));
            inbox.setUserId(receiver);
            inbox.setSubject(subject);
            inbox.setContent(content);
            inbox.setAttachmentPaths(attachmentPaths != null ? com.alibaba.fastjson.JSON.toJSONString(attachmentPaths) : null);
            inbox.setIsRead(0);
            inbox.setCreateTime(new Date());

            inboxMapper.insert(inbox);

            response.setSuccess(true);
            response.setLogId(inbox.getId());
            log.info("站内信发送成功, userId: {}, inboxId: {}", receiver, inbox.getId());
        } catch (Exception e) {
            log.error("站内信发送失败", e);
            response.setSuccess(false);
            response.setErrorMsg(e.getMessage());
        }
        return response;
    }

    @Override
    public List<NoticeSendResponse> batchSend(List<String> receivers, String subject, String content,
                                              Map<String, Object> params, List<String> attachmentPaths) {
        List<NoticeSendResponse> responses = new ArrayList<>();
        for (String receiver : receivers) {
            responses.add(send(receiver, subject, content, params, attachmentPaths));
        }
        return responses;
    }
}
