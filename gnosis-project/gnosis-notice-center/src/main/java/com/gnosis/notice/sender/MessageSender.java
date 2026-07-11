package com.gnosis.notice.sender;

import com.gnosis.notice.dto.send.NoticeSendResponse;

import java.util.List;
import java.util.Map;

/**
 * 消息发送器接口
 */
public interface MessageSender {

    /**
     * 获取通知类型
     */
    String getNoticeType();

    /**
     * 发送消息
     * @param receiver 接收人
     * @param subject 主题
     * @param content 内容
     * @param params 参数
     * @param attachmentPaths 附件路径
     * @return 发送响应
     */
    NoticeSendResponse send(String receiver, String subject, String content,
                           Map<String, Object> params, List<String> attachmentPaths);

    /**
     * 批量发送消息
     */
    List<NoticeSendResponse> batchSend(List<String> receivers, String subject, String content,
                                      Map<String, Object> params, List<String> attachmentPaths);
}
