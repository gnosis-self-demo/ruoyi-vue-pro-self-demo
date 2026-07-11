package com.gnosis.notice.sender;

import com.gnosis.notice.dto.send.NoticeSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 邮件发送器
 */
@Component
public class EmailSender implements MessageSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    @Override
    public String getNoticeType() {
        return "EMAIL";
    }

    @Override
    public NoticeSendResponse send(String receiver, String subject, String content,
                                   Map<String, Object> params, List<String> attachmentPaths) {
        NoticeSendResponse response = new NoticeSendResponse();
        try {
            // TODO: 集成JavaMail或其他邮件服务
            log.info("发送邮件到: {}, 主题: {}", receiver, subject);

            // 模拟发送成功
            response.setSuccess(true);
            response.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            log.info("邮件发送成功, requestId: {}", response.getRequestId());
        } catch (Exception e) {
            log.error("邮件发送失败", e);
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
