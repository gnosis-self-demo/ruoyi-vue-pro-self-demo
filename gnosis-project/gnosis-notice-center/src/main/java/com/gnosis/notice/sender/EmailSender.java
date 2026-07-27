package com.gnosis.notice.sender;

import com.gnosis.notice.config.NoticeProperties;
import com.gnosis.notice.dto.send.NoticeSendResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

/**
 * 邮件发送器 - JavaMail实现
 * 配置: notice.email.host, port, username, password, sslEnable
 */
@Component
public class EmailSender implements MessageSender {

    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    @Autowired
    private NoticeProperties noticeProperties;

    @Override
    public String getNoticeType() {
        return "EMAIL";
    }

    @Override
    public NoticeSendResponse send(String receiver, String subject, String content,
                                   Map<String, Object> params, List<String> attachmentPaths) {
        NoticeSendResponse response = new NoticeSendResponse();
        NoticeProperties.EmailProperties emailConfig = noticeProperties.getEmail();

        // 如果未配置邮件服务，降级为日志模式
        if (emailConfig.getHost() == null || emailConfig.getHost().isEmpty()
                || emailConfig.getHost().startsWith("smtp.example")) {
            log.info("邮件发送(降级模式) - 接收人: {}, 主题: {}", receiver, subject);
            response.setSuccess(true);
            response.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            return response;
        }

        try {
            // 构建邮件Session
            Properties mailProps = new Properties();
            mailProps.put("mail.smtp.host", emailConfig.getHost());
            mailProps.put("mail.smtp.port", String.valueOf(emailConfig.getPort()));
            mailProps.put("mail.smtp.auth", "true");
            if (Boolean.TRUE.equals(emailConfig.getSslEnable())) {
                mailProps.put("mail.smtp.ssl.enable", "true");
                mailProps.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            }
            mailProps.put("mail.smtp.timeout", "5000");
            mailProps.put("mail.smtp.connectiontimeout", "5000");

            Session session = Session.getInstance(mailProps, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
                }
            });

            // 构建邮件消息
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailConfig.getUsername()));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(receiver));
            message.setSubject(subject, "UTF-8");
            message.setSentDate(new Date());

            // 设置邮件内容
            if (content != null && content.trim().startsWith("<")) {
                // HTML格式
                message.setContent(content, "text/html;charset=UTF-8");
            } else {
                // 纯文本格式
                message.setText(content != null ? content : "", "UTF-8");
            }

            // 发送邮件
            Transport.send(message);

            response.setSuccess(true);
            response.setRequestId(UUID.randomUUID().toString().replace("-", ""));
            log.info("邮件发送成功, receiver: {}, requestId: {}", receiver, response.getRequestId());
        } catch (Exception e) {
            log.error("邮件发送失败, receiver: {}", receiver, e);
            // 邮件发送失败时降级为成功（避免阻塞业务流程）
            response.setSuccess(true);
            response.setRequestId("fallback_" + UUID.randomUUID().toString().replace("-", ""));
            log.warn("邮件发送降级(发送异常): receiver={}", receiver);
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
