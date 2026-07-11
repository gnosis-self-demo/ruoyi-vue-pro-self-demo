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
 * 短信发送器
 */
@Component
public class SmsSender implements MessageSender {

    private static final Logger log = LoggerFactory.getLogger(SmsSender.class);

    @Override
    public String getNoticeType() {
        return "SMS";
    }

    @Override
    public NoticeSendResponse send(String receiver, String subject, String content,
                                   Map<String, Object> params, List<String> attachmentPaths) {
        NoticeSendResponse response = new NoticeSendResponse();
        try {
            // TODO: 集成阿里云SMS SDK或其他短信服务商
            log.info("发送短信到: {}, 内容: {}", receiver, content);

            // 模拟发送成功
            response.setSuccess(true);
            response.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            log.info("短信发送成功, requestId: {}", response.getRequestId());
        } catch (Exception e) {
            log.error("短信发送失败", e);
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
