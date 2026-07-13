package com.gnosis.notice.api;

import com.gnosis.notice.dto.send.NoticeSendRequest;
import com.gnosis.notice.dto.send.NoticeSendResponse;
import com.gnosis.notice.service.NoticeSendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 消息通知 API 类
 * 供其他模块直接调用，无需通过 HTTP 接口
 *
 * 使用示例:
 * <pre>
 * {@code
 * @Service
 * public class OrderService {
 *     @Autowired
 *     private NoticeApi noticeApi;
 *
 *     public void createOrder(Order order) {
 *         // 使用模板发送短信
 *         NoticeSendRequest request = new NoticeSendRequest();
 *         request.setTemplateCode("ORDER_CREATE");
 *         request.setReceiver(order.getUserPhone());
 *         request.setParams(new HashMap<String, Object>() {{
 *             put("orderNo", order.getOrderNo());
 *         }});
 *         noticeApi.send(NoticeChannelEnum.SMS, request, order.getUserId());
 *
 *         // 快速发送短信
 *         noticeApi.send(NoticeChannelEnum.SMS, "13800138000", null, "您的订单已创建");
 *
 *         // 发送站内信
 *         noticeApi.send(NoticeChannelEnum.INBOX, Arrays.asList("user1"), "通知", "您有新消息");
 *     }
 * }
 * }
 * </pre>
 */
@Component
public class NoticeApi {

    @Autowired
    private NoticeSendService sendService;

    /**
     * 使用模板发送消息
     *
     * @param channel 消息通道
     * @param request 发送请求
     * @param userId  操作用户ID
     * @return 发送响应
     */
    public NoticeSendResponse send(NoticeChannelEnum channel, NoticeSendRequest request, String userId) {
        if (channel != null) {
            request.setNoticeType(channel.getCode());
        }
        return sendService.send(request, userId);
    }

    /**
     * 使用模板发送消息，默认用户为 system
     */
    public NoticeSendResponse send(NoticeChannelEnum channel, NoticeSendRequest request) {
        return send(channel, request, "system");
    }

    /**
     * 快速发送消息（指定接收人和内容）
     *
     * @param channel  消息通道
     * @param receiver 接收人（短信/邮件/微信）
     * @param subject  主题（仅邮件使用，其他传 null）
     * @param content  内容
     * @return 发送响应
     */
    public NoticeSendResponse send(NoticeChannelEnum channel, String receiver, String subject, String content) {
        return send(channel, receiver, subject, content, "system");
    }

    /**
     * 快速发送消息（指定接收人和内容）
     *
     * @param channel  消息通道
     * @param receiver 接收人（短信/邮件/微信）
     * @param subject  主题（仅邮件使用，其他传 null）
     * @param content  内容
     * @param userId   操作用户ID
     * @return 发送响应
     */
    public NoticeSendResponse send(NoticeChannelEnum channel, String receiver, String subject, String content, String userId) {
        NoticeSendRequest request = new NoticeSendRequest();
        request.setNoticeType(channel.getCode());
        request.setReceiver(receiver);
        request.setSubject(subject);
        request.setContent(content);

        switch (channel) {
            case SMS:
                return sendService.sendSms(receiver, content, userId);
            case EMAIL:
                return sendService.sendEmail(receiver, subject, content, userId);
            case WECHAT:
                return sendService.sendWechat(receiver, content, userId);
            default:
                return sendService.send(request, userId);
        }
    }

    /**
     * 发送站内信
     *
     * @param userIds 接收用户ID列表
     * @param subject 主题
     * @param content 内容
     * @return 发送响应列表
     */
    public List<NoticeSendResponse> sendInbox(List<String> userIds, String subject, String content) {
        return sendInbox(userIds, subject, content, "system");
    }

    /**
     * 发送站内信
     *
     * @param userIds 接收用户ID列表
     * @param subject 主题
     * @param content 内容
     * @param userId  操作用户ID
     * @return 发送响应列表
     */
    public List<NoticeSendResponse> sendInbox(List<String> userIds, String subject, String content, String userId) {
        return sendService.sendInbox(userIds, subject, content, userId);
    }
}
