package com.gnosis.notice.service;

/**
 * 消息发送频率控制服务接口
 */
public interface NoticeFrequencyService {

    /**
     * 检查发送频率是否超限
     *
     * @param receiver   接收人
     * @param noticeType 通知类型
     * @return true-未超限可发送, false-已超限
     */
    boolean checkFrequency(String receiver, String noticeType);

    /**
     * 记录一次发送
     *
     * @param receiver   接收人
     * @param noticeType 通知类型
     */
    void incrementFrequency(String receiver, String noticeType);
}
